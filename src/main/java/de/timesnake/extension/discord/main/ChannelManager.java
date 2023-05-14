/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import de.timesnake.basic.proxy.util.Network;
import de.timesnake.channel.util.listener.ChannelHandler;
import de.timesnake.channel.util.listener.ChannelListener;
import de.timesnake.channel.util.listener.ListenerType;
import de.timesnake.channel.util.message.ChannelDiscordMessage;
import de.timesnake.channel.util.message.ChannelDiscordMessage.Allocation;
import de.timesnake.library.basic.util.Loggers;
import de.timesnake.library.basic.util.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ChannelManager implements ChannelListener {

    private final Map<Long, Tuple<Long, CompletableFuture<Void>>> awaitingMoveByMemberId = new HashMap<>();
    private final Map<String, Tuple<String, CompletableFuture<VoiceChannel>>> awaitingChannelCreation = new HashMap<>();

    public ChannelManager() {
        Network.getChannel().addListener(this);
    }

    @ChannelHandler(type = {ListenerType.DISCORD_MOVE_MEMBERS})
    public void onMoveTeamsMessage(ChannelDiscordMessage<Allocation> message) {

        List<Category> categories = TimeSnakeGuild.getCategoriesByName(message.getIdentifier(),
                false);
        Category category;

        if (categories.size() == 0) {
            category = TimeSnakeGuild.createCategory(message.getIdentifier()).complete();
        } else {
            category = categories.get(0);
        }

        Map<String, ? extends Collection<UUID>> userAllocationByTeam = message.getValue()
                .getAllocation();

        for (Map.Entry<String, ? extends Collection<UUID>> entry : userAllocationByTeam.entrySet()) {
            String teamName = entry.getKey();
            Collection<UUID> uuids = entry.getValue();
            CompletableFuture<VoiceChannel> voiceChannel;

            List<VoiceChannel> voiceChannels = category.getVoiceChannels().stream().filter(v ->
                    v.getName().equals(teamName)).toList();
            if (voiceChannels.size() == 0) {
                voiceChannel = this.createVoiceChannel(category, teamName);
            } else {
                voiceChannel = CompletableFuture.completedFuture(voiceChannels.get(0));
            }

            voiceChannel.whenCompleteAsync((v, t) -> {
                        for (UUID uuid : uuids) {
                            Member member = TimeSnakeGuild.getMemberByUuid(uuid);
                            if (member != null && member.getVoiceState().inVoiceChannel()
                                    && !member.getVoiceState().getChannel().equals(v)) {
                                this.moveVoiceMember(member, v);
                            }
                        }
                    }
            );

            if (!uuids.isEmpty()) {
                Loggers.DISCORD.info("Moved members to '" + teamName + "': '"
                        + String.join("', '", uuids.stream().map(UUID::toString).toList()));
            }
        }
    }

    private CompletableFuture<Void> moveVoiceMember(Member member, VoiceChannel voiceChannel) {
        return this.moveVoiceMembers(List.of(member), voiceChannel);
    }

    private CompletableFuture<Void> moveVoiceMembers(Collection<Member> members,
            VoiceChannel voiceChannel) {

        List<CompletableFuture<Void>> futures = new ArrayList<>(members.size());

        for (Member member : members) {
            if (this.awaitingMoveByMemberId.containsKey(member.getIdLong())) {
                this.awaitingMoveByMemberId.remove(member.getIdLong()).getB().cancel(false);
            }

            CompletableFuture<Void> future = TimeSnakeGuild.moveVoiceMember(member, voiceChannel)
                    .submit();

            this.awaitingMoveByMemberId.put(member.getIdLong(),
                    new Tuple<>(member.getIdLong(), future));

            future.whenCompleteAsync(
                    (v, t) -> this.awaitingMoveByMemberId.remove(member.getIdLong()));

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<VoiceChannel> createVoiceChannel(Category category, String name) {
        if (this.awaitingChannelCreation.containsKey(name)) {
            this.awaitingChannelCreation.remove(name).getB().cancel(false);
        }

        CompletableFuture<VoiceChannel> result = category.createVoiceChannel(name).submit();
        this.awaitingChannelCreation.put(name, new Tuple<>(name,
                result.whenCompleteAsync((v, t) ->
                        this.awaitingChannelCreation.remove(name))));
        return result;
    }


    @ChannelHandler(type = {ListenerType.DISCORD_DESTROY_TEAMS})
    public void onDestroyTeamsMessage(ChannelDiscordMessage<List<String>> message) {
        Category category = this.checkCategory(message.getIdentifier());
        if (category == null) {
            return;
        }

        List<String> teamNames = message.getValue();

        if (teamNames.isEmpty()) {
            CompletableFuture.allOf(category.getVoiceChannels().stream()
                            .map(voiceChannel -> this.moveVoiceMembers(voiceChannel.getMembers(),
                                            TimeSnakeGuild.getFallbackChannel())
                                    .whenCompleteAsync((v, t) -> voiceChannel.delete().submit()))
                            .toArray(CompletableFuture[]::new))
                    .whenCompleteAsync((v, t) -> category.delete().submit());
            Loggers.DISCORD.info("Deleted category '" + category.getName() + "'");
        } else {
            category.getVoiceChannels().stream()
                    .filter(v -> teamNames.contains(v.getName()))
                    .forEach(voiceChannel -> this.moveVoiceMembers(voiceChannel.getMembers(),
                                    TimeSnakeGuild.getFallbackChannel())
                            .whenCompleteAsync((v, t) -> voiceChannel.delete().submit()));
            Loggers.DISCORD.info("Deleted channels '" + String.join("' , '", teamNames)
                    + "' in category '" + category.getName() + "'");
        }
    }

    @ChannelHandler(type = ListenerType.DISCORD_DELETE_UNUSED)
    public void onDeleteUnusedMessage(ChannelDiscordMessage<Void> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        category.getVoiceChannels().stream()
                .filter(vc -> vc.getMembers().size() == 0)
                .forEach(voiceChannel -> this.moveVoiceMembers(voiceChannel.getMembers(),
                                TimeSnakeGuild.getFallbackChannel())
                        .whenCompleteAsync((v, t) -> {
                            voiceChannel.delete().submit();
                            Loggers.DISCORD.info("Deleted unused channel in category '"
                                    + voiceChannel.getName() + "'");
                        }));
    }

    @ChannelHandler(type = ListenerType.DISCORD_HIDE_CHANNELS)
    public void onHideChannelsMessage(ChannelDiscordMessage<Boolean> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        if (msg.getValue()) {
            category.getVoiceChannels().get(0)
                    .createPermissionOverride(category.getGuild().getPublicRole())
                    .setDeny(Permission.VIEW_CHANNEL).submit();
        } else {
            category.getVoiceChannels().get(0)
                    .createPermissionOverride(category.getGuild().getPublicRole())
                    .setAllow(Permission.VIEW_CHANNEL).submit();
        }

        Loggers.DISCORD.info("Hide channels in category '" + category.getName() + "'");
    }

    @ChannelHandler(type = ListenerType.DISCORD_MUTE_CHANNEL)
    public void onMuteChannelMessage(ChannelDiscordMessage<String> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        List<VoiceChannel> channels = category.getVoiceChannels().stream()
                .filter(v -> v.getName().equals(msg.getValue())).toList();

        if (channels.isEmpty()) {
            return;
        }

        channels.get(0).createPermissionOverride(category.getGuild().getPublicRole())
                .setDeny(Permission.VOICE_SPEAK).submit();

        Loggers.DISCORD.info("Mute channel '" + channels.get(0).getName() + "' in category '"
                + category.getName() + "'");
    }

    @ChannelHandler(type = ListenerType.DISCORD_DISCONNECT_MEMBER)
    public void onDisconnectMemberChannelMessage(ChannelDiscordMessage<UUID> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        Member member = TimeSnakeGuild.getMemberByUuid(msg.getValue());
        if (member == null) {
            return;
        }

        TimeSnakeGuild.moveVoiceMember(member, null);
        Loggers.DISCORD.info("Disconnected member '" + msg.getValue().toString() + "'");
    }

    private Category checkCategory(String name) {
        List<Category> categories = TimeSnakeGuild.getCategoriesByName(name, false);
        if (categories.size() == 0) {
            return null;
        } else {
            return categories.get(0);
        }
    }

}

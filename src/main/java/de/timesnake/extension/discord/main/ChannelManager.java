/*
 * extension-discord.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.extension.discord.main;

import de.timesnake.basic.proxy.util.Network;
import de.timesnake.channel.util.listener.ChannelHandler;
import de.timesnake.channel.util.listener.ChannelListener;
import de.timesnake.channel.util.listener.ListenerType;
import de.timesnake.channel.util.message.ChannelDiscordMessage;
import de.timesnake.extension.discord.util.DatabaseUtil;
import de.timesnake.extension.discord.wrapper.ExMember;
import de.timesnake.library.basic.util.Tuple;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ChannelManager implements ChannelListener {

    private final Map<Long, Tuple<Long, CompletableFuture<Void>>> awaitingMoveByMemberId = new HashMap<>();
    private final Map<String, Tuple<String, CompletableFuture<VoiceChannel>>> awaitingChannelCreation = new HashMap<>();

    public ChannelManager() {
        Network.getChannel().addListener(this);
    }

    @ChannelHandler(type = {ListenerType.DISCORD_MOVE_MEMBERS})
    public void onMoveTeamsMessage(ChannelDiscordMessage<ChannelDiscordMessage.Allocation> message) {

        // Check if category already exists or needs to be created
        List<Category> categories = TimeSnakeGuild.getCategoriesByName(message.getIdentifier(), false);
        Category category;

        if (categories.size() == 0) { // Category does not exist
            category = TimeSnakeGuild.createCategory(message.getIdentifier()).complete();
        } else {
            if (categories.size() > 1) {
                Network.printWarning(Plugin.DISCORD, "Received MOVED_TEAMS message with ambiguous identifier.", "Category: " + message.getIdentifier());
            }
            category = categories.get(0);
        }

        // Extract allocation map and create map String -> ExChannel
        Map<String, ? extends Collection<UUID>> userAllocationByTeam = message.getValue().getAllocation();
        Map<String, CompletableFuture<VoiceChannel>> voiceChannelByTeam = new HashMap<>();

        // Create voice channels (if necessary)
        for (String teamName : userAllocationByTeam.keySet()) {
            List<VoiceChannel> voiceChannels = category.getVoiceChannels().stream().filter(v ->
                    v.getName().equals(teamName)).toList();
            if (voiceChannels.size() == 0) { // Voice channel does not exist
                voiceChannelByTeam.put(teamName, this.createVoiceChannel(category, teamName));
            } else {
                voiceChannelByTeam.put(teamName, CompletableFuture.completedFuture(voiceChannels.get(0)));
            }
        }

        // Move users
        for (Map.Entry<String, ? extends Collection<UUID>> entry : userAllocationByTeam.entrySet()) {
            voiceChannelByTeam.get(entry.getKey()).whenCompleteAsync(
                    (v, t) -> {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : entry.getValue()) {
                            Member member = TimeSnakeGuild.getMemberByUuid(uuid);
                            if (member != null && member.getVoiceState().inVoiceChannel()
                                    && !member.getVoiceState().getChannel().equals(v)) {
                                this.moveVoiceMember(member, v);
                                sb.append("#");
                            }
                            sb.append(Network.getUser(uuid).getName()).append(", ");
                        }
                        System.out.println(entry.getKey() + ": " + sb);
                    }
            );

        }
    }

    private void moveVoiceMember(Member member, VoiceChannel voiceChannel) {
        if (this.awaitingMoveByMemberId.containsKey(member.getIdLong())) {
            this.awaitingMoveByMemberId.remove(member.getIdLong()).getB().cancel(false);
        }

        TimeSnakeGuild.moveVoiceMember(member, voiceChannel);
        this.awaitingMoveByMemberId.put(member.getIdLong(), new Tuple<>(member.getIdLong(),
                TimeSnakeGuild.moveVoiceMember(member, voiceChannel).submit().whenCompleteAsync((v, t) ->
                        this.awaitingMoveByMemberId.remove(member.getIdLong()))));
    }

    private CompletableFuture<VoiceChannel> createVoiceChannel(Category category, String name) {
        if (this.awaitingChannelCreation.containsKey(name)) {
            this.awaitingChannelCreation.remove(name).getB().cancel(false);
        }

        TimeSnakeGuild.createVoiceChannel(name);
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

        // Extract channels to delete
        List<String> teamNames = message.getValue();

        if (teamNames.isEmpty()) { // Delete everything
            List<VoiceChannel> voiceChannels = category.getVoiceChannels();
            for (VoiceChannel vc : voiceChannels) {
                vc.delete();
            }
            category.delete();
        } else {
            for (String teamName : teamNames) {
                List<VoiceChannel> channels = category.getVoiceChannels().stream().filter(v -> v.getName().equals(teamName)).toList();
                if (channels.isEmpty()) {
                    Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with non-existent channel name.", "Name: " + teamName);
                } else {
                    if (channels.size() > 1) {
                        Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with ambiguous channel name.", "Name: " + teamName);
                    }
                    for (VoiceChannel c : channels) {
                        c.delete();
                    }
                }
            }
        }
    }

    @ChannelHandler(type = ListenerType.DISCORD_DELETE_UNUSED)
    public void onDeleteUnusedMessage(ChannelDiscordMessage<Void> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        List<VoiceChannel> voiceChannels = category.getVoiceChannels();
        for (VoiceChannel vc : voiceChannels) {
            if (vc.getMembers().size() == 0) {
                vc.delete();
            }
        }
    }

    @ChannelHandler(type = ListenerType.DISCORD_HIDE_CHANNELS)
    public void onHideChannelsMessage(ChannelDiscordMessage<Boolean> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        if (msg.getValue()) {
            category.getVoiceChannels().get(0).createPermissionOverride(category.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
        } else {
            category.getVoiceChannels().get(0).createPermissionOverride(category.getGuild().getPublicRole()).setAllow(Permission.VIEW_CHANNEL).queue();

        }
    }

    @ChannelHandler(type = ListenerType.DISCORD_MUTE_CHANNEL)
    public void onMuteChannelMessage(ChannelDiscordMessage<String> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        List<VoiceChannel> channels = category.getVoiceChannels().stream().filter(v -> v.getName().equals(msg.getValue())).toList();

        if (channels.isEmpty()) {
            return;
        }

        channels.get(0).createPermissionOverride(category.getGuild().getPublicRole()).setDeny(Permission.VOICE_SPEAK).queue();

    }

    @ChannelHandler(type = ListenerType.DISCORD_DISCONNECT_MEMBER)
    public void onDisconnectMemberChannelMessage(ChannelDiscordMessage<UUID> msg) {
        Category category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        ExMember member = DatabaseUtil.getMemberFromUUID(msg.getValue());
        if (member == null) {
            return;
        }

        TimeSnakeGuild.moveVoiceMember(member, null);
    }

    private Category checkCategory(String name) {
        List<Category> categories = TimeSnakeGuild.getCategoriesByName(name, false);
        if (categories.size() == 0) { // Category does not exist
            Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with non-existent identifier.", "Category: " + name);
            return null;
        } else {
            if (categories.size() > 1) {
                Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with ambiguous identifier.", "Category: " + name);
            }
            return categories.get(0);
        }
    }

}

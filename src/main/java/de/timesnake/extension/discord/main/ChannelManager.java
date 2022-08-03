package de.timesnake.extension.discord.main;

import de.timesnake.basic.proxy.util.Network;
import de.timesnake.channel.util.listener.ChannelHandler;
import de.timesnake.channel.util.listener.ChannelListener;
import de.timesnake.channel.util.listener.ListenerType;
import de.timesnake.channel.util.message.ChannelDiscordMessage;
import de.timesnake.extension.discord.util.DatabaseUtil;
import de.timesnake.extension.discord.wrapper.ExCategory;
import de.timesnake.extension.discord.wrapper.ExMember;
import de.timesnake.extension.discord.wrapper.ExVoiceChannel;
import net.dv8tion.jda.api.Permission;

import java.util.*;

public class ChannelManager implements ChannelListener {

    public ChannelManager() {
        Network.getChannel().addListener(this);
    }

    @ChannelHandler(type = {ListenerType.DISCORD_MOVE_MEMBERS})
    public void onMoveTeamsMessage(ChannelDiscordMessage<ChannelDiscordMessage.Allocation> message) {

        // Check if category already exists or needs to be created
        List<ExCategory> categories = TimeSnakeGuild.getCategoriesByName(message.getIdentifier());
        ExCategory category;

        if (categories.size() == 0) { // Category does not exist
            category = TimeSnakeGuild.createCategory(message.getIdentifier());
        } else {
            if (categories.size() > 1) {
                Network.printWarning(Plugin.DISCORD, "Received MOVED_TEAMS message with ambiguous identifier.", "Category: " + message.getIdentifier());
            }
            category = categories.get(0);
        }

        // Extract allocation map and create map String -> ExChannel
        Map<String, ? extends Collection<UUID>> userAllocationByTeam = message.getValue().getAllocation();
        Map<String, ExVoiceChannel> voiceChannelByTeam = new HashMap<>();

        // Create voice channels (if necessary)
        for (String teamName : userAllocationByTeam.keySet()) {
            List<ExVoiceChannel> voiceChannels = category.getVoiceChannelsByName(teamName, false);
            if (voiceChannels.size() == 0) { // Voice channel does not exist
                voiceChannelByTeam.put(teamName, category.createVoiceChannel(teamName));
            } else {
                voiceChannelByTeam.put(teamName, voiceChannels.get(0));
            }
        }

        // Move users
        for (Map.Entry<String, ? extends Collection<UUID>> entry : userAllocationByTeam.entrySet()) {
            ExVoiceChannel voiceChannel = voiceChannelByTeam.get(entry.getKey());
            StringBuilder sb = new StringBuilder();
            for (UUID uuid : entry.getValue()) {
                ExMember member = DatabaseUtil.getMemberFromUUID(uuid);
                if (member != null && member.isInVoiceChannel() && !member.getVoiceState().getChannel().equals(voiceChannel.getVoiceChannel())) { // Check if member is registered and online
                    TimeSnakeGuild.moveVoiceMember(member, voiceChannel);
                }
                sb.append(Network.getUser(uuid).getName()).append(", ");
            }
            System.out.println(entry.getKey() + ": " + sb);
        }


    }


    @ChannelHandler(type = {ListenerType.DISCORD_DESTROY_TEAMS})
    public void onDestroyTeamsMessage(ChannelDiscordMessage<List<String>> message) {
        ExCategory category = this.checkCategory(message.getIdentifier());
        if (category == null) {
            return;
        }

        // Extract channels to delete
        List<String> teamNames = message.getValue();

        if (teamNames.isEmpty()) { // Delete everything
            List<ExVoiceChannel> voiceChannels = category.getVoiceChannels();
            for (ExVoiceChannel vc : voiceChannels) {
                vc.delete();
            }
            category.delete();
        } else {
            for (String teamName : teamNames) {
                List<ExVoiceChannel> channels = category.getVoiceChannelsByName(teamName, false);
                if (channels.isEmpty()) {
                    Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with non-existent channel name.", "Name: " + teamName);
                } else {
                    if (channels.size() > 1) {
                        Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with ambiguous channel name.", "Name: " + teamName);
                    }
                    for (ExVoiceChannel c : channels) {
                        c.delete();
                    }
                }
            }
        }
    }

    @ChannelHandler(type = ListenerType.DISCORD_DELETE_UNUSED)
    public void onDeleteUnusedMessage(ChannelDiscordMessage<Void> msg) {
        ExCategory category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        List<ExVoiceChannel> voiceChannels = category.getVoiceChannels();
        for (ExVoiceChannel vc : voiceChannels) {
            if (vc.getMembers().size() == 0) {
                vc.delete();
            }
        }
    }

    @ChannelHandler(type = ListenerType.DISCORD_HIDE_CHANNELS)
    public void onHideChannelsMessage(ChannelDiscordMessage<Boolean> msg) {
        ExCategory category = this.checkCategory(msg.getIdentifier());
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
        ExCategory category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        List<ExVoiceChannel> channels = category.getVoiceChannelsByName(msg.getValue(), false);

        if (channels.isEmpty()) {
            return;
        }

        channels.get(0).createPermissionOverride(category.getGuild().getPublicRole()).setDeny(Permission.VOICE_SPEAK).queue();

    }

    @ChannelHandler(type = ListenerType.DISCORD_DISCONNECT_MEMBER)
    public void onDisconnectMemberChannelMessage(ChannelDiscordMessage<UUID> msg) {
        ExCategory category = this.checkCategory(msg.getIdentifier());
        if (category == null) {
            return;
        }

        ExMember member = DatabaseUtil.getMemberFromUUID(msg.getValue());
        if (member == null) {
            return;
        }

        TimeSnakeGuild.moveVoiceMember(member, null);
    }

    private ExCategory checkCategory(String name) {
        List<ExCategory> categories = TimeSnakeGuild.getCategoriesByName(name);
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

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChannelManager implements ChannelListener {

    public ChannelManager() {
        Network.getChannel().addListener(this);
    }

    @ChannelHandler(type = {ListenerType.DISCORD_MOVE_TEAMS})
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
        Map<String, List<UUID>> userAllocationByTeam = message.getValue().getAllocation();
        Map<String, ExVoiceChannel> voiceChannelByTeam = new HashMap<>();

        // Create voice channels (if necessary)
        for (String teamName : userAllocationByTeam.keySet()) {
            List<ExVoiceChannel> voiceChannels = category.getVoiceChannelsByName(teamName, false);
            if (voiceChannels.size() == 0) { // Voice channel does not exist
                voiceChannelByTeam.put(teamName, category.createVoiceChannel(teamName));
            } else {
                if (categories.size() > 1) {
                    Network.printWarning(Plugin.DISCORD, "Received MOVED_TEAMS message with ambiguous channel name.", "Name: " + teamName);
                }
                voiceChannelByTeam.put(teamName, voiceChannels.get(0));
            }
        }

        // Move users
        for (Map.Entry<String, List<UUID>> entry : userAllocationByTeam.entrySet()) {
            ExVoiceChannel voiceChannel = voiceChannelByTeam.get(entry.getKey());
            for (UUID uuid : entry.getValue()) {
                ExMember member = DatabaseUtil.getMemberFromUUID(uuid);
                if (member != null && member.isInVoiceChannel()) { // Check if member is registered and online
                    TimeSnakeGuild.moveVoiceMember(member, voiceChannel, true);
                }
            }
        }


    }


    @ChannelHandler(type = {ListenerType.DISCORD_DESTROY_TEAMS})
    public void onDestroyTeamsMessage(ChannelDiscordMessage<List<String>> message) {

        // Check if category exists
        List<ExCategory> categories = TimeSnakeGuild.getCategoriesByName(message.getIdentifier());
        ExCategory category;

        if (categories.size() == 0) { // Category does not exist
            Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with non-existent identifier.", "Category: " + message.getIdentifier());
            return;
        } else {
            if (categories.size() > 1) {
                Network.printWarning(Plugin.DISCORD, "Received DESTROY_TEAMS message with ambiguous identifier.", "Category: " + message.getIdentifier());
            }
            category = categories.get(0);
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

}

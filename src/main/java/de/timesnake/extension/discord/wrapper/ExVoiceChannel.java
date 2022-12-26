/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.ExDiscord;
import de.timesnake.extension.discord.main.TimeSnakeGuild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ExVoiceChannel extends ExGuildChannel {

    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExVoiceChannel(long id) {
        super(id);
    }

    public ExVoiceChannel(VoiceChannel channel) {
        super(channel);
    }

    public VoiceChannel getVoiceChannel() {
        return TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getVoiceChannelById(channelID);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public int getUserLimit() {
        return getVoiceChannel().getUserLimit();
    }

    public int getBitrate() {
        return getVoiceChannel().getBitrate();
    }

    @Nonnull
    public Region getRegion() {
        return getVoiceChannel().getRegion();
    }

    @Nullable
    public String getRegionRaw() {
        return getVoiceChannel().getRegionRaw();
    }

    @Override
    public void delete() {
        moveAllMembersToFallbackChannel();
        super.delete();
    }

    @Override
    public void delete(boolean async) {
        moveAllMembersToFallbackChannel();
        super.delete(async);
    }

    public void moveAllMembersToFallbackChannel() {
        for (ExMember member : getMembers()) {
            if (member.isInVoiceChannel()) {
                TimeSnakeGuild.moveVoiceMember(member, new ExVoiceChannel(ExDiscord.configFile.getFallbackChannel()));
            }
        }
    }

    /* TODO
        Nicht-implementierte Methoden von VoiceChannel:

        createCopy()
     */


}

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

package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.ExDiscord;
import de.timesnake.extension.discord.main.TimeSnakeGuild;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

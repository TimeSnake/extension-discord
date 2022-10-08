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

import de.timesnake.extension.discord.main.TimeSnakeGuild;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ExCategory extends ExGuildChannel {

    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExCategory(long id) {
        super(id);
    }

    public ExCategory(Category c) {
        super(c);
    }

    protected Category getCategory() {
        return TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getCategoryById(channelID);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Nonnull
    public List<ExGuildChannel> getChannels() {
        List<GuildChannel> channels = getCategory().getChannels();
        List<ExGuildChannel> res = new LinkedList<>();
        for (GuildChannel c : channels) {
            res.add(new ExGuildChannel(c));
        }
        return res;
    }

    @Nonnull
    public List<ExTextChannel> getTextChannels() {
        List<TextChannel> channels = getCategory().getTextChannels();
        List<ExTextChannel> res = new LinkedList<>();
        for (TextChannel c : channels) {
            res.add(new ExTextChannel(c));
        }
        return res;
    }

    @Nonnull
    public List<ExVoiceChannel> getVoiceChannels() {
        List<VoiceChannel> channels = getCategory().getVoiceChannels();
        List<ExVoiceChannel> res = new LinkedList<>();
        for (VoiceChannel c : channels) {
            res.add(new ExVoiceChannel(c));
        }
        return res;
    }

    /**
     * Create a text channel with the given name in this category and default setting. Use the
     * getManager() to change settings.
     *
     * @param s Name of the new textchannel
     * @return Wrapper-Object of the new textchannel
     */
    @CheckReturnValue
    @Nonnull
    public ExTextChannel createTextChannel(@NotNull String s) {
        return new ExTextChannel(getCategory().createTextChannel(s).complete());
    }

    /**
     * Create a voice channel with the given name in this category and default setting. Use the
     * getManager() to change settings.
     *
     * @param s Name of the new voicechannel
     * @return Wrapper-Object of the new voicechannel
     */
    @CheckReturnValue
    @Nonnull
    public ExVoiceChannel createVoiceChannel(@NotNull String s) {
        return new ExVoiceChannel(getCategory().createVoiceChannel(s).complete());
    }

    public boolean containsChannelWithName(String name) {
        for (GuildChannel c : getCategory().getChannels()) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<ExVoiceChannel> getVoiceChannelsByName(String name, boolean ignoreCase) {
        List<ExVoiceChannel> voiceChannels = getVoiceChannels();
        List<ExVoiceChannel> res = new LinkedList<>();
        for (ExVoiceChannel vc : voiceChannels) {
            if (ignoreCase) {
                if (vc.getName().equalsIgnoreCase(name)) {
                    res.add(vc);
                }
            } else {
                if (vc.getName().equals(name)) {
                    res.add(vc);
                }
            }
        }
        return res;
    }

    public List<ExGuildChannel> getGuildChannelsByName(String name, boolean ignoreCase) {
        List<ExGuildChannel> guildChannels = getChannels();
        List<ExGuildChannel> res = new LinkedList<>();
        for (ExGuildChannel c : guildChannels) {
            if (ignoreCase) {
                if (c.getName().equalsIgnoreCase(name)) {
                    res.add(c);
                }
            } else {
                if (c.getName().equals(name)) {
                    res.add(c);
                }
            }
        }
        return res;
    }

        /* TODO
        Nicht-implementierte Methoden von Category:

        getStoreChannels()
        createStageChannel()
        modifyTextChannelPositions() -> kann man sich mal ansehen
        modifyVoiceChannelPositions()
        createCopy()
     */


}

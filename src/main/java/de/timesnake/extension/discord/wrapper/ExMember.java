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
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

public class ExMember {

    protected final long memberId;

    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExMember(long id) {
        this.memberId = id;
    }

    public ExMember(Member member) {
        this.memberId = member.getIdLong();
    }


    protected Member getMember() {
        return TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getMemberById(memberId);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Check if member exists on the server
     *
     * @return
     */
    public boolean exists() {
        return getMember() != null;
    }

    /**
     * Check if member is in a voice channel
     *
     * @return
     */
    public boolean isInVoiceChannel() {
        return getMember().getVoiceState().inVoiceChannel();
    }

    public long getID() {
        return memberId;
    }

    @Nonnull
    public ExUser getUser() {
        return new ExUser(getMember().getUser());
    }

    @Nonnull
    public OffsetDateTime getTimeJoined() {
        return getMember().getTimeJoined();
    }

    public boolean hasTimeJoined() {
        return getMember().hasTimeJoined();
    }

    @Nullable
    public OffsetDateTime getTimeBoosted() {
        return getMember().getTimeBoosted();
    }

    // A voicestate is an entity. We dont cache it, since there is no reason to save a voicestate
    @Nullable
    public GuildVoiceState getVoiceState() {
        return getMember().getVoiceState();
    }

    @Nonnull
    public List<Activity> getActivities() {
        return getMember().getActivities();
    }

    @Nonnull
    public OnlineStatus getOnlineStatus() {
        return getMember().getOnlineStatus();
    }

    @Nonnull
    public OnlineStatus getOnlineStatus(@NotNull ClientType clientType) {
        return getMember().getOnlineStatus(clientType);
    }

    @Nonnull
    public EnumSet<ClientType> getActiveClients() {
        return getMember().getActiveClients();
    }

    @Nullable
    public String getNickname() {
        return getMember().getNickname();
    }

    @Nonnull
    public String getEffectiveName() {
        return getMember().getEffectiveName();
    }

    @Nullable
    public String getAvatarId() {
        return getMember().getAvatarId();
    }

    @Nullable
    public String getAvatarUrl() {
        return getMember().getAvatarUrl();
    }

    @Nonnull
    public String getEffectiveAvatarUrl() {
        return getMember().getEffectiveAvatarUrl();
    }

    @Nullable
    public Color getColor() {
        return getMember().getColor();
    }

    public int getColorRaw() {
        return getMember().getColorRaw();
    }

    public boolean canInteract(@NotNull ExMember member) {
        return getMember().canInteract(TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getMemberById(member.getID()));
    }

    public boolean isOwner() {
        return getMember().isOwner();
    }

    @Incubating
    public boolean isPending() {
        return getMember().isPending();
    }

    @Nullable
    public ExTextChannel getDefaultChannel() {
        return new ExTextChannel(getMember().getDefaultChannel());
    }

    @CheckReturnValue
    public void ban(int delDays) {
        getMember().ban(delDays).complete();
    }

    @CheckReturnValue
    public void ban(int delDays, @org.jetbrains.annotations.Nullable String reason) {
        getMember().ban(delDays, reason).complete();
    }

    @CheckReturnValue
    public void kick() {
        getMember().kick().complete();
    }

    @CheckReturnValue
    public void kick(@org.jetbrains.annotations.Nullable String reason) {
        getMember().kick(reason).complete();
    }

    @CheckReturnValue
    public void mute(boolean mute) {
        getMember().mute(mute).complete();
    }

    @CheckReturnValue
    public void deafen(boolean deafen) {
        getMember().deafen(deafen).complete();
    }

    @CheckReturnValue
    public void modifyNickname(@org.jetbrains.annotations.Nullable String nickname) {
        getMember().modifyNickname(nickname).complete();
    }

    /* TODO
        Nicht-implementierte Methoden von Member:

        getRoles() -> Rollensystem verstehen
        canInteract(Role) ^
        canInteract(Emote)
        Alle permission-Methoden aus iPermissionHolder
     */
}

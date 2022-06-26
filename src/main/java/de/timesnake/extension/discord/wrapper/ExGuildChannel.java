package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.TimeSnakeGuild;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class ExGuildChannel {

    protected final long channelID;


    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExGuildChannel(long id) {
        this.channelID = id;
    }

    public ExGuildChannel(GuildChannel channel) {
        this.channelID = channel.getIdLong();
    }

    protected GuildChannel getGuildChannel() {
        return TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getGuildChannelById(channelID);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isAvailable() {
        return getGuildChannel() != null;
    }

    public List<ExMember> getMembers() {
        List<Member> members = getGuildChannel().getMembers();
        List<ExMember> res = new LinkedList<>();
        for (Member m : members) {
            res.add(new ExMember(m));
        }
        return res;
    }

    public long getID() {
        return channelID;
    }

    public String getName() {
        return getGuildChannel().getName();
    }

    public void delete() {
        getGuildChannel().delete().complete();
    }

    public void delete(boolean async) {
        if (async) {
            getGuildChannel().delete().queue();
        } else {
            getGuildChannel().delete().complete();
        }
    }

    @Nullable
    public ExCategory getParent() {
        return new ExCategory(getGuildChannel().getParent());
    }

    public int getPosition() {
        return getGuildChannel().getPosition();
    }

    public int getPositionRaw() {
        return getGuildChannel().getPositionRaw();
    }

    @Nonnull
    public OffsetDateTime getTimeCreated() {
        return getGuildChannel().getTimeCreated();
    }

    @Nonnull
    public String getAsMention() {
        return getGuildChannel().getAsMention();
    }

    @Nonnull
    public ChannelManager getManager() {
        return getGuildChannel().getManager();
    }

    @CheckReturnValue
    @Nonnull
    public InviteAction createInvite() {
        return getGuildChannel().createInvite();
    }

    @CheckReturnValue
    @Nonnull
    public List<Invite> retrieveInvites() {
        return getGuildChannel().retrieveInvites().complete();
    }

    @Nonnull
    public ChannelType getType() {
        return getGuildChannel().getType();
    }

    /* TODO
        Alle Methoden zu Permissions wurden noch nicht implementiert!

        getGuild()
        getPermissionOverride(...)
        getPermissionOverrides()
        getMemberPermissionOverrides()
        getRolePermissionOverrides()
        isSynced()
        createCopy() -> Umständlich umzusetzen, könnte man sich ansehen
        createPermissionOverride(...)
        putPermissionOverride(...)
        upsertPermissionOverride(...)
        getJDA()
        getId()
        getIdLong()
        formatTo()
        compareTo()
     */

}

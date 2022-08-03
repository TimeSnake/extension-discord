package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.TimeSnakeGuild;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Formatter;
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
        getGuildChannel().delete().submit();
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

    @Nonnull
    public Guild getGuild() {return getGuildChannel().getGuild();}

    @Nullable
    public PermissionOverride getPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {return getGuildChannel().getPermissionOverride(iPermissionHolder);}

    @Nonnull
    public List<PermissionOverride> getPermissionOverrides() {return getGuildChannel().getPermissionOverrides();}

    @Nonnull
    public List<PermissionOverride> getMemberPermissionOverrides() {return getGuildChannel().getMemberPermissionOverrides();}

    @Nonnull
    public List<PermissionOverride> getRolePermissionOverrides() {return getGuildChannel().getRolePermissionOverrides();}

    public boolean isSynced() {return getGuildChannel().isSynced();}

    @CheckReturnValue
    @Nonnull
    public ChannelAction<? extends GuildChannel> createCopy(@NotNull Guild guild) {return getGuildChannel().createCopy(guild);}

    @CheckReturnValue
    @Nonnull
    public ChannelAction<? extends GuildChannel> createCopy() {return getGuildChannel().createCopy();}

    @CheckReturnValue
    @Nonnull
    public PermissionOverrideAction createPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {return getGuildChannel().createPermissionOverride(iPermissionHolder);}

    @CheckReturnValue
    @Nonnull
    public PermissionOverrideAction putPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {return getGuildChannel().putPermissionOverride(iPermissionHolder);}

    @CheckReturnValue
    @Nonnull
    public PermissionOverrideAction upsertPermissionOverride(@NotNull IPermissionHolder permissionHolder) {return getGuildChannel().upsertPermissionOverride(permissionHolder);}

    @Nonnull
    public JDA getJDA() {return getGuildChannel().getJDA();}

    @Nonnull
    public String getId() {return getGuildChannel().getId();}

    public long getIdLong() {return getGuildChannel().getIdLong();}

    public void formatTo(Formatter formatter, int flags, int width, int precision) {getGuildChannel().formatTo(formatter, flags, width, precision);}

    public int compareTo(@NotNull GuildChannel o) {return getGuildChannel().compareTo(o);}

}

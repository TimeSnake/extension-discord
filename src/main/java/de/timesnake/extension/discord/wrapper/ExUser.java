package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.TimeSnakeGuild;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class ExUser {

    protected final long userId;

    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExUser(long id) {
        this.userId = id;
    }

    public ExUser(User user) {
        this.userId = user.getIdLong();
    }

    protected User getUser() {
        return TimeSnakeGuild.getApi().getUserById(userId);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean exists() {
        return getUser() != null;
    }

    public ExMember getMember() {
        return TimeSnakeGuild.getMember(this);
    }

    public long getID() {
        return userId;
    }

    @Nonnull
    public String getName() {
        return getUser().getName();
    }

    @Nonnull
    public String getDiscriminator() {
        return getUser().getDiscriminator();
    }

    @Nullable
    public String getAvatarId() {
        return getUser().getAvatarId();
    }

    @Nullable
    public String getAvatarUrl() {
        return getUser().getAvatarUrl();
    }

    @Nonnull
    public String getDefaultAvatarId() {
        return getUser().getDefaultAvatarId();
    }

    @Nonnull
    public String getDefaultAvatarUrl() {
        return getUser().getDefaultAvatarUrl();
    }

    @Nonnull
    public String getEffectiveAvatarUrl() {
        return getUser().getEffectiveAvatarUrl();
    }

    @CheckReturnValue
    @Nonnull
    public User.Profile retrieveProfile() {
        return getUser().retrieveProfile().complete();
    }

    @Nonnull
    public String getAsTag() {
        return getUser().getAsTag();
    }

    public boolean hasPrivateChannel() {
        return getUser().hasPrivateChannel();
    }

    public boolean isBot() {
        return getUser().isBot();
    }

    public boolean isSystem() {
        return getUser().isSystem();
    }

    @Nonnull
    public EnumSet<User.UserFlag> getFlags() {
        return getUser().getFlags();
    }

    public int getFlagsRaw() {
        return getUser().getFlagsRaw();
    }

    @Nonnull
    public String getAsMention() {
        return getUser().getAsMention();
    }

    @CheckReturnValue
    @Nonnull
    public ExPrivateChannel openPrivateChannel() {
        return new ExPrivateChannel(getUser().openPrivateChannel().complete());
    }

    /* TODO
        Nicht-implementierte Methoden von User:

        getMutualGuilds() -> unnötig
     */


}

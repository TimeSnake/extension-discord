/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.discord.main;

import de.timesnake.database.util.Database;
import de.timesnake.database.util.user.DbUser;
import de.timesnake.extension.discord.wrapper.ExCategory;
import de.timesnake.extension.discord.wrapper.ExGuildChannel;
import de.timesnake.extension.discord.wrapper.ExMember;
import de.timesnake.extension.discord.wrapper.ExUser;
import de.timesnake.extension.discord.wrapper.ExVoiceChannel;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VanityInvite;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.MemberAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;

public class TimeSnakeGuild {

    private static TimeSnakeGuild getInstance() {
        if (instance == null) {
            instance = new TimeSnakeGuild();
        }
        return instance;
    }

    // Static function declaration /////////////////////////////////////////////////////////////////////////////////////
    public static JDA getApi() {
        return getInstance()._getApi();
    }

    public static long getGuildID() {
        return getInstance()._getGuildID();
    }

    public static void initialize(JDA api, long guildID) {
        getInstance()._initialize(api, guildID);
    }

    public static List<ExGuildChannel> getChannels() {
        return getInstance()._getChannels(true);
    }

    public static List<ExGuildChannel> getChannels(boolean showHidden) {
        return getInstance()._getChannels(showHidden);
    }

    public static ExCategory createExCategory(String name) {
        return getInstance()._createCategory(name, null);
    }

    public static ExCategory createExCategory(String name, Integer pos) {
        return getInstance()._createCategory(name, pos);
    }

    public static ExVoiceChannel createVoiceChannel(String name) {
        return getInstance()._createVoiceChannel(name, null);
    }

    public static ExVoiceChannel createVoiceChannel(String name, ExCategory parent) {
        return getInstance()._createVoiceChannel(name, parent);
    }

    public static boolean moveVoiceMember(ExMember member, ExVoiceChannel vc) {
        return getInstance()._moveVoiceMember(member, vc);
    }

    public static boolean moveVoiceMember(ExMember member, ExVoiceChannel vc, boolean async) {
        return getInstance()._moveVoiceMember(member, vc, async);
    }

    public static ExMember getMember(ExUser user) {
        return getInstance()._getMember(user);
    }

    public static List<ExCategory> getExCategoriesByName(String name) {
        return getInstance()._getCategoriesByName(name, false);
    }

    public static List<ExCategory> getExCategoriesByName(String name, boolean ignoreCase) {
        return getInstance()._getCategoriesByName(name, ignoreCase);
    }

    public static Guild getGuild() {
        return instance.api.getGuildById(instance.guildID);
    }

    @Nonnull
    public static List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {return getGuild().getCategoriesByName(name, ignoreCase);}

    @CheckReturnValue
    @Nonnull
    public static ChannelAction<Category> createCategory(@NotNull String name) {return getGuild().createCategory(name);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<Command>> retrieveCommands() {return getGuild().retrieveCommands();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Command> retrieveCommandById(@NotNull String id) {return getGuild().retrieveCommandById(id);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Command> retrieveCommandById(long id) {return getGuild().retrieveCommandById(id);}

    @CheckReturnValue
    @Nonnull
    public static CommandCreateAction upsertCommand(@NotNull CommandData command) {return getGuild().upsertCommand(command);}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CheckReturnValue
    @Nonnull
    public static CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {return getGuild().upsertCommand(name, description);}

    @CheckReturnValue
    @Nonnull
    public static CommandListUpdateAction updateCommands() {return getGuild().updateCommands();}

    @CheckReturnValue
    @Nonnull
    public static CommandEditAction editCommandById(@NotNull String id) {return getGuild().editCommandById(id);}

    @CheckReturnValue
    @Nonnull
    public static CommandEditAction editCommandById(long id) {return getGuild().editCommandById(id);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> deleteCommandById(@NotNull String commandId) {return getGuild().deleteCommandById(commandId);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> deleteCommandById(long commandId) {return getGuild().deleteCommandById(commandId);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(@NotNull String commandId) {return getGuild().retrieveCommandPrivilegesById(commandId);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(long commandId) {return getGuild().retrieveCommandPrivilegesById(commandId);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Map<String, List<CommandPrivilege>>> retrieveCommandPrivileges() {return getGuild().retrieveCommandPrivileges();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id, @NotNull Collection<? extends CommandPrivilege> privileges) {return getGuild().updateCommandPrivilegesById(id, privileges);}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id, @NotNull CommandPrivilege... privileges) {return getGuild().updateCommandPrivilegesById(id, privileges);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id, @NotNull Collection<? extends CommandPrivilege> privileges) {return getGuild().updateCommandPrivilegesById(id, privileges);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id, @NotNull CommandPrivilege... privileges) {return getGuild().updateCommandPrivilegesById(id, privileges);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Map<String, List<CommandPrivilege>>> updateCommandPrivileges(@NotNull Map<String, Collection<? extends CommandPrivilege>> privileges) {return getGuild().updateCommandPrivileges(privileges);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<EnumSet<Region>> retrieveRegions() {return getGuild().retrieveRegions();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated) {return getGuild().retrieveRegions(includeDeprecated);}

    @CheckReturnValue
    @Nonnull
    public static MemberAction addMember(@NotNull String accessToken, @NotNull String userId) {return getGuild().addMember(accessToken, userId);}

    @CheckReturnValue
    @Nonnull
    public static MemberAction addMember(@NotNull String accessToken, @NotNull User user) {return getGuild().addMember(accessToken, user);}

    @CheckReturnValue
    @Nonnull
    public static MemberAction addMember(@NotNull String accessToken, long userId) {return getGuild().addMember(accessToken, userId);}

    public static boolean isLoaded() {return getGuild().isLoaded();}

    public static void pruneMemberCache() {getGuild().pruneMemberCache();}

    public static boolean unloadMember(long userId) {return getGuild().unloadMember(userId);}

    public static int getMemberCount() {return getGuild().getMemberCount();}

    @Nonnull
    public static String getName() {return getGuild().getName();}

    @Nullable
    public static String getIconId() {return getGuild().getIconId();}

    @Nullable
    public static String getIconUrl() {return getGuild().getIconUrl();}

    @Nonnull
    public static Set<String> getFeatures() {return getGuild().getFeatures();}

    @Nullable
    public static String getSplashId() {return getGuild().getSplashId();}

    @Nullable
    public static String getSplashUrl() {return getGuild().getSplashUrl();}

    @CheckReturnValue
    @ReplaceWith("getVanityCode()")
    @DeprecatedSince("4.0.0")
    @ForRemoval(deadline = "4.4.0")
    @Deprecated
    @Nonnull
    public static RestAction<String> retrieveVanityUrl() {return getGuild().retrieveVanityUrl();}

    @Nullable
    public static String getVanityCode() {return getGuild().getVanityCode();}

    @Nullable
    public static String getVanityUrl() {return getGuild().getVanityUrl();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<VanityInvite> retrieveVanityInvite() {return getGuild().retrieveVanityInvite();}

    @Nullable
    public static String getDescription() {return getGuild().getDescription();}

    @Nonnull
    public static Locale getLocale() {return getGuild().getLocale();}

    @Nullable
    public static String getBannerId() {return getGuild().getBannerId();}

    @Nullable
    public static String getBannerUrl() {return getGuild().getBannerUrl();}

    @Nonnull
    public static Guild.BoostTier getBoostTier() {return getGuild().getBoostTier();}

    public static int getBoostCount() {return getGuild().getBoostCount();}

    @Nonnull
    public static List<Member> getBoosters() {return getGuild().getBoosters();}

    public static int getMaxBitrate() {return getGuild().getMaxBitrate();}

    public static long getMaxFileSize() {return getGuild().getMaxFileSize();}

    public static int getMaxEmotes() {return getGuild().getMaxEmotes();}

    public static int getMaxMembers() {return getGuild().getMaxMembers();}

    public static int getMaxPresences() {return getGuild().getMaxPresences();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Guild.MetaData> retrieveMetaData() {return getGuild().retrieveMetaData();}

    @Nullable
    public static VoiceChannel getAfkChannel() {return getGuild().getAfkChannel();}

    @Nullable
    public static TextChannel getSystemChannel() {return getGuild().getSystemChannel();}

    @Nullable
    public static TextChannel getRulesChannel() {return getGuild().getRulesChannel();}

    @Nullable
    public static TextChannel getCommunityUpdatesChannel() {return getGuild().getCommunityUpdatesChannel();}

    @Nullable
    public static Member getOwner() {return getGuild().getOwner();}

    public static long getOwnerIdLong() {return getGuild().getOwnerIdLong();}

    @Nonnull
    public static String getOwnerId() {return getGuild().getOwnerId();}

    @Nonnull
    public static Guild.Timeout getAfkTimeout() {return getGuild().getAfkTimeout();}

    @Nonnull
    @DeprecatedSince("4.3.0")
    @ReplaceWith("VoiceChannel.getRegion()")
    @ForRemoval(deadline = "5.0.0")
    @Deprecated
    public static Region getRegion() {return getGuild().getRegion();}

    @Nonnull
    @DeprecatedSince("4.3.0")
    @ReplaceWith("VoiceChannel.getRegionRaw()")
    @ForRemoval(deadline = "5.0.0")
    @Deprecated
    public static String getRegionRaw() {return getGuild().getRegionRaw();}

    public static boolean isMember(@NotNull User user) {return getGuild().isMember(user);}

    @Nonnull
    public static Member getSelfMember() {return getGuild().getSelfMember();}

    @Nonnull
    public static Guild.NSFWLevel getNSFWLevel() {return getGuild().getNSFWLevel();}

    @Nullable
    public static Member getMember(@NotNull User user) {return getGuild().getMember(user);}

    @Nullable
    public static Member getMemberById(@NotNull String userId) {return getGuild().getMemberById(userId);}

    @Nullable
    public static Member getMemberById(long userId) {return getGuild().getMemberById(userId);}

    @Nullable
    public static Member getMemberByTag(@NotNull String tag) {return getGuild().getMemberByTag(tag);}

    @Nullable
    public static Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {return getGuild().getMemberByTag(username, discriminator);}

    @Nonnull
    public static List<Member> getMembers() {return getGuild().getMembers();}

    @Nonnull
    public static List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {return getGuild().getMembersByName(name, ignoreCase);}

    @Nonnull
    public static List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname, boolean ignoreCase) {return getGuild().getMembersByNickname(nickname, ignoreCase);}

    @Nonnull
    public static List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {return getGuild().getMembersByEffectiveName(name, ignoreCase);}

    @Nonnull
    public static List<Member> getMembersWithRoles(@NotNull Role... roles) {return getGuild().getMembersWithRoles(roles);}

    @Nonnull
    public static List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {return getGuild().getMembersWithRoles(roles);}

    @Nonnull
    public static MemberCacheView getMemberCache() {return getGuild().getMemberCache();}

    @Nullable
    public static GuildChannel getGuildChannelById(@NotNull String id) {return getGuild().getGuildChannelById(id);}

    @Nullable
    public static GuildChannel getGuildChannelById(long id) {return getGuild().getGuildChannelById(id);}

    @Nullable
    public static GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {return getGuild().getGuildChannelById(type, id);}

    @Nullable
    public static GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {return getGuild().getGuildChannelById(type, id);}

    @Nonnull
    public static List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {return getGuild().getStageChannelsByName(name, ignoreCase);}

    @Nullable
    public static StageChannel getStageChannelById(@NotNull String id) {return getGuild().getStageChannelById(id);}

    @Nullable
    public static StageChannel getStageChannelById(long id) {return getGuild().getStageChannelById(id);}

    @Nonnull
    public static List<StageChannel> getStageChannels() {return getGuild().getStageChannels();}

    @Nullable
    public static Category getCategoryById(@NotNull String id) {return getGuild().getCategoryById(id);}

    @Nullable
    public static Category getCategoryById(long id) {return getGuild().getCategoryById(id);}

    @Nonnull
    public static List<Category> getCategories() {return getGuild().getCategories();}

    @Nonnull
    public static SortedSnowflakeCacheView<Category> getCategoryCache() {return getGuild().getCategoryCache();}

    @Nullable
    public static StoreChannel getStoreChannelById(@NotNull String id) {return getGuild().getStoreChannelById(id);}

    @Nullable
    public static StoreChannel getStoreChannelById(long id) {return getGuild().getStoreChannelById(id);}

    @Nonnull
    public static List<StoreChannel> getStoreChannels() {return getGuild().getStoreChannels();}

    @Nonnull
    public static List<StoreChannel> getStoreChannelsByName(@NotNull String name, boolean ignoreCase) {return getGuild().getStoreChannelsByName(name, ignoreCase);}

    @Nonnull
    public static SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache() {return getGuild().getStoreChannelCache();}

    @Nullable
    public static TextChannel getTextChannelById(@NotNull String id) {return getGuild().getTextChannelById(id);}

    @Nullable
    public static TextChannel getTextChannelById(long id) {return getGuild().getTextChannelById(id);}

    @Nonnull
    public static List<TextChannel> getTextChannels() {return getGuild().getTextChannels();}

    @Nonnull
    public static List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {return getGuild().getTextChannelsByName(name, ignoreCase);}

    @Nonnull
    public static SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {return getGuild().getTextChannelCache();}

    @Nullable
    public static VoiceChannel getVoiceChannelById(@NotNull String id) {return getGuild().getVoiceChannelById(id);}

    @Nullable
    public static VoiceChannel getVoiceChannelById(long id) {return getGuild().getVoiceChannelById(id);}

    @Nonnull
    public static List<VoiceChannel> getVoiceChannels() {return getGuild().getVoiceChannels();}

    @Nonnull
    public static List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {return getGuild().getVoiceChannelsByName(name, ignoreCase);}

    @Nonnull
    public static SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {return getGuild().getVoiceChannelCache();}

    @Nullable
    public static Role getRoleById(@NotNull String id) {return getGuild().getRoleById(id);}

    @Nullable
    public static Role getRoleById(long id) {return getGuild().getRoleById(id);}

    @Nonnull
    public static List<Role> getRoles() {return getGuild().getRoles();}

    @Nonnull
    public static List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {return getGuild().getRolesByName(name, ignoreCase);}

    @Nullable
    public static Role getRoleByBot(long userId) {return getGuild().getRoleByBot(userId);}

    @Nullable
    public static Role getRoleByBot(@NotNull String userId) {return getGuild().getRoleByBot(userId);}

    @Nullable
    public static Role getRoleByBot(@NotNull User user) {return getGuild().getRoleByBot(user);}

    @Nullable
    public static Role getBotRole() {return getGuild().getBotRole();}

    @Nullable
    public static Role getBoostRole() {return getGuild().getBoostRole();}

    @Nonnull
    public static SortedSnowflakeCacheView<Role> getRoleCache() {return getGuild().getRoleCache();}

    @Nullable
    public static Emote getEmoteById(@NotNull String id) {return getGuild().getEmoteById(id);}

    @Nullable
    public static Emote getEmoteById(long id) {return getGuild().getEmoteById(id);}

    @Nonnull
    public static List<Emote> getEmotes() {return getGuild().getEmotes();}

    @Nonnull
    public static List<Emote> getEmotesByName(@NotNull String name, boolean ignoreCase) {return getGuild().getEmotesByName(name, ignoreCase);}

    @Nonnull
    public static SnowflakeCacheView<Emote> getEmoteCache() {return getGuild().getEmoteCache();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<ListedEmote>> retrieveEmotes() {return getGuild().retrieveEmotes();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<ListedEmote> retrieveEmoteById(@NotNull String id) {return getGuild().retrieveEmoteById(id);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<ListedEmote> retrieveEmoteById(long id) {return getGuild().retrieveEmoteById(id);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<ListedEmote> retrieveEmote(@NotNull Emote emote) {return getGuild().retrieveEmote(emote);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<Guild.Ban>> retrieveBanList() {return getGuild().retrieveBanList();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Guild.Ban> retrieveBanById(long userId) {return getGuild().retrieveBanById(userId);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Guild.Ban> retrieveBanById(@NotNull String userId) {return getGuild().retrieveBanById(userId);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Guild.Ban> retrieveBan(@NotNull User bannedUser) {return getGuild().retrieveBan(bannedUser);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Integer> retrievePrunableMemberCount(int days) {return getGuild().retrievePrunableMemberCount(days);}

    @Nonnull
    public static Role getPublicRole() {return getGuild().getPublicRole();}

    @Nullable
    public static TextChannel getDefaultChannel() {return getGuild().getDefaultChannel();}

    @Nonnull
    public static GuildManager getManager() {return getGuild().getManager();}

    @CheckReturnValue
    @Nonnull
    public static AuditLogPaginationAction retrieveAuditLogs() {return getGuild().retrieveAuditLogs();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> leave() {return getGuild().leave();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> delete() {return getGuild().delete();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> delete(@org.jetbrains.annotations.Nullable String mfaCode) {return getGuild().delete(mfaCode);}

    @Nonnull
    public static AudioManager getAudioManager() {return getGuild().getAudioManager();}

    @Nonnull
    public static Task<Void> requestToSpeak() {return getGuild().requestToSpeak();}

    @Nonnull
    public static Task<Void> cancelRequestToSpeak() {return getGuild().cancelRequestToSpeak();}

    @Nonnull
    public static JDA getJDA() {return getGuild().getJDA();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<Invite>> retrieveInvites() {return getGuild().retrieveInvites();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<Template>> retrieveTemplates() {return getGuild().retrieveTemplates();}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Template> createTemplate(@NotNull String name, @org.jetbrains.annotations.Nullable String description) {return getGuild().createTemplate(name, description);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<List<Webhook>> retrieveWebhooks() {return getGuild().retrieveWebhooks();}

    @Nonnull
    public static List<GuildVoiceState> getVoiceStates() {return getGuild().getVoiceStates();}

    @Nonnull
    public static Guild.VerificationLevel getVerificationLevel() {return getGuild().getVerificationLevel();}

    @Nonnull
    public static Guild.NotificationLevel getDefaultNotificationLevel() {return getGuild().getDefaultNotificationLevel();}

    @Nonnull
    public static Guild.MFALevel getRequiredMFALevel() {return getGuild().getRequiredMFALevel();}

    @Nonnull
    public static Guild.ExplicitContentLevel getExplicitContentLevel() {return getGuild().getExplicitContentLevel();}

    @DeprecatedSince("4.2.0")
    @ForRemoval(deadline = "4.4.0")
    @Deprecated
    public static boolean checkVerification() {return getGuild().checkVerification();}

    @ReplaceWith("getJDA().isUnavailable(guild.getIdLong())")
    @DeprecatedSince("4.1.0")
    @Deprecated
    @ForRemoval(deadline = "4.4.0")
    public static boolean isAvailable() {return getGuild().isAvailable();}

    @ReplaceWith("loadMembers(Consumer<Member>) or loadMembers()")
    @DeprecatedSince("4.2.0")
    @ForRemoval(deadline = "5.0.0")
    @Deprecated
    @Nonnull
    public static CompletableFuture<Void> retrieveMembers() {return getGuild().retrieveMembers();}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> loadMembers() {return getGuild().loadMembers();}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {return getGuild().findMembers(filter);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {return getGuild().findMembersWithRoles(roles);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {return getGuild().findMembersWithRoles(roles);}

    @Nonnull
    public static Task<Void> loadMembers(@NotNull Consumer<Member> callback) {return getGuild().loadMembers(callback);}

    @Nonnull
    public static RestAction<Member> retrieveMember(@NotNull User user) {return getGuild().retrieveMember(user);}

    @Nonnull
    public static RestAction<Member> retrieveMemberById(@NotNull String id) {return getGuild().retrieveMemberById(id);}

    @Nonnull
    public static RestAction<Member> retrieveMemberById(long id) {return getGuild().retrieveMemberById(id);}

    @Nonnull
    public static RestAction<Member> retrieveOwner() {return getGuild().retrieveOwner();}

    @Nonnull
    public static RestAction<Member> retrieveMember(@NotNull User user, boolean update) {return getGuild().retrieveMember(user, update);}

    @Nonnull
    public static RestAction<Member> retrieveMemberById(@NotNull String id, boolean update) {return getGuild().retrieveMemberById(id, update);}

    @Nonnull
    public static RestAction<Member> retrieveMemberById(long id, boolean update) {return getGuild().retrieveMemberById(id, update);}

    @Nonnull
    public static RestAction<Member> retrieveOwner(boolean update) {return getGuild().retrieveOwner(update);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembers(@NotNull Collection<User> users) {return getGuild().retrieveMembers(users);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {return getGuild().retrieveMembersByIds(ids);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {return getGuild().retrieveMembersByIds(ids);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {return getGuild().retrieveMembersByIds(ids);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembers(boolean includePresence, @NotNull Collection<User> users) {return getGuild().retrieveMembers(includePresence, users);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull Collection<Long> ids) {return getGuild().retrieveMembersByIds(includePresence, ids);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull String... ids) {return getGuild().retrieveMembersByIds(includePresence, ids);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull long... ids) {return getGuild().retrieveMembersByIds(includePresence, ids);}

    @CheckReturnValue
    @Nonnull
    public static Task<List<Member>> retrieveMembersByPrefix(@NotNull String prefix, int limit) {return getGuild().retrieveMembersByPrefix(prefix, limit);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> moveVoiceMember(@NotNull Member member, @org.jetbrains.annotations.Nullable VoiceChannel voiceChannel) {return getGuild().moveVoiceMember(member, voiceChannel);}

    @CheckReturnValue
    @Nonnull
    public static RestAction<Void> kickVoiceMember(@NotNull Member member) {return getGuild().kickVoiceMember(member);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> modifyNickname(@NotNull Member member, @org.jetbrains.annotations.Nullable String nickname) {return getGuild().modifyNickname(member, nickname);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {return getGuild().prune(days, roles);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Integer> prune(int days, boolean wait, @NotNull Role... roles) {return getGuild().prune(days, wait, roles);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> kick(@NotNull Member member, @org.jetbrains.annotations.Nullable String reason) {return getGuild().kick(member, reason);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> kick(@NotNull String userId, @org.jetbrains.annotations.Nullable String reason) {return getGuild().kick(userId, reason);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> kick(@NotNull Member member) {return getGuild().kick(member);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> kick(@NotNull String userId) {return getGuild().kick(userId);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> ban(@NotNull User user, int delDays, @org.jetbrains.annotations.Nullable String reason) {return getGuild().ban(user, delDays, reason);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> ban(@NotNull String userId, int delDays, @org.jetbrains.annotations.Nullable String reason) {return getGuild().ban(userId, delDays, reason);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> ban(@NotNull Member member, int delDays, @org.jetbrains.annotations.Nullable String reason) {return getGuild().ban(member, delDays, reason);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> ban(@NotNull Member member, int delDays) {return getGuild().ban(member, delDays);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> ban(@NotNull User user, int delDays) {return getGuild().ban(user, delDays);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> ban(@NotNull String userId, int delDays) {return getGuild().ban(userId, delDays);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> unban(@NotNull User user) {return getGuild().unban(user);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> unban(@NotNull String userId) {return getGuild().unban(userId);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> deafen(@NotNull Member member, boolean deafen) {return getGuild().deafen(member, deafen);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> mute(@NotNull Member member, boolean mute) {return getGuild().mute(member, mute);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> addRoleToMember(@NotNull Member member, @NotNull Role role) {return getGuild().addRoleToMember(member, role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> addRoleToMember(long userId, @NotNull Role role) {return getGuild().addRoleToMember(userId, role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> addRoleToMember(@NotNull String userId, @NotNull Role role) {return getGuild().addRoleToMember(userId, role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> removeRoleFromMember(@NotNull Member member, @NotNull Role role) {return getGuild().removeRoleFromMember(member, role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> removeRoleFromMember(long userId, @NotNull Role role) {return getGuild().removeRoleFromMember(userId, role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> removeRoleFromMember(@NotNull String userId, @NotNull Role role) {return getGuild().removeRoleFromMember(userId, role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @org.jetbrains.annotations.Nullable Collection<Role> rolesToAdd, @org.jetbrains.annotations.Nullable Collection<Role> rolesToRemove) {return getGuild().modifyMemberRoles(member, rolesToAdd, rolesToRemove);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Role... roles) {return getGuild().modifyMemberRoles(member, roles);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> roles) {return getGuild().modifyMemberRoles(member, roles);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Void> transferOwnership(@NotNull Member newOwner) {return getGuild().transferOwnership(newOwner);}

    @CheckReturnValue
    @Nonnull
    public static ChannelAction<TextChannel> createTextChannel(@NotNull String name) {return getGuild().createTextChannel(name);}

    @CheckReturnValue
    @Nonnull
    public static ChannelAction<TextChannel> createTextChannel(@NotNull String name, @org.jetbrains.annotations.Nullable Category parent) {return getGuild().createTextChannel(name, parent);}

    @CheckReturnValue
    @Nonnull
    public static ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name, @org.jetbrains.annotations.Nullable Category parent) {return getGuild().createVoiceChannel(name, parent);}

    @CheckReturnValue
    @Nonnull
    public static ChannelAction<StageChannel> createStageChannel(@NotNull String name) {return getGuild().createStageChannel(name);}

    @CheckReturnValue
    @Nonnull
    public static ChannelAction<StageChannel> createStageChannel(@NotNull String name, @org.jetbrains.annotations.Nullable Category parent) {return getGuild().createStageChannel(name, parent);}

    @CheckReturnValue
    @Nonnull
    public static <T extends GuildChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {return getGuild().createCopyOfChannel(channel);}

    @CheckReturnValue
    @Nonnull
    public static RoleAction createRole() {return getGuild().createRole();}

    @CheckReturnValue
    @Nonnull
    public static RoleAction createCopyOfRole(@NotNull Role role) {return getGuild().createCopyOfRole(role);}

    @CheckReturnValue
    @Nonnull
    public static AuditableRestAction<Emote> createEmote(@NotNull String name, @NotNull Icon icon, @NotNull Role... roles) {return getGuild().createEmote(name, icon, roles);}

    @CheckReturnValue
    @Nonnull
    public static ChannelOrderAction modifyCategoryPositions() {return getGuild().modifyCategoryPositions();}

    @CheckReturnValue
    @Nonnull
    public static ChannelOrderAction modifyTextChannelPositions() {return getGuild().modifyTextChannelPositions();}

    @CheckReturnValue
    @Nonnull
    public static ChannelOrderAction modifyVoiceChannelPositions() {return getGuild().modifyVoiceChannelPositions();}

    @CheckReturnValue
    @Nonnull
    public static CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {return getGuild().modifyTextChannelPositions(category);}

    @CheckReturnValue
    @Nonnull
    public static CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {return getGuild().modifyVoiceChannelPositions(category);}

    @CheckReturnValue
    @Nonnull
    public static RoleOrderAction modifyRolePositions() {return getGuild().modifyRolePositions();}

    @CheckReturnValue
    @Nonnull
    public static RoleOrderAction modifyRolePositions(boolean useAscendingOrder) {return getGuild().modifyRolePositions(useAscendingOrder);}

    @Nonnull
    public static String getId() {return getGuild().getId();}

    public static long getIdLong() {return getGuild().getIdLong();}

    @Nonnull
    public static OffsetDateTime getTimeCreated() {return getGuild().getTimeCreated();}

    public static Member getMemberByUuid(UUID uuid) {
        return instance._getMemberByUuid(uuid);
    }

    protected static TimeSnakeGuild instance;
    private final Map<UUID, Long> discordIdByUuid = new ConcurrentHashMap<>();
    private JDA api;
    private long guildID;

    protected TimeSnakeGuild() {

    }

    // protected implementation ////////////////////////////////////////////////////////////////////////////////////////
    protected JDA _getApi() {
        return api;
    }

    protected long _getGuildID() {
        return guildID;
    }

    protected void _initialize(JDA api, long guildID) {
        this.api = api;
        this.guildID = guildID;

        for (DbUser user : Database.getUsers().getUsers()) {
            Long discordId = user.getDiscordId();
            if (discordId != null) {
                this.discordIdByUuid.put(user.getUniqueId(), discordId);
            }
        }
    }

    public Member _getMemberByUuid(UUID uuid) {
        if (!this.discordIdByUuid.containsKey(uuid)) {
            DbUser user = Database.getUsers().getUser(uuid);
            Long discordId = user.getDiscordId();
            if (discordId != null) {
                this.discordIdByUuid.put(user.getUniqueId(), discordId);
            } else {
                return null;
            }
        }

        return getMemberById(this.discordIdByUuid.get(uuid));
    }

    protected List<ExGuildChannel> _getChannels(boolean showHidden) {
        List<GuildChannel> channels = getGuild().getChannels(showHidden);
        List<ExGuildChannel> res = new LinkedList<>();
        for (GuildChannel c : channels) {
            res.add(new ExGuildChannel(c));
        }
        return res;
    }

    protected ExCategory _createCategory(String name, Integer pos) {
        Category c = getGuild().createCategory(name).setPosition(pos).complete();
        return new ExCategory(c);
    }

    protected ExVoiceChannel _createVoiceChannel(String name, ExCategory parent) {
        VoiceChannel vc = getGuild().createVoiceChannel(name, getApi().getCategoryById(parent.getID())).complete();
        return new ExVoiceChannel(vc);
    }

    protected boolean _moveVoiceMember(ExMember member, ExVoiceChannel vc) {
        if (!member.exists() || !member.isInVoiceChannel()) return false;

        getGuild().moveVoiceMember(getGuild().getMemberById(member.getID()), getGuild().getVoiceChannelById(vc.getID())).queue();
        return true;
    }

    protected boolean _moveVoiceMember(ExMember member, ExVoiceChannel vc, boolean async) {
        if (!member.exists() || !member.isInVoiceChannel()) return false;

        RestAction<Void> action = getGuild().moveVoiceMember(getGuild().getMemberById(member.getID()), getGuild().getVoiceChannelById(vc.getID()));
        if (async) {
            action.queue();
        } else {
            action.complete();
        }
        return true;
    }

    protected ExMember _getMember(ExUser user) {
        if (!user.exists()) return null;
        return new ExMember(getGuild().getMember(api.getUserById(user.getID())));
    }

    protected List<ExCategory> _getCategoriesByName(String name, boolean ignoreCase) {
        List<Category> categories = getGuild().getCategoriesByName(name, ignoreCase);
        List<ExCategory> res = new LinkedList<>();
        for (Category c : categories) {
            res.add(new ExCategory(c));
        }
        return res;
    }
}

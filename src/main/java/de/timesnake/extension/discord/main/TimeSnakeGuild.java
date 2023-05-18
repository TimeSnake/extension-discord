/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Guild.BoostTier;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.MFALevel;
import net.dv8tion.jda.api.entities.Guild.MetaData;
import net.dv8tion.jda.api.entities.Guild.NSFWLevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.Timeout;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
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

  private static final ExGuild guild = ExGuild.getInstance();

  public static long getGuildId() {
    return guild.getGuildId();
  }

  public static Guild getGuild() {
    return guild.getGuild();
  }

  public static JDA getApi() {
    return guild.getApi();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<Command>> retrieveCommands() {
    return guild.retrieveCommands();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Command> retrieveCommandById(@NotNull String s) {
    return guild.retrieveCommandById(s);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Command> retrieveCommandById(long id) {
    return guild.retrieveCommandById(id);
  }

  @Nonnull
  @CheckReturnValue
  public static CommandCreateAction upsertCommand(@NotNull CommandData commandData) {
    return guild.upsertCommand(commandData);
  }

  @Nonnull
  @CheckReturnValue
  public static CommandCreateAction upsertCommand(@NotNull String name,
      @NotNull String description) {
    return guild.upsertCommand(name, description);
  }

  @Nonnull
  @CheckReturnValue
  public static CommandListUpdateAction updateCommands() {
    return guild.updateCommands();
  }

  @Nonnull
  @CheckReturnValue
  public static CommandEditAction editCommandById(@NotNull String s) {
    return guild.editCommandById(s);
  }

  @Nonnull
  @CheckReturnValue
  public static CommandEditAction editCommandById(long id) {
    return guild.editCommandById(id);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> deleteCommandById(@NotNull String s) {
    return guild.deleteCommandById(s);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> deleteCommandById(long commandId) {
    return guild.deleteCommandById(commandId);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(
      @NotNull String s) {
    return guild.retrieveCommandPrivilegesById(s);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(long commandId) {
    return guild.retrieveCommandPrivilegesById(commandId);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Map<String, List<CommandPrivilege>>> retrieveCommandPrivileges() {
    return guild.retrieveCommandPrivileges();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String s,
      @NotNull Collection<? extends CommandPrivilege> collection) {
    return guild.updateCommandPrivilegesById(s, collection);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id,
      @NotNull CommandPrivilege... privileges) {
    return guild.updateCommandPrivilegesById(id, privileges);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id,
      @NotNull Collection<? extends CommandPrivilege> privileges) {
    return guild.updateCommandPrivilegesById(id, privileges);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id,
      @NotNull CommandPrivilege... privileges) {
    return guild.updateCommandPrivilegesById(id, privileges);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Map<String, List<CommandPrivilege>>> updateCommandPrivileges(
      @NotNull Map<String, Collection<? extends CommandPrivilege>> map) {
    return guild.updateCommandPrivileges(map);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<EnumSet<Region>> retrieveRegions() {
    return guild.retrieveRegions();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<EnumSet<Region>> retrieveRegions(boolean b) {
    return guild.retrieveRegions(b);
  }

  @Nonnull
  @CheckReturnValue
  public static MemberAction addMember(@NotNull String s, @NotNull String s1) {
    return guild.addMember(s, s1);
  }

  @Nonnull
  @CheckReturnValue
  public static MemberAction addMember(@NotNull String accessToken, @NotNull User user) {
    return guild.addMember(accessToken, user);
  }

  @Nonnull
  @CheckReturnValue
  public static MemberAction addMember(@NotNull String accessToken, long userId) {
    return guild.addMember(accessToken, userId);
  }

  public static boolean isLoaded() {
    return guild.isLoaded();
  }

  public static void pruneMemberCache() {
    guild.pruneMemberCache();
  }

  public static boolean unloadMember(long l) {
    return guild.unloadMember(l);
  }

  public static int getMemberCount() {
    return guild.getMemberCount();
  }

  @Nonnull
  public static String getName() {
    return guild.getName();
  }

  @Nullable
  public static String getIconId() {
    return guild.getIconId();
  }

  @Nullable
  public static String getIconUrl() {
    return guild.getIconUrl();
  }

  @Nonnull
  public static Set<String> getFeatures() {
    return guild.getFeatures();
  }

  @Nullable
  public static String getSplashId() {
    return guild.getSplashId();
  }

  @Nullable
  public static String getSplashUrl() {
    return guild.getSplashUrl();
  }

  /**
   * @deprecated
   */
  @Nonnull
  @Deprecated
  @ForRemoval(deadline = "4.4.0")
  @DeprecatedSince("4.0.0")
  @ReplaceWith("getVanityCode()")
  @CheckReturnValue
  public static RestAction<String> retrieveVanityUrl() {
    return guild.retrieveVanityUrl();
  }

  @Nullable
  public static String getVanityCode() {
    return guild.getVanityCode();
  }

  @Nullable
  public static String getVanityUrl() {
    return guild.getVanityUrl();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<VanityInvite> retrieveVanityInvite() {
    return guild.retrieveVanityInvite();
  }

  @Nullable
  public static String getDescription() {
    return guild.getDescription();
  }

  @Nonnull
  public static Locale getLocale() {
    return guild.getLocale();
  }

  @Nullable
  public static String getBannerId() {
    return guild.getBannerId();
  }

  @Nullable
  public static String getBannerUrl() {
    return guild.getBannerUrl();
  }

  @Nonnull
  public static BoostTier getBoostTier() {
    return guild.getBoostTier();
  }

  public static int getBoostCount() {
    return guild.getBoostCount();
  }

  @Nonnull
  public static List<Member> getBoosters() {
    return guild.getBoosters();
  }

  public static int getMaxBitrate() {
    return guild.getMaxBitrate();
  }

  public static long getMaxFileSize() {
    return guild.getMaxFileSize();
  }

  public static int getMaxEmotes() {
    return guild.getMaxEmotes();
  }

  public static int getMaxMembers() {
    return guild.getMaxMembers();
  }

  public static int getMaxPresences() {
    return guild.getMaxPresences();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<MetaData> retrieveMetaData() {
    return guild.retrieveMetaData();
  }

  @Nullable
  public static VoiceChannel getAfkChannel() {
    return guild.getAfkChannel();
  }

  @Nullable
  public static TextChannel getSystemChannel() {
    return guild.getSystemChannel();
  }

  @Nullable
  public static TextChannel getRulesChannel() {
    return guild.getRulesChannel();
  }

  @Nullable
  public static TextChannel getCommunityUpdatesChannel() {
    return guild.getCommunityUpdatesChannel();
  }

  @Nullable
  public static Member getOwner() {
    return guild.getOwner();
  }

  public static long getOwnerIdLong() {
    return guild.getOwnerIdLong();
  }

  @Nonnull
  public static String getOwnerId() {
    return guild.getOwnerId();
  }

  @Nonnull
  public static Timeout getAfkTimeout() {
    return guild.getAfkTimeout();
  }

  /**
   * @deprecated
   */
  @Deprecated
  @ForRemoval(deadline = "5.0.0")
  @ReplaceWith("VoiceChannel.getRegion()")
  @DeprecatedSince("4.3.0")
  @Nonnull
  public static Region getRegion() {
    return guild.getRegion();
  }

  /**
   * @deprecated
   */
  @Deprecated
  @ForRemoval(deadline = "5.0.0")
  @ReplaceWith("VoiceChannel.getRegionRaw()")
  @DeprecatedSince("4.3.0")
  @Nonnull
  public static String getRegionRaw() {
    return guild.getRegionRaw();
  }

  public static boolean isMember(@NotNull User user) {
    return guild.isMember(user);
  }

  @Nonnull
  public static Member getSelfMember() {
    return guild.getSelfMember();
  }

  @Nonnull
  public static NSFWLevel getNSFWLevel() {
    return guild.getNSFWLevel();
  }

  @Nullable
  public static Member getMember(@NotNull User user) {
    return guild.getMember(user);
  }

  @Nullable
  public static Member getMemberById(@NotNull String userId) {
    return guild.getMemberById(userId);
  }

  @Nullable
  public static Member getMemberById(long userId) {
    return guild.getMemberById(userId);
  }

  @Nullable
  public static Member getMemberByTag(@NotNull String tag) {
    return guild.getMemberByTag(tag);
  }

  @Nullable
  public static Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
    return guild.getMemberByTag(username, discriminator);
  }

  @Nonnull
  public static List<Member> getMembers() {
    return guild.getMembers();
  }

  @Nonnull
  public static List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
    return guild.getMembersByName(name, ignoreCase);
  }

  @Nonnull
  public static List<Member> getMembersByNickname(
      @org.jetbrains.annotations.Nullable String nickname, boolean ignoreCase) {
    return guild.getMembersByNickname(nickname, ignoreCase);
  }

  @Nonnull
  public static List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
    return guild.getMembersByEffectiveName(name, ignoreCase);
  }

  @Nonnull
  public static List<Member> getMembersWithRoles(@NotNull Role... roles) {
    return guild.getMembersWithRoles(roles);
  }

  @Nonnull
  public static List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
    return guild.getMembersWithRoles(roles);
  }

  @Nonnull
  public static MemberCacheView getMemberCache() {
    return guild.getMemberCache();
  }

  @Nullable
  public static GuildChannel getGuildChannelById(@NotNull String id) {
    return guild.getGuildChannelById(id);
  }

  @Nullable
  public static GuildChannel getGuildChannelById(long id) {
    return guild.getGuildChannelById(id);
  }

  @Nullable
  public static GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
    return guild.getGuildChannelById(type, id);
  }

  @Nullable
  public static GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
    return guild.getGuildChannelById(type, id);
  }

  @Nonnull
  public static List<StageChannel> getStageChannelsByName(@NotNull String name,
      boolean ignoreCase) {
    return guild.getStageChannelsByName(name, ignoreCase);
  }

  @Nullable
  public static StageChannel getStageChannelById(@NotNull String id) {
    return guild.getStageChannelById(id);
  }

  @Nullable
  public static StageChannel getStageChannelById(long id) {
    return guild.getStageChannelById(id);
  }

  @Nonnull
  public static List<StageChannel> getStageChannels() {
    return guild.getStageChannels();
  }

  @Nullable
  public static Category getCategoryById(@NotNull String id) {
    return guild.getCategoryById(id);
  }

  @Nullable
  public static Category getCategoryById(long id) {
    return guild.getCategoryById(id);
  }

  @Nonnull
  public static List<Category> getCategories() {
    return guild.getCategories();
  }

  @Nonnull
  public static List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
    return guild.getCategoriesByName(name, ignoreCase);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<Category> getCategoryCache() {
    return guild.getCategoryCache();
  }

  @Nullable
  public static StoreChannel getStoreChannelById(@NotNull String id) {
    return guild.getStoreChannelById(id);
  }

  @Nullable
  public static StoreChannel getStoreChannelById(long id) {
    return guild.getStoreChannelById(id);
  }

  @Nonnull
  public static List<StoreChannel> getStoreChannels() {
    return guild.getStoreChannels();
  }

  @Nonnull
  public static List<StoreChannel> getStoreChannelsByName(@NotNull String name,
      boolean ignoreCase) {
    return guild.getStoreChannelsByName(name, ignoreCase);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache() {
    return guild.getStoreChannelCache();
  }

  @Nullable
  public static TextChannel getTextChannelById(@NotNull String id) {
    return guild.getTextChannelById(id);
  }

  @Nullable
  public static TextChannel getTextChannelById(long id) {
    return guild.getTextChannelById(id);
  }

  @Nonnull
  public static List<TextChannel> getTextChannels() {
    return guild.getTextChannels();
  }

  @Nonnull
  public static List<TextChannel> getTextChannelsByName(@NotNull String name,
      boolean ignoreCase) {
    return guild.getTextChannelsByName(name, ignoreCase);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
    return guild.getTextChannelCache();
  }

  @Nullable
  public static VoiceChannel getVoiceChannelById(@NotNull String id) {
    return guild.getVoiceChannelById(id);
  }

  @Nullable
  public static VoiceChannel getVoiceChannelById(long id) {
    return guild.getVoiceChannelById(id);
  }

  @Nonnull
  public static List<VoiceChannel> getVoiceChannels() {
    return guild.getVoiceChannels();
  }

  @Nonnull
  public static List<VoiceChannel> getVoiceChannelsByName(@NotNull String name,
      boolean ignoreCase) {
    return guild.getVoiceChannelsByName(name, ignoreCase);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
    return guild.getVoiceChannelCache();
  }

  @Nonnull
  public static List<GuildChannel> getChannels() {
    return guild.getChannels();
  }

  @Nonnull
  public static List<GuildChannel> getChannels(boolean b) {
    return guild.getChannels(b);
  }

  @Nullable
  public static Role getRoleById(@NotNull String id) {
    return guild.getRoleById(id);
  }

  @Nullable
  public static Role getRoleById(long id) {
    return guild.getRoleById(id);
  }

  @Nonnull
  public static List<Role> getRoles() {
    return guild.getRoles();
  }

  @Nonnull
  public static List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
    return guild.getRolesByName(name, ignoreCase);
  }

  @Nullable
  public static Role getRoleByBot(long userId) {
    return guild.getRoleByBot(userId);
  }

  @Nullable
  public static Role getRoleByBot(@NotNull String userId) {
    return guild.getRoleByBot(userId);
  }

  @Nullable
  public static Role getRoleByBot(@NotNull User user) {
    return guild.getRoleByBot(user);
  }

  @Nullable
  public static Role getBotRole() {
    return guild.getBotRole();
  }

  @Nullable
  public static Role getBoostRole() {
    return guild.getBoostRole();
  }

  @Nonnull
  public static SortedSnowflakeCacheView<Role> getRoleCache() {
    return guild.getRoleCache();
  }

  @Nullable
  public static Emote getEmoteById(@NotNull String id) {
    return guild.getEmoteById(id);
  }

  @Nullable
  public static Emote getEmoteById(long id) {
    return guild.getEmoteById(id);
  }

  @Nonnull
  public static List<Emote> getEmotes() {
    return guild.getEmotes();
  }

  @Nonnull
  public static List<Emote> getEmotesByName(@NotNull String name, boolean ignoreCase) {
    return guild.getEmotesByName(name, ignoreCase);
  }

  @Nonnull
  public static SnowflakeCacheView<Emote> getEmoteCache() {
    return guild.getEmoteCache();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<ListedEmote>> retrieveEmotes() {
    return guild.retrieveEmotes();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<ListedEmote> retrieveEmoteById(@NotNull String s) {
    return guild.retrieveEmoteById(s);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<ListedEmote> retrieveEmoteById(long id) {
    return guild.retrieveEmoteById(id);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<ListedEmote> retrieveEmote(@NotNull Emote emote) {
    return guild.retrieveEmote(emote);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<Ban>> retrieveBanList() {
    return guild.retrieveBanList();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Ban> retrieveBanById(long userId) {
    return guild.retrieveBanById(userId);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Ban> retrieveBanById(@NotNull String s) {
    return guild.retrieveBanById(s);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Ban> retrieveBan(@NotNull User bannedUser) {
    return guild.retrieveBan(bannedUser);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Integer> retrievePrunableMemberCount(int i) {
    return guild.retrievePrunableMemberCount(i);
  }

  @Nonnull
  public static Role getPublicRole() {
    return guild.getPublicRole();
  }

  @Nullable
  public static TextChannel getDefaultChannel() {
    return guild.getDefaultChannel();
  }

  @Nonnull
  public static GuildManager getManager() {
    return guild.getManager();
  }

  @Nonnull
  @CheckReturnValue
  public static AuditLogPaginationAction retrieveAuditLogs() {
    return guild.retrieveAuditLogs();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> leave() {
    return guild.leave();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> delete() {
    return guild.delete();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> delete(@org.jetbrains.annotations.Nullable String s) {
    return guild.delete(s);
  }

  @Nonnull
  public static AudioManager getAudioManager() {
    return guild.getAudioManager();
  }

  @Nonnull
  public static Task<Void> requestToSpeak() {
    return guild.requestToSpeak();
  }

  @Nonnull
  public static Task<Void> cancelRequestToSpeak() {
    return guild.cancelRequestToSpeak();
  }

  @Nonnull
  public static JDA getJDA() {
    return guild.getJDA();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<Invite>> retrieveInvites() {
    return guild.retrieveInvites();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<Template>> retrieveTemplates() {
    return guild.retrieveTemplates();
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Template> createTemplate(@NotNull String s,
      @org.jetbrains.annotations.Nullable String s1) {
    return guild.createTemplate(s, s1);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<List<Webhook>> retrieveWebhooks() {
    return guild.retrieveWebhooks();
  }

  @Nonnull
  public static List<GuildVoiceState> getVoiceStates() {
    return guild.getVoiceStates();
  }

  @Nonnull
  public static VerificationLevel getVerificationLevel() {
    return guild.getVerificationLevel();
  }

  @Nonnull
  public static NotificationLevel getDefaultNotificationLevel() {
    return guild.getDefaultNotificationLevel();
  }

  @Nonnull
  public static MFALevel getRequiredMFALevel() {
    return guild.getRequiredMFALevel();
  }

  @Nonnull
  public static ExplicitContentLevel getExplicitContentLevel() {
    return guild.getExplicitContentLevel();
  }

  /**
   * @deprecated
   */
  @Deprecated
  @ForRemoval(deadline = "4.4.0")
  @DeprecatedSince("4.2.0")
  public static boolean checkVerification() {
    return guild.checkVerification();
  }

  /**
   * @deprecated
   */
  @ForRemoval(deadline = "4.4.0")
  @Deprecated
  @DeprecatedSince("4.1.0")
  @ReplaceWith("getJDA().isUnavailable(guild.getIdLong())")
  public static boolean isAvailable() {
    return guild.isAvailable();
  }

  /**
   * @deprecated
   */
  @Nonnull
  @Deprecated
  @ForRemoval(deadline = "5.0.0")
  @DeprecatedSince("4.2.0")
  @ReplaceWith("loadMembers(Consumer<Member>) or loadMembers()")
  public static CompletableFuture<Void> retrieveMembers() {
    return guild.retrieveMembers();
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> loadMembers() {
    return guild.loadMembers();
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
    return guild.findMembers(filter);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
    return guild.findMembersWithRoles(roles);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {
    return guild.findMembersWithRoles(roles);
  }

  @Nonnull
  public static Task<Void> loadMembers(@NotNull Consumer<Member> consumer) {
    return guild.loadMembers(consumer);
  }

  @Nonnull
  public static RestAction<Member> retrieveMember(@NotNull User user) {
    return guild.retrieveMember(user);
  }

  @Nonnull
  public static RestAction<Member> retrieveMemberById(@NotNull String id) {
    return guild.retrieveMemberById(id);
  }

  @Nonnull
  public static RestAction<Member> retrieveMemberById(long id) {
    return guild.retrieveMemberById(id);
  }

  @Nonnull
  public static RestAction<Member> retrieveOwner() {
    return guild.retrieveOwner();
  }

  @Nonnull
  public static RestAction<Member> retrieveMember(@NotNull User user, boolean update) {
    return guild.retrieveMember(user, update);
  }

  @Nonnull
  public static RestAction<Member> retrieveMemberById(@NotNull String id, boolean update) {
    return guild.retrieveMemberById(id, update);
  }

  @Nonnull
  public static RestAction<Member> retrieveMemberById(long l, boolean b) {
    return guild.retrieveMemberById(l, b);
  }

  @Nonnull
  public static RestAction<Member> retrieveOwner(boolean update) {
    return guild.retrieveOwner(update);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembers(@NotNull Collection<User> users) {
    return guild.retrieveMembers(users);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
    return guild.retrieveMembersByIds(ids);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {
    return guild.retrieveMembersByIds(ids);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {
    return guild.retrieveMembersByIds(ids);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembers(boolean includePresence,
      @NotNull Collection<User> users) {
    return guild.retrieveMembers(includePresence, users);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByIds(boolean includePresence,
      @NotNull Collection<Long> ids) {
    return guild.retrieveMembersByIds(includePresence, ids);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByIds(boolean includePresence,
      @NotNull String... ids) {
    return guild.retrieveMembersByIds(includePresence, ids);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByIds(boolean b, @NotNull long... longs) {
    return guild.retrieveMembersByIds(b, longs);
  }

  @Nonnull
  @CheckReturnValue
  public static Task<List<Member>> retrieveMembersByPrefix(@NotNull String s, int i) {
    return guild.retrieveMembersByPrefix(s, i);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> moveVoiceMember(@NotNull Member member,
      @org.jetbrains.annotations.Nullable VoiceChannel voiceChannel) {
    return guild.moveVoiceMember(member, voiceChannel);
  }

  @Nonnull
  @CheckReturnValue
  public static RestAction<Void> kickVoiceMember(@NotNull Member member) {
    return guild.kickVoiceMember(member);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> modifyNickname(@NotNull Member member,
      @org.jetbrains.annotations.Nullable String s) {
    return guild.modifyNickname(member, s);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {
    return guild.prune(days, roles);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Integer> prune(int i, boolean b, @NotNull Role... roles) {
    return guild.prune(i, b, roles);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> kick(@NotNull Member member,
      @org.jetbrains.annotations.Nullable String s) {
    return guild.kick(member, s);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> kick(@NotNull String s,
      @org.jetbrains.annotations.Nullable String s1) {
    return guild.kick(s, s1);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> kick(@NotNull Member member) {
    return guild.kick(member);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> kick(@NotNull String userId) {
    return guild.kick(userId);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> ban(@NotNull User user, int i,
      @org.jetbrains.annotations.Nullable String s) {
    return guild.ban(user, i, s);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> ban(@NotNull String s, int i,
      @org.jetbrains.annotations.Nullable String s1) {
    return guild.ban(s, i, s1);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> ban(@NotNull Member member, int delDays,
      @org.jetbrains.annotations.Nullable String reason) {
    return guild.ban(member, delDays, reason);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> ban(@NotNull Member member, int delDays) {
    return guild.ban(member, delDays);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> ban(@NotNull User user, int delDays) {
    return guild.ban(user, delDays);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> ban(@NotNull String userId, int delDays) {
    return guild.ban(userId, delDays);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> unban(@NotNull User user) {
    return guild.unban(user);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> unban(@NotNull String s) {
    return guild.unban(s);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> deafen(@NotNull Member member, boolean b) {
    return guild.deafen(member, b);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> mute(@NotNull Member member, boolean b) {
    return guild.mute(member, b);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> addRoleToMember(@NotNull Member member,
      @NotNull Role role) {
    return guild.addRoleToMember(member, role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> addRoleToMember(long userId, @NotNull Role role) {
    return guild.addRoleToMember(userId, role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> addRoleToMember(@NotNull String userId,
      @NotNull Role role) {
    return guild.addRoleToMember(userId, role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> removeRoleFromMember(@NotNull Member member,
      @NotNull Role role) {
    return guild.removeRoleFromMember(member, role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> removeRoleFromMember(long userId, @NotNull Role role) {
    return guild.removeRoleFromMember(userId, role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> removeRoleFromMember(@NotNull String userId,
      @NotNull Role role) {
    return guild.removeRoleFromMember(userId, role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
      @org.jetbrains.annotations.Nullable Collection<Role> collection,
      @org.jetbrains.annotations.Nullable Collection<Role> collection1) {
    return guild.modifyMemberRoles(member, collection, collection1);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
      @NotNull Role... roles) {
    return guild.modifyMemberRoles(member, roles);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
      @NotNull Collection<Role> collection) {
    return guild.modifyMemberRoles(member, collection);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Void> transferOwnership(@NotNull Member member) {
    return guild.transferOwnership(member);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
    return guild.createTextChannel(name);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<TextChannel> createTextChannel(@NotNull String s,
      @org.jetbrains.annotations.Nullable Category category) {
    return guild.createTextChannel(s, category);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
    return guild.createVoiceChannel(name);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String s,
      @org.jetbrains.annotations.Nullable Category category) {
    return guild.createVoiceChannel(s, category);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
    return guild.createStageChannel(name);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<StageChannel> createStageChannel(@NotNull String s,
      @org.jetbrains.annotations.Nullable Category category) {
    return guild.createStageChannel(s, category);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelAction<Category> createCategory(@NotNull String s) {
    return guild.createCategory(s);
  }

  @Nonnull
  @CheckReturnValue
  public static <T extends GuildChannel> ChannelAction<T> createCopyOfChannel(
      @NotNull T channel) {
    return guild.createCopyOfChannel(channel);
  }

  @Nonnull
  @CheckReturnValue
  public static RoleAction createRole() {
    return guild.createRole();
  }

  @Nonnull
  @CheckReturnValue
  public static RoleAction createCopyOfRole(@NotNull Role role) {
    return guild.createCopyOfRole(role);
  }

  @Nonnull
  @CheckReturnValue
  public static AuditableRestAction<Emote> createEmote(@NotNull String s, @NotNull Icon icon,
      @NotNull Role... roles) {
    return guild.createEmote(s, icon, roles);
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelOrderAction modifyCategoryPositions() {
    return guild.modifyCategoryPositions();
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelOrderAction modifyTextChannelPositions() {
    return guild.modifyTextChannelPositions();
  }

  @Nonnull
  @CheckReturnValue
  public static ChannelOrderAction modifyVoiceChannelPositions() {
    return guild.modifyVoiceChannelPositions();
  }

  @Nonnull
  @CheckReturnValue
  public static CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
    return guild.modifyTextChannelPositions(category);
  }

  @Nonnull
  @CheckReturnValue
  public static CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
    return guild.modifyVoiceChannelPositions(category);
  }

  @Nonnull
  @CheckReturnValue
  public static RoleOrderAction modifyRolePositions() {
    return guild.modifyRolePositions();
  }

  @Nonnull
  @CheckReturnValue
  public static RoleOrderAction modifyRolePositions(boolean b) {
    return guild.modifyRolePositions(b);
  }

  @Nonnull
  public static String getId() {
    return guild.getId();
  }

  public static long getIdLong() {
    return guild.getIdLong();
  }

  @Nonnull
  public static OffsetDateTime getTimeCreated() {
    return guild.getTimeCreated();
  }

  public static Member getMemberByUuid(UUID uuid) {
    return guild.getMemberByUuid(uuid);
  }

  public static VoiceChannel getFallbackChannel() {
    return guild.getFallbackChannel();
  }
}

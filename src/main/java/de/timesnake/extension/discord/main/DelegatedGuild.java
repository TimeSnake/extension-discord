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

public class DelegatedGuild {

  protected JDA api;
  protected long guildId;

  public DelegatedGuild(JDA api, long guildId) {
    this.api = api;
    this.guildId = guildId;
  }

  public long getGuildId() {
    return guildId;
  }


  public Guild getGuild() {
    return getApi().getGuildById(getGuildId());
  }

  public JDA getApi() {
    return api;
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Command>> retrieveCommands() {
    return getGuild().retrieveCommands();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Command> retrieveCommandById(@NotNull String s) {
    return getGuild().retrieveCommandById(s);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Command> retrieveCommandById(long id) {
    return getGuild().retrieveCommandById(id);
  }

  @CheckReturnValue
  @Nonnull
  public CommandCreateAction upsertCommand(@NotNull CommandData commandData) {
    return getGuild().upsertCommand(commandData);
  }

  @CheckReturnValue
  @Nonnull
  public CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {
    return getGuild().upsertCommand(name, description);
  }

  @CheckReturnValue
  @Nonnull
  public CommandListUpdateAction updateCommands() {
    return getGuild().updateCommands();
  }

  @CheckReturnValue
  @Nonnull
  public CommandEditAction editCommandById(@NotNull String s) {
    return getGuild().editCommandById(s);
  }

  @CheckReturnValue
  @Nonnull
  public CommandEditAction editCommandById(long id) {
    return getGuild().editCommandById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> deleteCommandById(@NotNull String s) {
    return getGuild().deleteCommandById(s);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> deleteCommandById(long commandId) {
    return getGuild().deleteCommandById(commandId);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(@NotNull String s) {
    return getGuild().retrieveCommandPrivilegesById(s);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<CommandPrivilege>> retrieveCommandPrivilegesById(long commandId) {
    return getGuild().retrieveCommandPrivilegesById(commandId);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Map<String, List<CommandPrivilege>>> retrieveCommandPrivileges() {
    return getGuild().retrieveCommandPrivileges();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String s,
      @NotNull Collection<? extends CommandPrivilege> collection) {
    return getGuild().updateCommandPrivilegesById(s, collection);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(@NotNull String id,
      @NotNull CommandPrivilege... privileges) {
    return getGuild().updateCommandPrivilegesById(id, privileges);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id,
      @NotNull Collection<? extends CommandPrivilege> privileges) {
    return getGuild().updateCommandPrivilegesById(id, privileges);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<CommandPrivilege>> updateCommandPrivilegesById(long id,
      @NotNull CommandPrivilege... privileges) {
    return getGuild().updateCommandPrivilegesById(id, privileges);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Map<String, List<CommandPrivilege>>> updateCommandPrivileges(
      @NotNull Map<String, Collection<? extends CommandPrivilege>> map) {
    return getGuild().updateCommandPrivileges(map);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<EnumSet<Region>> retrieveRegions() {
    return getGuild().retrieveRegions();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<EnumSet<Region>> retrieveRegions(boolean b) {
    return getGuild().retrieveRegions(b);
  }

  @CheckReturnValue
  @Nonnull
  public MemberAction addMember(@NotNull String s, @NotNull String s1) {
    return getGuild().addMember(s, s1);
  }

  @CheckReturnValue
  @Nonnull
  public MemberAction addMember(@NotNull String accessToken, @NotNull User user) {
    return getGuild().addMember(accessToken, user);
  }

  @CheckReturnValue
  @Nonnull
  public MemberAction addMember(@NotNull String accessToken, long userId) {
    return getGuild().addMember(accessToken, userId);
  }

  public boolean isLoaded() {
    return getGuild().isLoaded();
  }

  public void pruneMemberCache() {
    getGuild().pruneMemberCache();
  }

  public boolean unloadMember(long l) {
    return getGuild().unloadMember(l);
  }

  public int getMemberCount() {
    return getGuild().getMemberCount();
  }

  @Nonnull
  public String getName() {
    return getGuild().getName();
  }

  @Nullable
  public String getIconId() {
    return getGuild().getIconId();
  }

  @Nullable
  public String getIconUrl() {
    return getGuild().getIconUrl();
  }

  @Nonnull
  public Set<String> getFeatures() {
    return getGuild().getFeatures();
  }

  @Nullable
  public String getSplashId() {
    return getGuild().getSplashId();
  }

  @Nullable
  public String getSplashUrl() {
    return getGuild().getSplashUrl();
  }

  /**
   * @deprecated
   */
  @CheckReturnValue
  @ReplaceWith("getVanityCode()")
  @DeprecatedSince("4.0.0")
  @ForRemoval(deadline = "4.4.0")
  @Deprecated
  @Nonnull
  public RestAction<String> retrieveVanityUrl() {
    return getGuild().retrieveVanityUrl();
  }

  @Nullable
  public String getVanityCode() {
    return getGuild().getVanityCode();
  }

  @Nullable
  public String getVanityUrl() {
    return getGuild().getVanityUrl();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<VanityInvite> retrieveVanityInvite() {
    return getGuild().retrieveVanityInvite();
  }

  @Nullable
  public String getDescription() {
    return getGuild().getDescription();
  }

  @Nonnull
  public Locale getLocale() {
    return getGuild().getLocale();
  }

  @Nullable
  public String getBannerId() {
    return getGuild().getBannerId();
  }

  @Nullable
  public String getBannerUrl() {
    return getGuild().getBannerUrl();
  }

  @Nonnull
  public BoostTier getBoostTier() {
    return getGuild().getBoostTier();
  }

  public int getBoostCount() {
    return getGuild().getBoostCount();
  }

  @Nonnull
  public List<Member> getBoosters() {
    return getGuild().getBoosters();
  }

  public int getMaxBitrate() {
    return getGuild().getMaxBitrate();
  }

  public long getMaxFileSize() {
    return getGuild().getMaxFileSize();
  }

  public int getMaxEmotes() {
    return getGuild().getMaxEmotes();
  }

  public int getMaxMembers() {
    return getGuild().getMaxMembers();
  }

  public int getMaxPresences() {
    return getGuild().getMaxPresences();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<MetaData> retrieveMetaData() {
    return getGuild().retrieveMetaData();
  }

  @Nullable
  public VoiceChannel getAfkChannel() {
    return getGuild().getAfkChannel();
  }

  @Nullable
  public TextChannel getSystemChannel() {
    return getGuild().getSystemChannel();
  }

  @Nullable
  public TextChannel getRulesChannel() {
    return getGuild().getRulesChannel();
  }

  @Nullable
  public TextChannel getCommunityUpdatesChannel() {
    return getGuild().getCommunityUpdatesChannel();
  }

  @Nullable
  public Member getOwner() {
    return getGuild().getOwner();
  }

  public long getOwnerIdLong() {
    return getGuild().getOwnerIdLong();
  }

  @Nonnull
  public String getOwnerId() {
    return getGuild().getOwnerId();
  }

  @Nonnull
  public Timeout getAfkTimeout() {
    return getGuild().getAfkTimeout();
  }

  /**
   * @deprecated
   */
  @Nonnull
  @DeprecatedSince("4.3.0")
  @ReplaceWith("VoiceChannel.getRegion()")
  @ForRemoval(deadline = "5.0.0")
  @Deprecated
  public Region getRegion() {
    return getGuild().getRegion();
  }

  /**
   * @deprecated
   */
  @Nonnull
  @DeprecatedSince("4.3.0")
  @ReplaceWith("VoiceChannel.getRegionRaw()")
  @ForRemoval(deadline = "5.0.0")
  @Deprecated
  public String getRegionRaw() {
    return getGuild().getRegionRaw();
  }

  public boolean isMember(@NotNull User user) {
    return getGuild().isMember(user);
  }

  @Nonnull
  public Member getSelfMember() {
    return getGuild().getSelfMember();
  }

  @Nonnull
  public NSFWLevel getNSFWLevel() {
    return getGuild().getNSFWLevel();
  }

  @Nullable
  public Member getMember(@NotNull User user) {
    return getGuild().getMember(user);
  }

  @Nullable
  public Member getMemberById(@NotNull String userId) {
    return getGuild().getMemberById(userId);
  }

  @Nullable
  public Member getMemberById(long userId) {
    return getGuild().getMemberById(userId);
  }

  @Nullable
  public Member getMemberByTag(@NotNull String tag) {
    return getGuild().getMemberByTag(tag);
  }

  @Nullable
  public Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
    return getGuild().getMemberByTag(username, discriminator);
  }

  @Nonnull
  public List<Member> getMembers() {
    return getGuild().getMembers();
  }

  @Nonnull
  public List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMembersByName(name, ignoreCase);
  }

  @Nonnull
  public List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname,
      boolean ignoreCase) {
    return getGuild().getMembersByNickname(nickname, ignoreCase);
  }

  @Nonnull
  public List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMembersByEffectiveName(name, ignoreCase);
  }

  @Nonnull
  public List<Member> getMembersWithRoles(@NotNull Role... roles) {
    return getGuild().getMembersWithRoles(roles);
  }

  @Nonnull
  public List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
    return getGuild().getMembersWithRoles(roles);
  }

  @Nonnull
  public MemberCacheView getMemberCache() {
    return getGuild().getMemberCache();
  }

  @Nullable
  public GuildChannel getGuildChannelById(@NotNull String id) {
    return getGuild().getGuildChannelById(id);
  }

  @Nullable
  public GuildChannel getGuildChannelById(long id) {
    return getGuild().getGuildChannelById(id);
  }

  @Nullable
  public GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
    return getGuild().getGuildChannelById(type, id);
  }

  @Nullable
  public GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
    return getGuild().getGuildChannelById(type, id);
  }

  @Nonnull
  public List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getStageChannelsByName(name, ignoreCase);
  }

  @Nullable
  public StageChannel getStageChannelById(@NotNull String id) {
    return getGuild().getStageChannelById(id);
  }

  @Nullable
  public StageChannel getStageChannelById(long id) {
    return getGuild().getStageChannelById(id);
  }

  @Nonnull
  public List<StageChannel> getStageChannels() {
    return getGuild().getStageChannels();
  }

  @Nullable
  public Category getCategoryById(@NotNull String id) {
    return getGuild().getCategoryById(id);
  }

  @Nullable
  public Category getCategoryById(long id) {
    return getGuild().getCategoryById(id);
  }

  @Nonnull
  public List<Category> getCategories() {
    return getGuild().getCategories();
  }

  @Nonnull
  public List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getCategoriesByName(name, ignoreCase);
  }

  @Nonnull
  public SortedSnowflakeCacheView<Category> getCategoryCache() {
    return getGuild().getCategoryCache();
  }

  @Nullable
  public StoreChannel getStoreChannelById(@NotNull String id) {
    return getGuild().getStoreChannelById(id);
  }

  @Nullable
  public StoreChannel getStoreChannelById(long id) {
    return getGuild().getStoreChannelById(id);
  }

  @Nonnull
  public List<StoreChannel> getStoreChannels() {
    return getGuild().getStoreChannels();
  }

  @Nonnull
  public List<StoreChannel> getStoreChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getStoreChannelsByName(name, ignoreCase);
  }

  @Nonnull
  public SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache() {
    return getGuild().getStoreChannelCache();
  }

  @Nullable
  public TextChannel getTextChannelById(@NotNull String id) {
    return getGuild().getTextChannelById(id);
  }

  @Nullable
  public TextChannel getTextChannelById(long id) {
    return getGuild().getTextChannelById(id);
  }

  @Nonnull
  public List<TextChannel> getTextChannels() {
    return getGuild().getTextChannels();
  }

  @Nonnull
  public List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getTextChannelsByName(name, ignoreCase);
  }

  @Nonnull
  public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
    return getGuild().getTextChannelCache();
  }

  @Nullable
  public VoiceChannel getVoiceChannelById(@NotNull String id) {
    return getGuild().getVoiceChannelById(id);
  }

  @Nullable
  public VoiceChannel getVoiceChannelById(long id) {
    return getGuild().getVoiceChannelById(id);
  }

  @Nonnull
  public List<VoiceChannel> getVoiceChannels() {
    return getGuild().getVoiceChannels();
  }

  @Nonnull
  public List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getVoiceChannelsByName(name, ignoreCase);
  }

  @Nonnull
  public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
    return getGuild().getVoiceChannelCache();
  }

  @Nonnull
  public List<GuildChannel> getChannels() {
    return getGuild().getChannels();
  }

  @Nonnull
  public List<GuildChannel> getChannels(boolean b) {
    return getGuild().getChannels(b);
  }

  @Nullable
  public Role getRoleById(@NotNull String id) {
    return getGuild().getRoleById(id);
  }

  @Nullable
  public Role getRoleById(long id) {
    return getGuild().getRoleById(id);
  }

  @Nonnull
  public List<Role> getRoles() {
    return getGuild().getRoles();
  }

  @Nonnull
  public List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getRolesByName(name, ignoreCase);
  }

  @Nullable
  public Role getRoleByBot(long userId) {
    return getGuild().getRoleByBot(userId);
  }

  @Nullable
  public Role getRoleByBot(@NotNull String userId) {
    return getGuild().getRoleByBot(userId);
  }

  @Nullable
  public Role getRoleByBot(@NotNull User user) {
    return getGuild().getRoleByBot(user);
  }

  @Nullable
  public Role getBotRole() {
    return getGuild().getBotRole();
  }

  @Nullable
  public Role getBoostRole() {
    return getGuild().getBoostRole();
  }

  @Nonnull
  public SortedSnowflakeCacheView<Role> getRoleCache() {
    return getGuild().getRoleCache();
  }

  @Nullable
  public Emote getEmoteById(@NotNull String id) {
    return getGuild().getEmoteById(id);
  }

  @Nullable
  public Emote getEmoteById(long id) {
    return getGuild().getEmoteById(id);
  }

  @Nonnull
  public List<Emote> getEmotes() {
    return getGuild().getEmotes();
  }

  @Nonnull
  public List<Emote> getEmotesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getEmotesByName(name, ignoreCase);
  }

  @Nonnull
  public SnowflakeCacheView<Emote> getEmoteCache() {
    return getGuild().getEmoteCache();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<ListedEmote>> retrieveEmotes() {
    return getGuild().retrieveEmotes();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<ListedEmote> retrieveEmoteById(@NotNull String s) {
    return getGuild().retrieveEmoteById(s);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<ListedEmote> retrieveEmoteById(long id) {
    return getGuild().retrieveEmoteById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<ListedEmote> retrieveEmote(@NotNull Emote emote) {
    return getGuild().retrieveEmote(emote);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Ban>> retrieveBanList() {
    return getGuild().retrieveBanList();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Ban> retrieveBanById(long userId) {
    return getGuild().retrieveBanById(userId);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Ban> retrieveBanById(@NotNull String s) {
    return getGuild().retrieveBanById(s);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Ban> retrieveBan(@NotNull User bannedUser) {
    return getGuild().retrieveBan(bannedUser);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Integer> retrievePrunableMemberCount(int i) {
    return getGuild().retrievePrunableMemberCount(i);
  }

  @Nonnull
  public Role getPublicRole() {
    return getGuild().getPublicRole();
  }

  @Nullable
  public TextChannel getDefaultChannel() {
    return getGuild().getDefaultChannel();
  }

  @Nonnull
  public GuildManager getManager() {
    return getGuild().getManager();
  }

  @CheckReturnValue
  @Nonnull
  public AuditLogPaginationAction retrieveAuditLogs() {
    return getGuild().retrieveAuditLogs();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> leave() {
    return getGuild().leave();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> delete() {
    return getGuild().delete();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> delete(@org.jetbrains.annotations.Nullable String s) {
    return getGuild().delete(s);
  }

  @Nonnull
  public AudioManager getAudioManager() {
    return getGuild().getAudioManager();
  }

  @Nonnull
  public Task<Void> requestToSpeak() {
    return getGuild().requestToSpeak();
  }

  @Nonnull
  public Task<Void> cancelRequestToSpeak() {
    return getGuild().cancelRequestToSpeak();
  }

  @Nonnull
  public JDA getJDA() {
    return getGuild().getJDA();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Invite>> retrieveInvites() {
    return getGuild().retrieveInvites();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Template>> retrieveTemplates() {
    return getGuild().retrieveTemplates();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Template> createTemplate(@NotNull String s,
      @org.jetbrains.annotations.Nullable String s1) {
    return getGuild().createTemplate(s, s1);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Webhook>> retrieveWebhooks() {
    return getGuild().retrieveWebhooks();
  }

  @Nonnull
  public List<GuildVoiceState> getVoiceStates() {
    return getGuild().getVoiceStates();
  }

  @Nonnull
  public VerificationLevel getVerificationLevel() {
    return getGuild().getVerificationLevel();
  }

  @Nonnull
  public NotificationLevel getDefaultNotificationLevel() {
    return getGuild().getDefaultNotificationLevel();
  }

  @Nonnull
  public MFALevel getRequiredMFALevel() {
    return getGuild().getRequiredMFALevel();
  }

  @Nonnull
  public ExplicitContentLevel getExplicitContentLevel() {
    return getGuild().getExplicitContentLevel();
  }

  /**
   * @deprecated
   */
  @DeprecatedSince("4.2.0")
  @ForRemoval(deadline = "4.4.0")
  @Deprecated
  public boolean checkVerification() {
    return getGuild().checkVerification();
  }

  /**
   * @deprecated
   */
  @ReplaceWith("getJDA().isUnavailable(guild.getIdLong())")
  @DeprecatedSince("4.1.0")
  @Deprecated
  @ForRemoval(deadline = "4.4.0")
  public boolean isAvailable() {
    return getGuild().isAvailable();
  }

  /**
   * @deprecated
   */
  @ReplaceWith("loadMembers(Consumer<Member>) or loadMembers()")
  @DeprecatedSince("4.2.0")
  @ForRemoval(deadline = "5.0.0")
  @Deprecated
  @Nonnull
  public CompletableFuture<Void> retrieveMembers() {
    return getGuild().retrieveMembers();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> loadMembers() {
    return getGuild().loadMembers();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
    return getGuild().findMembers(filter);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
    return getGuild().findMembersWithRoles(roles);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {
    return getGuild().findMembersWithRoles(roles);
  }

  @Nonnull
  public Task<Void> loadMembers(@NotNull Consumer<Member> consumer) {
    return getGuild().loadMembers(consumer);
  }

  @Nonnull
  public RestAction<Member> retrieveMember(@NotNull User user) {
    return getGuild().retrieveMember(user);
  }

  @Nonnull
  public RestAction<Member> retrieveMemberById(@NotNull String id) {
    return getGuild().retrieveMemberById(id);
  }

  @Nonnull
  public RestAction<Member> retrieveMemberById(long id) {
    return getGuild().retrieveMemberById(id);
  }

  @Nonnull
  public RestAction<Member> retrieveOwner() {
    return getGuild().retrieveOwner();
  }

  @Nonnull
  public RestAction<Member> retrieveMember(@NotNull User user, boolean update) {
    return getGuild().retrieveMember(user, update);
  }

  @Nonnull
  public RestAction<Member> retrieveMemberById(@NotNull String id, boolean update) {
    return getGuild().retrieveMemberById(id, update);
  }

  @Nonnull
  public RestAction<Member> retrieveMemberById(long l, boolean b) {
    return getGuild().retrieveMemberById(l, b);
  }

  @Nonnull
  public RestAction<Member> retrieveOwner(boolean update) {
    return getGuild().retrieveOwner(update);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembers(@NotNull Collection<User> users) {
    return getGuild().retrieveMembers(users);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembers(boolean includePresence,
      @NotNull Collection<User> users) {
    return getGuild().retrieveMembers(includePresence, users);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(boolean includePresence,
      @NotNull Collection<Long> ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(boolean includePresence,
      @NotNull String... ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(boolean b, @NotNull long... longs) {
    return getGuild().retrieveMembersByIds(b, longs);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByPrefix(@NotNull String s, int i) {
    return getGuild().retrieveMembersByPrefix(s, i);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> moveVoiceMember(@NotNull Member member,
      @org.jetbrains.annotations.Nullable VoiceChannel voiceChannel) {
    return getGuild().moveVoiceMember(member, voiceChannel);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> kickVoiceMember(@NotNull Member member) {
    return getGuild().kickVoiceMember(member);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyNickname(@NotNull Member member,
      @org.jetbrains.annotations.Nullable String s) {
    return getGuild().modifyNickname(member, s);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {
    return getGuild().prune(days, roles);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Integer> prune(int i, boolean b, @NotNull Role... roles) {
    return getGuild().prune(i, b, roles);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> kick(@NotNull Member member,
      @org.jetbrains.annotations.Nullable String s) {
    return getGuild().kick(member, s);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> kick(@NotNull String s,
      @org.jetbrains.annotations.Nullable String s1) {
    return getGuild().kick(s, s1);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> kick(@NotNull Member member) {
    return getGuild().kick(member);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> kick(@NotNull String userId) {
    return getGuild().kick(userId);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull User user, int i,
      @org.jetbrains.annotations.Nullable String s) {
    return getGuild().ban(user, i, s);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull String s, int i,
      @org.jetbrains.annotations.Nullable String s1) {
    return getGuild().ban(s, i, s1);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull Member member, int delDays,
      @org.jetbrains.annotations.Nullable String reason) {
    return getGuild().ban(member, delDays, reason);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull Member member, int delDays) {
    return getGuild().ban(member, delDays);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull User user, int delDays) {
    return getGuild().ban(user, delDays);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull String userId, int delDays) {
    return getGuild().ban(userId, delDays);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> unban(@NotNull User user) {
    return getGuild().unban(user);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> unban(@NotNull String s) {
    return getGuild().unban(s);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> deafen(@NotNull Member member, boolean b) {
    return getGuild().deafen(member, b);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> mute(@NotNull Member member, boolean b) {
    return getGuild().mute(member, b);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> addRoleToMember(@NotNull Member member, @NotNull Role role) {
    return getGuild().addRoleToMember(member, role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> addRoleToMember(long userId, @NotNull Role role) {
    return getGuild().addRoleToMember(userId, role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> addRoleToMember(@NotNull String userId, @NotNull Role role) {
    return getGuild().addRoleToMember(userId, role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> removeRoleFromMember(@NotNull Member member,
      @NotNull Role role) {
    return getGuild().removeRoleFromMember(member, role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> removeRoleFromMember(long userId, @NotNull Role role) {
    return getGuild().removeRoleFromMember(userId, role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> removeRoleFromMember(@NotNull String userId,
      @NotNull Role role) {
    return getGuild().removeRoleFromMember(userId, role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
      @org.jetbrains.annotations.Nullable Collection<Role> collection,
      @org.jetbrains.annotations.Nullable Collection<Role> collection1) {
    return getGuild().modifyMemberRoles(member, collection, collection1);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
      @NotNull Role... roles) {
    return getGuild().modifyMemberRoles(member, roles);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
      @NotNull Collection<Role> collection) {
    return getGuild().modifyMemberRoles(member, collection);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> transferOwnership(@NotNull Member member) {
    return getGuild().transferOwnership(member);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
    return getGuild().createTextChannel(name);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<TextChannel> createTextChannel(@NotNull String s,
      @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createTextChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
    return getGuild().createVoiceChannel(name);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String s,
      @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createVoiceChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
    return getGuild().createStageChannel(name);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<StageChannel> createStageChannel(@NotNull String s,
      @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createStageChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<Category> createCategory(@NotNull String s) {
    return getGuild().createCategory(s);
  }

  @CheckReturnValue
  @Nonnull
  public <T extends GuildChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {
    return getGuild().createCopyOfChannel(channel);
  }

  @CheckReturnValue
  @Nonnull
  public RoleAction createRole() {
    return getGuild().createRole();
  }

  @CheckReturnValue
  @Nonnull
  public RoleAction createCopyOfRole(@NotNull Role role) {
    return getGuild().createCopyOfRole(role);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Emote> createEmote(@NotNull String s, @NotNull Icon icon,
      @NotNull Role... roles) {
    return getGuild().createEmote(s, icon, roles);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelOrderAction modifyCategoryPositions() {
    return getGuild().modifyCategoryPositions();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelOrderAction modifyTextChannelPositions() {
    return getGuild().modifyTextChannelPositions();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelOrderAction modifyVoiceChannelPositions() {
    return getGuild().modifyVoiceChannelPositions();
  }

  @CheckReturnValue
  @Nonnull
  public CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
    return getGuild().modifyTextChannelPositions(category);
  }

  @CheckReturnValue
  @Nonnull
  public CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
    return getGuild().modifyVoiceChannelPositions(category);
  }

  @CheckReturnValue
  @Nonnull
  public RoleOrderAction modifyRolePositions() {
    return getGuild().modifyRolePositions();
  }

  @CheckReturnValue
  @Nonnull
  public RoleOrderAction modifyRolePositions(boolean b) {
    return getGuild().modifyRolePositions(b);
  }

  @Nonnull
  public String getId() {
    return getGuild().getId();
  }

  public long getIdLong() {
    return getGuild().getIdLong();
  }

  @Nonnull
  public OffsetDateTime getTimeCreated() {
    return getGuild().getTimeCreated();
  }
}
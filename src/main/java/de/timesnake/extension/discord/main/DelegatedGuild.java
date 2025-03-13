/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild.*;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICopyableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.PrivilegeConfig;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.managers.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedChannelCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
  public RestAction<PrivilegeConfig> retrieveCommandPrivileges() {
    return getGuild().retrieveCommandPrivileges();
  }

  @CheckReturnValue
  @Nonnull
  public GuildStickerManager editSticker(@NotNull StickerSnowflake stickerSnowflake) {
    return getGuild().editSticker(stickerSnowflake);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Template>> retrieveTemplates() {
    return getGuild().retrieveTemplates();
  }

  @Nullable
  public Category getCategoryById(long id) {
    return getGuild().getCategoryById(id);
  }

  @Nonnull
  public Task<Void> loadMembers(@NotNull Consumer<Member> consumer) {
    return getGuild().loadMembers(consumer);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> ban(@NotNull UserSnowflake userSnowflake, int i, @NotNull TimeUnit timeUnit) {
    return getGuild().ban(userSnowflake, i, timeUnit);
  }

  @Nullable
  public Member getMemberById(long userId) {
    return getGuild().getMemberById(userId);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  @Nonnull
  @Unmodifiable
  public List<Category> getCategories() {
    return getGuild().getCategories();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Integer> prune(int i, boolean b, @NotNull Role... roles) {
    return getGuild().prune(i, b, roles);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
    return getGuild().createVoiceChannel(name);
  }

  @Nullable
  public GuildChannel getGuildChannelById(@NotNull String id) {
    return getGuild().getGuildChannelById(id);
  }

  @Nullable
  public ImageProxy getSplash() {
    return getGuild().getSplash();
  }

  @Nullable
  public TextChannel getCommunityUpdatesChannel() {
    return getGuild().getCommunityUpdatesChannel();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> timeoutUntil(@NotNull UserSnowflake userSnowflake,
                                                @NotNull TemporalAccessor temporalAccessor) {
    return getGuild().timeoutUntil(userSnowflake, temporalAccessor);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<AutoModRule> createAutoModRule(@NotNull AutoModRuleData autoModRuleData) {
    return getGuild().createAutoModRule(autoModRuleData);
  }

  @Nonnull
  public MFALevel getRequiredMFALevel() {
    return getGuild().getRequiredMFALevel();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> delete(@org.jetbrains.annotations.Nullable String s) {
    return getGuild().delete(s);
  }

  public boolean isInvitesDisabled() {
    return getGuild().isInvitesDisabled();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> moveVoiceMember(@NotNull Member member,
                                          @org.jetbrains.annotations.Nullable AudioChannel audioChannel) {
    return getGuild().moveVoiceMember(member, audioChannel);
  }

  @CheckReturnValue
  @Nonnull
  public MemberAction addMember(@NotNull String s, @NotNull UserSnowflake userSnowflake) {
    return getGuild().addMember(s, userSnowflake);
  }

  @CheckReturnValue
  @Nonnull
  public RoleAction createRole() {
    return getGuild().createRole();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Command> retrieveCommandById(@NotNull String s) {
    return getGuild().retrieveCommandById(s);
  }

  @Nonnull
  @Unmodifiable
  public List<GuildChannel> getChannels(boolean b) {
    return getGuild().getChannels(b);
  }

  @Nullable
  public TextChannel getTextChannelById(long id) {
    return getGuild().getTextChannelById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<MetaData> retrieveMetaData() {
    return getGuild().retrieveMetaData();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<StageChannel> createStageChannel(@NotNull String s,
                                                        @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createStageChannel(s, category);
  }

  @Nullable
  public ThreadChannel getThreadChannelById(@NotNull String id) {
    return getGuild().getThreadChannelById(id);
  }

  @Nullable
  public Role getRoleById(@NotNull String id) {
    return getGuild().getRoleById(id);
  }

  @Nullable
  public String getIconId() {
    return getGuild().getIconId();
  }

  @Nullable
  public Member getMemberByTag(@NotNull String tag) {
    return getGuild().getMemberByTag(tag);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<GuildVoiceState> retrieveMemberVoiceStateById(@NotNull String id) {
    return getGuild().retrieveMemberVoiceStateById(id);
  }

  @CheckReturnValue
  @Nonnull
  public CommandEditAction editCommandById(@NotNull String s) {
    return getGuild().editCommandById(s);
  }

  @Nonnull
  public String getOwnerId() {
    return getGuild().getOwnerId();
  }

  @Nonnull
  public SortedSnowflakeCacheView<Category> getCategoryCache() {
    return getGuild().getCategoryCache();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(long targetId) {
    return getGuild().retrieveIntegrationPrivilegesById(targetId);
  }

  public int getMaxBitrate() {
    return getGuild().getMaxBitrate();
  }

  @Nonnull
  @Unmodifiable
  public List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getRolesByName(name, ignoreCase);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyNickname(@NotNull Member member,
                                                  @org.jetbrains.annotations.Nullable String s) {
    return getGuild().modifyNickname(member, s);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
    return getGuild().createStageChannel(name);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Template> createTemplate(@NotNull String s, @org.jetbrains.annotations.Nullable String s1) {
    return getGuild().createTemplate(s, s1);
  }

  @Nullable
  public Member getMemberById(@NotNull String userId) {
    return getGuild().getMemberById(userId);
  }

  @Nonnull
  @Unmodifiable
  public List<TextChannel> getTextChannels() {
    return getGuild().getTextChannels();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<AutoModRule> retrieveAutoModRuleById(long id) {
    return getGuild().retrieveAutoModRuleById(id);
  }

  @Nonnull
  public SortedSnowflakeCacheView<ForumChannel> getForumChannelCache() {
    return getGuild().getForumChannelCache();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<BulkBanResponse> ban(@NotNull Collection<? extends UserSnowflake> collection,
                                                  @org.jetbrains.annotations.Nullable Duration duration) {
    return getGuild().ban(collection, duration);
  }

  @Nullable
  public Category getCategoryById(@NotNull String id) {
    return getGuild().getCategoryById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<ThreadChannel>> retrieveActiveThreads() {
    return getGuild().retrieveActiveThreads();
  }

  @Nullable
  public Member getMember(@NotNull UserSnowflake userSnowflake) {
    return getGuild().getMember(userSnowflake);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<GuildVoiceState> retrieveMemberVoiceStateById(long l) {
    return getGuild().retrieveMemberVoiceStateById(l);
  }

  @Nullable
  public TextChannel getTextChannelById(@NotNull String id) {
    return getGuild().getTextChannelById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<AutoModRule>> retrieveAutoModRules() {
    return getGuild().retrieveAutoModRules();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> deleteAutoModRuleById(long id) {
    return getGuild().deleteAutoModRuleById(id);
  }

  @Nullable
  public VoiceChannel getAfkChannel() {
    return getGuild().getAfkChannel();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<ForumChannel> createForumChannel(@NotNull String s,
                                                        @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createForumChannel(s, category);
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
  public RoleAction createCopyOfRole(@NotNull Role role) {
    return getGuild().createCopyOfRole(role);
  }

  @Nonnull
  public Task<Void> requestToSpeak() {
    return getGuild().requestToSpeak();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull Collection<Long> ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Command>> retrieveCommands(boolean b) {
    return getGuild().retrieveCommands(b);
  }

  @Nonnull
  public DiscordLocale getLocale() {
    return getGuild().getLocale();
  }

  @CheckReturnValue
  @Nonnull
  public GuildManager getManager() {
    return getGuild().getManager();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<GuildSticker>> retrieveStickers() {
    return getGuild().retrieveStickers();
  }

  @Nullable
  public NewsChannel getNewsChannelById(long id) {
    return getGuild().getNewsChannelById(id);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(boolean b, @NotNull long... longs) {
    return getGuild().retrieveMembersByIds(b, longs);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<EnumSet<Region>> retrieveRegions() {
    return getGuild().retrieveRegions();
  }

  @Nonnull
  @Unmodifiable
  public List<NewsChannel> getNewsChannels() {
    return getGuild().getNewsChannels();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Role... roles) {
    return getGuild().modifyMemberRoles(member, roles);
  }

  @Nonnull
  public SortedSnowflakeCacheView<Role> getRoleCache() {
    return getGuild().getRoleCache();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<MediaChannel> createMediaChannel(@NotNull String s,
                                                        @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createMediaChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public RoleOrderAction modifyRolePositions() {
    return getGuild().modifyRolePositions();
  }

  @Nonnull
  @Unmodifiable
  public List<VoiceChannel> getVoiceChannels() {
    return getGuild().getVoiceChannels();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> addRoleToMember(@NotNull UserSnowflake userSnowflake, @NotNull Role role) {
    return getGuild().addRoleToMember(userSnowflake, role);
  }

  @CheckReturnValue
  @Nonnull
  public ScheduledEventAction createScheduledEvent(@NotNull String s, @NotNull GuildChannel guildChannel,
                                                   @NotNull OffsetDateTime offsetDateTime) {
    return getGuild().createScheduledEvent(s, guildChannel, offsetDateTime);
  }

  @Nullable
  public String getVanityCode() {
    return getGuild().getVanityCode();
  }

  @Nonnull
  public MemberCacheView getMemberCache() {
    return getGuild().getMemberCache();
  }

  @Nonnull
  public SnowflakeCacheView<GuildSticker> getStickerCache() {
    return getGuild().getStickerCache();
  }

  @Nullable
  public NewsChannel getNewsChannelById(@NotNull String id) {
    return getGuild().getNewsChannelById(id);
  }

  public boolean unloadMember(long l) {
    return getGuild().unloadMember(l);
  }

  @Nullable
  public RichCustomEmoji getEmojiById(long id) {
    return getGuild().getEmojiById(id);
  }

  @CheckReturnValue
  @Nonnull
  public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(long id) {
    return getGuild().retrieveScheduledEventById(id);
  }

  @Nullable
  public TextChannel getSafetyAlertsChannel() {
    return getGuild().getSafetyAlertsChannel();
  }

  @Nonnull
  public ExplicitContentLevel getExplicitContentLevel() {
    return getGuild().getExplicitContentLevel();
  }

  @Nonnull
  @Unmodifiable
  public List<RichCustomEmoji> getEmojis() {
    return getGuild().getEmojis();
  }

  @Nonnull
  public BoostTier getBoostTier() {
    return getGuild().getBoostTier();
  }

  @Nullable
  public VoiceChannel getVoiceChannelById(long id) {
    return getGuild().getVoiceChannelById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname, boolean ignoreCase) {
    return getGuild().getMembersByNickname(nickname, ignoreCase);
  }

  @Nonnull
  public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
    return getGuild().getEmojiCache();
  }

  @Nonnull
  public OffsetDateTime getTimeCreated() {
    return getGuild().getTimeCreated();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> deafen(@NotNull UserSnowflake userSnowflake, boolean b) {
    return getGuild().deafen(userSnowflake, b);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<AutoModRule> retrieveAutoModRuleById(@NotNull String s) {
    return getGuild().retrieveAutoModRuleById(s);
  }

  @Nullable
  public ForumChannel getForumChannelById(long id) {
    return getGuild().getForumChannelById(id);
  }

  @CheckReturnValue
  @Nonnull
  public CacheRestAction<ScheduledEvent> retrieveScheduledEventById(@NotNull String s) {
    return getGuild().retrieveScheduledEventById(s);
  }

  @Nonnull
  @Unmodifiable
  public List<ForumChannel> getForumChannels() {
    return getGuild().getForumChannels();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {
    return getGuild().findMembersWithRoles(roles);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> kick(@NotNull UserSnowflake userSnowflake) {
    return getGuild().kick(userSnowflake);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<EnumSet<Region>> retrieveRegions(boolean b) {
    return getGuild().retrieveRegions(b);
  }

  @Nullable
  public String getIconUrl() {
    return getGuild().getIconUrl();
  }

  @Nullable
  public <T extends GuildChannel> T getChannelById(@NotNull Class<T> type, @NotNull String id) {
    return getGuild().getChannelById(type, id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<GuildSticker> retrieveSticker(@NotNull StickerSnowflake stickerSnowflake) {
    return getGuild().retrieveSticker(stickerSnowflake);
  }

  public boolean isBoostProgressBarEnabled() {
    return getGuild().isBoostProgressBarEnabled();
  }

  public int getMaxMembers() {
    return getGuild().getMaxMembers();
  }

  @Nonnull
  @Unmodifiable
  public List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getStageChannelsByName(name, ignoreCase);
  }

  @Nullable
  public RichCustomEmoji getEmojiById(@NotNull String id) {
    return getGuild().getEmojiById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<ThreadChannel> getThreadChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getThreadChannelsByName(name, ignoreCase);
  }

  public int getMemberCount() {
    return getGuild().getMemberCount();
  }

  @CheckReturnValue
  @Nonnull
  public RoleOrderAction modifyRolePositions(boolean b) {
    return getGuild().modifyRolePositions(b);
  }

  @Nonnull
  public SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
    return getGuild().getTextChannelCache();
  }

  @Nullable
  public MediaChannel getMediaChannelById(long id) {
    return getGuild().getMediaChannelById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<Member> getMembers() {
    return getGuild().getMembers();
  }

  @Nullable
  public GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
    return getGuild().getGuildChannelById(type, id);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<TextChannel> createTextChannel(@NotNull String s,
                                                      @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createTextChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@NotNull String s) {
    return getGuild().retrieveIntegrationPrivilegesById(s);
  }

  @Nullable
  public VoiceChannel getVoiceChannelById(@NotNull String id) {
    return getGuild().getVoiceChannelById(id);
  }

  @Nullable
  public ScheduledEvent getScheduledEventById(@NotNull String id) {
    return getGuild().getScheduledEventById(id);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<ForumChannel> createForumChannel(@NotNull String name) {
    return getGuild().createForumChannel(name);
  }

  /**
   * @param user
   * @param reason
   * @deprecated
   */
  @DeprecatedSince("5.0.0")
  @ReplaceWith("kick(user).reason(reason)")
  @ForRemoval
  @Deprecated
  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> kick(@NotNull UserSnowflake user,
                                        @org.jetbrains.annotations.Nullable String reason) {
    return getGuild().kick(user, reason);
  }

  @Nullable
  public TextChannel getSystemChannel() {
    return getGuild().getSystemChannel();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> deleteCommandById(long commandId) {
    return getGuild().deleteCommandById(commandId);
  }

  @Nullable
  public ScheduledEvent getScheduledEventById(long id) {
    return getGuild().getScheduledEventById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<RichCustomEmoji> retrieveEmoji(@NotNull CustomEmoji emoji) {
    return getGuild().retrieveEmoji(emoji);
  }

  @Nonnull
  public Task<Void> cancelRequestToSpeak() {
    return getGuild().cancelRequestToSpeak();
  }

  @Nonnull
  @Unmodifiable
  public List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getCategoriesByName(name, ignoreCase);
  }

  @Nullable
  public String getBannerId() {
    return getGuild().getBannerId();
  }

  @CheckReturnValue
  @Nonnull
  public GuildWelcomeScreenManager modifyWelcomeScreen() {
    return getGuild().modifyWelcomeScreen();
  }

  @Nonnull
  @Unmodifiable
  public List<ScheduledEvent> getScheduledEvents() {
    return getGuild().getScheduledEvents();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> deleteAutoModRuleById(@NotNull String s) {
    return getGuild().deleteAutoModRuleById(s);
  }

  @Nonnull
  @Unmodifiable
  public List<MediaChannel> getMediaChannels() {
    return getGuild().getMediaChannels();
  }

  @Nullable
  public GuildSticker getStickerById(long id) {
    return getGuild().getStickerById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> kickVoiceMember(@NotNull Member member) {
    return getGuild().kickVoiceMember(member);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelOrderAction modifyCategoryPositions() {
    return getGuild().modifyCategoryPositions();
  }

  @Nonnull
  public Timeout getAfkTimeout() {
    return getGuild().getAfkTimeout();
  }

  @Nonnull
  @Unmodifiable
  public List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
    return getGuild().getMembersWithRoles(roles);
  }

  @Nullable
  public ForumChannel getForumChannelById(@NotNull String id) {
    return getGuild().getForumChannelById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<GuildSticker> getStickers() {
    return getGuild().getStickers();
  }

  public long getMaxFileSize() {
    return getGuild().getMaxFileSize();
  }

  @Nullable
  public MediaChannel getMediaChannelById(@NotNull String id) {
    return getGuild().getMediaChannelById(id);
  }

  @Nullable
  public String getVanityUrl() {
    return getGuild().getVanityUrl();
  }

  @Nullable
  public Member getOwner() {
    return getGuild().getOwner();
  }

  @Nonnull
  public VerificationLevel getVerificationLevel() {
    return getGuild().getVerificationLevel();
  }

  @Nonnull
  public SortedSnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
    return getGuild().getScheduledEventCache();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembers(boolean includePresence,
                                            @NotNull Collection<? extends UserSnowflake> users) {
    return getGuild().retrieveMembers(includePresence, users);
  }

  public boolean isLoaded() {
    return getGuild().isLoaded();
  }

  public int getBoostCount() {
    return getGuild().getBoostCount();
  }

  @CheckReturnValue
  @Nonnull
  public CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {
    return getGuild().upsertCommand(name, description);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<RichCustomEmoji>> retrieveEmojis() {
    return getGuild().retrieveEmojis();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> loadMembers() {
    return getGuild().loadMembers();
  }

  @Nonnull
  public String getId() {
    return getGuild().getId();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<MediaChannel> createMediaChannel(@NotNull String name) {
    return getGuild().createMediaChannel(name);
  }

  @Nonnull
  @Unmodifiable
  public List<RichCustomEmoji> getEmojisByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getEmojisByName(name, ignoreCase);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Webhook>> retrieveWebhooks() {
    return getGuild().retrieveWebhooks();
  }

  @Nullable
  public String getSplashId() {
    return getGuild().getSplashId();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {
    return getGuild().prune(days, roles);
  }

  @CheckReturnValue
  @Nonnull
  public CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
    return getGuild().modifyVoiceChannelPositions(category);
  }

  @Nullable
  public <T extends GuildChannel> T getChannelById(@NotNull Class<T> type, long id) {
    return getGuild().getChannelById(type, id);
  }

  @Nonnull
  public SortedChannelCacheView<GuildChannel> getChannelCache() {
    return getGuild().getChannelCache();
  }

  public boolean isDetached() {
    return getGuild().isDetached();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<RichCustomEmoji> retrieveEmojiById(long id) {
    return getGuild().retrieveEmojiById(id);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
    return getGuild().findMembersWithRoles(roles);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelOrderAction modifyTextChannelPositions() {
    return getGuild().modifyTextChannelPositions();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> removeTimeout(@NotNull UserSnowflake userSnowflake) {
    return getGuild().removeTimeout(userSnowflake);
  }

  @Nullable
  public ImageProxy getIcon() {
    return getGuild().getIcon();
  }

  @Nullable
  public GuildSticker getStickerById(@NotNull String id) {
    return getGuild().getStickerById(id);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> collection) {
    return getGuild().modifyMemberRoles(member, collection);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> timeoutFor(@NotNull UserSnowflake user, long amount, @NotNull TimeUnit unit) {
    return getGuild().timeoutFor(user, amount, unit);
  }

  @Nonnull
  @Unmodifiable
  public List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getTextChannelsByName(name, ignoreCase);
  }

  @Nonnull
  @Unmodifiable
  public List<Member> getMembersWithRoles(@NotNull Role... roles) {
    return getGuild().getMembersWithRoles(roles);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembers(@NotNull Collection<? extends UserSnowflake> users) {
    return getGuild().retrieveMembers(users);
  }

  @CheckReturnValue
  @Nonnull
  public AutoModRuleManager modifyAutoModRuleById(long id) {
    return getGuild().modifyAutoModRuleById(id);
  }

  @CheckReturnValue
  @Nonnull
  public <T extends ICopyableChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {
    return getGuild().createCopyOfChannel(channel);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByPrefix(@NotNull String s, int i) {
    return getGuild().retrieveMembersByPrefix(s, i);
  }

  @Nonnull
  @Unmodifiable
  public List<GuildSticker> getStickersByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getStickersByName(name, ignoreCase);
  }

  @CheckReturnValue
  @Nonnull
  public AuditLogPaginationAction retrieveAuditLogs() {
    return getGuild().retrieveAuditLogs();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> timeoutFor(@NotNull UserSnowflake user, @NotNull Duration duration) {
    return getGuild().timeoutFor(user, duration);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
    return getGuild().createTextChannel(name);
  }

  @Nonnull
  public SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache() {
    return getGuild().getNewsChannelCache();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<Category> createCategory(@NotNull String s) {
    return getGuild().createCategory(s);
  }

  @Incubating
  @Nonnull
  @Unmodifiable
  public List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMembersByName(name, ignoreCase);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<GuildWelcomeScreen> retrieveWelcomeScreen() {
    return getGuild().retrieveWelcomeScreen();
  }

  @Nonnull
  public SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
    return getGuild().getMediaChannelCache();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> removeRoleFromMember(@NotNull UserSnowflake userSnowflake, @NotNull Role role) {
    return getGuild().removeRoleFromMember(userSnowflake, role);
  }

  @Nonnull
  @Unmodifiable
  public List<NewsChannel> getNewsChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getNewsChannelsByName(name, ignoreCase);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<RichCustomEmoji> retrieveEmojiById(@NotNull String s) {
    return getGuild().retrieveEmojiById(s);
  }

  @CheckReturnValue
  @Nonnull
  public ScheduledEventAction createScheduledEvent(@NotNull String s, @NotNull String s1,
                                                   @NotNull OffsetDateTime offsetDateTime,
                                                   @NotNull OffsetDateTime offsetDateTime1) {
    return getGuild().createScheduledEvent(s, s1, offsetDateTime, offsetDateTime1);
  }

  @CheckReturnValue
  @Nonnull
  public CacheRestAction<Member> retrieveOwner() {
    return getGuild().retrieveOwner();
  }

  @Nonnull
  @Unmodifiable
  public List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getVoiceChannelsByName(name, ignoreCase);
  }

  @Nullable
  public String getBannerUrl() {
    return getGuild().getBannerUrl();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<NewsChannel> createNewsChannel(@NotNull String s,
                                                      @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createNewsChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> deleteCommandById(@NotNull String s) {
    return getGuild().deleteCommandById(s);
  }

  @Nonnull
  public Member getSelfMember() {
    return getGuild().getSelfMember();
  }

  @Nonnull
  public Role getPublicRole() {
    return getGuild().getPublicRole();
  }

  @CheckReturnValue
  @Nonnull
  public CacheRestAction<Member> retrieveMemberById(long l) {
    return getGuild().retrieveMemberById(l);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<BulkBanResponse> ban(@NotNull Collection<? extends UserSnowflake> users,
                                                  int deletionTimeframe, @NotNull TimeUnit unit) {
    return getGuild().ban(users, deletionTimeframe, unit);
  }

  @CheckReturnValue
  @Nonnull
  public ChannelOrderAction modifyVoiceChannelPositions() {
    return getGuild().modifyVoiceChannelPositions();
  }

  @Nullable
  public Role getBotRole() {
    return getGuild().getBotRole();
  }

  @Nonnull
  public JDA getJDA() {
    return getGuild().getJDA();
  }

  public int getMaxPresences() {
    return getGuild().getMaxPresences();
  }

  public boolean isMember(@NotNull UserSnowflake userSnowflake) {
    return getGuild().isMember(userSnowflake);
  }

  @Nonnull
  public SortedSnowflakeCacheView<StageChannel> getStageChannelCache() {
    return getGuild().getStageChannelCache();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<GuildSticker> createSticker(@NotNull String name, @NotNull String description,
                                                         @NotNull FileUpload file, @NotNull String tag,
                                                         @NotNull String... tags) {
    return getGuild().createSticker(name, description, file, tag, tags);
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull String... ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  @Nonnull
  public String getName() {
    return getGuild().getName();
  }

  @Nonnull
  public NotificationLevel getDefaultNotificationLevel() {
    return getGuild().getDefaultNotificationLevel();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  public long getOwnerIdLong() {
    return getGuild().getOwnerIdLong();
  }

  @Nonnull
  @Unmodifiable
  public List<ForumChannel> getForumChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getForumChannelsByName(name, ignoreCase);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> leave() {
    return getGuild().leave();
  }

  @CheckReturnValue
  @Nonnull
  public AutoModRuleManager modifyAutoModRuleById(@NotNull String s) {
    return getGuild().modifyAutoModRuleById(s);
  }

  @Nullable
  public String getSplashUrl() {
    return getGuild().getSplashUrl();
  }

  @Nonnull
  @Unmodifiable
  public List<ScheduledEvent> getScheduledEventsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getScheduledEventsByName(name, ignoreCase);
  }

  @Nullable
  public Role getRoleByBot(long userId) {
    return getGuild().getRoleByBot(userId);
  }

  @CheckReturnValue
  @Nonnull
  public CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
    return getGuild().modifyTextChannelPositions(category);
  }

  @CheckReturnValue
  @Nonnull
  public BanPaginationAction retrieveBanList() {
    return getGuild().retrieveBanList();
  }

  @CheckReturnValue
  @Nonnull
  public CacheRestAction<Member> retrieveMemberById(@NotNull String id) {
    return getGuild().retrieveMemberById(id);
  }

  @Nullable
  public TextChannel getRulesChannel() {
    return getGuild().getRulesChannel();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String s,
                                                        @org.jetbrains.annotations.Nullable Category category) {
    return getGuild().createVoiceChannel(s, category);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Command> upsertCommand(@NotNull CommandData commandData) {
    return getGuild().upsertCommand(commandData);
  }

  @Nullable
  public Role getRoleByBot(@NotNull String userId) {
    return getGuild().getRoleByBot(userId);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Command> retrieveCommandById(long id) {
    return getGuild().retrieveCommandById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<GuildChannel> getChannels() {
    return getGuild().getChannels();
  }

  @Nullable
  public Role getRoleByBot(@NotNull User user) {
    return getGuild().getRoleByBot(user);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Integer> retrievePrunableMemberCount(int i) {
    return getGuild().retrievePrunableMemberCount(i);
  }

  @Nonnull
  @Unmodifiable
  public Set<String> getFeatures() {
    return getGuild().getFeatures();
  }

  @CheckReturnValue
  @Nonnull
  public Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
    return getGuild().findMembers(filter);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<GuildVoiceState> retrieveMemberVoiceState(@NotNull UserSnowflake user) {
    return getGuild().retrieveMemberVoiceState(user);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> mute(@NotNull UserSnowflake userSnowflake, boolean b) {
    return getGuild().mute(userSnowflake, b);
  }

  @CheckReturnValue
  @Nonnull
  public CommandEditAction editCommandById(long id) {
    return getGuild().editCommandById(id);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> unban(@NotNull UserSnowflake userSnowflake) {
    return getGuild().unban(userSnowflake);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<VanityInvite> retrieveVanityInvite() {
    return getGuild().retrieveVanityInvite();
  }

  @CheckReturnValue
  @Nonnull
  public CommandListUpdateAction updateCommands() {
    return getGuild().updateCommands();
  }

  @Nullable
  public Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
    return getGuild().getMemberByTag(username, discriminator);
  }

  @Nullable
  public GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
    return getGuild().getGuildChannelById(type, id);
  }

  @CheckReturnValue
  @Nonnull
  public CacheRestAction<Member> retrieveMember(@NotNull UserSnowflake user) {
    return getGuild().retrieveMember(user);
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<GuildSticker> createSticker(@NotNull String s, @NotNull String s1,
                                                         @NotNull FileUpload fileUpload,
                                                         @NotNull Collection<String> collection) {
    return getGuild().createSticker(s, s1, fileUpload, collection);
  }

  @Nonnull
  @Unmodifiable
  public List<Role> getRoles() {
    return getGuild().getRoles();
  }

  @Nonnull
  @Unmodifiable
  public List<Member> getBoosters() {
    return getGuild().getBoosters();
  }

  @Nullable
  public StageChannel getStageChannelById(long id) {
    return getGuild().getStageChannelById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<MediaChannel> getMediaChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMediaChannelsByName(name, ignoreCase);
  }

  public void pruneMemberCache() {
    getGuild().pruneMemberCache();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<RichCustomEmoji> createEmoji(@NotNull String s, @NotNull Icon icon,
                                                          @NotNull Role... roles) {
    return getGuild().createEmoji(s, icon, roles);
  }

  @Nonnull
  public SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
    return getGuild().getVoiceChannelCache();
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Void> delete() {
    return getGuild().delete();
  }

  @Nonnull
  @Unmodifiable
  public List<StageChannel> getStageChannels() {
    return getGuild().getStageChannels();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> transferOwnership(@NotNull Member member) {
    return getGuild().transferOwnership(member);
  }

  @Nullable
  public GuildChannel getGuildChannelById(long id) {
    return getGuild().getGuildChannelById(id);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<Ban> retrieveBan(@NotNull UserSnowflake userSnowflake) {
    return getGuild().retrieveBan(userSnowflake);
  }

  @Nullable
  public ImageProxy getBanner() {
    return getGuild().getBanner();
  }

  @Nonnull
  public AudioManager getAudioManager() {
    return getGuild().getAudioManager();
  }

  @Nonnull
  public NSFWLevel getNSFWLevel() {
    return getGuild().getNSFWLevel();
  }

  @Nonnull
  @Unmodifiable
  public List<ThreadChannel> getThreadChannels() {
    return getGuild().getThreadChannels();
  }

  @Nullable
  public StageChannel getStageChannelById(@NotNull String id) {
    return getGuild().getStageChannelById(id);
  }

  @Nonnull
  @Unmodifiable
  public List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMembersByEffectiveName(name, ignoreCase);
  }

  @Nonnull
  public List<GuildVoiceState> getVoiceStates() {
    return getGuild().getVoiceStates();
  }

  @CheckReturnValue
  @Nonnull
  public AuditableRestAction<Void> deleteSticker(@NotNull StickerSnowflake stickerSnowflake) {
    return getGuild().deleteSticker(stickerSnowflake);
  }

  public long getIdLong() {
    return getGuild().getIdLong();
  }

  @Nullable
  public Role getBoostRole() {
    return getGuild().getBoostRole();
  }

  @Nullable
  public DefaultGuildChannelUnion getDefaultChannel() {
    return getGuild().getDefaultChannel();
  }

  @Nullable
  public String getDescription() {
    return getGuild().getDescription();
  }

  @CheckReturnValue
  @Nonnull
  public ChannelAction<NewsChannel> createNewsChannel(@NotNull String name) {
    return getGuild().createNewsChannel(name);
  }

  @CheckReturnValue
  @Nonnull
  public RestAction<List<Invite>> retrieveInvites() {
    return getGuild().retrieveInvites();
  }

  public int getMaxEmojis() {
    return getGuild().getMaxEmojis();
  }

  @Nonnull
  public SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
    return getGuild().getThreadChannelCache();
  }

  @Nullable
  public ThreadChannel getThreadChannelById(long id) {
    return getGuild().getThreadChannelById(id);
  }

  @Nullable
  public Role getRoleById(long id) {
    return getGuild().getRoleById(id);
  }
}
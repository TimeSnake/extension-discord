/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild.*;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICopyableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.*;
import net.dv8tion.jda.api.entities.templates.Template;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.exceptions.*;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.PrivilegeConfig;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.managers.*;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.OrderAction;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.cache.*;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

  public static Member getMemberByUuid(UUID uuid) {
    return guild.getMemberByUuid(uuid);
  }

  public static VoiceChannel getFallbackChannel() {
    return guild.getFallbackChannel();
  }

  /**
   * Retrieves the list of guild commands.
   * <br>This list does not include global commands! Use {@link JDA#retrieveCommands()} for global commands.
   * <br>This list does not include localization data. Use {@link #retrieveCommands(boolean)} to get localization data
   *
   * @return {@link RestAction} - Type: {@link List} of {@link Command}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<Command>> retrieveCommands() {
    return getGuild().retrieveCommands();
  }

  /**
   * Retrieves the {@link IntegrationPrivilege IntegrationPrivileges} for the commands in this guild.
   * <br>The RestAction provides a {@link PrivilegeConfig} providing the privileges of this application and its
   * commands.
   *
   * <p>Moderators of a guild can modify these privileges through the Integrations Menu
   *
   * @return {@link RestAction} - Type: {@link PrivilegeConfig}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<PrivilegeConfig> retrieveCommandPrivileges() {
    return getGuild().retrieveCommandPrivileges();
  }

  /**
   * Modify a sticker using {@link GuildStickerManager}.
   * <br>You can update multiple fields at once, by calling the respective setters before executing the request.
   *
   * <p>The returned {@link RestAction RestAction} can encounter the following Discord errors:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_STICKER UNKNOWN_STICKER}
   *     <br>Occurs when the provided id does not refer to a sticker known by Discord.</li>
   * </ul>
   *
   * @param sticker
   * @return {@link GuildStickerManager}
   * @throws IllegalArgumentException        If null is provided
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         {@link Permission#MANAGE_GUILD_EXPRESSIONS MANAGE_GUILD_EXPRESSIONS} in
   *                                         the guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static GuildStickerManager editSticker(@NotNull StickerSnowflake sticker) {
    return getGuild().editSticker(sticker);
  }

  /**
   * Retrieves all {@link Template Templates} for this guild.
   * <br>Requires {@link Permission#MANAGE_SERVER MANAGE_SERVER} in this guild.
   * Will throw an {@link InsufficientPermissionException InsufficientPermissionException} otherwise.
   *
   * @return {@link RestAction RestAction} - Type: List{@literal <}{@link Template Template}{@literal >}
   * <br>The list of Template objects
   * @throws InsufficientPermissionException if the account does not have {@link Permission#MANAGE_SERVER
   *                                         MANAGE_SERVER} in this Guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<Template>> retrieveTemplates() {
    return getGuild().retrieveTemplates();
  }

  /**
   * Gets a {@link Category Category} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of
   * {@link net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer} and only checks the caches with
   * the relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The snowflake ID of the wanted Category
   * @return Possibly-null {@link Category Category} for the provided ID.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static Category getCategoryById(long id) {
    return getGuild().getCategoryById(id);
  }

  /**
   * Retrieves all members of this guild.
   * <br>This will use the configured {@link MemberCachePolicy MemberCachePolicy}
   * to decide which members to retain in cache.
   *
   * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param callback Consumer callback for each member
   * @return {@link Task} cancellable handle for this request
   * @throws IllegalArgumentException If the callback is null
   * @throws IllegalStateException    If the {@link GatewayIntent#GUILD_MEMBERS
   *                                  GatewayIntent.GUILD_MEMBERS} is not enabled
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static Task<Void> loadMembers(@NotNull Consumer<Member> callback) {
    return getGuild().loadMembers(callback);
  }

  /**
   * Bans the user specified by the provided {@link UserSnowflake} and deletes messages sent by the user based on the
   * {@code deletionTimeframe}.
   * <br>If you wish to ban a user without deleting any messages, provide {@code deletionTimeframe} with a value of 0.
   * To set a ban reason, use {@link AuditableRestAction#reason(String)}.
   *
   * <p>You can unban a user with {@link Guild#unban(UserSnowflake) Guild.unban(UserReference)}.
   *
   * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User's}
   * {@link Member Member} object (if the User was in the Guild)
   * until Discord sends the {@link GuildMemberRemoveEvent GuildMemberRemoveEvent}.
   *
   * <p><b>Examples</b><br>
   * Banning a user without deleting any messages:
   * <pre>{@code
   * guild.ban(user, 0, TimeUnit.SECONDS)
   *      .reason("Banned for rude behavior")
   *      .queue();
   * }</pre>
   * Banning a user and deleting messages from the past hour:
   * <pre>{@code
   * guild.ban(user, 1, TimeUnit.HOURS)
   *      .reason("Banned for spamming")
   *      .queue();
   * }</pre>
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be banned due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_USER UNKNOWN_USER}
   *     <br>The user does not exist</li>
   * </ul>
   *
   * @param user              The {@link UserSnowflake} for the user to ban.
   *                          This can be a member or user instance or {@link User#fromId(long)}.
   * @param deletionTimeframe The timeframe for the history of messages that will be deleted. (seconds precision)
   * @param unit              Timeframe unit as a {@link TimeUnit} (for example {@code ban(user, 7, TimeUnit.DAYS)}).
   * @return {@link AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   *                                         {@link Permission#BAN_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot ban the other user due to permission
   *                                         hierarchy position.
   *                                         <br>See {@link Member#canInteract(Member)}
   * @throws IllegalArgumentException        <ul>
   *                                                                                                                                                                                                            <li>If the provided deletionTimeframe is negative.</li>
   *                                                                                                                                                                                                            <li>If the provided deletionTimeframe is longer than 7 days.</li>
   *                                                                                                                                                                                                            <li>If the provided user or time unit is {@code null}</li>
   *                                                                                                                                                                                                        </ul>
   *                                                                                 s DetachedEntityException
     *                                                                                 If this entity is
   *                                                                                 {@link #isDetached() detached}
   * @see AuditableRestAction#reason(String)
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> ban(@NotNull UserSnowflake user, int deletionTimeframe,
                                              @NotNull TimeUnit unit) {
    return getGuild().ban(user, deletionTimeframe, unit);
  }

  /**
   * Gets a {@link Member Member} object via the id of the user. The id relates to
   * {@link User#getIdLong()}, and this method is similar to {@link JDA#getUserById(long)}
   * <br>This is more efficient that using {@link JDA#getUserById(long)} and {@link #getMember(UserSnowflake)}.
   * <br>If no Member in this Guild has the {@code userId} provided, this returns {@code null}.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @param userId The Discord id of the User for which a Member object is requested.
   * @return Possibly-null {@link Member Member} with the related {@code userId}.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveMemberById(long)
   */
  @Nullable
  public static Member getMemberById(long userId) {
    return getGuild().getMemberById(userId);
  }

  /**
   * Retrieves a list of members by their user id.
   * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
   *
   * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
   * this will load the {@link OnlineStatus OnlineStatus} and {@link Activity Activities}
   * of the members. You can use {@link #retrieveMembersByIds(boolean, String...)} to disable presences.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param ids The ids of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            IDs</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByIds(@NotNull String... ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  /**
   * Gets all {@link Category Categories} in the cache.
   * <br>In {@link Guild} cache, channels are sorted according to their position and id.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getCategoryCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable list of all {@link Category Categories} in this Guild.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<Category> getCategories() {
    return getGuild().getCategories();
  }

  /**
   * This method will prune (kick) all members who were offline for at least <i>days</i> days.
   * <br>The RestAction returned from this method will return the amount of Members that were pruned.
   * <br>You can use {@link Guild#retrievePrunableMemberCount(int)} to determine how many Members would be pruned if
   * you were to
   * call this method.
   *
   * <p>This might timeout when pruning many members with {@code wait=true}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The prune cannot finished due to a permission discrepancy</li>
   * </ul>
   *
   * @param days  Minimum number of days since a member has been offline to get affected.
   * @param wait  Whether to calculate the number of pruned members and wait for the response (timeout for too many
   *              pruned)
   * @param roles Optional roles to include in prune filter
   * @return {@link AuditableRestAction AuditableRestAction} - Type: Integer
   * <br>Provides the amount of Members that were pruned from the Guild, if wait is true.
   * @throws InsufficientPermissionException If the account doesn't have {@link Permission#KICK_MEMBERS KICK_MEMBER}
   * Permission.
   * @throws IllegalArgumentException                                       <ul>
   *                                                                                    <li>If the provided days are
   *                                                                                    not in the range from 1 to 30
   *                                                                                    (inclusive)</li>
   *                                                                                    <li>If null is provided</li>
   *                                                                                    <li>If any of the provided
   *                                                                                    roles is not from this
   *                                                                                    guild</li>
   *                                                                                </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Integer> prune(int days, boolean wait, @NotNull Role... roles) {
    return getGuild().prune(days, wait, roles);
  }

  /**
   * Creates a new {@link VoiceChannel VoiceChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the VoiceChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new VoiceChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name) {
    return getGuild().createVoiceChannel(name);
  }

  /**
   * Get {@link GuildChannel GuildChannel} for the provided ID.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * <p>To get more specific channel types you can use one of the following:
   * <ul>
   *     <li>{@link #getChannelById(Class, String)}</li>
   *     <li>{@link #getTextChannelById(String)}</li>
   *     <li>{@link #getNewsChannelById(String)}</li>
   *     <li>{@link #getStageChannelById(String)}</li>
   *     <li>{@link #getVoiceChannelById(String)}</li>
   *     <li>{@link #getCategoryById(String)}</li>
   * </ul>
   *
   * @param id The ID of the channel
   * @return The GuildChannel or null
   * @throws IllegalArgumentException If the provided ID is null
   * @throws NumberFormatException    If the provided ID is not a snowflake
   * @throws DetachedEntityException  If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static GuildChannel getGuildChannelById(@NotNull String id) {
    return getGuild().getGuildChannelById(id);
  }

  /**
   * Returns an {@link ImageProxy} for this guild's splash icon.
   *
   * @return Possibly-null {@link ImageProxy} of this guild's splash icon
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getSplashUrl()
   */
  @Nullable
  public static ImageProxy getSplash() {
    return getGuild().getSplash();
  }

  /**
   * Provides the {@link TextChannel TextChannel} that receives community updates.
   * <br>If this guild doesn't have the COMMUNITY {@link #getFeatures() feature}, this returns {@code null}.
   *
   * @return Possibly-null {@link TextChannel TextChannel} that is the community updates channel
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getFeatures()
   */
  @Nullable
  public static TextChannel getCommunityUpdatesChannel() {
    return getGuild().getCommunityUpdatesChannel();
  }

  /**
   * Puts the specified Member in time out in this {@link Guild Guild} until the specified date.
   * <br>While a Member is in time out, all permissions except {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} and
   * {@link Permission#MESSAGE_HISTORY MESSAGE_HISTORY} are removed from them.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be put into time out due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param user     The {@link UserSnowflake} to timeout.
   *                 This can be a member or user instance or {@link User#fromId(long)}.
   * @param temporal The time the specified Member will be released from time out
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MODERATE_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot put a timeout on the other Member due to
   * permission hierarchy position. (See {@link Member#canInteract(Member)})
   * @throws IllegalArgumentException                                       If any of the following are true
   *                                                                        <ul>
   *                                                                            <li>The provided {@code user} is
   *                                                                            null</li>
   *                                                                            <li>The provided {@code temporal} is
   *                                                                            null</li>
   *                                                                            <li>The provided {@code temporal} is
   *                                                                            in the past</li>
   *                                                                            <li>The provided {@code temporal} is
   *                                                                            more than
   *                                                                            {@value Member#MAX_TIME_OUT_LENGTH}
   *                                                                            days in the future</li>
   *                                                                        </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> timeoutUntil(@NotNull UserSnowflake user,
                                                       @NotNull TemporalAccessor temporal) {
    return getGuild().timeoutUntil(user, temporal);
  }

  /**
   * Creates a new {@link AutoModRule} for this guild.
   *
   * <p>You can only create a certain number of rules for each {@link AutoModTriggerType AutoModTriggerType}.
   * The maximum is provided by {@link AutoModTriggerType#getMaxPerGuild()}.
   *
   * @param data The data for the new rule
   * @return {@link AuditableRestAction} - Type: {@link AutoModRule}
   * @throws InsufficientPermissionException                        If the currently logged in account does not have
   * the {@link AutoModRuleData#getRequiredPermissions() required permissions}
   * @throws IllegalStateException                                  <ul>
   *                                                                            <li>If the provided data does not
   *                                                                            have any {@link AutoModResponse}
   *                                                                            configured</li>
   *                                                                            <li>If any of the configured
   *                                                                            {@link AutoModResponse
   *                                                                            AutoModResponses} is not supported
   *                                                                            by the {@link AutoModTriggerType}</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<AutoModRule> createAutoModRule(@NotNull AutoModRuleData data) {
    return getGuild().createAutoModRule(data);
  }

  /**
   * Returns the level of multifactor authentication required to execute administrator restricted functions in this
   * guild.
   * <br>For a short description of the different values, see {@link MFALevel MFALevel}.
   * <p>
   * This value can be modified using {@link GuildManager#setRequiredMFALevel(MFALevel)}.
   *
   * @return The MFA-Level required by this Guild.
   */
  @Nonnull
  public static MFALevel getRequiredMFALevel() {
    return getGuild().getRequiredMFALevel();
  }

  /**
   * Used to completely delete a guild. This can only be done if the currently logged in account is the owner of the
   * Guild.
   * <br>This method is specifically used for when MFA is enabled on the logged in account
   * {@link SelfUser#isMfaEnabled()}.
   * If MFA is not enabled, use {@link #delete()}.
   *
   * @param mfaCode The Multifactor Authentication code generated by an app like
   *                <a href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2" target="_blank">Google Authenticator</a>.
   *                <br><b>This is not the MFA token given to you by Discord.</b> The code is typically 6 characters
   *                long.
   * @return {@link RestAction} - Type: {@link Void}
   * @throws PermissionException     Thrown if the currently logged in account is not the owner of this Guild.
   * @throws IllegalArgumentException                               If the provided {@code mfaCode} is {@code null}
   * or empty when {@link SelfUser#isMfaEnabled()} is true.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> delete(@org.jetbrains.annotations.Nullable String mfaCode) {
    return getGuild().delete(mfaCode);
  }

  /**
   * Whether the invites for this guild are paused/disabled.
   * <br>This is equivalent to {@code getFeatures().contains("INVITES_DISABLED")}.
   *
   * @return True, if invites are paused/disabled
   */
  public static boolean isInvitesDisabled() {
    return getGuild().isInvitesDisabled();
  }

  /**
   * Used to move a {@link Member Member} from one {@link AudioChannel AudioChannel}
   * to another {@link AudioChannel AudioChannel}.
   * <br>As a note, you cannot move a Member that isn't already in a AudioChannel. Also they must be in a AudioChannel
   * in the same Guild as the one that you are moving them to.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be moved due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} permission was removed</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
   *     <br>The specified channel was deleted before finishing the task</li>
   * </ul>
   *
   * @param member       The {@link Member Member} that you are moving.
   * @param audioChannel The destination {@link AudioChannel AudioChannel} to which the member is being
   *                     moved to. Or null to perform a voice kick.
   * @return {@link RestAction RestAction}
   * @throws IllegalStateException                                          If the Member isn't currently in a
   * AudioChannel in this Guild, or {@link CacheFlag#VOICE_STATE} is disabled.
   * @throws IllegalArgumentException                                       <ul>
   *                                                                                    <li>If the provided member is
   *                                                                                    {@code null}</li>
   *                                                                                    <li>If the provided Member
   *                                                                                    isn't part of this
   *                                                                                    {@link Guild Guild}</li>
   *                                                                                    <li>If the provided
   *                                                                                    AudioChannel isn't part of
   *                                                                                    this {@link Guild Guild}</li>
   *                                                                                </ul>
   * @throws InsufficientPermissionException <ul>
   *                                                                                    <li>If this account doesn't
   *                                                                                    have
   *                                                                                    {k Permission#VOICE_MOVE_OTHERS}
   *                                                                                        in the AudioChannel that
   *                                                                                        the Member is currently
   *                                                                                        in.</li>
   *                                                                                    <li>If this account
   *                                                                                    <b>AND</b> the Member being
   *                                                                                    moved don't have
  {@link Permission#VOICE_CONNECT} for the destination AudioChannel.</li>
   *                                                                                </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> moveVoiceMember(@NotNull Member member,
                                                 @org.jetbrains.annotations.Nullable AudioChannel audioChannel) {
    return getGuild().moveVoiceMember(member, audioChannel);
  }

  /**
   * Adds the user to this guild as a member.
   * <br>This requires an <b>OAuth2 Access Token</b> with the scope {@code guilds.join}.
   *
   * @param accessToken The access token
   * @param user        The {@link UserSnowflake} for the member to add.
   *                    This can be a member or user instance or {@link User#fromId(long)}.
   * @return {@link MemberAction MemberAction}
   * @throws IllegalArgumentException                                       If the access token is blank, empty, or
   * null,
   *                                                                        or if the provided user reference is null
   *                                                                        or is already in this guild
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#CREATE_INSTANT_INVITE Permission.CREATE_INSTANT_INVITE}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @see <a href="https://discord.com/developers/docs/topics/oauth2" target="_blank">Discord OAuth2 Documentation</a>
   * @since 3.7.0
   */
  @CheckReturnValue
  @Nonnull
  public static MemberAction addMember(@NotNull String accessToken, @NotNull UserSnowflake user) {
    return getGuild().addMember(accessToken, user);
  }

  /**
   * Creates a new {@link Role Role} in this Guild.
   * <br>It will be placed at the bottom (just over the Public Role) to avoid permission hierarchy conflicts.
   * <br>For this to be successful, the logged in account has to have the {@link Permission#MANAGE_ROLES MANAGE_ROLES
   * } Permission
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The role could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_ROLES_PER_GUILD MAX_ROLES_PER_GUILD}
   *     <br>There are too many roles in this Guild</li>
   * </ul>
   *
   * @return {@link RoleAction RoleAction}
   * <br>Creates a new role with previously selected field values
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_ROLES} Permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RoleAction createRole() {
    return getGuild().createRole();
  }

  /**
   * Retrieves the existing {@link Command} instance by id.
   *
   * <p>If there is no command with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param id The command id
   * @return {@link RestAction} - Type: {@link Command}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @throws IllegalArgumentException                               If the provided id is not a valid snowflake
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Command> retrieveCommandById(@NotNull String id) {
    return getGuild().retrieveCommandById(id);
  }

  /**
   * Populated list of {@link GuildChannel channels} for this guild.
   * <br>This includes all types of channels, except for threads.
   *
   * <p>The returned list is ordered in the same fashion as it would be by the official discord client.
   * <ol>
   *     <li>TextChannel, ForumChannel, and NewsChannel without parent</li>
   *     <li>VoiceChannel and StageChannel without parent</li>
   *     <li>Categories
   *         <ol>
   *             <li>TextChannel, ForumChannel, and NewsChannel with category as parent</li>
   *             <li>VoiceChannel and StageChannel with category as parent</li>
   *         </ol>
   *     </li>
   * </ol>
   *
   * @param includeHidden Whether to include channels with denied {@link Permission#VIEW_CHANNEL View Channel
   * Permission}
   * @return Immutable list of channels for this guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getChannels()
   */
  @Nonnull
  @Unmodifiable
  public static List<GuildChannel> getChannels(boolean includeHidden) {
    return getGuild().getChannels(includeHidden);
  }

  /**
   * Gets a {@link TextChannel TextChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link TextChannel TextChannel}.
   * @return Possibly-null {@link TextChannel TextChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static TextChannel getTextChannelById(long id) {
    return getGuild().getTextChannelById(id);
  }

  /**
   * Loads {@link MetaData} for this guild instance.
   *
   * @return {@link RestAction} - Type: {@link MetaData}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.2.0
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<MetaData> retrieveMetaData() {
    return getGuild().retrieveMetaData();
  }

  /**
   * Creates a new {@link StageChannel StageChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name   The name of the StageChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @param parent The optional parent category for this channel, or null
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new StageChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters;
   *                                                                        or the provided parent is not in the same
   *                                                                        guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<StageChannel> createStageChannel(@NotNull String name,
                                                               @org.jetbrains.annotations.Nullable Category parent) {
    return getGuild().createStageChannel(name, parent);
  }

  /**
   * Gets a {@link ThreadChannel ThreadChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>These threads can also represent posts in {@link ForumChannel ForumChannels}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link ThreadChannel ThreadChannel}.
   * @return Possibly-null {@link ThreadChannel ThreadChannel} with matching id.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by
   *                                 {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static ThreadChannel getThreadChannelById(@NotNull String id) {
    return getGuild().getThreadChannelById(id);
  }

  /**
   * Gets a {@link Role Role} from this guild that has the same id as the
   * one provided.
   * <br>If there is no {@link Role Role} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * @param id The id of the {@link Role Role}.
   * @return Possibly-null {@link Role Role} with matching id.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by
   *                                 {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getRoleById(@NotNull String id) {
    return getGuild().getRoleById(id);
  }

  /**
   * The Discord hash-id of the {@link Guild Guild} icon image.
   * If no icon has been set, this returns {@code null}.
   * <p>
   * The Guild icon can be modified using {@link GuildManager#setIcon(Icon)}.
   *
   * @return Possibly-null String containing the Guild's icon hash-id.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static String getIconId() {
    return getGuild().getIconId();
  }

  /**
   * Searches for a {@link Member} that has the matching Discord Tag.
   * <br>Format has to be in the form {@code Username#Discriminator} where the
   * username must be between 2 and 32 characters (inclusive) matching the exact casing and the discriminator
   * must be exactly 4 digits.
   * <br>This does not check the {@link Member#getNickname() nickname} of the member
   * but the username.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * <p>This only checks users that are in this guild. If a user exists
   * with the tag that is not available in the {@link #getMemberCache() Member-Cache} it will not be detected.
   * <br>Currently Discord does not offer a way to retrieve a user by their discord tag.
   *
   * @param tag The Discord Tag in the format {@code Username#Discriminator}
   * @return The {@link Member} for the discord tag or null if no member has the provided tag
   * @throws IllegalArgumentException If the provided tag is null or not in the
   *                                  described format
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see JDA#getUserByTag(String)
   */
  @Nullable
  public static Member getMemberByTag(@NotNull String tag) {
    return getGuild().getMemberByTag(tag);
  }

  /**
   * Load the member's voice state for the specified user.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_VOICE_STATE}
   *     <br>The specified user does not exist, is not a member of this guild or is not connected to a voice
   *     channel</li>
   * </ul>
   *
   * @param id The user id to load the voice state from
   * @return {@link RestAction} - Type: {@link GuildVoiceState}
   * @throws IllegalArgumentException                               If the provided id is empty or null
   * @throws NumberFormatException                                  If the provided id is not a snowflake
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<GuildVoiceState> retrieveMemberVoiceStateById(@NotNull String id) {
    return getGuild().retrieveMemberVoiceStateById(id);
  }

  /**
   * Edit an existing command by id.
   *
   * <p>If there is no command with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param id The id of the command to edit
   * @return {@link CommandEditAction} used to edit the command
   * @throws IllegalArgumentException                               If the provided id is not a valid snowflake
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static CommandEditAction editCommandById(@NotNull String id) {
    return getGuild().editCommandById(id);
  }

  /**
   * The ID for the current owner of this guild.
   * <br>This is useful for debugging purposes or as a shortcut.
   *
   * @return The ID for the current owner
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getOwner()
   */
  @Nonnull
  public static String getOwnerId() {
    return getGuild().getOwnerId();
  }

  @Nonnull
  public static SortedSnowflakeCacheView<Category> getCategoryCache() {
    return getGuild().getCategoryCache();
  }

  /**
   * Retrieves the {@link IntegrationPrivilege IntegrationPrivileges} for the target with the specified ID.
   * <br><b>The ID can either be of a Command or Application!</b>
   *
   * <p>Moderators of a guild can modify these privileges through the Integrations Menu
   *
   * <p>If there is no command or application with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param targetId The id of the command (global or guild), or application
   * @return {@link RestAction} - Type: {@link List} of {@link IntegrationPrivilege}
   * @throws IllegalArgumentException If the id is not a valid snowflake
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(long targetId) {
    return getGuild().retrieveIntegrationPrivilegesById(targetId);
  }

  /**
   * The maximum bitrate that can be applied to a voice channel in this guild.
   * <br>This depends on the features of this guild that can be unlocked for partners or through boosting.
   *
   * @return The maximum bitrate
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.0.0
   */
  public static int getMaxBitrate() {
    return getGuild().getMaxBitrate();
  }

  /**
   * Gets a list of all {@link Role Roles} in this Guild that have the same
   * name as the one provided.
   * <br>If there are no {@link Role Roles} with the provided name, then this returns an empty list.
   *
   * @param name       The name used to filter the returned {@link Role Roles}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all Role names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<Role> getRolesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getRolesByName(name, ignoreCase);
  }

  /**
   * Changes the Member's nickname in this guild.
   * The nickname is visible to all members of this guild.
   *
   * <p>To change the nickname for the currently logged in account
   * only the Permission {@link Permission#NICKNAME_CHANGE NICKNAME_CHANGE} is required.
   * <br>To change the nickname of <b>any</b> {@link Member Member} for this {@link Guild Guild}
   * the Permission {@link Permission#NICKNAME_MANAGE NICKNAME_MANAGE} is required.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The nickname of the target Member is not modifiable due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param member   The {@link Member Member} for which the nickname should be changed.
   * @param nickname The new nickname of the {@link Member Member}, provide {@code null} or an
   *                 empty String to reset the nickname
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws IllegalArgumentException        If the specified {@link Member Member}
   *                                         is not from the same {@link Guild Guild}.
   *                                         Or if the provided member is {@code null}
   * @throws InsufficientPermissionException <ul>
   *                                                                                                                            <li>If attempting to set nickname for self and the logged in account has neither {@link Permission#NICKNAME_CHANGE}
   *                                                                                                                                or {@link Permission#NICKNAME_MANAGE}</li>
   *                                                                                                                            <li>If attempting to set nickname for another member and the logged in account does not have {@link Permission#NICKNAME_MANAGE}</li>
   *                                                                                                                        </ul>
   * @throws HierarchyException                       tempting to set nickname for another member and the logged in
   * account cannot manipulate the other user due to permission hierarchy position.
   *                                         <br>See {@link Member#canInteract(Member)}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> modifyNickname(@NotNull Member member,
                                                         @org.jetbrains.annotations.Nullable String nickname) {
    return getGuild().modifyNickname(member, nickname);
  }

  /**
   * Creates a new {@link StageChannel StageChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the StageChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new StageChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException        If the provided name is {@code null}, blank, or longer than
   * {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<StageChannel> createStageChannel(@NotNull String name) {
    return getGuild().createStageChannel(name);
  }

  /**
   * Used to create a new {@link Template Template} for this Guild.
   * <br>Requires {@link Permission#MANAGE_SERVER MANAGE_SERVER} in this Guild.
   * Will throw an {@link InsufficientPermissionException InsufficientPermissionException} otherwise.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#ALREADY_HAS_TEMPLATE Guild already has a template}
   *     <br>The guild already has a template.</li>
   * </ul>
   *
   * @param name        The name of the template
   * @param description The description of the template
   * @return {@link RestAction RestAction} - Type: {@link Template Template}
   * <br>The created Template object
   * @throws InsufficientPermissionException if the account does not have {@link Permission#MANAGE_SERVER
   * MANAGE_SERVER} in this Guild
   * @throws IllegalArgumentException                                       If the provided name is {@code null} or
   * not between 1-100 characters long, or
   *                                                                        if the provided description is not
   *                                                                        between 0-120 characters long
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Template> createTemplate(@NotNull String name,
                                                    @org.jetbrains.annotations.Nullable String description) {
    return getGuild().createTemplate(name, description);
  }

  /**
   * Gets a {@link Member Member} object via the id of the user. The id relates to
   * {@link User#getId()}, and this method is similar to {@link JDA#getUserById(String)}
   * <br>This is more efficient that using {@link JDA#getUserById(String)} and {@link #getMember(UserSnowflake)}.
   * <br>If no Member in this Guild has the {@code userId} provided, this returns {@code null}.
   *
   * <p>This will only check cached members!
   *
   * @param userId The Discord id of the User for which a Member object is requested.
   * @return Possibly-null {@link Member Member} with the related {@code userId}.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveMemberById(String)
   */
  @Nullable
  public static Member getMemberById(@NotNull String userId) {
    return getGuild().getMemberById(userId);
  }

  /**
   * Gets all {@link TextChannel TextChannels} in the cache.
   * <br>In {@link Guild} cache, channels are sorted according to their position and id.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getTextChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of all {@link TextChannel TextChannels} in this Guild.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<TextChannel> getTextChannels() {
    return getGuild().getTextChannels();
  }

  /**
   * Retrieves the {@link AutoModRule} for the provided id.
   *
   * @param id The id of the rule
   * @return {@link RestAction} - Type: {@link AutoModRule}
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<AutoModRule> retrieveAutoModRuleById(long id) {
    return getGuild().retrieveAutoModRuleById(id);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<ForumChannel> getForumChannelCache() {
    return getGuild().getForumChannelCache();
  }

  /**
   * Bans up to 200 of the provided users.
   * <br>To set a ban reason, use {@link AuditableRestAction#reason(String)}.
   *
   * <p>The {@link BulkBanResponse} includes a list of {@link BulkBanResponse#getFailedUsers() failed users},
   * which is populated with users that could not be banned, for instance due to some internal server error or
   * permission issues.
   * This list of failed users also includes all users that were already banned.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be banned due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#FAILED_TO_BAN_USERS FAILED_TO_BAN_USERS}
   *     <br>None of the users could be banned</li>
   * </ul>
   *
   * @param users        The users to ban
   * @param deletionTime Delete recent messages of the given timeframe (for instance the last hour with {@code
   *                     Duration.ofHours(1)})
   * @return {@link AuditableRestAction} - Type: {@link BulkBanResponse}
   * @throws HierarchyException              If any of the provided users is the guild owner or has a higher or equal
   * role
   *                                         position
   * @throws InsufficientPermissionException If the bot does not have
   *                                         {@link Permission#BAN_MEMBERS} or {@link Permission#MANAGE_SERVER}
   * @throws IllegalArgumentException        <ul>
   *                                                                                                                    <li>If the users collection is null
   *                                                                                                                    or contains null</li>
   *                                                                                                                    <li>If the deletionTime is negative</li>
   *                                                                                                                </ul>
     @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<BulkBanResponse> ban(@NotNull Collection<? extends UserSnowflake> users,
                                                         @org.jetbrains.annotations.Nullable Duration deletionTime) {
    return getGuild().ban(users, deletionTime);
  }

  /**
   * Gets a {@link Category Category} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The snowflake ID of the wanted Category
   * @return Possibly-null {@link Category Category} for the provided ID.
   * @throws IllegalArgumentException                               If the provided ID is not a valid {@code long}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static Category getCategoryById(@NotNull String id) {
    return getGuild().getCategoryById(id);
  }

  /**
   * Retrieves the active threads in this guild.
   *
   * @return {@link RestAction} - List of {@link ThreadChannel}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<ThreadChannel>> retrieveActiveThreads() {
    return getGuild().retrieveActiveThreads();
  }

  /**
   * Gets the Guild specific {@link Member Member} object for the provided
   * {@link UserSnowflake}.
   * <br>If the user is not in this guild or currently uncached, {@code null} is returned.
   *
   * <p>This will only check cached members!
   *
   * @param user The {@link UserSnowflake} for the member to get.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @return Possibly-null {@link Member Member} for the related {@link User User}.
   * @throws IllegalArgumentException                               If the provided user is null
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveMember(UserSnowflake)
   */
  @Nullable
  public static Member getMember(@NotNull UserSnowflake user) {
    return getGuild().getMember(user);
  }

  /**
   * Load the member's voice state for the specified user.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_VOICE_STATE}
   *     <br>The specified user does not exist, is not a member of this guild or is not connected to a voice
   *     channel</li>
   * </ul>
   *
   * @param id The user id to load the voice state from
   * @return {@link RestAction} - Type: {@link GuildVoiceState}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<GuildVoiceState> retrieveMemberVoiceStateById(long id) {
    return getGuild().retrieveMemberVoiceStateById(id);
  }

  /**
   * Gets a {@link TextChannel TextChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link TextChannel TextChannel}.
   * @return Possibly-null {@link TextChannel TextChannel} with matching id.
   * @throws NumberFormatException                                  If the provided {@code id} cannot be parsed by
   * {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static TextChannel getTextChannelById(@NotNull String id) {
    return getGuild().getTextChannelById(id);
  }

  /**
   * Retrieves all current {@link AutoModRule AutoModRules} for this guild.
   *
   * @return {@link RestAction} - Type: {@link List} of {@link AutoModRule}
   * @throws InsufficientPermissionException If the currently logged in account does not have the
   *                                         {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<AutoModRule>> retrieveAutoModRules() {
    return getGuild().retrieveAutoModRules();
  }

  /**
   * Deletes the {@link AutoModRule} for the provided id.
   *
   * @param id The id of the rule
   * @return {@link AuditableRestAction} - Type: {@link Void}
   * @throws InsufficientPermissionException                        If the currently logged in account does not have
   * the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> deleteAutoModRuleById(long id) {
    return getGuild().deleteAutoModRuleById(id);
  }

  /**
   * Provides the {@link VoiceChannel VoiceChannel} that has been set as the channel
   * which {@link Member Members} will be moved to after they have been inactive in a
   * {@link VoiceChannel VoiceChannel} for longer than {@link #getAfkTimeout()}.
   * <br>If no channel has been set as the AFK channel, this returns {@code null}.
   * <p>
   * This value can be modified using {@link GuildManager#setAfkChannel(VoiceChannel)}.
   *
   * @return Possibly-null {@link VoiceChannel VoiceChannel} that is the AFK Channel.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static VoiceChannel getAfkChannel() {
    return getGuild().getAfkChannel();
  }

  /**
   * Creates a new {@link ForumChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name   The name of the ForumChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @param parent The optional parent category for this channel, or null
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new ForumChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters;
   *                                                                        or the provided parent is not in the same
   *                                                                        guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<ForumChannel> createForumChannel(@NotNull String name,
                                                               @org.jetbrains.annotations.Nullable Category parent) {
    return getGuild().createForumChannel(name, parent);
  }

  /**
   * Modifies the {@link Role Roles} of the specified {@link Member Member}
   * by adding and removing a collection of roles.
   * <br>None of the provided roles may be the <u>Public Role</u> of the current Guild.
   * <br>If a role is both in {@code rolesToAdd} and {@code rolesToRemove} it will be removed.
   *
   * <p><b>Example</b><br>
   * <pre>{@code
   * public static void promote(Member member) {
   *     Guild guild = member.getGuild();
   *     List<Role> pleb = guild.getRolesByName("Pleb", true); // remove all roles named "pleb"
   *     List<Role> knight = guild.getRolesByName("Knight", true); // add all roles named "knight"
   *     // update roles in single request
   *     guild.modifyMemberRoles(member, knight, pleb).queue();
   * }
   * }</pre>
   *
   * <p><b>Warning</b><br>
   * <b>This may <u>not</u> be used together with any other role add/remove/modify methods for the same Member
   * within one event listener cycle! The changes made by this require cache updates which are triggered by
   * lifecycle events which are received later. This may only be called again once the specific Member has been updated
   * by a {@link GenericGuildMemberEvent GenericGuildMemberEvent} targeting the same Member.</b>
   *
   * <p>This is logically equivalent to:
   * <pre>{@code
   * Set<Role> roles = new HashSet<>(member.getRoles());
   * roles.addAll(rolesToAdd);
   * roles.removeAll(rolesToRemove);
   * RestAction<Void> action = guild.modifyMemberRoles(member, roles);
   * }</pre>
   *
   * <p>You can use {@link #addRoleToMember(UserSnowflake, Role)} and
   * {@link #removeRoleFromMember(UserSnowflake, Role)} to make updates
   * independent of the cache.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The target Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param member        The {@link Member Member} that should be modified
   * @param rolesToAdd    A {@link Collection Collection} of {@link Role Roles}
   *                      to add to the current Roles the specified {@link Member Member} already has, or null
   * @param rolesToRemove A {@link Collection Collection} of {@link Role Roles}
   *                      to remove from the current Roles the specified {@link Member Member} already has, or null
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
   * @throws HierarchyException              If the provided roles are higher in the Guild's hierarchy
   *                                                                        and thus cannot be modified by the
   *                                                                        currently logged in account
   * @throws IllegalArgumentException                                       <ul>
   *                                                                                    <li>If the target member is
   *                                                                                    {@code null}</li>
   *                                                                                    <li>If any of the specified
   *                                                                                    Roles is managed or is the
   *                                                                                    {@code Public Role} of the
   *                                                                                    Guild</li>
   *                                                                                </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member,
                                                            @org.jetbrains.annotations.Nullable Collection<Role> rolesToAdd,
                                                            @org.jetbrains.annotations.Nullable Collection<Role> rolesToRemove) {
    return getGuild().modifyMemberRoles(member, rolesToAdd, rolesToRemove);
  }

  /**
   * Creates a new {@link Role Role} in this {@link Guild Guild}
   * with the same settings as the given {@link Role Role}.
   * <br>The position of the specified Role does not matter in this case!
   *
   * <p>It will be placed at the bottom (just over the Public Role) to avoid permission hierarchy conflicts.
   * <br>For this to be successful, the logged in account has to have the {@link Permission#MANAGE_ROLES MANAGE_ROLES
   * } Permission
   * and all {@link Permission Permissions} the given {@link Role Role} has.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The role could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_ROLES_PER_GUILD MAX_ROLES_PER_GUILD}
   *     <br>There are too many roles in this Guild</li>
   * </ul>
   *
   * @param role The {@link Role Role} that should be copied
   * @return {@link RoleAction RoleAction}
   * <br>RoleAction with already copied values from the specified {@link Role Role}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_ROLES} Permission and every Permission the provided Role has
   * @throws IllegalArgumentException                                       If the specified role is {@code null}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RoleAction createCopyOfRole(@NotNull Role role) {
    return getGuild().createCopyOfRole(role);
  }

  /**
   * Once the currently logged in account is connected to a {@link StageChannel},
   * this will trigger a {@link GuildVoiceState#getRequestToSpeakTimestamp() Request-to-Speak} (aka raise your hand).
   *
   * <p>This will set an internal flag to automatically request to speak once the bot joins a stage channel.
   * <br>You can use {@link #cancelRequestToSpeak()} to move back to the audience or cancel your pending request.
   *
   * <p>If the self member has {@link Permission#VOICE_MUTE_OTHERS} this will immediately promote them to speaker.
   *
   * <p>Example:
   * <pre>{@code
   * stageChannel.createStageInstance("Talent Show").queue()
   * guild.requestToSpeak(); // Set request to speak flag
   * guild.getAudioManager().openAudioConnection(stageChannel); // join the channel
   * }</pre>
   *
   * @return {@link Task} representing the request to speak.
   * Calling {@link Task#get()} can result in deadlocks and should be avoided at all times.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #cancelRequestToSpeak()
   */
  @Nonnull
  public static Task<Void> requestToSpeak() {
    return getGuild().requestToSpeak();
  }

  /**
   * Retrieves a list of members by their user id.
   * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
   *
   * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param includePresence Whether to load presences of the members (online status/activity)
   * @param ids             The ids of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If includePresence is {@code true
   *                                                                            } and the GUILD_PRESENCES intent is
   *                                                                            disabled</li>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            IDs</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull Collection<Long> ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  /**
   * Retrieves the list of guild commands.
   * <br>This list does not include global commands! Use {@link JDA#retrieveCommands()} for global commands.
   *
   * @param withLocalizations {@code true} if the localization data (such as name and description) should be included
   * @return {@link RestAction} - Type: {@link List} of {@link Command}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
    return getGuild().retrieveCommands(withLocalizations);
  }

  /**
   * The preferred locale for this guild.
   * <br>If the guild doesn't have the COMMUNITY feature, this returns the default.
   *
   * <br>Default: {@link DiscordLocale#ENGLISH_US}
   *
   * @return The preferred {@link DiscordLocale} for this guild
   * @since 4.2.1
   */
  @Nonnull
  public static DiscordLocale getLocale() {
    return getGuild().getLocale();
  }

  /**
   * Returns the {@link GuildManager GuildManager} for this Guild, used to modify
   * all properties and settings of the Guild.
   * <br>You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue()
   * RestAction.queue()}.
   *
   * @return The Manager of this Guild
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#MANAGE_SERVER Permission.MANAGE_SERVER}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static GuildManager getManager() {
    return getGuild().getManager();
  }

  /**
   * Retrieves all the stickers from this guild.
   * <br>This also includes {@link GuildSticker#isAvailable() unavailable} stickers.
   *
   * @return {@link RestAction} - Type: List of {@link GuildSticker}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<GuildSticker>> retrieveStickers() {
    return getGuild().retrieveStickers();
  }

  /**
   * Gets a {@link NewsChannel NewsChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link NewsChannel NewsChannel}.
   * @return Possibly-null {@link NewsChannel NewsChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static NewsChannel getNewsChannelById(long id) {
    return getGuild().getNewsChannelById(id);
  }

  /**
   * Retrieves a list of members by their user id.
   * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
   *
   * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param includePresence Whether to load presences of the members (online status/activity)
   * @param ids             The ids of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If includePresence is {@code true
   *                                                                            } and the GUILD_PRESENCES intent is
   *                                                                            disabled</li>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            IDs</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull long... ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  /**
   * Retrieves the available regions for this Guild
   * <br>Shortcut for {@link #retrieveRegions(boolean) retrieveRegions(true)}
   * <br>This will include deprecated voice regions by default.
   *
   * @return {@link RestAction RestAction} - Type {@link EnumSet EnumSet}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<EnumSet<Region>> retrieveRegions() {
    return getGuild().retrieveRegions();
  }

  /**
   * Gets all {@link NewsChannel NewsChannels} in the cache.
   * <br>In {@link Guild} cache, channels are sorted according to their position and id.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getNewsChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of all {@link NewsChannel NewsChannels} in this Guild.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<NewsChannel> getNewsChannels() {
    return getGuild().getNewsChannels();
  }

  /**
   * Modifies the complete {@link Role Role} set of the specified {@link Member Member}
   * <br>The provided roles will replace all current Roles of the specified Member.
   *
   * <p><b>Warning</b><br>
   * <b>This may <u>not</u> be used together with any other role add/remove/modify methods for the same Member
   * within one event listener cycle! The changes made by this require cache updates which are triggered by
   * lifecycle events which are received later. This may only be called again once the specific Member has been updated
   * by a {@link GenericGuildMemberEvent GenericGuildMemberEvent} targeting the same Member.</b>
   *
   * <p><b>The new roles <u>must not</u> contain the Public Role of the Guild</b>
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The target Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * <p><b>Example</b><br>
   * <pre>{@code
   * public static void removeRoles(Member member) {
   *     Guild guild = member.getGuild();
   *     // pass no role, this means we set the roles of the member to an empty array.
   *     guild.modifyMemberRoles(member).queue();
   * }
   * }</pre>
   *
   * @param member A {@link Member Member} of which to override the Roles of
   * @param roles  New collection of {@link Role Roles} for the specified Member
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
   * @throws HierarchyException              If the provided roles are higher in the Guild's hierarchy
   *                                                                        and thus cannot be modified by the
   *                                                                        currently logged in account
   * @throws IllegalArgumentException                                       <ul>
   *                                                                                    <li>If any of the provided
   *                                                                                    arguments is {@code null}</li>
   *                                                                                    <li>If any of the provided
   *                                                                                    arguments is not from this
   *                                                                                    Guild</li>
   *                                                                                    <li>If any of the specified
   *                                                                                    {@link Role Roles} is
   *                                                                                    managed</li>
   *                                                                                    <li>If any of the specified
   *                                                                                    {@link Role Roles} is the
   *                                                                                    {@code Public Role} of this
   *                                                                                    Guild</li>
   *                                                                                </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @see #modifyMemberRoles(Member, Collection)
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Role... roles) {
    return getGuild().modifyMemberRoles(member, roles);
  }

  /**
   * Sorted {@link SnowflakeCacheView SnowflakeCacheView} of
   * all cached {@link Role Roles} of this Guild.
   * <br>Roles are sorted according to their position.
   *
   * @return {@link SortedSnowflakeCacheView SortedSnowflakeCacheView}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static SortedSnowflakeCacheView<Role> getRoleCache() {
    return getGuild().getRoleCache();
  }

  /**
   * Creates a new {@link MediaChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name   The name of the MediaChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @param parent The optional parent category for this channel, or null
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new MediaChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters;
   *                                                                        or the provided parent is not in the same
   *                                                                        guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<MediaChannel> createMediaChannel(@NotNull String name,
                                                               @org.jetbrains.annotations.Nullable Category parent) {
    return getGuild().createMediaChannel(name, parent);
  }

  /**
   * Modifies the positional order of {@link Guild#getRoles() Guild.getRoles()}
   * using a specific {@link RestAction RestAction} extension to allow moving Roles
   * {@link OrderAction#moveUp(int) up}/{@link OrderAction#moveDown(int) down}
   * or {@link OrderAction#moveTo(int) to} a specific position.
   *
   * <p>You can also move roles to a position relative to another role, by using
   * {@link OrderAction#moveBelow(Object) moveBelow(...)}
   * and {@link OrderAction#moveAbove(Object) moveAbove(...)}.
   *
   * <p>This uses <b>descending</b> ordering which means the highest role is first!
   * <br>This means the lowest role appears at index {@code n - 1} and the highest role at index {@code 0}.
   * <br>Providing {@code true} to {@link #modifyRolePositions(boolean)} will result in the ordering being
   * in ascending order, with the highest role at index {@code n - 1} and the lowest at index {@code 0}.
   *
   * <br>As a note: {@link Member#getRoles() Member.getRoles()}
   * and {@link Guild#getRoles() Guild.getRoles()} are both in descending order, just like this method.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
   *     <br>One of the roles was deleted before the completion of the task</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild</li>
   * </ul>
   *
   * @return {@link RoleOrderAction}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RoleOrderAction modifyRolePositions() {
    return getGuild().modifyRolePositions();
  }

  /**
   * Gets all {@link VoiceChannel VoiceChannels} in the cache.
   * <br>In {@link Guild} cache, channels are sorted according to their position and id.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getVoiceChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of {@link VoiceChannel VoiceChannels}.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<VoiceChannel> getVoiceChannels() {
    return getGuild().getVoiceChannels();
  }

  /**
   * Atomically assigns the provided {@link Role Role} to the specified {@link Member Member}.
   * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
   *
   * <p>If multiple roles should be added/removed (efficiently) in one request
   * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection,
   * Collection)} or similar methods.
   *
   * <p>If the specified role is already present in the member's set of roles this does nothing.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The target Member was removed from the Guild before finishing the task</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
   *     <br>If the specified Role does not exist</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} to change roles for.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @param role The role which should be assigned atomically
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws IllegalArgumentException        <ul>
   *                                                                                                                            <li>If the specified member
   *                                                                                                                            or role are not from the
   *                                                                                                                            current Guild</li>
   *                                                                                                                            <li>Either member or role are
   *                                                                                                                               *                                                                                                                 null}</li>
   *                                                                                                                        </ul>
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
   * @throws HierarchyException              If the provided roles are higher in the Guild's hierarchy
   *                                         and thus cannot be modified by the
   *                                         currently logged in account
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> addRoleToMember(@NotNull UserSnowflake user, @NotNull Role role) {
    return getGuild().addRoleToMember(user, role);
  }

  /**
   * Creates a new {@link ScheduledEvent}.
   *
   * <p><b>Requirements</b><br>
   * <p>
   * Events are required to have a name, channel and start time. Depending on the
   * type of channel provided, an event will be of one of two different {@link ScheduledEvent.Type Types}:
   * <ol>
   *     <li>
   *         {@link ScheduledEvent.Type#STAGE_INSTANCE Type.STAGE_INSTANCE}
   *         <br>These events are set to take place inside of a {@link StageChannel}. The
   *         following permissions are required in the specified stage channel in order to create an event there:
   *          <ul>
   *              <li>{@link Permission#MANAGE_EVENTS}</li>
   *              <li>{@link Permission#MANAGE_CHANNEL}</li>
   *              <li>{@link Permission#VOICE_MUTE_OTHERS}</li>
   *              <li>{@link Permission#VOICE_MOVE_OTHERS}}</li>
   *         </ul>
   *     </li>
   *     <li>
   *         {@link ScheduledEvent.Type#VOICE Type.VOICE}
   *         <br>These events are set to take place inside of a {@link VoiceChannel}. The
   *         following permissions are required in the specified voice channel in order to create an event there:
   *         <ul>
   *             <li>{@link Permission#MANAGE_EVENTS}</li>
   *             <li>{@link Permission#VIEW_CHANNEL}</li>
   *             <li>{@link Permission#VOICE_CONNECT}</li>
   *         </ul>
   *     </li>
   * </ol>
   *
   * <p><b>Example</b><br>
   * <pre>{@code
   * guild.createScheduledEvent("Cactus Beauty Contest", guild.getGuildChannelById(channelId), OffsetDateTime.now().plusHours(1))
   *     .setDescription("Come and have your cacti judged! _Must be spikey to enter_")
   *     .queue();
   * }</pre>
   *
   * @param name      the name for this scheduled event, 1-100 characters
   * @param channel   the voice or stage channel where this scheduled event will take place
   * @param startTime the start time for this scheduled event, can't be in the past
   * @return {@link ScheduledEventAction}
   * @throws IllegalArgumentException <ul>
   *                                                                                                             <li>If a required parameter is {@code
   *                                                                                                             null} or empty</li>
   *                                                                                                             <li>If the start time is in the
   *                                                                                                             past</li>
   *                                                                                                             <li>If the name is longer than 100
   *                                                                                                             characters</li>
   *                                                                                                             <li>If the description is longer than
   *                                                                                                             1000
   *                                                                                                             characters</li>
   *                                                                                                             <li>If the channel is not a Stage or
   *                                                                                                             Voice channel</li>
   *                                                                                                             <li>If the channel is not from the
   *                                                                                                             same
   *                                                                                                             guild as the scheduled event</li>
   *                                                                                                         </ul>
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ScheduledEventAction createScheduledEvent(@NotNull String name, @NotNull GuildChannel channel,
                                                          @NotNull OffsetDateTime startTime) {
    return getGuild().createScheduledEvent(name, channel, startTime);
  }

  /**
   * The vanity url code for this Guild. The vanity url is the custom invite code of partnered / official / boosted
   * Guilds.
   * <br>The returned String will be the code that can be provided to {@code discord.gg/{code}} to get the invite link.
   *
   * @return The vanity code or null
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getVanityUrl()
   * @since 4.0.0
   */
  @Nullable
  public static String getVanityCode() {
    return getGuild().getVanityCode();
  }

  /**
   * {@link MemberCacheView MemberCacheView} for all cached
   * {@link Member Members} of this Guild.
   *
   * <p>This will only provide cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @return {@link MemberCacheView MemberCacheView}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #loadMembers()
   */
  @Nonnull
  public static MemberCacheView getMemberCache() {
    return getGuild().getMemberCache();
  }

  /**
   * {@link SnowflakeCacheView SnowflakeCacheView} of
   * all cached {@link GuildSticker GuildStickers} of this Guild.
   * <br>This will be empty if {@link CacheFlag#STICKER} is disabled.
   *
   * <p>This requires the {@link CacheFlag#STICKER CacheFlag.STICKER} to be enabled!
   *
   * @return {@link SnowflakeCacheView SnowflakeCacheView}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveStickers()
   */
  @Nonnull
  public static SnowflakeCacheView<GuildSticker> getStickerCache() {
    return getGuild().getStickerCache();
  }

  /**
   * Gets a {@link NewsChannel NewsChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link NewsChannel NewsChannel}.
   * @return Possibly-null {@link NewsChannel NewsChannel} with matching id.
   * @throws NumberFormatException                                  If the provided {@code id} cannot be parsed by
   * {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static NewsChannel getNewsChannelById(@NotNull String id) {
    return getGuild().getNewsChannelById(id);
  }

  /**
   * Attempts to remove the user with the provided id from the member cache.
   * <br>If you attempt to remove the {@link JDA#getSelfUser() SelfUser} this will simply return {@code false}.
   *
   * <p>This should be used by an implementation of {@link MemberCachePolicy MemberCachePolicy}
   * as an upstream request to remove a member. For example a Least-Recently-Used (LRU) cache might use this to drop
   * old members if the cache capacity is reached. Or a timeout cache could use this to remove expired members.
   *
   * @param userId The target user id
   * @return True, if the cache was changed
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #pruneMemberCache()
   * @see JDA#unloadUser(long)
   */
  public static boolean unloadMember(long userId) {
    return getGuild().unloadMember(userId);
  }

  /**
   * Gets an {@link RichCustomEmoji} from this guild that has the same id as the
   * one provided.
   * <br>If there is no {@link RichCustomEmoji} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * <p><b>Unicode emojis are not included as {@link RichCustomEmoji}!</b>
   *
   * <p>This requires the {@link CacheFlag#EMOJI CacheFlag.EMOJI} to be enabled!
   *
   * @param id the emoji id
   * @return An emoji matching the specified id
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveEmojiById(long)
   */
  @Nullable
  public static RichCustomEmoji getEmojiById(long id) {
    return getGuild().getEmojiById(id);
  }

  /**
   * Retrieves a {@link ScheduledEvent} by its ID.
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_SCHEDULED_EVENT ErrorResponse.UNKNOWN_SCHEDULED_EVENT}
   *     <br>A scheduled event with the specified ID does not exist in the guild, or the currently logged in user
   *     does not
   *     have access to it.</li>
   * </ul>
   *
   * @param id The ID of the {@link ScheduledEvent}
   * @return {@link RestAction} - Type: {@link ScheduledEvent}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getScheduledEventById(long)
   */
  @CheckReturnValue
  @Nonnull
  public static CacheRestAction<ScheduledEvent> retrieveScheduledEventById(long id) {
    return getGuild().retrieveScheduledEventById(id);
  }

  /**
   * Provides the {@link TextChannel TextChannel} that receives discord safety alerts.
   * <br>If this guild doesn't have the COMMUNITY {@link #getFeatures() feature}, this returns {@code null}.
   *
   * @return Possibly-null {@link TextChannel TextChannel} that is the saferty alerts channel.
   * @see #getFeatures()
   */
  @Nullable
  public static TextChannel getSafetyAlertsChannel() {
    return getGuild().getSafetyAlertsChannel();
  }

  /**
   * The level of content filtering enabled in this Guild.
   * <br>This decides which messages sent by which Members will be scanned for explicit content.
   *
   * @return {@link ExplicitContentLevel ExplicitContentLevel} for this Guild
   */
  @Nonnull
  public static ExplicitContentLevel getExplicitContentLevel() {
    return getGuild().getExplicitContentLevel();
  }

  /**
   * Gets all {@link RichCustomEmoji Custom Emojis} belonging to this {@link Guild Guild}.
   * <br>Emojis are not ordered in any specific way in the returned list.
   *
   * <p><b>Unicode emojis are not included as {@link RichCustomEmoji}!</b>
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getEmojiCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This requires the {@link CacheFlag#EMOJI CacheFlag.EMOJI} to be enabled!
   *
   * @return An immutable List of {@link RichCustomEmoji Custom Emojis}.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveEmojis()
   */
  @Nonnull
  @Unmodifiable
  public static List<RichCustomEmoji> getEmojis() {
    return getGuild().getEmojis();
  }

  /**
   * The boost tier for this guild.
   * <br>Each tier unlocks new perks for a guild that can be seen in the {@link #getFeatures() features}.
   *
   * @return The boost tier.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.0.0
   */
  @Nonnull
  public static BoostTier getBoostTier() {
    return getGuild().getBoostTier();
  }

  /**
   * Gets a {@link VoiceChannel VoiceChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link VoiceChannel VoiceChannel}.
   * @return Possibly-null {@link VoiceChannel VoiceChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static VoiceChannel getVoiceChannelById(long id) {
    return getGuild().getVoiceChannelById(id);
  }

  /**
   * Gets a list of all {@link Member Members} who have the same nickname as the one provided.
   * <br>This compares against {@link Member#getNickname()}. If a Member does not have a nickname, the comparison
   * results as false.
   * <br>If there are no {@link Member Members} with the provided name, then this returns an empty list.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @param nickname   The nickname used to filter the returned Members.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all Members with the same nickname as the nickname provided.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveMembersByPrefix(String, int)
   */
  @Nonnull
  @Unmodifiable
  public static List<Member> getMembersByNickname(@org.jetbrains.annotations.Nullable String nickname,
                                                  boolean ignoreCase) {
    return getGuild().getMembersByNickname(nickname, ignoreCase);
  }

  /**
   * {@link SnowflakeCacheView SnowflakeCacheView} of
   * all cached {@link RichCustomEmoji Custom Emojis} of this Guild.
   * <br>This will be empty if {@link CacheFlag#EMOJI} is disabled.
   *
   * <p>This requires the {@link CacheFlag#EMOJI CacheFlag.EMOJI} to be enabled!
   *
   * @return {@link SnowflakeCacheView SnowflakeCacheView}
   * @see #retrieveEmojis()
   */
  @Nonnull
  public static SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
    return getGuild().getEmojiCache();
  }

  /**
   * The time this entity was created. Calculated through the Snowflake in {@link #getIdLong}.
   *
   * @return OffsetDateTime - Time this entity was created at.
   * @see TimeUtil#getTimeCreated(long)
   */
  @Nonnull
  public static OffsetDateTime getTimeCreated() {
    return getGuild().getTimeCreated();
  }

  /**
   * Sets the Guild Deafened state of the {@link Member Member} based on the provided
   * boolean.
   *
   * <p><b>Note:</b> The Member's {@link GuildVoiceState#isGuildDeafened() GuildVoiceState.isGuildDeafened()} value
   * won't change
   * until JDA receives the {@link GuildVoiceGuildDeafenEvent GuildVoiceGuildDeafenEvent} event related to this change.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be deafened due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   *
   *     <li>{@link ErrorResponse#USER_NOT_CONNECTED USER_NOT_CONNECTED}
   *     <br>The specified Member is not connected to a voice channel</li>
   * </ul>
   *
   * @param user   The {@link UserSnowflake} who's {@link GuildVoiceState} to change.
   *               This can be a member or user instance or {@link User#fromId(long)}.
   * @param deafen Whether this {@link Member Member} should be deafened or undeafened.
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#VOICE_DEAF_OTHERS} permission
   *                                         in the given channel.
   * @throws IllegalArgumentException        If the provided user is null.
   * @throws IllegalStateException           If the provided user is not currently connected to a voice channel.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> deafen(@NotNull UserSnowflake user, boolean deafen) {
    return getGuild().deafen(user, deafen);
  }

  /**
   * Retrieves the {@link AutoModRule} for the provided id.
   *
   * @param id The id of the rule
   * @return {@link RestAction} - Type: {@link AutoModRule}
   * @throws IllegalArgumentException        If the provided id is not a valid snowflake
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<AutoModRule> retrieveAutoModRuleById(@NotNull String id) {
    return getGuild().retrieveAutoModRuleById(id);
  }

  /**
   * Gets a {@link ForumChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link ForumChannel}.
   * @return Possibly-null {@link ForumChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static ForumChannel getForumChannelById(long id) {
    return getGuild().getForumChannelById(id);
  }

  /**
   * Retrieves a {@link ScheduledEvent} by its ID.
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_SCHEDULED_EVENT ErrorResponse.UNKNOWN_SCHEDULED_EVENT}
   *     <br>A scheduled event with the specified ID does not exist in this guild, or the currently logged in user
   *     does not
   *     have access to it.</li>
   * </ul>
   *
   * @param id The ID of the {@link ScheduledEvent}
   * @return {@link RestAction} - Type: {@link ScheduledEvent}
   * @throws IllegalArgumentException If the specified ID is {@code null} or empty
   * @throws NumberFormatException    If the specified ID cannot be parsed by {@link Long#parseLong(String)}
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see #getScheduledEventById(long)
   */
  @CheckReturnValue
  @Nonnull
  public static CacheRestAction<ScheduledEvent> retrieveScheduledEventById(@NotNull String id) {
    return getGuild().retrieveScheduledEventById(id);
  }

  /**
   * Gets all {@link ForumChannel} in the cache.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getForumChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of {@link ForumChannel}.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<ForumChannel> getForumChannels() {
    return getGuild().getForumChannels();
  }

  /**
   * Retrieves and collects members of this guild into a list.
   * <br>This will use the configured {@link MemberCachePolicy MemberCachePolicy}
   * to decide which members to retain in cache.
   *
   * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param roles All roles the members must have
   * @return {@link Task} - Type: {@link List} of {@link Member}
   * @throws IllegalArgumentException If null is provided
   * @throws IllegalStateException    If the {@link GatewayIntent#GUILD_MEMBERS
   *                                  GatewayIntent.GUILD_MEMBERS} is not enabled
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @since 4.2.1
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> findMembersWithRoles(@NotNull Role... roles) {
    return getGuild().findMembersWithRoles(roles);
  }

  /**
   * Retrieves a list of members by their user id.
   * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
   *
   * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
   * this will load the {@link OnlineStatus OnlineStatus} and {@link Activity Activities}
   * of the members. You can use {@link #retrieveMembersByIds(boolean, Collection)} to disable presences.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param ids The ids of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            IDs</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByIds(@NotNull Collection<Long> ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  /**
   * Kicks a {@link Member Member} from the {@link Guild Guild}.
   *
   * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User}
   * until Discord sends the {@link GuildMemberRemoveEvent GuildMemberRemoveEvent}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be kicked due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} for the user to kick.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @return {@link AuditableRestAction AuditableRestAction}
   * Kicks the provided Member from the current Guild
   * @throws IllegalArgumentException                                       If the user cannot be kicked from this
   * Guild or the provided {@code user} is null.
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#KICK_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot kick the other member due to permission
   * hierarchy position. (See {@link Member#canInteract(Member)})
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> kick(@NotNull UserSnowflake user) {
    return getGuild().kick(user);
  }

  /**
   * Retrieves the available regions for this Guild
   *
   * @param includeDeprecated Whether to include deprecated regions
   * @return {@link RestAction RestAction} - Type {@link EnumSet EnumSet}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<EnumSet<Region>> retrieveRegions(boolean includeDeprecated) {
    return getGuild().retrieveRegions(includeDeprecated);
  }

  /**
   * The URL of the {@link Guild Guild} icon image.
   * If no icon has been set, this returns {@code null}.
   * <p>
   * The Guild icon can be modified using {@link GuildManager#setIcon(Icon)}.
   *
   * @return Possibly-null String containing the Guild's icon URL.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static String getIconUrl() {
    return getGuild().getIconUrl();
  }

  /**
   * Get a channel of the specified type by id.
   *
   * <p>This will automatically check for all channel types and cast to the specified class.
   * If a channel with the specified id does not exist,
   * or exists but is not an instance of the provided class, this returns null.
   *
   * @param type {@link Class} of a channel type
   * @param id   The snowflake id of the channel
   * @return The casted channel, if it exists and is assignable to the provided class, or null
   * @throws IllegalArgumentException If null is provided, or the id is not a valid snowflake
   * @throws DetachedEntityException  If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static <T extends GuildChannel> T getChannelById(@NotNull Class<T> type, @NotNull String id) {
    return getGuild().getChannelById(type, id);
  }

  /**
   * Attempts to retrieve a {@link GuildSticker} object for this guild based on the provided snowflake reference.
   *
   * <p>The returned {@link RestAction RestAction} can encounter the following Discord errors:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_STICKER UNKNOWN_STICKER}
   *     <br>Occurs when the provided id does not refer to a sticker known by Discord.</li>
   * </ul>
   *
   * @param sticker The reference of the requested {@link Sticker}.
   *                <br>Can be {@link RichSticker}, {@link StickerItem}, or {@link Sticker#fromId(long)}.
   * @return {@link RestAction RestAction} - Type: {@link GuildSticker}
   * <br>On request, gets the sticker with id matching provided id from Discord.
   * @throws IllegalArgumentException If null is provided
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<GuildSticker> retrieveSticker(@NotNull StickerSnowflake sticker) {
    return getGuild().retrieveSticker(sticker);
  }

  /**
   * Returns whether this Guild has its boost progress bar shown.
   *
   * @return True, if this Guild has its boost progress bar shown
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  public static boolean isBoostProgressBarEnabled() {
    return getGuild().isBoostProgressBarEnabled();
  }

  /**
   * The maximum amount of members that can join this guild.
   *
   * @return The maximum amount of members
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveMetaData()
   * @since 4.0.0
   */
  public static int getMaxMembers() {
    return getGuild().getMaxMembers();
  }

  /**
   * Gets a list of all {@link StageChannel StageChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link StageChannel StageChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all StageChannel names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<StageChannel> getStageChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getStageChannelsByName(name, ignoreCase);
  }

  /**
   * Gets a {@link RichCustomEmoji} from this guild that has the same id as the
   * one provided.
   * <br>If there is no {@link RichCustomEmoji} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * <p><b>Unicode emojis are not included as {@link RichCustomEmoji}!</b>
   *
   * <p>This requires the {@link CacheFlag#EMOJI CacheFlag.EMOJI} to be enabled!
   *
   * @param id the emoji id
   * @return An Emoji matching the specified id
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveEmojiById(String)
   */
  @Nullable
  public static RichCustomEmoji getEmojiById(@NotNull String id) {
    return getGuild().getEmojiById(id);
  }

  /**
   * Gets a list of all {@link ThreadChannel ThreadChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>These threads can also represent posts in {@link ForumChannel ForumChannels}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link ThreadChannel ThreadChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all ThreadChannel names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<ThreadChannel> getThreadChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getThreadChannelsByName(name, ignoreCase);
  }

  /**
   * The expected member count for this guild.
   * <br>If this guild is not lazy loaded this should be identical to the size returned by {@link #getMemberCache()}.
   *
   * <p>When {@link GatewayIntent#GUILD_MEMBERS GatewayIntent.GUILD_MEMBERS} is disabled, this will not be updated.
   *
   * @return The expected member count for this guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  public static int getMemberCount() {
    return getGuild().getMemberCount();
  }

  /**
   * Modifies the positional order of {@link Guild#getRoles() Guild.getRoles()}
   * using a specific {@link RestAction RestAction} extension to allow moving Roles
   * {@link OrderAction#moveUp(int) up}/{@link OrderAction#moveDown(int) down}
   * or {@link OrderAction#moveTo(int) to} a specific position.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
   *     <br>One of the roles was deleted before the completion of the task</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild</li>
   * </ul>
   *
   * @param useAscendingOrder Defines the ordering of the OrderAction. If {@code false}, the OrderAction will be in
   *                          the ordering
   *                          defined by Discord for roles, which is Descending. This means that the highest role
   *                          appears at index {@code 0}
   *                          and the lowest role at index {@code n - 1}. Providing {@code true} will result in the
   *                          ordering being
   *                          in ascending order, with the lower role at index {@code 0} and the highest at index
   *                          {@code n - 1}.
   *                          <br>As a note: {@link Member#getRoles() Member.getRoles()}
   *                          and {@link Guild#getRoles() Guild.getRoles()} are both in descending order.
   * @return {@link RoleOrderAction}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RoleOrderAction modifyRolePositions(boolean useAscendingOrder) {
    return getGuild().modifyRolePositions(useAscendingOrder);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<TextChannel> getTextChannelCache() {
    return getGuild().getTextChannelCache();
  }

  /**
   * Gets a {@link MediaChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link MediaChannel}.
   * @return Possibly-null {@link MediaChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static MediaChannel getMediaChannelById(long id) {
    return getGuild().getMediaChannelById(id);
  }

  /**
   * A list of all {@link Member Members} in this Guild.
   * <br>The Members are not provided in any particular order.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getMemberCache()} and use its more efficient
   * versions of handling these values.
   *
   * @return Immutable list of all <b>cached</b> members in this Guild.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #loadMembers()
   */
  @Nonnull
  @Unmodifiable
  public static List<Member> getMembers() {
    return getGuild().getMembers();
  }

  /**
   * Get {@link GuildChannel GuildChannel} for the provided ID.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * <br>This is meant for systems that use a dynamic {@link ChannelType} and can
   * profit from a simple function to get the channel instance.
   *
   * <p>To get more specific channel types you can use one of the following:
   * <ul>
   *     <li>{@link #getChannelById(Class, long)}</li>
   *     <li>{@link #getTextChannelById(long)}</li>
   *     <li>{@link #getNewsChannelById(long)}</li>
   *     <li>{@link #getStageChannelById(long)}</li>
   *     <li>{@link #getVoiceChannelById(long)}</li>
   *     <li>{@link #getCategoryById(long)}</li>
   *     <li>{@link #getForumChannelById(long)}</li>
   * </ul>
   *
   * @param type The {@link ChannelType}
   * @param id   The ID of the channel
   * @return The GuildChannel or null
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static GuildChannel getGuildChannelById(@NotNull ChannelType type, long id) {
    return getGuild().getGuildChannelById(type, id);
  }

  /**
   * Creates a new {@link TextChannel TextChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name   The name of the TextChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @param parent The optional parent category for this channel, or null
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new TextChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters;
   *                                                                        or the provided parent is not in the same
   *                                                                        guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<TextChannel> createTextChannel(@NotNull String name,
                                                             @org.jetbrains.annotations.Nullable Category parent) {
    return getGuild().createTextChannel(name, parent);
  }

  /**
   * Retrieves the {@link IntegrationPrivilege IntegrationPrivileges} for the target with the specified ID.
   * <br><b>The ID can either be of a Command or Application!</b>
   *
   * <p>Moderators of a guild can modify these privileges through the Integrations Menu
   *
   * <p>If there is no command or application with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param targetId The id of the command (global or guild), or application
   * @return {@link RestAction} - Type: {@link List} of {@link IntegrationPrivilege}
   * @throws IllegalArgumentException If the id is not a valid snowflake
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<IntegrationPrivilege>> retrieveIntegrationPrivilegesById(@NotNull String targetId) {
    return getGuild().retrieveIntegrationPrivilegesById(targetId);
  }

  /**
   * Gets a {@link VoiceChannel VoiceChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link VoiceChannel VoiceChannel}.
   * @return Possibly-null {@link VoiceChannel VoiceChannel} with matching id.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by
   *                                 {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static VoiceChannel getVoiceChannelById(@NotNull String id) {
    return getGuild().getVoiceChannelById(id);
  }

  /**
   * Gets a {@link ScheduledEvent} from this guild that has the same id as the
   * one provided. This method is similar to {@link JDA#getScheduledEventById(String)}, but it only
   * checks this specific Guild for a scheduled event.
   * <br>If there is no {@link ScheduledEvent} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * <p>This requires {@link CacheFlag#SCHEDULED_EVENTS} to be enabled.
   *
   * @param id The id of the {@link ScheduledEvent}.
   * @return Possibly-null {@link ScheduledEvent} with matching id.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static ScheduledEvent getScheduledEventById(@NotNull String id) {
    return getGuild().getScheduledEventById(id);
  }

  /**
   * Creates a new {@link ForumChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the ForumChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new ForumChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException        If the provided name is {@code null}, blank, or longer than
   * {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<ForumChannel> createForumChannel(@NotNull String name) {
    return getGuild().createForumChannel(name);
  }

  /**
   * Kicks the {@link UserSnowflake} from the {@link Guild Guild}.
   *
   * <p><b>Note:</b> {@link Guild#getMembers()} will still contain the {@link User User}
   * until Discord sends the {@link GuildMemberRemoveEvent GuildMemberRemoveEvent}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be kicked due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param user   The {@link UserSnowflake} for the user to kick.
   *               This can be a member or user instance or {@link User#fromId(long)}.
   * @param reason The reason for this action or {@code null} if there is no specified reason
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   *                                         {@link Permission#KICK_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot kick the other member due to permission
   *                                         hierarchy position. (See {@link Member#canInteract(Member)})
   * @throws IllegalArgumentException        <ul>
   *                                                                                                                                                                    <li>If the user cannot be
   *                                                                                                                                                                    kicked from this Guild or the
   *                                                                                                                                                                    provided {@code user} is
   *                                                                                                                                                                    null.</li>
   *                                                                                                                                                                    <li>If the provided reason is
   *                                                                                                                                                                    longer than 512 characters</li>
   *                                                                                                                                                                </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @deprecated Use {@link #kick(UserSnowflake)} and {@link AuditableRestAction#reason(String)} instead.
   */
  @DeprecatedSince("5.0.0")
  @ReplaceWith("kick(user).reason(reason)")
  @ForRemoval
  @Deprecated
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> kick(@NotNull UserSnowflake user,
                                               @org.jetbrains.annotations.Nullable String reason) {
    return getGuild().kick(user, reason);
  }

  /**
   * Provides the {@link TextChannel TextChannel} that has been set as the channel
   * which newly joined {@link Member Members} will be announced in.
   * <br>If no channel has been set as the system channel, this returns {@code null}.
   * <p>
   * This value can be modified using {@link GuildManager#setSystemChannel(TextChannel)}.
   *
   * @return Possibly-null {@link TextChannel TextChannel} that is the system Channel.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static TextChannel getSystemChannel() {
    return getGuild().getSystemChannel();
  }

  /**
   * Delete the command for this id.
   *
   * <p>If there is no command with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param commandId The id of the command that should be deleted
   * @return {@link RestAction}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> deleteCommandById(long commandId) {
    return getGuild().deleteCommandById(commandId);
  }

  /**
   * Gets a {@link ScheduledEvent} from this guild that has the same id as the
   * one provided. This method is similar to {@link JDA#getScheduledEventById(long)}, but it only
   * checks this specific Guild for a scheduled event.
   * <br>If there is no {@link ScheduledEvent} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * <p>This requires {@link CacheFlag#SCHEDULED_EVENTS} to be enabled.
   *
   * @param id The id of the {@link ScheduledEvent}.
   * @return Possibly-null {@link ScheduledEvent} with matching id.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static ScheduledEvent getScheduledEventById(long id) {
    return getGuild().getScheduledEventById(id);
  }

  /**
   * Retrieves a custom emoji together with its respective creator.
   *
   * <p>Note that {@link RichCustomEmoji#getOwner()} is only available if the currently
   * logged in account has {@link Permission#MANAGE_GUILD_EXPRESSIONS Permission.MANAGE_GUILD_EXPRESSIONS}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_EMOJI UNKNOWN_EMOJI}
   *     <br>If the provided emoji does not correspond to an emoji in this guild anymore</li>
   * </ul>
   *
   * @param emoji The emoji reference to retrieve
   * @return {@link RestAction} - Type: {@link RichCustomEmoji}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<RichCustomEmoji> retrieveEmoji(@NotNull CustomEmoji emoji) {
    return getGuild().retrieveEmoji(emoji);
  }

  /**
   * Cancels the {@link #requestToSpeak() Request-to-Speak}.
   * <br>This can also be used to move back to the audience if you are currently a speaker.
   *
   * <p>If there is no request to speak or the member is not currently connected to a {@link StageChannel}, this does
   * nothing.
   *
   * @return {@link Task} representing the request to speak cancellation.
   * Calling {@link Task#get()} can result in deadlocks and should be avoided at all times.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #requestToSpeak()
   */
  @Nonnull
  public static Task<Void> cancelRequestToSpeak() {
    return getGuild().cancelRequestToSpeak();
  }

  /**
   * Gets a list of all {@link Category Categories}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name to check
   * @param ignoreCase Whether to ignore case on name checking
   * @return Immutable list of all categories matching the provided name
   * @throws IllegalArgumentException                               If the provided name is {@code null}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<Category> getCategoriesByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getCategoriesByName(name, ignoreCase);
  }

  /**
   * The guild banner id.
   * <br>This is shown in guilds below the guild name.
   *
   * <p>The banner can be modified using {@link GuildManager#setBanner(Icon)}.
   *
   * @return The guild banner id or null
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getBannerUrl()
   * @since 4.0.0
   */
  @Nullable
  public static String getBannerId() {
    return getGuild().getBannerId();
  }

  /**
   * The {@link GuildWelcomeScreenManager Manager} for this guild's welcome screen, used to modify
   * properties of the welcome screen like if the welcome screen is enabled, the description and welcome channels.
   * <br>You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue()
   * RestAction.queue()}.
   *
   * @return The GuildWelcomeScreenManager for this guild's welcome screen
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#MANAGE_SERVER Permission.MANAGE_SERVER}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static GuildWelcomeScreenManager modifyWelcomeScreen() {
    return getGuild().modifyWelcomeScreen();
  }

  /**
   * Gets all {@link ScheduledEvent ScheduledEvents} in this guild.
   * <br>Scheduled events are sorted by their start time, and events that start at the same time
   * are sorted by their snowflake ID.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getScheduledEventCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This requires {@link CacheFlag#SCHEDULED_EVENTS} to be enabled.
   *
   * @return Possibly-empty immutable List of {@link ScheduledEvent ScheduledEvents}.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<ScheduledEvent> getScheduledEvents() {
    return getGuild().getScheduledEvents();
  }

  /**
   * Deletes the {@link AutoModRule} for the provided id.
   *
   * @param id The id of the rule
   * @return {@link AuditableRestAction} - Type: {@link Void}
   * @throws IllegalArgumentException        If the provided id is not a valid snowflake
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> deleteAutoModRuleById(@NotNull String id) {
    return getGuild().deleteAutoModRuleById(id);
  }

  /**
   * Gets all {@link MediaChannel} in the cache.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getForumChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of {@link MediaChannel}.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<MediaChannel> getMediaChannels() {
    return getGuild().getMediaChannels();
  }

  /**
   * Gets a {@link GuildSticker GuildSticker} from this guild that has the same id as the
   * one provided.
   * <br>If there is no {@link GuildSticker GuildSticker} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * <p>This requires the {@link CacheFlag#STICKER CacheFlag.STICKER} to be enabled!
   *
   * @param id the sticker id
   * @return A Sticker matching the specified id
   * @see #retrieveSticker(StickerSnowflake)
   */
  @Nullable
  public static GuildSticker getStickerById(long id) {
    return getGuild().getStickerById(id);
  }

  /**
   * Used to kick a {@link Member Member} from a {@link AudioChannel AudioChannel}.
   * <br>As a note, you cannot kick a Member that isn't already in a AudioChannel. Also they must be in a AudioChannel
   * in the same Guild.
   *
   * <p>Equivalent to {@code moveVoiceMember(member, null)}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be moved due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
   *     <br>The specified channel was deleted before finishing the task</li>
   * </ul>
   *
   * @param member The {@link Member Member} that you are moving.
   * @return {@link RestAction RestAction}
   * @throws IllegalStateException           If the Member isn't currently in a AudioChannel in this Guild, or
   * {@link CacheFlag#VOICE_STATE} is disabled.
   * @throws IllegalArgumentException        <ul>
   *                                                                                                                            <li>If any of the provided arguments is {@code null}</li>
   *                                                                                                                            <li>If the provided Member isn't part of this {@link Guild Guild}</li>
   *                                                                                                                            <li>If the provided AudioChannel isn't part of this {@link Guild Guild}</li>
   *                                                                                                                        </ul>
   * @throws InsufficientPermissionException If this account doesn't have {@link Permission#VOICE_MOVE_OTHERS}
   *                                         in the AudioChannel that the Member is currently in.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> kickVoiceMember(@NotNull Member member) {
    return getGuild().kickVoiceMember(member);
  }

  /**
   * Modifies the positional order of {@link Guild#getCategories() Guild.getCategories()}
   * using a specific {@link RestAction RestAction} extension to allow moving Channels
   * {@link OrderAction#moveUp(int) up}/{@link OrderAction#moveDown(int) down}
   * or {@link OrderAction#moveTo(int) to} a specific position.
   * <br>This uses <b>ascending</b> order with a 0 based index.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
   *     <br>One of the channels has been deleted before the completion of the task</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild</li>
   * </ul>
   *
   * @return {@link ChannelOrderAction ChannelOrderAction} - Type: {@link Category Category}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelOrderAction modifyCategoryPositions() {
    return getGuild().modifyCategoryPositions();
  }

  /**
   * The {@link Timeout Timeout} set for this Guild representing the amount of time
   * that must pass for a Member to have had no activity in a {@link VoiceChannel VoiceChannel}
   * to be considered AFK. If {@link #getAfkChannel()} is not {@code null} (thus an AFK channel has been set) then
   * Member
   * will be automatically moved to the AFK channel after they have been inactive for longer than the returned Timeout.
   * <br>Default is {@link Timeout#SECONDS_300 300 seconds (5 minutes)}.
   * <p>
   * This value can be modified using {@link GuildManager#setAfkTimeout(Timeout)}.
   *
   * @return The {@link Timeout Timeout} set for this Guild.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static Timeout getAfkTimeout() {
    return getGuild().getAfkTimeout();
  }

  /**
   * Gets a list of {@link Member Members} that have all provided {@link Role Roles}.
   * <br>If there are no {@link Member Members} with all provided roles, then this returns an empty list.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @param roles The {@link Role Roles} that a {@link Member Member}
   *              must have to be included in the returned list.
   * @return Possibly-empty immutable list of Members with all provided Roles.
   * @throws IllegalArgumentException If a provided {@link Role Role} is from a
   *                                  different guild or null.
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see #findMembersWithRoles(Collection)
   */
  @Nonnull
  @Unmodifiable
  public static List<Member> getMembersWithRoles(@NotNull Collection<Role> roles) {
    return getGuild().getMembersWithRoles(roles);
  }

  /**
   * Gets a {@link ForumChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link ForumChannel}.
   * @return Possibly-null {@link ForumChannel} with matching id.
   * @throws NumberFormatException                                  If the provided {@code id} cannot be parsed by
   * {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static ForumChannel getForumChannelById(@NotNull String id) {
    return getGuild().getForumChannelById(id);
  }

  /**
   * Gets all custom {@link GuildSticker GuildStickers} belonging to this {@link Guild Guild}.
   * <br>GuildStickers are not ordered in any specific way in the returned list.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getStickerCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This requires the {@link CacheFlag#STICKER CacheFlag.STICKER} to be enabled!
   *
   * @return An immutable List of {@link GuildSticker GuildStickers}.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveStickers()
   */
  @Nonnull
  @Unmodifiable
  public static List<GuildSticker> getStickers() {
    return getGuild().getStickers();
  }

  /**
   * Returns the maximum size for files that can be uploaded to this Guild.
   * This returns 8 MiB for Guilds without a Boost Tier or Guilds with Boost Tier 1, 50 MiB for Guilds with Boost
   * Tier 2 and 100 MiB for Guilds with Boost Tier 3.
   *
   * @return The maximum size for files that can be uploaded to this Guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.2.0
   */
  public static long getMaxFileSize() {
    return getGuild().getMaxFileSize();
  }

  /**
   * Gets a {@link MediaChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link MediaChannel}.
   * @return Possibly-null {@link MediaChannel} with matching id.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static MediaChannel getMediaChannelById(@NotNull String id) {
    return getGuild().getMediaChannelById(id);
  }

  /**
   * The vanity url for this Guild. The vanity url is the custom invite code of partnered / official / boosted Guilds.
   * <br>The returned String will be the vanity invite link to this guild.
   *
   * @return The vanity url or null
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.0.0
   */
  @Nullable
  public static String getVanityUrl() {
    return getGuild().getVanityUrl();
  }

  /**
   * The {@link Member Member} object for the owner of this Guild.
   * <br>This is null when the owner is no longer in this guild or not yet loaded (lazy loading).
   * Sometimes owners of guilds delete their account or get banned by Discord.
   *
   * <p>If lazy-loading is used it is recommended to use {@link #retrieveOwner()} instead.
   *
   * <p>Ownership can be transferred using {@link Guild#transferOwnership(Member)}.
   *
   * <p>This only works when the member was added to cache. Lazy loading might load this later.
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @return Possibly-null Member object for the Guild owner.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getOwnerIdLong()
   * @see #retrieveOwner()
   */
  @Nullable
  public static Member getOwner() {
    return getGuild().getOwner();
  }

  /**
   * Returns the verification-Level of this Guild. Verification level is one of the factors that determines if a Member
   * can send messages in a Guild.
   * <br>For a short description of the different values, see {@link VerificationLevel}.
   * <p>
   * This value can be modified using {@link GuildManager#setVerificationLevel(VerificationLevel)}.
   *
   * @return The Verification-Level of this Guild.
   */
  @Nonnull
  public static VerificationLevel getVerificationLevel() {
    return getGuild().getVerificationLevel();
  }

  /**
   * Sorted {@link SnowflakeCacheView} of
   * all cached {@link ScheduledEvent ScheduledEvents} of this Guild.
   * <br>Scheduled events are sorted by their start time, and events that start at the same time
   * are sorted by their snowflake ID.
   *
   * <p>This requires {@link CacheFlag#SCHEDULED_EVENTS} to be enabled.
   *
   * @return {@link SortedSnowflakeCacheView}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static SortedSnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
    return getGuild().getScheduledEventCache();
  }

  /**
   * Retrieves a list of members.
   * <br>If the user does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the users resolve to a member, in which case an empty list will be the result.
   *
   * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param includePresence Whether to load presences of the members (online status/activity)
   * @param users           The users of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If includePresence is {@code true
   *                                                                            } and the GUILD_PRESENCES intent is
   *                                                                            disabled</li>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            users</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembers(boolean includePresence, @NotNull Collection<?
      extends UserSnowflake> users) {
    return getGuild().retrieveMembers(includePresence, users);
  }

  /**
   * Whether this guild has loaded members.
   * <br>This will always be false if the {@link GatewayIntent#GUILD_MEMBERS GUILD_MEMBERS} intent is disabled.
   *
   * @return True, if members are loaded.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  public static boolean isLoaded() {
    return getGuild().isLoaded();
  }

  /**
   * The amount of boosts this server currently has.
   *
   * @return The boost count
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.0.0
   */
  public static int getBoostCount() {
    return getGuild().getBoostCount();
  }

  /**
   * Creates or updates a slash command.
   * <br>If a command with the same name exists, it will be replaced.
   * This operation is idempotent.
   * Commands will persist between restarts of your bot, you only have to create a command once.
   *
   * <p>To specify a complete list of all commands you can use {@link #updateCommands()} instead.
   *
   * <p>You need the OAuth2 scope {@code "applications.commands"} in order to add commands to a guild.
   *
   * @param name        The lowercase alphanumeric (with dash) name, 1-32 characters
   * @param description The description for the command, 1-100 characters
   * @return {@link CommandCreateAction}
   * @throws IllegalArgumentException If null is provided or the name/description do not meet the requirements
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static CommandCreateAction upsertCommand(@NotNull String name, @NotNull String description) {
    return getGuild().upsertCommand(name, description);
  }

  /**
   * Retrieves an immutable list of Custom Emojis together with their respective creators.
   *
   * <p>Note that {@link RichCustomEmoji#getOwner()} is only available if the currently
   * logged in account has {@link Permission#MANAGE_GUILD_EXPRESSIONS Permission.MANAGE_GUILD_EXPRESSIONS}.
   *
   * @return {@link RestAction RestAction} - Type: List of {@link RichCustomEmoji}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<RichCustomEmoji>> retrieveEmojis() {
    return getGuild().retrieveEmojis();
  }

  /**
   * Retrieves and collects members of this guild into a list.
   * <br>This will use the configured {@link MemberCachePolicy MemberCachePolicy}
   * to decide which members to retain in cache.
   *
   * <p>You can use {@link #findMembers(Predicate)} to filter specific members.
   *
   * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @return {@link Task} - Type: {@link List} of {@link Member}
   * @throws IllegalStateException                                  If the {@link GatewayIntent#GUILD_MEMBERS
   * GatewayIntent.GUILD_MEMBERS} is not enabled
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> loadMembers() {
    return getGuild().loadMembers();
  }

  /**
   * The Snowflake id of this entity. This is unique to every entity and will never change.
   *
   * @return Never-null String containing the Id.
   */
  @Nonnull
  public static String getId() {
    return getGuild().getId();
  }

  /**
   * Creates a new {@link MediaChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the MediaChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new MediaChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<MediaChannel> createMediaChannel(@NotNull String name) {
    return getGuild().createMediaChannel(name);
  }

  /**
   * Gets a list of all {@link RichCustomEmoji Custom Emojis} in this Guild that have the same
   * name as the one provided.
   * <br>If there are no {@link RichCustomEmoji Emojis} with the provided name, then this returns an empty list.
   *
   * <p><b>Unicode emojis are not included as {@link RichCustomEmoji}!</b>
   *
   * <p>This requires the {@link CacheFlag#EMOJI CacheFlag.EMOJI} to be enabled!
   *
   * @param name       The name used to filter the returned {@link RichCustomEmoji Emojis}. Without colons.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all Emojis that match the provided name.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<RichCustomEmoji> getEmojisByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getEmojisByName(name, ignoreCase);
  }

  /**
   * Retrieves all {@link Webhook Webhooks} for this Guild.
   * <br>Requires {@link Permission#MANAGE_WEBHOOKS MANAGE_WEBHOOKS} in this Guild.
   *
   * <p>To get all webhooks for a specific {@link TextChannel TextChannel}, use
   * {@link TextChannel#retrieveWebhooks()}
   *
   * @return {@link RestAction RestAction} - Type: List{@literal <}{@link Webhook Webhook}{@literal >}
   * <br>A list of all Webhooks in this Guild.
   * @throws InsufficientPermissionException if the account does not have {@link Permission#MANAGE_WEBHOOKS
   *                                         MANAGE_WEBHOOKS} in this Guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @see TextChannel#retrieveWebhooks()
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<Webhook>> retrieveWebhooks() {
    return getGuild().retrieveWebhooks();
  }

  /**
   * The Discord hash-id of the splash image for this Guild. A Splash image is an image displayed when viewing a
   * Discord Guild Invite on the web or in client just before accepting or declining the invite.
   * If no splash has been set, this returns {@code null}.
   * <br>Splash images are VIP/Partner Guild only.
   * <p>
   * The Guild splash can be modified using {@link GuildManager#setSplash(Icon)}.
   *
   * @return Possibly-null String containing the Guild's splash hash-id
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static String getSplashId() {
    return getGuild().getSplashId();
  }

  /**
   * This method will prune (kick) all members who were offline for at least <i>days</i> days.
   * <br>The RestAction returned from this method will return the amount of Members that were pruned.
   * <br>You can use {@link Guild#retrievePrunableMemberCount(int)} to determine how many Members would be pruned if
   * you were to
   * call this method.
   *
   * <p>This might timeout when pruning many members.
   * You can use {@code prune(days, false)} to ignore the prune count and avoid a timeout.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The prune cannot finished due to a permission discrepancy</li>
   * </ul>
   *
   * @param days  Minimum number of days since a member has been offline to get affected.
   * @param roles Optional roles to include in prune filter
   * @return {@link AuditableRestAction AuditableRestAction} - Type: Integer
   * <br>The amount of Members that were pruned from the Guild.
   * @throws InsufficientPermissionException If the account doesn't have {@link Permission#KICK_MEMBERS KICK_MEMBER}
   * Permission.
   * @throws IllegalArgumentException                                       <ul>
   *                                                                                    <li>If the provided days are
   *                                                                                    not in the range from 1 to 30
   *                                                                                    (inclusive)</li>
   *                                                                                    <li>If null is provided</li>
   *                                                                                    <li>If any of the provided
   *                                                                                    roles is not from this
   *                                                                                    guild</li>
   *                                                                                </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Integer> prune(int days, @NotNull Role... roles) {
    return getGuild().prune(days, roles);
  }

  /**
   * Modifies the positional order of {@link Category#getVoiceChannels() Category#getVoiceChannels()}
   * using an extension of {@link ChannelOrderAction ChannelOrderAction}
   * specialized for ordering the nested {@link VoiceChannel VoiceChannels} of this
   * {@link Category Category}.
   * <br>Like {@code ChannelOrderAction}, the returned {@link CategoryOrderAction CategoryOrderAction}
   * can be used to move VoiceChannels {@link OrderAction#moveUp(int) up},
   * {@link OrderAction#moveDown(int) down}, or
   * {@link OrderAction#moveTo(int) to} a specific position.
   * <br>This uses <b>ascending</b> order with a 0 based index.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
   *     <br>One of the channels has been deleted before the completion of the task.</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild.</li>
   * </ul>
   *
   * @param category The {@link Category Category} to order
   *                 {@link VoiceChannel VoiceChannels} from.
   * @return {@link CategoryOrderAction CategoryOrderAction} - Type: {@link VoiceChannel VoiceChannels}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static CategoryOrderAction modifyVoiceChannelPositions(@NotNull Category category) {
    return getGuild().modifyVoiceChannelPositions(category);
  }

  /**
   * Get a channel of the specified type by id.
   *
   * <p>This will automatically check for all channel types and cast to the specified class.
   * If a channel with the specified id does not exist,
   * or exists but is not an instance of the provided class, this returns null.
   *
   * @param type {@link Class} of a channel type
   * @param id   The snowflake id of the channel
   * @return The casted channel, if it exists and is assignable to the provided class, or null
   * @throws IllegalArgumentException                               If null is provided
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static <T extends GuildChannel> T getChannelById(@NotNull Class<T> type, long id) {
    return getGuild().getChannelById(type, id);
  }

  /**
   * {@link SortedChannelCacheView SortedChannelCacheView} of {@link GuildChannel}.
   *
   * <p>Provides cache access to all channels of this guild, including thread channels (unlike {@link #getChannels()}).
   * The cache view attempts to provide a sorted list, based on how channels are displayed in the client.
   * Various methods like {@link SortedChannelCacheView#forEachUnordered(Consumer)} or
   * {@link SortedChannelCacheView#lockedIterator()}
   * bypass sorting for optimization reasons.
   *
   * <p>It is possible to filter the channels to more specific types using
   * {@link ChannelCacheView#getElementById(ChannelType, long)} or {@link SortedChannelCacheView#ofType(Class)}.
   *
   * @return {@link SortedChannelCacheView SortedChannelCacheView}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static SortedChannelCacheView<GuildChannel> getChannelCache() {
    return getGuild().getChannelCache();
  }

  /**
   * Whether this entity is detached.
   *
   * <p>If this returns {@code true},
   * this entity cannot be retrieved, will never be updated, and
   * most methods that would otherwise return a {@link RestAction RestAction}
   * will throw a {@link DetachedEntityException DetachedEntityException} instead.
   *
   * @return {@code True}, if the entity is detached
   */
  public static boolean isDetached() {
    return getGuild().isDetached();
  }

  /**
   * Retrieves a Custom Emoji together with its respective creator.
   *
   * <p>Note that {@link RichCustomEmoji#getOwner()} is only available if the currently
   * logged in account has {@link Permission#MANAGE_GUILD_EXPRESSIONS Permission.MANAGE_GUILD_EXPRESSIONS}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_EMOJI UNKNOWN_EMOJI}
   *     <br>If the provided id does not correspond to an emoji in this guild</li>
   * </ul>
   *
   * @param id The emoji id
   * @return {@link RestAction RestAction} - Type: {@link RichCustomEmoji}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<RichCustomEmoji> retrieveEmojiById(long id) {
    return getGuild().retrieveEmojiById(id);
  }

  /**
   * Retrieves and collects members of this guild into a list.
   * <br>This will use the configured {@link MemberCachePolicy MemberCachePolicy}
   * to decide which members to retain in cache.
   *
   * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param roles Collection of all roles the members must have
   * @return {@link Task} - Type: {@link List} of {@link Member}
   * @throws IllegalArgumentException If null is provided
   * @throws IllegalStateException    If the {@link GatewayIntent#GUILD_MEMBERS
   *                                  GatewayIntent.GUILD_MEMBERS} is not enabled
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @since 4.2.1
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> findMembersWithRoles(@NotNull Collection<Role> roles) {
    return getGuild().findMembersWithRoles(roles);
  }

  /**
   * Modifies the positional order of {@link Guild#getTextChannels() Guild.getTextChannels()}
   * using a specific {@link RestAction RestAction} extension to allow moving Channels
   * {@link OrderAction#moveUp(int) up}/{@link OrderAction#moveDown(int) down}
   * or {@link OrderAction#moveTo(int) to} a specific position.
   * <br>This uses <b>ascending</b> order with a 0 based index.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
   *     <br>One of the channels has been deleted before the completion of the task</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild</li>
   * </ul>
   *
   * @return {@link ChannelOrderAction ChannelOrderAction} - Type: {@link TextChannel TextChannel}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelOrderAction modifyTextChannelPositions() {
    return getGuild().modifyTextChannelPositions();
  }

  /**
   * Removes a time out from the specified Member in this {@link Guild Guild}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The time out cannot be removed due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} to timeout.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MODERATE_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot remove the timeout from the other Member
   * due to permission hierarchy position. (See {@link Member#canInteract(Member)})
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> removeTimeout(@NotNull UserSnowflake user) {
    return getGuild().removeTimeout(user);
  }

  /**
   * Returns an {@link ImageProxy} for this guild's icon.
   *
   * @return The {@link ImageProxy} of this guild's icon
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getIconUrl()
   */
  @Nullable
  public static ImageProxy getIcon() {
    return getGuild().getIcon();
  }

  /**
   * Gets a {@link GuildSticker GuildSticker} from this guild that has the same id as the
   * one provided.
   * <br>If there is no {@link GuildSticker GuildSticker} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * <p>This requires the {@link CacheFlag#STICKER CacheFlag.STICKER} to be enabled!
   *
   * @param id the sticker id
   * @return A Sticker matching the specified id
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveSticker(StickerSnowflake)
   */
  @Nullable
  public static GuildSticker getStickerById(@NotNull String id) {
    return getGuild().getStickerById(id);
  }

  /**
   * Modifies the complete {@link Role Role} set of the specified {@link Member Member}
   * <br>The provided roles will replace all current Roles of the specified Member.
   *
   * <p><u>The new roles <b>must not</b> contain the Public Role of the Guild</u>
   *
   * <p><b>Warning</b><br>
   * <b>This may <u>not</u> be used together with any other role add/remove/modify methods for the same Member
   * within one event listener cycle! The changes made by this require cache updates which are triggered by
   * lifecycle events which are received later. This may only be called again once the specific Member has been updated
   * by a {@link GenericGuildMemberEvent GenericGuildMemberEvent} targeting the same Member.</b>
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The target Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * <p><b>Example</b><br>
   * <pre>{@code
   * public static void makeModerator(Member member) {
   *     Guild guild = member.getGuild();
   *     List<Role> roles = new ArrayList<>(member.getRoles()); // modifiable copy
   *     List<Role> modRoles = guild.getRolesByName("moderator", true); // get roles with name "moderator"
   *     roles.addAll(modRoles); // add new roles
   *     // update the member with new roles
   *     guild.modifyMemberRoles(member, roles).queue();
   * }
   * }</pre>
   *
   * @param member A {@link Member Member} of which to override the Roles of
   * @param roles  New collection of {@link Role Roles} for the specified Member
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
   * @throws HierarchyException              If the provided roles are higher in the Guild's hierarchy
   *                                         and thus cannot be modified by the currently logged in account
   * @throws IllegalArgumentException          *
   * <li>If any of the provided arguments is {@code null}</li>
   *                                                                                                                            <li>If any of the provided arguments is not from this Guild</li>
   *                                                                                                                            <li>If any of the specified {@link Role Roles} is managed</li>
  <li>If any of the specified {@link Role Roles} is the {@codeof this Guild</li>
   *                                                                                                                        </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @see #modifyMemberRoles(Member, Collection)
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> modifyMemberRoles(@NotNull Member member, @NotNull Collection<Role> roles) {
    return getGuild().modifyMemberRoles(member, roles);
  }

  /**
   * Puts the specified Member in time out in this {@link Guild Guild} for a specific amount of time.
   * <br>While a Member is in time out, they cannot send messages, reply, react, or speak in voice channels.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be put into time out due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param user   The {@link UserSnowflake} to timeout.
   *               This can be a member or user instance or {@link User#fromId(long)}.
   * @param amount The amount of the provided {@link TimeUnit unit} to put the specified Member in time out for
   * @param unit   The {@link TimeUnit Unit} type of {@code amount}
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MODERATE_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot put a timeout on the other Member due to
   * permission hierarchy position. (See {@link Member#canInteract(Member)})
   * @throws IllegalArgumentException                                       If any of the following checks are true
   *                                                                        <ul>
   *                                                                            <li>The provided {@code user} is
   *                                                                            null</li>
   *                                                                            <li>The provided {@code amount} is
   *                                                                            lower than or equal to {@code 0}</li>
   *                                                                            <li>The provided {@code unit} is
   *                                                                            null</li>
   *                                                                            <li>The provided {@code amount} with
   *                                                                            the {@code unit} results in a date
   *                                                                            that is more than
   *                                                                            {@value Member#MAX_TIME_OUT_LENGTH}
   *                                                                            days in the future</li>
   *                                                                        </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> timeoutFor(@NotNull UserSnowflake user, long amount, @NotNull TimeUnit unit) {
    return getGuild().timeoutFor(user, amount, unit);
  }

  /**
   * Gets a list of all {@link TextChannel TextChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link TextChannel TextChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all TextChannels names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<TextChannel> getTextChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getTextChannelsByName(name, ignoreCase);
  }

  /**
   * Gets a list of {@link Member Members} that have all {@link Role Roles} provided.
   * <br>If there are no {@link Member Members} with all provided roles, then this returns an empty list.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @param roles The {@link Role Roles} that a {@link Member Member}
   *              must have to be included in the returned list.
   * @return Possibly-empty immutable list of Members with all provided Roles.
   * @throws IllegalArgumentException If a provided {@link Role Role} is from a
   *                                  different guild or null.
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see #findMembersWithRoles(Role...)
   */
  @Nonnull
  @Unmodifiable
  public static List<Member> getMembersWithRoles(@NotNull Role... roles) {
    return getGuild().getMembersWithRoles(roles);
  }

  /**
   * Retrieves a list of members.
   * <br>If the user does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the users resolve to a member, in which case an empty list will be the result.
   *
   * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
   * this will load the {@link OnlineStatus OnlineStatus} and {@link Activity Activities}
   * of the members. You can use {@link #retrieveMembers(boolean, Collection)} to disable presences.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param users The users of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            IDs</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembers(@NotNull Collection<? extends UserSnowflake> users) {
    return getGuild().retrieveMembers(users);
  }

  /**
   * Returns an {@link AutoModRuleManager}, which can be used to modify the rule for the provided id.
   * <p>The manager allows modifying multiple fields in a single request.
   * <br>You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue()
   * RestAction.queue()}.
   *
   * @param id
   * @return The manager instance
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AutoModRuleManager modifyAutoModRuleById(long id) {
    return getGuild().modifyAutoModRuleById(id);
  }

  /**
   * Creates a copy of the specified {@link GuildChannel GuildChannel}
   * in this {@link Guild Guild}.
   * <br>The provided channel need not be in the same Guild for this to work!
   *
   * <p>This copies the following elements:
   * <ol>
   *     <li>Name</li>
   *     <li>Parent Category (if present)</li>
   *     <li>Voice Elements (Bitrate, Userlimit)</li>
   *     <li>Text Elements (Topic, NSFW)</li>
   *     <li>All permission overrides for Members/Roles</li>
   * </ol>
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param channel The {@link GuildChannel GuildChannel} to use for the copy template
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new GuildChannel before creating it!
   * @throws IllegalArgumentException                                       If the provided channel is {@code null}
   * @throws InsufficientPermissionException If the currently logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} Permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @see #createTextChannel(String)
   * @see #createVoiceChannel(String)
   * @see ChannelAction ChannelAction
   * @since 3.1
   */
  @CheckReturnValue
  @Nonnull
  public static <T extends ICopyableChannel> ChannelAction<T> createCopyOfChannel(@NotNull T channel) {
    return getGuild().createCopyOfChannel(channel);
  }

  /**
   * Queries a list of members using a radix tree based on the provided name prefix.
   * <br>This will check both the username and the nickname of the members.
   * Additional filtering may be required. If no members with the specified prefix exist, the list will be empty.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param prefix The case-insensitive name prefix
   * @param limit  The max amount of members to retrieve (1-100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the provided prefix is null or
   *                                                                            empty.</li>
   *                                                                            <li>If the provided limit is not in
   *                                                                            the range of [1, 100]</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getMembersByName(String, boolean)
   * @see #getMembersByNickname(String, boolean)
   * @see #getMembersByEffectiveName(String, boolean)
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByPrefix(@NotNull String prefix, int limit) {
    return getGuild().retrieveMembersByPrefix(prefix, limit);
  }

  /**
   * Gets a list of all {@link GuildSticker GuildStickers} in this Guild that have the same
   * name as the one provided.
   * <br>If there are no {@link GuildSticker GuildStickers} with the provided name, then this returns an empty list.
   *
   * <p>This requires the {@link CacheFlag#STICKER CacheFlag.STICKER} to be enabled!
   *
   * @param name       The name used to filter the returned {@link GuildSticker GuildStickers}. Without colons.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all Stickers that match the provided name.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<GuildSticker> getStickersByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getStickersByName(name, ignoreCase);
  }

  /**
   * A {@link PaginationAction PaginationAction} implementation
   * that allows to {@link Iterable iterate} over all {@link AuditLogEntry AuditLogEntries} of
   * this Guild.
   * <br>This iterates from the most recent action to the first logged one. (Limit 90 days into history by discord api)
   *
   * <p><b>Examples</b><br>
   * <pre>{@code
   * public void logBan(GuildBanEvent event) {
   *     Guild guild = event.getGuild();
   *     List<TextChannel> modLog = guild.getTextChannelsByName("mod-log", true);
   *     guild.retrieveAuditLogs()
   *          .type(ActionType.BAN) // filter by type
   *          .limit(1)
   *          .queue(list -> {
   *             if (list.isEmpty()) return;
   *             AuditLogEntry entry = list.get(0);
   *             String message = String.format("%#s banned %#s with reason %s",
   *                                            entry.getUser(), event.getUser(), entry.getReason());
   *             modLog.forEach(channel ->
   *               channel.sendMessage(message).queue()
   *             );
   *          });
   * }
   * }</pre>
   *
   * @return {@link AuditLogPaginationAction AuditLogPaginationAction}
   * @throws InsufficientPermissionException If the currently logged in account
   *                                         does not have the permission {@link Permission#VIEW_AUDIT_LOGS
   *                                         VIEW_AUDIT_LOGS}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditLogPaginationAction retrieveAuditLogs() {
    return getGuild().retrieveAuditLogs();
  }

  /**
   * Puts the specified Member in time out in this {@link Guild Guild} for a specific amount of time.
   * <br>While a Member is in time out, all permissions except {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} and
   * {@link Permission#MESSAGE_HISTORY MESSAGE_HISTORY} are removed from them.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be put into time out due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param user     The {@link UserSnowflake} to timeout.
   *                 This can be a member or user instance or {@link User#fromId(long)}.
   * @param duration The duration to put the specified Member in time out for
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MODERATE_MEMBERS} permission.
   * @throws HierarchyException              If the logged in account cannot put a timeout on the other Member due to
   * permission hierarchy position.
   *                                                                        <br>See {@link Member#canInteract(Member)}
   * @throws IllegalArgumentException                                       If any of the following checks are true
   *                                                                        <ul>
   *                                                                            <li>The provided {@code user} is
   *                                                                            null</li>
   *                                                                            <li>The provided {@code duration} is
   *                                                                            null</li>
   *                                                                            <li>The provided {@code duration}
   *                                                                            results in a date that is more than
   *                                                                            {@value Member#MAX_TIME_OUT_LENGTH}
   *                                                                            days in the future</li>
   *                                                                        </ul>
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> timeoutFor(@NotNull UserSnowflake user, @NotNull Duration duration) {
    return getGuild().timeoutFor(user, duration);
  }

  /**
   * Creates a new {@link TextChannel TextChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the TextChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new TextChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<TextChannel> createTextChannel(@NotNull String name) {
    return getGuild().createTextChannel(name);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<NewsChannel> getNewsChannelCache() {
    return getGuild().getNewsChannelCache();
  }

  /**
   * Creates a new {@link Category Category} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the Category to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new Category before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException                                       If the provided name is {@code null},
   * blank, or longer than {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<Category> createCategory(@NotNull String name) {
    return getGuild().createCategory(name);
  }

  /**
   * Gets a list of all {@link Member Members} who have the same name as the one provided.
   * <br>This compares against {@link Member#getUser()}{@link User#getName() .getName()}
   * <br>If there are no {@link Member Members} with the provided name, then this returns an empty list.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @param name       The name used to filter the returned Members.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all Members with the same name as the name provided.
   * @throws IllegalArgumentException If the provided name is null
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @incubating This will be replaced in the future when the rollout of globally unique usernames has been completed.
   * @see #retrieveMembersByPrefix(String, int)
   */
  @Incubating
  @Nonnull
  @Unmodifiable
  public static List<Member> getMembersByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMembersByName(name, ignoreCase);
  }

  /**
   * Retrieves the {@link GuildWelcomeScreen welcome screen} for this Guild.
   * <br>The welcome screen is shown to all members after joining the Guild.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_GUILD_WELCOME_SCREEN Unknown Guild Welcome Screen}
   *     <br>The guild has no welcome screen</li>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS Missing Permissions}
   *     <br>The guild's welcome screen is disabled
   *     and the currently logged in account doesn't have the {@link Permission#MANAGE_SERVER MANAGE_SERVER}
   *     permission</li>
   * </ul>
   *
   * @return {@link RestAction} - Type: {@link GuildWelcomeScreen}
   * <br>The welcome screen for this Guild.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<GuildWelcomeScreen> retrieveWelcomeScreen() {
    return getGuild().retrieveWelcomeScreen();
  }

  /**
   * {@link SnowflakeCacheView SnowflakeCacheView} of {@link MediaChannel}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return {@link SnowflakeCacheView SnowflakeCacheView}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  public static SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
    return getGuild().getMediaChannelCache();
  }

  /**
   * Atomically removes the provided {@link Role Role} from the specified {@link Member Member}.
   * <br><b>This can be used together with other role modification methods as it does not require an updated cache!</b>
   *
   * <p>If multiple roles should be added/removed (efficiently) in one request
   * you may use {@link #modifyMemberRoles(Member, Collection, Collection) modifyMemberRoles(Member, Collection,
   * Collection)} or similar methods.
   *
   * <p>If the specified role is not present in the member's set of roles this does nothing.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The Members Roles could not be modified due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The target Member was removed from the Guild before finishing the task</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_ROLE UNKNOWN_ROLE}
   *     <br>If the specified Role does not exist</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} to change roles for.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @param role The role which should be removed atomically
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws IllegalArgumentException                                       <ul>
   *                                                                                    <li>If the specified member
   *                                                                                    or role are not from the
   *                                                                                    current Guild</li>
   *                                                                                    <li>Either member or role are
   *                                                                                    {@code null}</li>
   *                                                                                </ul>
   * @throws InsufficientPermissionException If the currently logged in account does not have
   * {@link Permission#MANAGE_ROLES Permission.MANAGE_ROLES}
   * @throws HierarchyException              If the provided roles are higher in the Guild's hierarchy
   *                                                                        and thus cannot be modified by the
   *                                                                        currently logged in account
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> removeRoleFromMember(@NotNull UserSnowflake user, @NotNull Role role) {
    return getGuild().removeRoleFromMember(user, role);
  }

  /**
   * Gets a list of all {@link NewsChannel NewsChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link NewsChannel NewsChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all NewsChannels names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<NewsChannel> getNewsChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getNewsChannelsByName(name, ignoreCase);
  }

  /**
   * Retrieves a custom emoji together with its respective creator.
   * <br><b>This does not include unicode emoji.</b>
   *
   * <p>Note that {@link RichCustomEmoji#getOwner()} is only available if the currently
   * logged in account has {@link Permission#MANAGE_GUILD_EXPRESSIONS Permission.MANAGE_GUILD_EXPRESSIONS}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_EMOJI UNKNOWN_EMOJI}
   *     <br>If the provided id does not correspond to an emoji in this guild</li>
   * </ul>
   *
   * @param id The emoji id
   * @return {@link RestAction RestAction} - Type: {@link RichCustomEmoji}
   * @throws IllegalArgumentException If the provided id is not a valid snowflake
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<RichCustomEmoji> retrieveEmojiById(@NotNull String id) {
    return getGuild().retrieveEmojiById(id);
  }

  /**
   * Creates a new {@link ScheduledEvent}.
   * Events created with this method will be of {@link ScheduledEvent.Type#EXTERNAL Type.EXTERNAL}.
   * These events are set to take place at an external location.
   *
   * <p><b>Requirements</b><br>
   * <p>
   * Events are required to have a name, location and start time.
   * Additionally, an end time <em>must</em> also be specified for events of {@link ScheduledEvent.Type#EXTERNAL Type
   * .EXTERNAL}.
   * {@link Permission#MANAGE_EVENTS} is required on the guild level in order to create this type of event.
   *
   * <p><b>Example</b><br>
   * <pre>{@code
   * guild.createScheduledEvent("Cactus Beauty Contest", "Mike's Backyard", OffsetDateTime.now().plusHours(1), OffsetDateTime.now().plusHours(3))
   *     .setDescription("Come and have your cacti judged! _Must be spikey to enter_")
   *     .queue();
   * }</pre>
   *
   * @param name      the name for this scheduled event, 1-100 characters
   * @param location  the external location for this scheduled event, 1-100 characters
   * @param startTime the start time for this scheduled event, can't be in the past or after the end time
   * @param endTime   the end time for this scheduled event, has to be later than the start time
   * @return {@link ScheduledEventAction}
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If a required parameter is {@code
   *                                                                            null} or empty</li>
   *                                                                            <li>If the start time is in the
   *                                                                            past</li>
   *                                                                            <li>If the end time is before the
   *                                                                            start time</li>
   *                                                                            <li>If the name is longer than 100
   *                                                                            characters</li>
   *                                                                            <li>If the description is longer than
   *                                                                            1000 characters</li>
   *                                                                            <li>If the location is longer than
   *                                                                            100 characters</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ScheduledEventAction createScheduledEvent(@NotNull String name, @NotNull String location,
                                                          @NotNull OffsetDateTime startTime,
                                                          @NotNull OffsetDateTime endTime) {
    return getGuild().createScheduledEvent(name, location, startTime, endTime);
  }

  /**
   * Shortcut for {@code guild.retrieveMemberById(guild.getOwnerIdLong())}.
   * <br>This will retrieve the current owner of the guild.
   * It is possible that the owner of a guild is no longer a registered discord user in which case this will fail.
   * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
   * and immediately provided if the member information is consistent. The cache consistency directly
   * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent
   * .GUILD_MEMBERS}
   * is required to keep the cache updated with the latest information. You can use
   * {@link CacheRestAction#useCache(boolean) useCache(false)} to always
   * make a new request, which is the default behavior if the required intents are disabled.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER}
   *     <br>The specified user is not a member of this guild</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_USER}
   *     <br>The specified user does not exist</li>
   * </ul>
   *
   * @return {@link RestAction} - Type: {@link Member}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #pruneMemberCache()
   * @see #unloadMember(long)
   * @see #getOwner()
   * @see #getOwnerIdLong()
   * @see #retrieveMemberById(long)
   */
  @CheckReturnValue
  @Nonnull
  public static CacheRestAction<Member> retrieveOwner() {
    return getGuild().retrieveOwner();
  }

  /**
   * Gets a list of all {@link VoiceChannel VoiceChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link VoiceChannel VoiceChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all VoiceChannel names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<VoiceChannel> getVoiceChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getVoiceChannelsByName(name, ignoreCase);
  }

  /**
   * The guild banner url.
   * <br>This is shown in guilds below the guild name.
   *
   * <p>The banner can be modified using {@link GuildManager#setBanner(Icon)}.
   *
   * @return The guild banner url or null
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.0.0
   */
  @Nullable
  public static String getBannerUrl() {
    return getGuild().getBannerUrl();
  }

  /**
   * Creates a new {@link NewsChannel NewsChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name   The name of the NewsChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @param parent The optional parent category for this channel, or null
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new NewsChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException        If the provided name is {@code null}, blank, or longer than
   * {@value Channel#MAX_NAME_LENGTH} characters;
   *                                         or the provided parent is not in the same guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<NewsChannel> createNewsChannel(@NotNull String name,
                                                             @org.jetbrains.annotations.Nullable Category parent) {
    return getGuild().createNewsChannel(name, parent);
  }

  /**
   * Delete the command for this id.
   *
   * <p>If there is no command with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param commandId The id of the command that should be deleted
   * @return {@link RestAction}
   * @throws IllegalArgumentException If the provided id is not a valid snowflake
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> deleteCommandById(@NotNull String commandId) {
    return getGuild().deleteCommandById(commandId);
  }

  /**
   * Gets the {@link Member Member} object of the currently logged in account in this guild.
   * <br>This is basically {@link JDA#getSelfUser()} being provided to {@link #getMember(UserSnowflake)}.
   *
   * @return The Member object of the currently logged in account.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static Member getSelfMember() {
    return getGuild().getSelfMember();
  }

  /**
   * The @everyone {@link Role Role} of this {@link Guild Guild}.
   * <br>This role is special because its {@link Role#getPosition() position} is calculated as
   * {@code -1}. All other role positions are 0 or greater. This implies that the public role is <b>always</b> below
   * any custom roles created in this Guild. Additionally, all members of this guild are implied to have this role so
   * it is not included in the list returned by {@link Member#getRoles() Member.getRoles()}.
   * <br>The ID of this Role is the Guild's ID thus it is equivalent to using {@link #getRoleById(long) getRoleById
   * (getIdLong())}.
   *
   * @return The @everyone {@link Role Role}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static Role getPublicRole() {
    return getGuild().getPublicRole();
  }

  /**
   * Load the member for the specified user.
   * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
   * and immediately provided if the member information is consistent. The cache consistency directly
   * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent
   * .GUILD_MEMBERS}
   * is required to keep the cache updated with the latest information. You can use
   * {@link CacheRestAction#useCache(boolean) useCache(false)} to always
   * make a new request, which is the default behavior if the required intents are disabled.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER}
   *     <br>The specified user is not a member of this guild</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_USER}
   *     <br>The specified user does not exist</li>
   * </ul>
   *
   * @param id The user id to load the member from
   * @return {@link RestAction} - Type: {@link Member}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #pruneMemberCache()
   * @see #unloadMember(long)
   */
  @CheckReturnValue
  @Nonnull
  public static CacheRestAction<Member> retrieveMemberById(long id) {
    return getGuild().retrieveMemberById(id);
  }

  /**
   * Bans up to 200 of the provided users.
   * <br>To set a ban reason, use {@link AuditableRestAction#reason(String)}.
   *
   * <p>The {@link BulkBanResponse} includes a list of {@link BulkBanResponse#getFailedUsers() failed users},
   * which is populated with users that could not be banned, for instance due to some internal server error or
   * permission issues.
   * This list of failed users also includes all users that were already banned.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be banned due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#FAILED_TO_BAN_USERS FAILED_TO_BAN_USERS}
   *     <br>None of the users could be banned</li>
   * </ul>
   *
   * @param users             The users to ban
   * @param deletionTimeframe The timeframe for the history of messages that will be deleted. (seconds precision)
   * @param unit              Timeframe unit as a {@link TimeUnit} (for example {@code ban(user, 7, TimeUnit.DAYS)}).
   * @return {@link AuditableRestAction} - Type: {@link BulkBanResponse}
   * @throws HierarchyException      If any of the provided users is the guild owner or has a higher or equal role
   * position
   * @throws InsufficientPermissionException                        If the bot does not have
   * {@link Permission#BAN_MEMBERS} or {@link Permission#MANAGE_SERVER}
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If null is provided</li>
   *                                                                            <li>If the deletionTimeframe is
   *                                                                            negative</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<BulkBanResponse> ban(@NotNull Collection<? extends UserSnowflake> users,
                                                         int deletionTimeframe, @NotNull TimeUnit unit) {
    return getGuild().ban(users, deletionTimeframe, unit);
  }

  /**
   * Modifies the positional order of {@link Guild#getVoiceChannels() Guild.getVoiceChannels()}
   * using a specific {@link RestAction RestAction} extension to allow moving Channels
   * {@link OrderAction#moveUp(int) up}/{@link OrderAction#moveDown(int) down}
   * or {@link OrderAction#moveTo(int) to} a specific position.
   * <br>This uses <b>ascending</b> order with a 0 based index.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
   *     <br>One of the channels has been deleted before the completion of the task</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild</li>
   * </ul>
   *
   * @return {@link ChannelOrderAction ChannelOrderAction} - Type: {@link VoiceChannel VoiceChannel}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelOrderAction modifyVoiceChannelPositions() {
    return getGuild().modifyVoiceChannelPositions();
  }

  /**
   * Looks up the role which is the integration role for the currently connected bot (self-user).
   * <br>These roles are created when the bot requested a list of permission in the authorization URL.
   *
   * <p>To check whether a role is a bot role you can use {@code role.getTags().isBot()} and you can use
   * {@link Role.RoleTags#getBotIdLong()} to check which bot it applies to.
   *
   * <p>This requires {@link CacheFlag#ROLE_TAGS CacheFlag.ROLE_TAGS} to be enabled.
   * See {@link JDABuilder#enableCache(CacheFlag, CacheFlag...) JDABuilder.enableCache(...)}.
   *
   * @return The bot role, or null if no role matches
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getBotRole() {
    return getGuild().getBotRole();
  }

  /**
   * Returns the {@link JDA JDA} instance of this Guild
   *
   * @return the corresponding JDA instance
   */
  @Nonnull
  public static JDA getJDA() {
    return getGuild().getJDA();
  }

  /**
   * The maximum amount of connected members this guild can have at a time.
   * <br>This includes members that are invisible but still connected to discord.
   * If too many members are online the guild will become unavailable for others.
   *
   * @return The maximum amount of connected members this guild can have
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #retrieveMetaData()
   * @since 4.0.0
   */
  public static int getMaxPresences() {
    return getGuild().getMaxPresences();
  }

  /**
   * Used to determine if the provided {@link UserSnowflake} is a member of this Guild.
   *
   * <p>This will only check cached members! If the cache is not loaded (see {@link #isLoaded()}), this may return
   * false despite the user being a member.
   * This is false when {@link #getMember(UserSnowflake)} returns {@code null}.
   *
   * @param user The user to check
   * @return True - if this user is present and cached in this guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  public static boolean isMember(@NotNull UserSnowflake user) {
    return getGuild().isMember(user);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<StageChannel> getStageChannelCache() {
    return getGuild().getStageChannelCache();
  }

  /**
   * Creates a new {@link GuildSticker} in this Guild.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#INVALID_FILE_UPLOADED INVALID_FILE_UPLOADED}
   *     <br>The sticker file asset is not in a supported file format</li>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The sticker could not be created due to a permission discrepancy</li>
   * </ul>
   *
   * @param name        The sticker name (2-30 characters)
   * @param description The sticker description (2-100 characters, or empty)
   * @param file        The sticker file containing the asset (png/apng/gif/lottie) with valid file extension (png,
   *                    gif, or json)
   * @param tag         The sticker tag used for suggestions (emoji or tag words)
   * @param tags        Additional tags to use for suggestions
   * @return {@link AuditableRestAction} - Type: {@link GuildSticker}
   * @throws InsufficientPermissionException                        If the currently logged in account does not have
   * the {@link Permission#MANAGE_GUILD_EXPRESSIONS MANAGE_GUILD_EXPRESSIONS} permission
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the name is not between 2 and
   *                                                                            30 characters long</li>
   *                                                                            <li>If the description is more than
   *                                                                            100 characters long or exactly 1
   *                                                                            character long</li>
   *                                                                            <li>If the asset file is null or of
   *                                                                            an invalid format (must be PNG, GIF,
   *                                                                            or LOTTIE)</li>
   *                                                                            <li>If anything is {@code null}</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<GuildSticker> createSticker(@NotNull String name, @NotNull String description,
                                                                @NotNull FileUpload file, @NotNull String tag,
                                                                @NotNull String... tags) {
    return getGuild().createSticker(name, description, file, tag, tags);
  }

  /**
   * Retrieves a list of members by their user id.
   * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
   *
   * <p>You can only load presences with the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent enabled.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param includePresence Whether to load presences of the members (online status/activity)
   * @param ids             The ids of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If includePresence is {@code true
   *                                                                            } and the GUILD_PRESENCES intent is
   *                                                                            disabled</li>
   *                                                                            <li>If the input contains null</li>
   *                                                                            <li>If the input is more than 100
   *                                                                            IDs</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByIds(boolean includePresence, @NotNull String... ids) {
    return getGuild().retrieveMembersByIds(includePresence, ids);
  }

  /**
   * The human readable name of the {@link Guild Guild}.
   * <p>
   * This value can be modified using {@link GuildManager#setName(String)}.
   *
   * @return Never-null String containing the Guild's name.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static String getName() {
    return getGuild().getName();
  }

  /**
   * Returns the default message Notification-Level of this Guild. Notification level determines when Members get
   * notification
   * for messages. The value returned is the default level set for any new Members that join the Guild.
   * <br>For a short description of the different values, see {@link NotificationLevel NotificationLevel}.
   * <p>
   * This value can be modified using {@link GuildManager#setDefaultNotificationLevel(NotificationLevel)}.
   *
   * @return The default message Notification-Level of this Guild.
   */
  @Nonnull
  public static NotificationLevel getDefaultNotificationLevel() {
    return getGuild().getDefaultNotificationLevel();
  }

  /**
   * Retrieves a list of members by their user id.
   * <br>If the id does not resolve to a member of this guild, then it will not appear in the resulting list.
   * It is possible that none of the IDs resolve to a member, in which case an empty list will be the result.
   *
   * <p>If the {@link GatewayIntent#GUILD_PRESENCES GUILD_PRESENCES} intent is enabled,
   * this will load the {@link OnlineStatus OnlineStatus} and {@link Activity Activities}
   * of the members. You can use {@link #retrieveMembersByIds(boolean, long...)} to disable presences.
   *
   * <p>The requests automatically timeout after {@code 10} seconds.
   * When the timeout occurs a {@link TimeoutException TimeoutException} will be used to complete exceptionally.
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param ids The ids of the members (max 100)
   * @return {@link Task} handle for the request
   * @throws IllegalArgumentException <ul>
   *                                  <li>If the input contains null</li>
   *                                  <li>If the input is more than 100 IDs</li>
   *                                  </ul>
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> retrieveMembersByIds(@NotNull long... ids) {
    return getGuild().retrieveMembersByIds(ids);
  }

  /**
   * The ID for the current owner of this guild.
   * <br>This is useful for debugging purposes or as a shortcut.
   *
   * @return The ID for the current owner
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getOwner()
   */
  public static long getOwnerIdLong() {
    return getGuild().getOwnerIdLong();
  }

  /**
   * Gets a list of all {@link ForumChannel ForumChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link ForumChannel ForumChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all ForumChannel names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<ForumChannel> getForumChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getForumChannelsByName(name, ignoreCase);
  }

  /**
   * Used to leave a Guild. If the currently logged in account is the owner of this guild ({@link Guild#getOwner()})
   * then ownership of the Guild needs to be transferred to a different {@link Member Member}
   * before leaving using {@link #transferOwnership(Member)}.
   *
   * @return {@link RestAction RestAction} - Type: {@link Void}
   * @throws IllegalStateException   Thrown if the currently logged in account is the Owner of this Guild.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> leave() {
    return getGuild().leave();
  }

  /**
   * Returns an {@link AutoModRuleManager}, which can be used to modify the rule for the provided id.
   * <p>The manager allows modifying multiple fields in a single request.
   * <br>You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue()
   * RestAction.queue()}.
   *
   * @param id
   * @return The manager instance
   * @throws InsufficientPermissionException                        If the currently logged in account does not have
   * the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AutoModRuleManager modifyAutoModRuleById(@NotNull String id) {
    return getGuild().modifyAutoModRuleById(id);
  }

  /**
   * The URL of the splash image for this Guild. A Splash image is an image displayed when viewing a
   * Discord Guild Invite on the web or in client just before accepting or declining the invite.
   * If no splash has been set, this returns {@code null}.
   * <br>Splash images are VIP/Partner Guild only.
   * <p>
   * The Guild splash can be modified using {@link GuildManager#setSplash(Icon)}.
   *
   * @return Possibly-null String containing the Guild's splash URL.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static String getSplashUrl() {
    return getGuild().getSplashUrl();
  }

  /**
   * Gets a list of all {@link ScheduledEvent ScheduledEvents} in this Guild that have the same
   * name as the one provided.
   * <br>If there are no {@link ScheduledEvent ScheduledEvents} with the provided name,
   * then this returns an empty list.
   *
   * <p>This requires {@link CacheFlag#SCHEDULED_EVENTS} to be enabled.
   *
   * @param name       The name used to filter the returned {@link ScheduledEvent} objects.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all ScheduledEvent names that match the provided name.
   * @throws IllegalArgumentException If the name is blank, empty or {@code null}
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<ScheduledEvent> getScheduledEventsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getScheduledEventsByName(name, ignoreCase);
  }

  /**
   * Looks up a role which is the integration role for a bot.
   * <br>These roles are created when the bot requested a list of permission in the authorization URL.
   *
   * <p>To check whether a role is a bot role you can use {@code role.getTags().isBot()} and you can use
   * {@link Role.RoleTags#getBotIdLong()} to check which bot it applies to.
   *
   * <p>This requires {@link CacheFlag#ROLE_TAGS CacheFlag.ROLE_TAGS} to be enabled.
   * See {@link JDABuilder#enableCache(CacheFlag, CacheFlag...) JDABuilder.enableCache(...)}.
   *
   * @param userId The user id of the bot
   * @return The bot role, or null if no role matches
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getRoleByBot(long userId) {
    return getGuild().getRoleByBot(userId);
  }

  /**
   * Modifies the positional order of {@link Category#getTextChannels() Category#getTextChannels()}
   * using an extension of {@link ChannelOrderAction ChannelOrderAction}
   * specialized for ordering the nested {@link TextChannel TextChannels} of this
   * {@link Category Category}.
   * <br>Like {@code ChannelOrderAction}, the returned {@link CategoryOrderAction CategoryOrderAction}
   * can be used to move TextChannels {@link OrderAction#moveUp(int) up},
   * {@link OrderAction#moveDown(int) down}, or
   * {@link OrderAction#moveTo(int) to} a specific position.
   * <br>This uses <b>ascending</b> order with a 0 based index.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNNKOWN_CHANNEL}
   *     <br>One of the channels has been deleted before the completion of the task.</li>
   *
   *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
   *     <br>The currently logged in account was removed from the Guild.</li>
   * </ul>
   *
   * @param category The {@link Category Category} to order
   *                 {@link TextChannel TextChannels} from.
   * @return {@link CategoryOrderAction CategoryOrderAction} - Type: {@link TextChannel TextChannel}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static CategoryOrderAction modifyTextChannelPositions(@NotNull Category category) {
    return getGuild().modifyTextChannelPositions(category);
  }

  /**
   * Retrieves an immutable list of the currently banned {@link User Users}.
   * <br>If you wish to ban or unban a user, use either {@link #ban(UserSnowflake, int, TimeUnit)} or
   * {@link #unban(UserSnowflake)}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
   * </ul>
   *
   * @return The {@link BanPaginationAction BanPaginationAction} of the guild's bans.
   * @throws InsufficientPermissionException If the logged in account does not have the
   *                                         {@link Permission#BAN_MEMBERS} permission.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static BanPaginationAction retrieveBanList() {
    return getGuild().retrieveBanList();
  }

  /**
   * Load the member for the specified user.
   * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
   * and immediately provided if the member information is consistent. The cache consistency directly
   * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent
   * .GUILD_MEMBERS}
   * is required to keep the cache updated with the latest information. You can use
   * {@link CacheRestAction#useCache(boolean) useCache(false)} to always
   * make a new request, which is the default behavior if the required intents are disabled.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER}
   *     <br>The specified user is not a member of this guild</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_USER}
   *     <br>The specified user does not exist</li>
   * </ul>
   *
   * @param id The user id to load the member from
   * @return {@link RestAction} - Type: {@link Member}
   * @throws IllegalArgumentException                               If the provided id is empty or null
   * @throws NumberFormatException                                  If the provided id is not a snowflake
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #pruneMemberCache()
   * @see #unloadMember(long)
   */
  @CheckReturnValue
  @Nonnull
  public static CacheRestAction<Member> retrieveMemberById(@NotNull String id) {
    return getGuild().retrieveMemberById(id);
  }

  /**
   * Provides the {@link TextChannel TextChannel} that lists the rules of the guild.
   * <br>If this guild doesn't have the COMMUNITY {@link #getFeatures() feature}, this returns {@code null}.
   *
   * @return Possibly-null {@link TextChannel TextChannel} that is the rules channel
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getFeatures()
   */
  @Nullable
  public static TextChannel getRulesChannel() {
    return getGuild().getRulesChannel();
  }

  /**
   * Creates a new {@link VoiceChannel VoiceChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name   The name of the VoiceChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @param parent The optional parent category for this channel, or null
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new VoiceChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException        If the provided name is {@code null}, blank, or longer than
   * {@value Channel#MAX_NAME_LENGTH} characters;
   *                                         or the provided parent is not in the same guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<VoiceChannel> createVoiceChannel(@NotNull String name,
                                                               @org.jetbrains.annotations.Nullable Category parent) {
    return getGuild().createVoiceChannel(name, parent);
  }

  /**
   * Creates or updates a command.
   * <br>If a command with the same name exists, it will be replaced.
   * This operation is idempotent.
   * Commands will persist between restarts of your bot, you only have to create a command once.
   *
   * <p>To specify a complete list of all commands you can use {@link #updateCommands()} instead.
   *
   * <p>You need the OAuth2 scope {@code "applications.commands"} in order to add commands to a guild.
   *
   * @param command The {@link CommandData} for the command
   * @return {@link RestAction} - Type: {@link Command}
   * <br>The RestAction used to create or update the command
   * @throws IllegalArgumentException If null is provided
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see Commands#slash(String, String) Commands.slash(...)
   * @see Commands#message(String) Commands.message(...)
   * @see Commands#user(String) Commands.user(...)
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Command> upsertCommand(@NotNull CommandData command) {
    return getGuild().upsertCommand(command);
  }

  /**
   * Looks up a role which is the integration role for a bot.
   * <br>These roles are created when the bot requested a list of permission in the authorization URL.
   *
   * <p>To check whether a role is a bot role you can use {@code role.getTags().isBot()} and you can use
   * {@link Role.RoleTags#getBotIdLong()} to check which bot it applies to.
   *
   * <p>This requires {@link CacheFlag#ROLE_TAGS CacheFlag.ROLE_TAGS} to be enabled.
   * See {@link JDABuilder#enableCache(CacheFlag, CacheFlag...) JDABuilder.enableCache(...)}.
   *
   * @param userId The user id of the bot
   * @return The bot role, or null if no role matches
   * @throws IllegalArgumentException If the userId is null or not a valid snowflake
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getRoleByBot(@NotNull String userId) {
    return getGuild().getRoleByBot(userId);
  }

  /**
   * Retrieves the existing {@link Command} instance by id.
   *
   * <p>If there is no command with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param id The command id
   * @return {@link RestAction} - Type: {@link Command}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Command> retrieveCommandById(long id) {
    return getGuild().retrieveCommandById(id);
  }

  /**
   * Populated list of {@link GuildChannel channels} for this guild.
   * <br>This includes all types of channels, except for threads.
   * <br>This includes hidden channels by default,
   * you can use {@link #getChannels(boolean) getChannels(false)} to exclude hidden channels.
   *
   * <p>The returned list is ordered in the same fashion as it would be by the official discord client.
   * <ol>
   *     <li>TextChannel, ForumChannel, and NewsChannel without parent</li>
   *     <li>VoiceChannel and StageChannel without parent</li>
   *     <li>Categories
   *         <ol>
   *             <li>TextChannel, ForumChannel, and NewsChannel with category as parent</li>
   *             <li>VoiceChannel and StageChannel with category as parent</li>
   *         </ol>
   *     </li>
   * </ol>
   *
   * @return Immutable list of channels for this guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getChannels(boolean)
   */
  @Nonnull
  @Unmodifiable
  public static List<GuildChannel> getChannels() {
    return getGuild().getChannels();
  }

  /**
   * Looks up a role which is the integration role for a bot.
   * <br>These roles are created when the bot requested a list of permission in the authorization URL.
   *
   * <p>To check whether a role is a bot role you can use {@code role.getTags().isBot()} and you can use
   * {@link Role.RoleTags#getBotIdLong()} to check which bot it applies to.
   *
   * <p>This requires {@link CacheFlag#ROLE_TAGS CacheFlag.ROLE_TAGS} to be enabled.
   * See {@link JDABuilder#enableCache(CacheFlag, CacheFlag...) JDABuilder.enableCache(...)}.
   *
   * @param user The bot user
   * @return The bot role, or null if no role matches
   * @throws IllegalArgumentException If null is provided
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getRoleByBot(@NotNull User user) {
    return getGuild().getRoleByBot(user);
  }

  /**
   * The method calculates the amount of Members that would be pruned if {@link #prune(int, Role...)} was executed.
   * Prunability is determined by a Member being offline for at least <i>days</i> days.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The prune count cannot be fetched due to a permission discrepancy</li>
   * </ul>
   *
   * @param days Minimum number of days since a member has been offline to get affected.
   * @return {@link RestAction RestAction} - Type: Integer
   * <br>The amount of Members that would be affected.
   * @throws InsufficientPermissionException If the account doesn't have {@link Permission#KICK_MEMBERS KICK_MEMBER}
   * Permission.
   * @throws IllegalArgumentException        If the provided days are less than {@code 1} or more than {@code 30}
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Integer> retrievePrunableMemberCount(int days) {
    return getGuild().retrievePrunableMemberCount(days);
  }

  /**
   * The Features of the {@link Guild Guild}.
   *
   * <p>Features can be updated using {@link GuildManager#setFeatures(Collection)}.
   *
   * @return Never-null, unmodifiable Set containing all of the Guild's features.
   * @see
   * <a target="_blank" href="https://discord.com/developers/docs/resources/guild#guild-object-guild-features">List of Features</a>
   */
  @Nonnull
  @Unmodifiable
  public static Set<String> getFeatures() {
    return getGuild().getFeatures();
  }

  /**
   * Retrieves and collects members of this guild into a list.
   * <br>This will use the configured {@link MemberCachePolicy MemberCachePolicy}
   * to decide which members to retain in cache.
   *
   * <p><b>This requires the privileged GatewayIntent.GUILD_MEMBERS to be enabled!</b>
   *
   * <p><b>You MUST NOT use blocking operations such as {@link Task#get()}!</b>
   * The response handling happens on the event thread by default.
   *
   * @param filter Filter to decide which members to include
   * @return {@link Task} - Type: {@link List} of {@link Member}
   * @throws IllegalArgumentException                               If the provided filter is null
   * @throws IllegalStateException                                  If the {@link GatewayIntent#GUILD_MEMBERS
   * GatewayIntent.GUILD_MEMBERS} is not enabled
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static Task<List<Member>> findMembers(@NotNull Predicate<? super Member> filter) {
    return getGuild().findMembers(filter);
  }

  /**
   * Load the member's voice state for the specified {@link UserSnowflake}.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_VOICE_STATE}
   *     <br>The specified user does not exist, is not a member of this guild or is not connected to a voice
   *     channel</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} for the member's voice state to retrieve.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @return {@link RestAction} - Type: {@link GuildVoiceState}
   * @throws IllegalArgumentException If provided with null
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<GuildVoiceState> retrieveMemberVoiceState(@NotNull UserSnowflake user) {
    return getGuild().retrieveMemberVoiceState(user);
  }

  /**
   * Sets the Guild Muted state of the {@link Member Member} based on the provided
   * boolean.
   *
   * <p><b>Note:</b> The Member's {@link GuildVoiceState#isGuildMuted() GuildVoiceState.isGuildMuted()} value won't
   * change
   * until JDA receives the {@link GuildVoiceGuildMuteEvent GuildVoiceGuildMuteEvent} event related to this change.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be muted due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The specified Member was removed from the Guild before finishing the task</li>
   *
   *     <li>{@link ErrorResponse#USER_NOT_CONNECTED USER_NOT_CONNECTED}
   *     <br>The specified Member is not connected to a voice channel</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} who's {@link GuildVoiceState} to change.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @param mute Whether this {@link Member Member} should be muted or unmuted.
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#VOICE_DEAF_OTHERS} permission
   *                                                                        in the given channel.
   * @throws IllegalArgumentException                                       If the provided user is null.
   * @throws IllegalStateException                                          If the provided user is not currently
   * connected to a voice channel.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> mute(@NotNull UserSnowflake user, boolean mute) {
    return getGuild().mute(user, mute);
  }

  /**
   * Edit an existing command by id.
   *
   * <p>If there is no command with the provided ID,
   * this RestAction fails with {@link ErrorResponse#UNKNOWN_COMMAND ErrorResponse.UNKNOWN_COMMAND}
   *
   * @param id The id of the command to edit
   * @return {@link CommandEditAction} used to edit the command
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static CommandEditAction editCommandById(long id) {
    return getGuild().editCommandById(id);
  }

  /**
   * Unbans the specified {@link UserSnowflake} from this Guild.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The target Member cannot be unbanned due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_USER UNKNOWN_USER}
   *     <br>The specified User does not exist</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} to unban.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#BAN_MEMBERS} permission.
   * @throws IllegalArgumentException                                       If the provided user is null
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> unban(@NotNull UserSnowflake user) {
    return getGuild().unban(user);
  }

  /**
   * Retrieves the Vanity Invite meta data for this guild.
   * <br>This allows you to inspect how many times the vanity invite has been used.
   * You can use {@link #getVanityUrl()} if you only care about the invite.
   *
   * <p>This action requires the {@link Permission#MANAGE_SERVER MANAGE_SERVER} permission.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#INVITE_CODE_INVALID INVITE_CODE_INVALID}
   *     <br>If this guild does not have a vanity invite</li>
   *
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The vanity invite cannot be fetched due to a permission discrepancy</li>
   * </ul>
   *
   * @return {@link RestAction} - Type: {@link VanityInvite}
   * @throws InsufficientPermissionException                        If the currently logged in account does not have
   * {@link Permission#MANAGE_SERVER Permission.MANAGE_SERVER}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.2.1
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<VanityInvite> retrieveVanityInvite() {
    return getGuild().retrieveVanityInvite();
  }

  /**
   * Configures the complete list of guild commands.
   * <br>This will replace the existing command list for this guild. You should only use this at most once on startup!
   *
   * <p>This operation is idempotent.
   * Commands will persist between restarts of your bot, you only have to create a command once.
   *
   * <p>You need the OAuth2 scope {@code "applications.commands"} in order to add commands to a guild.
   *
   * <p><b>Examples</b>
   *
   * <p>Set list to 2 commands:
   * <pre>{@code
   * guild.updateCommands()
   *   .addCommands(Commands.slash("ping", "Gives the current ping"))
   *   .addCommands(Commands.slash("ban", "Ban the target user")
   *     .addOption(OptionType.USER, "user", "The user to ban", true))
   *     .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
   *   .queue();
   * }</pre>
   *
   * <p>Delete all commands:
   * <pre>{@code
   * guild.updateCommands().queue();
   * }</pre>
   *
   * @return {@link CommandListUpdateAction}
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see JDA#updateCommands()
   */
  @CheckReturnValue
  @Nonnull
  public static CommandListUpdateAction updateCommands() {
    return getGuild().updateCommands();
  }

  /**
   * Searches for a {@link Member} that has the matching Discord Tag.
   * <br>Format has to be in the form {@code Username#Discriminator} where the
   * username must be between 2 and 32 characters (inclusive) matching the exact casing and the discriminator
   * must be exactly 4 digits.
   * <br>This does not check the {@link Member#getNickname() nickname} of the member
   * but the username.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * <p>This only checks users that are in this guild. If a user exists
   * with the tag that is not available in the {@link #getMemberCache() Member-Cache} it will not be detected.
   * <br>Currently Discord does not offer a way to retrieve a user by their discord tag.
   *
   * @param username      The name of the user
   * @param discriminator The discriminator of the user
   * @return The {@link Member} for the discord tag or null if no member has the provided tag
   * @throws IllegalArgumentException If the provided arguments are null or not in the
   *                                  described format
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see #getMemberByTag(String)
   */
  @Nullable
  public static Member getMemberByTag(@NotNull String username, @NotNull String discriminator) {
    return getGuild().getMemberByTag(username, discriminator);
  }

  /**
   * Get {@link GuildChannel GuildChannel} for the provided ID.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * <br>This is meant for systems that use a dynamic {@link ChannelType} and can
   * profit from a simple function to get the channel instance.
   *
   * <p>To get more specific channel types you can use one of the following:
   * <ul>
   *     <li>{@link #getChannelById(Class, String)}</li>
   *     <li>{@link #getTextChannelById(String)}</li>
   *     <li>{@link #getNewsChannelById(String)}</li>
   *     <li>{@link #getStageChannelById(String)}</li>
   *     <li>{@link #getVoiceChannelById(String)}</li>
   *     <li>{@link #getCategoryById(String)}</li>
   *     <li>{@link #getForumChannelById(String)}</li>
   * </ul>
   *
   * @param type The {@link ChannelType}
   * @param id   The ID of the channel
   * @return The GuildChannel or null
   * @throws IllegalArgumentException If the provided ID is null
   * @throws NumberFormatException    If the provided ID is not a snowflake
   * @throws DetachedEntityException  If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static GuildChannel getGuildChannelById(@NotNull ChannelType type, @NotNull String id) {
    return getGuild().getGuildChannelById(type, id);
  }

  /**
   * Load the member for the specified {@link UserSnowflake}.
   * <br>If the member is already loaded it will be retrieved from {@link #getMemberById(long)}
   * and immediately provided if the member information is consistent. The cache consistency directly
   * relies on the enabled {@link GatewayIntent GatewayIntents} as {@link GatewayIntent#GUILD_MEMBERS GatewayIntent
   * .GUILD_MEMBERS}
   * is required to keep the cache updated with the latest information. You can use
   * {@link CacheRestAction#useCache(boolean) useCache(false)} to always
   * make a new request, which is the default behavior if the required intents are disabled.
   *
   * <p>Possible {@link ErrorResponseException ErrorResponseExceptions} include:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER}
   *     <br>The specified user is not a member of this guild</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_USER}
   *     <br>The specified user does not exist</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} for the member to retrieve.
   *             This can be a member or user instance or {@link User#fromId(long)}.
   * @return {@link RestAction} - Type: {@link Member}
   * @throws IllegalArgumentException If provided with null
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see #pruneMemberCache()
   * @see #unloadMember(long)
   */
  @CheckReturnValue
  @Nonnull
  public static CacheRestAction<Member> retrieveMember(@NotNull UserSnowflake user) {
    return getGuild().retrieveMember(user);
  }

  /**
   * Creates a new {@link GuildSticker} in this Guild.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} include:
   * <ul>
   *     <li>{@link ErrorResponse#INVALID_FILE_UPLOADED INVALID_FILE_UPLOADED}
   *     <br>The sticker file asset is not in a supported file format</li>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The sticker could not be created due to a permission discrepancy</li>
   * </ul>
   *
   * @param name        The sticker name (2-30 characters)
   * @param description The sticker description (2-100 characters, or empty)
   * @param file        The sticker file containing the asset (png/apng/gif/lottie) with valid file extension (png,
   *                    gif, or json)
   * @param tags        The tags to use for auto-suggestions (Up to 200 characters in total)
   * @return {@link AuditableRestAction} - Type: {@link GuildSticker}
   * @throws InsufficientPermissionException                        If the currently logged in account does not have
   * the {@link Permission#MANAGE_GUILD_EXPRESSIONS MANAGE_GUILD_EXPRESSIONS} permission
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the name is not between 2 and
   *                                                                            30 characters long</li>
   *                                                                            <li>If the description is more than
   *                                                                            100 characters long or exactly 1
   *                                                                            character long</li>
   *                                                                            <li>If the asset file is null or of
   *                                                                            an invalid format (must be PNG, GIF,
   *                                                                            or LOTTIE)</li>
   *                                                                            <li>If anything is {@code null}</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<GuildSticker> createSticker(@NotNull String name, @NotNull String description,
                                                                @NotNull FileUpload file,
                                                                @NotNull Collection<String> tags) {
    return getGuild().createSticker(name, description, file, tags);
  }

  /**
   * Gets all {@link Role Roles} in this {@link Guild Guild}.
   * <br>The roles returned will be sorted according to their position. The highest role being at index 0
   * and the lowest at the last index.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getRoleCache()} and use its more efficient
   * versions of handling these values.
   *
   * @return An immutable List of {@link Role Roles}.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<Role> getRoles() {
    return getGuild().getRoles();
  }

  /**
   * Sorted list of {@link Member Members} that boost this guild.
   * <br>The list is sorted by {@link Member#getTimeBoosted()} ascending.
   * This means the first element will be the member who has been boosting for the longest time.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @return Immutable list of members who boost this guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<Member> getBoosters() {
    return getGuild().getBoosters();
  }

  /**
   * Gets a {@link StageChannel StageChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link StageChannel StageChannel}.
   * @return Possibly-null {@link StageChannel StageChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static StageChannel getStageChannelById(long id) {
    return getGuild().getStageChannelById(id);
  }

  /**
   * Gets a list of all {@link MediaChannel MediaChannels}
   * in this Guild that have the same name as the one provided.
   * <br>If there are no channels with the provided name, then this returns an empty list.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param name       The name used to filter the returned {@link MediaChannel MediaChannels}.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all ForumChannel names that match the provided name.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<MediaChannel> getMediaChannelsByName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMediaChannelsByName(name, ignoreCase);
  }

  /**
   * Re-apply the {@link MemberCachePolicy MemberCachePolicy} of this session to all {@link Member Members} of this
   * Guild.
   *
   * <p><b>Example</b><br>
   * <pre>{@code
   * // Check if the members of this guild have at least 50% bots (bot collection/farm)
   * public void checkBots(Guild guild) {
   *     // Keep in mind: This requires the GUILD_MEMBERS intent which is disabled in createDefault and createLight by default
   *     guild.retrieveMembers() // Load members CompletableFuture<Void> (async and eager)
   *          .thenApply((v) -> guild.getMemberCache()) // Turn into CompletableFuture<MemberCacheView>
   *          .thenAccept((members) -> {
   *              int total = members.size();
   *              // Casting to double to get a double as result of division, don't need to worry about precision with small counts like this
   *              double bots = (double) members.applyStream(stream ->
   *                  stream.map(Member::getUser)
   *                        .filter(User::isBot)
   *                        .count()); // Count bots
   *              if (bots / total > 0.5) // Check how many members are bots
   *                  System.out.println("More than 50% of members in this guild are bots");
   *          })
   *          .thenRun(guild::pruneMemberCache); // Then prune the cache
   * }
   * }</pre>
   *
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #unloadMember(long)
   * @see JDA#unloadUser(long)
   */
  public static void pruneMemberCache() {
    getGuild().pruneMemberCache();
  }

  /**
   * Creates a new {@link RichCustomEmoji} in this Guild.
   * <br>If one or more Roles are specified the new emoji will only be available to Members with any of the specified
   * Roles (see {@link Member#canInteract(RichCustomEmoji)})
   * <br>For this to be successful, the logged in account has to have the {@link Permission#MANAGE_GUILD_EXPRESSIONS
   * MANAGE_GUILD_EXPRESSIONS} Permission.
   *
   * <p><b><u>Unicode emojis are not included as {@link RichCustomEmoji}!</u></b>
   *
   * <p>Note that a guild is limited to 50 normal and 50 animated emojis by default.
   * Some guilds are able to add additional emojis beyond this limitation due to the
   * {@code MORE_EMOJI} feature (see {@link Guild#getFeatures() Guild.getFeatures()}).
   * <br>Due to simplicity we do not check for these limits.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The emoji could not be created due to a permission discrepancy</li>
   * </ul>
   *
   * @param name  The name for the new emoji
   * @param icon  The {@link Icon} for the new emoji
   * @param roles The {@link Role Roles} the new emoji should be restricted to
   *              <br>If no roles are provided the emoji will be available to all Members of this Guild
   * @return {@link AuditableRestAction AuditableRestAction} - Type: {@link RichCustomEmoji}
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_GUILD_EXPRESSIONS MANAGE_GUILD_EXPRESSIONS} Permission
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<RichCustomEmoji> createEmoji(@NotNull String name, @NotNull Icon icon,
                                                                 @NotNull Role... roles) {
    return getGuild().createEmoji(name, icon, roles);
  }

  @Nonnull
  public static SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
    return getGuild().getVoiceChannelCache();
  }

  /**
   * Used to completely delete a Guild. This can only be done if the currently logged in account is the owner of the
   * Guild.
   * <br>If the account has MFA enabled, use {@link #delete(String)} instead to provide the MFA code.
   *
   * @return {@link RestAction} - Type: {@link Void}
   * @throws PermissionException     Thrown if the currently logged in account is not the owner of this Guild.
   * @throws IllegalStateException   If the currently logged in account has MFA
   *                                 enabled. ({@link SelfUser#isMfaEnabled()}).
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Void> delete() {
    return getGuild().delete();
  }

  /**
   * Gets all {@link StageChannel StageChannels} in the cache.
   * <br>In {@link Guild} cache, channels are sorted according to their position and id.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getStageChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of {@link StageChannel StageChannels}.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<StageChannel> getStageChannels() {
    return getGuild().getStageChannels();
  }

  /**
   * Transfers the Guild ownership to the specified {@link Member Member}
   * <br>Only available if the currently logged in account is the owner of this Guild
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The currently logged in account lost ownership before completing the task</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_MEMBER UNKNOWN_MEMBER}
   *     <br>The target Member was removed from the Guild before finishing the task</li>
   * </ul>
   *
   * @param newOwner Not-null Member to transfer ownership to
   * @return {@link AuditableRestAction AuditableRestAction}
   * @throws PermissionException     If the currently logged in account is not the owner of this Guild
   * @throws IllegalArgumentException                               <ul>
   *                                                                            <li>If the specified Member is {@code
   *                                                                            null} or not from the same Guild</li>
   *                                                                            <li>If the specified Member already
   *                                                                            is the Guild owner</li>
   *                                                                        </ul>
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> transferOwnership(@NotNull Member newOwner) {
    return getGuild().transferOwnership(newOwner);
  }

  /**
   * Get {@link GuildChannel GuildChannel} for the provided ID.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * <p>To get more specific channel types you can use one of the following:
   * <ul>
   *     <li>{@link #getChannelById(Class, long)}</li>
   *     <li>{@link #getTextChannelById(long)}</li>
   *     <li>{@link #getNewsChannelById(long)}</li>
   *     <li>{@link #getStageChannelById(long)}</li>
   *     <li>{@link #getVoiceChannelById(long)}</li>
   *     <li>{@link #getCategoryById(long)}</li>
   *     <li>{@link #getForumChannelById(long)}</li>
   * </ul>
   *
   * @param id The ID of the channel
   * @return The GuildChannel or null
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static GuildChannel getGuildChannelById(long id) {
    return getGuild().getGuildChannelById(id);
  }

  /**
   * Retrieves a {@link Ban Ban} of the provided {@link UserSnowflake}.
   * <br>If you wish to ban or unban a user, use either {@link #ban(UserSnowflake, int, TimeUnit)} or
   * {@link #unban(UserSnowflake)}.
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The ban list cannot be fetched due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#UNKNOWN_BAN UNKNOWN_BAN}
   *     <br>Either the ban was removed before finishing the task or it did not exist in the first place</li>
   * </ul>
   *
   * @param user The {@link UserSnowflake} for the banned user.
   *             This can be a user instance or {@link User#fromId(long)}.
   * @return {@link RestAction RestAction} - Type: {@link Ban Ban}
   * <br>An unmodifiable ban object for the user banned from this guild
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#BAN_MEMBERS} permission.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<Ban> retrieveBan(@NotNull UserSnowflake user) {
    return getGuild().retrieveBan(user);
  }

  /**
   * Returns an {@link ImageProxy} for this guild's banner image.
   *
   * @return Possibly-null {@link ImageProxy} of this guild's banner image
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see #getBannerUrl()
   */
  @Nullable
  public static ImageProxy getBanner() {
    return getGuild().getBanner();
  }

  /**
   * The {@link AudioManager AudioManager} that represents the
   * audio connection for this Guild.
   * <br>If no AudioManager exists for this Guild, this will create a new one.
   * <br>This operation is synchronized on all audio managers for this JDA instance,
   * this means that calling getAudioManager() on any other guild while a thread is accessing this method may be locked.
   *
   * @return The AudioManager for this Guild.
   * @throws IllegalStateException   If {@link GatewayIntent#GUILD_VOICE_STATES} is
   *                                 disabled
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @see JDA#getAudioManagerCache() JDA.getAudioManagerCache()
   */
  @Nonnull
  public static AudioManager getAudioManager() {
    return getGuild().getAudioManager();
  }

  /**
   * Returns the NSFW Level that this guild is classified with.
   * <br>For a short description of the different values, see {@link NSFWLevel NSFWLevel}.
   * <p>
   * This value can only be modified by Discord after reviewing the Guild.
   *
   * @return The NSFWLevel of this guild.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nonnull
  public static NSFWLevel getNSFWLevel() {
    return getGuild().getNSFWLevel();
  }

  /**
   * Gets all {@link ThreadChannel ThreadChannel} in the cache.
   *
   * <p>These threads can also represent posts in {@link ForumChannel ForumChannels}.
   *
   * <p>This copies the backing store into a list. This means every call
   * creates a new list with O(n) complexity. It is recommended to store this into
   * a local variable or use {@link #getThreadChannelCache()} and use its more efficient
   * versions of handling these values.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @return An immutable List of {@link ThreadChannel ThreadChannels}.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nonnull
  @Unmodifiable
  public static List<ThreadChannel> getThreadChannels() {
    return getGuild().getThreadChannels();
  }

  /**
   * Gets a {@link StageChannel StageChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link StageChannel StageChannel}.
   * @return Possibly-null {@link StageChannel StageChannel} with matching id.
   * @throws NumberFormatException   If the provided {@code id} cannot be parsed by
   *                                 {@link Long#parseLong(String)}
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static StageChannel getStageChannelById(@NotNull String id) {
    return getGuild().getStageChannelById(id);
  }

  /**
   * Gets a list of all {@link Member Members} who have the same effective name as the one provided.
   * <br>This compares against {@link Member#getEffectiveName()}.
   * <br>If there are no {@link Member Members} with the provided name, then this returns an empty list.
   *
   * <p>This will only check cached members!
   * <br>See {@link MemberCachePolicy MemberCachePolicy}
   *
   * @param name       The name used to filter the returned Members.
   * @param ignoreCase Determines if the comparison ignores case when comparing. True - case insensitive.
   * @return Possibly-empty immutable list of all Members with the same effective name as the name provided.
   * @throws IllegalArgumentException If the provided name is null
   * @throws DetachedEntityException  If this entity is {@link #isDetached() detached}
   * @see #retrieveMembersByPrefix(String, int)
   */
  @Nonnull
  @Unmodifiable
  public static List<Member> getMembersByEffectiveName(@NotNull String name, boolean ignoreCase) {
    return getGuild().getMembersByEffectiveName(name, ignoreCase);
  }

  /**
   * A list containing the {@link GuildVoiceState GuildVoiceState} of every {@link Member Member}
   * in this {@link Guild Guild}.
   * <br>This will never return an empty list because if it were empty, that would imply that there are no
   * {@link Member Members} in this {@link Guild Guild}, which is
   * impossible.
   *
   * @return Never-empty immutable list containing all the {@link GuildVoiceState GuildVoiceStates} on this
   * {@link Guild Guild}.
   */
  @Nonnull
  public static List<GuildVoiceState> getVoiceStates() {
    return getGuild().getVoiceStates();
  }

  /**
   * Deletes a sticker from the guild.
   *
   * <p>The returned {@link RestAction RestAction} can encounter the following Discord errors:
   * <ul>
   *     <li>{@link ErrorResponse#UNKNOWN_STICKER UNKNOWN_STICKER}
   *     <br>Occurs when the provided id does not refer to a sticker known by Discord.</li>
   * </ul>
   *
   * @param id
   * @return {@link AuditableRestAction}
   * @throws IllegalStateException           If null is provided
   * @throws InsufficientPermissionException If the currently logged in account does not have
   *                                         {@link Permission#MANAGE_GUILD_EXPRESSIONS MANAGE_GUILD_EXPRESSIONS} in
   *                                         the guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static AuditableRestAction<Void> deleteSticker(@NotNull StickerSnowflake id) {
    return getGuild().deleteSticker(id);
  }

  /**
   * The Snowflake id of this entity. This is unique to every entity and will never change.
   *
   * @return Long containing the Id.
   */
  public static long getIdLong() {
    return getGuild().getIdLong();
  }

  /**
   * Looks up the role which is the booster role of this guild.
   * <br>These roles are created when the first user boosts this guild.
   *
   * <p>To check whether a role is a booster role you can use {@code role.getTags().isBoost()}.
   *
   * <p>This requires {@link CacheFlag#ROLE_TAGS CacheFlag.ROLE_TAGS} to be enabled.
   * See {@link JDABuilder#enableCache(CacheFlag, CacheFlag...) JDABuilder.enableCache(...)}.
   *
   * @return The boost role, or null if no role matches
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getBoostRole() {
    return getGuild().getBoostRole();
  }

  /**
   * The default {@link StandardGuildChannel} for a {@link Guild Guild}.
   * <br>This is the channel that the Discord client will default to opening when a Guild is opened for the first
   * time when accepting an invite
   * that is not directed at a specific {@link IInviteContainer channel}.
   *
   * <p>Note: This channel is the first channel in the guild (ordered by position) that the {@link #getPublicRole()}
   * has the {@link Permission#VIEW_CHANNEL Permission.VIEW_CHANNEL} in.
   *
   * @return The {@link StandardGuildChannel channel} representing the default channel for this guild
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static DefaultGuildChannelUnion getDefaultChannel() {
    return getGuild().getDefaultChannel();
  }

  /**
   * The description for this guild.
   * <br>This is displayed in the server browser below the guild name for verified guilds.
   *
   * <p>The description can be modified using {@link GuildManager#setDescription(String)}.
   *
   * @return The description
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   * @since 4.0.0
   */
  @Nullable
  public static String getDescription() {
    return getGuild().getDescription();
  }

  /**
   * Creates a new {@link NewsChannel NewsChannel} in this Guild.
   * For this to be successful, the logged in account has to have the {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL
   * } Permission
   *
   * <p>Possible {@link ErrorResponse ErrorResponses} caused by
   * the returned {@link RestAction RestAction} include the following:
   * <ul>
   *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
   *     <br>The channel could not be created due to a permission discrepancy</li>
   *
   *     <li>{@link ErrorResponse#MAX_CHANNELS MAX_CHANNELS}
   *     <br>The maximum number of channels were exceeded</li>
   * </ul>
   *
   * @param name The name of the NewsChannel to create (up to {@value Channel#MAX_NAME_LENGTH} characters)
   * @return A specific {@link ChannelAction ChannelAction}
   * <br>This action allows to set fields for the new NewsChannel before creating it
   * @throws InsufficientPermissionException If the logged in account does not have the
   * {@link Permission#MANAGE_CHANNEL} permission
   * @throws IllegalArgumentException        If the provided name is {@code null}, blank, or longer than
   * {@value Channel#MAX_NAME_LENGTH} characters
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   */
  @CheckReturnValue
  @Nonnull
  public static ChannelAction<NewsChannel> createNewsChannel(@NotNull String name) {
    return getGuild().createNewsChannel(name);
  }

  /**
   * Retrieves all {@link Invite Invites} for this guild.
   * <br>Requires {@link Permission#MANAGE_SERVER MANAGE_SERVER} in this guild.
   * Will throw an {@link InsufficientPermissionException InsufficientPermissionException} otherwise.
   *
   * <p>To get all invites for a {@link GuildChannel GuildChannel}
   * use {@link IInviteContainer#retrieveInvites() GuildChannel.retrieveInvites()}
   *
   * @return {@link RestAction RestAction} - Type: List{@literal <}{@link Invite Invite}{@literal >}
   * <br>The list of expanded Invite objects
   * @throws InsufficientPermissionException if the account does not have {@link Permission#MANAGE_SERVER
   * MANAGE_SERVER} in this Guild.
   * @throws DetachedEntityException         If this entity is {@link #isDetached() detached}
   * @see IInviteContainer#retrieveInvites()
   */
  @CheckReturnValue
  @Nonnull
  public static RestAction<List<Invite>> retrieveInvites() {
    return getGuild().retrieveInvites();
  }

  /**
   * The maximum amount of custom emojis a guild can have based on the guilds boost tier.
   *
   * @return The maximum amount of custom emojis
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  public static int getMaxEmojis() {
    return getGuild().getMaxEmojis();
  }

  @Nonnull
  public static SortedSnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
    return getGuild().getThreadChannelCache();
  }

  /**
   * Gets a {@link ThreadChannel ThreadChannel} that has the same id as the one provided.
   * <br>If there is no channel with an id that matches the provided one, then this returns {@code null}.
   *
   * <p>These threads can also represent posts in {@link ForumChannel ForumChannels}.
   *
   * <p>This getter exists on any instance of {@link IGuildChannelContainer} and only checks the caches with the
   * relevant scoping.
   * For {@link Guild}, {@link JDA}, or {@link ShardManager},
   * this returns the relevant channel with respect to the cache within each of those objects.
   * For a guild, this would mean it only returns channels within the same guild.
   * <br>If this is called on {@link JDA} or {@link ShardManager}, this may return null immediately after building,
   * because the cache isn't initialized yet.
   * To make sure the cache is initialized after building your {@link JDA} instance, you can use
   * {@link JDA#awaitReady()}.
   *
   * @param id The id of the {@link ThreadChannel ThreadChannel}.
   * @return Possibly-null {@link ThreadChannel ThreadChannel} with matching id.
   * @throws DetachedEntityException If this entity is {@link IDetachableEntity#isDetached() detached}
   */
  @Nullable
  public static ThreadChannel getThreadChannelById(long id) {
    return getGuild().getThreadChannelById(id);
  }

  /**
   * Gets a {@link Role Role} from this guild that has the same id as the
   * one provided.
   * <br>If there is no {@link Role Role} with an id that matches the provided
   * one, then this returns {@code null}.
   *
   * @param id The id of the {@link Role Role}.
   * @return Possibly-null {@link Role Role} with matching id.
   * @throws DetachedEntityException If this entity is {@link #isDetached() detached}
   */
  @Nullable
  public static Role getRoleById(long id) {
    return getGuild().getRoleById(id);
  }
}

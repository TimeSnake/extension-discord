/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.TimeSnakeGuild;
import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

public class ExPrivateChannel {

    protected final long channelID;

    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExPrivateChannel(long id) {
        this.channelID = id;
    }

    public ExPrivateChannel(PrivateChannel channel) {
        this.channelID = channel.getIdLong();
    }

    protected PrivateChannel getPrivateChannel() {
        return TimeSnakeGuild.getApi().getPrivateChannelById(channelID);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public long getID() {
        return channelID;
    }

    public boolean isAvailable() {
        return getPrivateChannel() != null;
    }

    @Nonnull
    public ExUser getUser() {
        return new ExUser(getPrivateChannel().getUser());
    }

    @CheckReturnValue
    public void close() {
        getPrivateChannel().close().complete();
    }

    @Nonnull
    public String getLatestMessageId() {
        return getPrivateChannel().getLatestMessageId();
    }

    public long getLatestMessageIdLong() {
        return getPrivateChannel().getLatestMessageIdLong();
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessagesById(@NotNull List<String> messageIds) {
        return getPrivateChannel().purgeMessagesById(messageIds);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessagesById(@NotNull String... messageIds) {
        return getPrivateChannel().purgeMessagesById(messageIds);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessages(@NotNull Message... messages) {
        return getPrivateChannel().purgeMessages(messages);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessages(@NotNull List<? extends Message> messages) {
        return getPrivateChannel().purgeMessages(messages);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessagesById(long... messageIds) {
        return getPrivateChannel().purgeMessagesById(messageIds);
    }

    public boolean hasLatestMessage() {
        return getPrivateChannel().hasLatestMessage();
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessage(@NotNull CharSequence text) {
        return getPrivateChannel().sendMessage(text);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessageFormat(@NotNull String format, @NotNull Object... args) {
        return getPrivateChannel().sendMessageFormat(format, args);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessageEmbeds(@NotNull MessageEmbed embed, @NotNull MessageEmbed... other) {
        return getPrivateChannel().sendMessageEmbeds(embed, other);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessageEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return getPrivateChannel().sendMessageEmbeds(embeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessage(@NotNull Message msg) {
        return getPrivateChannel().sendMessage(msg);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(@NotNull File file, @NotNull AttachmentOption... options) {
        return getPrivateChannel().sendFile(file, options);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(@NotNull File file, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return getPrivateChannel().sendFile(file, fileName, options);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(@NotNull InputStream data, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return getPrivateChannel().sendFile(data, fileName, options);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(byte[] data, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return getPrivateChannel().sendFile(data, fileName, options);
    }

    @CheckReturnValue
    @Nonnull
    public Message retrieveMessageById(@NotNull String messageId) {
        return getPrivateChannel().retrieveMessageById(messageId).complete();
    }

    @CheckReturnValue
    @Nonnull
    public Message retrieveMessageById(long messageId) {
        return getPrivateChannel().retrieveMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(@NotNull String messageId) {
        getPrivateChannel().deleteMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId) {
        getPrivateChannel().deleteMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId, long delay, TimeUnit timeUnit) {
        getPrivateChannel().deleteMessageById(messageId).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId, String reason) {
        getPrivateChannel().deleteMessageById(messageId).reason(reason).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId, String reason, long delay, TimeUnit timeUnit) {
        getPrivateChannel().deleteMessageById(messageId).reason(reason).queueAfter(delay, timeUnit);
    }

    public MessageHistory getHistory() {
        return getPrivateChannel().getHistory();
    }

    @CheckReturnValue
    @Nonnull
    public MessagePaginationAction getIterableHistory() {
        return getPrivateChannel().getIterableHistory();
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAround(@NotNull String messageId, int limit) {
        return getPrivateChannel().getHistoryAround(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAround(long messageId, int limit) {
        return getPrivateChannel().getHistoryAround(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAround(@NotNull Message message, int limit) {
        return getPrivateChannel().getHistoryAround(message, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAfter(@NotNull String messageId, int limit) {
        return getPrivateChannel().getHistoryAfter(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAfter(long messageId, int limit) {
        return getPrivateChannel().getHistoryAfter(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAfter(@NotNull Message message, int limit) {
        return getPrivateChannel().getHistoryAfter(message, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryBefore(@NotNull String messageId, int limit) {
        return getPrivateChannel().getHistoryBefore(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryBefore(long messageId, int limit) {
        return getPrivateChannel().getHistoryBefore(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryBefore(@NotNull Message message, int limit) {
        return getPrivateChannel().getHistoryBefore(message, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryFromBeginning(int limit) {
        return getPrivateChannel().getHistoryFromBeginning(limit);
    }

    @CheckReturnValue
    public void sendTyping() {
        getPrivateChannel().sendTyping().complete();
    }

    @CheckReturnValue
    public void sendTyping(long delay, TimeUnit timeUnit) {
        getPrivateChannel().sendTyping().queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void addReactionById(@NotNull String messageId, @NotNull String unicode) {
        getPrivateChannel().addReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull String unicode) {
        getPrivateChannel().addReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull String unicode, long delay, TimeUnit timeUnit) {
        getPrivateChannel().addReactionById(messageId, unicode).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void addReactionById(@NotNull String messageId, @NotNull Emote emote) {
        getPrivateChannel().addReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull Emote emote) {
        getPrivateChannel().addReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull Emote emote, long delay, TimeUnit timeUnit) {
        getPrivateChannel().addReactionById(messageId, emote).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void removeReactionById(@NotNull String messageId, @NotNull String unicode) {
        getPrivateChannel().removeReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull String unicode) {
        getPrivateChannel().removeReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull String unicode, long delay, TimeUnit timeUnit) {
        getPrivateChannel().removeReactionById(messageId, unicode).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void removeReactionById(@NotNull String messageId, @NotNull Emote emote) {
        getPrivateChannel().removeReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull Emote emote) {
        getPrivateChannel().removeReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull Emote emote, long delay, TimeUnit timeUnit) {
        getPrivateChannel().removeReactionById(messageId, emote).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(@NotNull String messageId, @NotNull String unicode) {
        return getPrivateChannel().retrieveReactionUsersById(messageId, unicode);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(long messageId, @NotNull String unicode) {
        return getPrivateChannel().retrieveReactionUsersById(messageId, unicode);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(@NotNull String messageId, @NotNull Emote emote) {
        return getPrivateChannel().retrieveReactionUsersById(messageId, emote);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(long messageId, @NotNull Emote emote) {
        return getPrivateChannel().retrieveReactionUsersById(messageId, emote);
    }

    @CheckReturnValue
    public void pinMessageById(@NotNull String messageId) {
        getPrivateChannel().pinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void pinMessageById(long messageId) {
        getPrivateChannel().pinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void pinMessageById(long messageId, long delay, TimeUnit timeUnit) {
        getPrivateChannel().pinMessageById(messageId).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void unpinMessageById(@NotNull String messageId) {
        getPrivateChannel().unpinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void unpinMessageById(long messageId) {
        getPrivateChannel().unpinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void unpinMessageById(long messageId, long delay, TimeUnit timeUnit) {
        getPrivateChannel().unpinMessageById(messageId).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    @Nonnull
    public List<Message> retrievePinnedMessages() {
        return getPrivateChannel().retrievePinnedMessages().complete();
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(@NotNull String messageId, @NotNull CharSequence newContent) {
        return getPrivateChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(long messageId, @NotNull CharSequence newContent) {
        return getPrivateChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(@NotNull String messageId, @NotNull Message newContent) {
        return getPrivateChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(long messageId, @NotNull Message newContent) {
        return getPrivateChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageFormatById(@NotNull String messageId, @NotNull String format, @NotNull Object... args) {
        return getPrivateChannel().editMessageFormatById(messageId, format, args);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageFormatById(long messageId, @NotNull String format, @NotNull Object... args) {
        return getPrivateChannel().editMessageFormatById(messageId, format, args);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(@NotNull String messageId, @NotNull MessageEmbed... newEmbeds) {
        return getPrivateChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(long messageId, @NotNull MessageEmbed... newEmbeds) {
        return getPrivateChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(@NotNull String messageId, @NotNull Collection<? extends MessageEmbed> newEmbeds) {
        return getPrivateChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(long messageId, @NotNull Collection<? extends MessageEmbed> newEmbeds) {
        return getPrivateChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(@NotNull String messageId, @NotNull Collection<? extends ComponentLayout> components) {
        return getPrivateChannel().editMessageComponentsById(messageId, components);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(long messageId, @NotNull Collection<? extends ComponentLayout> components) {
        return getPrivateChannel().editMessageComponentsById(messageId, components);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(@NotNull String messageId, @NotNull ComponentLayout... components) {
        return getPrivateChannel().editMessageComponentsById(messageId, components);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(long messageId, @NotNull ComponentLayout... components) {
        return getPrivateChannel().editMessageComponentsById(messageId, components);
    }

    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        getPrivateChannel().formatTo(formatter, flags, width, precision);
    }

    @Nonnull
    public String getName() {
        return getPrivateChannel().getName();
    }

    @Nonnull
    public ChannelType getType() {
        return getPrivateChannel().getType();
    }

    @Nonnull
    public OffsetDateTime getTimeCreated() {
        return getPrivateChannel().getTimeCreated();
    }
}

/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.discord.wrapper;

import de.timesnake.extension.discord.main.TimeSnakeGuild;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

/**
 * This Class incorporates the Channel History, granting access
 */
public class ExTextChannel extends ExGuildChannel {

    // Constructors & Wrapper methods /////////////////////////////////////////////////////////////////////////
    public ExTextChannel(long id) {
        super(id);
    }

    public ExTextChannel(TextChannel channel) {
        super(channel);
    }

    protected TextChannel getTextChannel() {
        return TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getTextChannelById(channelID);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Nullable
    public String getTopic() {
        return getTextChannel().getTopic();
    }

    public boolean isNSFW() {
        return getTextChannel().isNSFW();
    }

    public boolean isNews() {
        return getTextChannel().isNews();
    }

    public int getSlowmode() {
        return getTextChannel().getSlowmode();
    }

    public boolean canTalk() {
        return getTextChannel().canTalk();
    }

    public boolean canTalk(@NotNull ExMember member) {
        return getTextChannel().canTalk(TimeSnakeGuild.getApi().getGuildById(TimeSnakeGuild.getGuildID()).getMemberById(member.getID()));
    }

    @Nonnull
    public String getLatestMessageId() {
        return getTextChannel().getLatestMessageId();
    }

    public long getLatestMessageIdLong() {
        return getTextChannel().getLatestMessageIdLong();
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessagesById(@NotNull List<String> messageIds) {
        return getTextChannel().purgeMessagesById(messageIds);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessagesById(@NotNull String... messageIds) {
        return getTextChannel().purgeMessagesById(messageIds);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessages(@NotNull Message... messages) {
        return getTextChannel().purgeMessages(messages);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessages(@NotNull List<? extends Message> messages) {
        return getTextChannel().purgeMessages(messages);
    }

    @Nonnull
    public List<CompletableFuture<Void>> purgeMessagesById(long... messageIds) {
        return getTextChannel().purgeMessagesById(messageIds);
    }

    public boolean hasLatestMessage() {
        return getTextChannel().hasLatestMessage();
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessage(@NotNull CharSequence text) {
        return getTextChannel().sendMessage(text);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessageFormat(@NotNull String format, @NotNull Object... args) {
        return getTextChannel().sendMessageFormat(format, args);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessageEmbeds(@NotNull MessageEmbed embed, @NotNull MessageEmbed... other) {
        return getTextChannel().sendMessageEmbeds(embed, other);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessageEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return getTextChannel().sendMessageEmbeds(embeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendMessage(@NotNull Message msg) {
        return getTextChannel().sendMessage(msg);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(@NotNull File file, @NotNull AttachmentOption... options) {
        return getTextChannel().sendFile(file, options);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(@NotNull File file, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return getTextChannel().sendFile(file, fileName, options);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(@NotNull InputStream data, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return getTextChannel().sendFile(data, fileName, options);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction sendFile(byte[] data, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return getTextChannel().sendFile(data, fileName, options);
    }

    @CheckReturnValue
    @Nonnull
    public Message retrieveMessageById(@NotNull String messageId) {
        return getTextChannel().retrieveMessageById(messageId).complete();
    }

    @CheckReturnValue
    @Nonnull
    public Message retrieveMessageById(long messageId) {
        return getTextChannel().retrieveMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(@NotNull String messageId) {
        getTextChannel().deleteMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId) {
        getTextChannel().deleteMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId, long delay, TimeUnit timeUnit) {
        getTextChannel().deleteMessageById(messageId).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId, String reason) {
        getTextChannel().deleteMessageById(messageId).reason(reason).complete();
    }

    @CheckReturnValue
    public void deleteMessageById(long messageId, String reason, long delay, TimeUnit timeUnit) {
        getTextChannel().deleteMessageById(messageId).reason(reason).queueAfter(delay, timeUnit);
    }

    public MessageHistory getHistory() {
        return getTextChannel().getHistory();
    }

    @CheckReturnValue
    @Nonnull
    public MessagePaginationAction getIterableHistory() {
        return getTextChannel().getIterableHistory();
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAround(@NotNull String messageId, int limit) {
        return getTextChannel().getHistoryAround(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAround(long messageId, int limit) {
        return getTextChannel().getHistoryAround(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAround(@NotNull Message message, int limit) {
        return getTextChannel().getHistoryAround(message, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAfter(@NotNull String messageId, int limit) {
        return getTextChannel().getHistoryAfter(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAfter(long messageId, int limit) {
        return getTextChannel().getHistoryAfter(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryAfter(@NotNull Message message, int limit) {
        return getTextChannel().getHistoryAfter(message, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryBefore(@NotNull String messageId, int limit) {
        return getTextChannel().getHistoryBefore(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryBefore(long messageId, int limit) {
        return getTextChannel().getHistoryBefore(messageId, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryBefore(@NotNull Message message, int limit) {
        return getTextChannel().getHistoryBefore(message, limit);
    }

    @CheckReturnValue
    @Nonnull
    public MessageHistory.MessageRetrieveAction getHistoryFromBeginning(int limit) {
        return getTextChannel().getHistoryFromBeginning(limit);
    }

    @CheckReturnValue
    public void sendTyping() {
        getTextChannel().sendTyping().complete();
    }

    @CheckReturnValue
    public void sendTyping(long delay, TimeUnit timeUnit) {
        getTextChannel().sendTyping().queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void addReactionById(@NotNull String messageId, @NotNull String unicode) {
        getTextChannel().addReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull String unicode) {
        getTextChannel().addReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull String unicode, long delay, TimeUnit timeUnit) {
        getTextChannel().addReactionById(messageId, unicode).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void addReactionById(@NotNull String messageId, @NotNull Emote emote) {
        getTextChannel().addReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull Emote emote) {
        getTextChannel().addReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void addReactionById(long messageId, @NotNull Emote emote, long delay, TimeUnit timeUnit) {
        getTextChannel().addReactionById(messageId, emote).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void removeReactionById(@NotNull String messageId, @NotNull String unicode) {
        getTextChannel().removeReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull String unicode) {
        getTextChannel().removeReactionById(messageId, unicode).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull String unicode, long delay, TimeUnit timeUnit) {
        getTextChannel().removeReactionById(messageId, unicode).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void removeReactionById(@NotNull String messageId, @NotNull Emote emote) {
        getTextChannel().removeReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull Emote emote) {
        getTextChannel().removeReactionById(messageId, emote).complete();
    }

    @CheckReturnValue
    public void removeReactionById(long messageId, @NotNull Emote emote, long delay, TimeUnit timeUnit) {
        getTextChannel().removeReactionById(messageId, emote).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(@NotNull String messageId, @NotNull String unicode) {
        return getTextChannel().retrieveReactionUsersById(messageId, unicode);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(long messageId, @NotNull String unicode) {
        return getTextChannel().retrieveReactionUsersById(messageId, unicode);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(@NotNull String messageId, @NotNull Emote emote) {
        return getTextChannel().retrieveReactionUsersById(messageId, emote);
    }

    @CheckReturnValue
    @Nonnull
    public ReactionPaginationAction retrieveReactionUsersById(long messageId, @NotNull Emote emote) {
        return getTextChannel().retrieveReactionUsersById(messageId, emote);
    }

    @CheckReturnValue
    public void pinMessageById(@NotNull String messageId) {
        getTextChannel().pinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void pinMessageById(long messageId) {
        getTextChannel().pinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void pinMessageById(long messageId, long delay, TimeUnit timeUnit) {
        getTextChannel().pinMessageById(messageId).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    public void unpinMessageById(@NotNull String messageId) {
        getTextChannel().unpinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void unpinMessageById(long messageId) {
        getTextChannel().unpinMessageById(messageId).complete();
    }

    @CheckReturnValue
    public void unpinMessageById(long messageId, long delay, TimeUnit timeUnit) {
        getTextChannel().unpinMessageById(messageId).queueAfter(delay, timeUnit);
    }

    @CheckReturnValue
    @Nonnull
    public List<Message> retrievePinnedMessages() {
        return getTextChannel().retrievePinnedMessages().complete();
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(@NotNull String messageId, @NotNull CharSequence newContent) {
        return getTextChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(long messageId, @NotNull CharSequence newContent) {
        return getTextChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(@NotNull String messageId, @NotNull Message newContent) {
        return getTextChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageById(long messageId, @NotNull Message newContent) {
        return getTextChannel().editMessageById(messageId, newContent);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageFormatById(@NotNull String messageId, @NotNull String format, @NotNull Object... args) {
        return getTextChannel().editMessageFormatById(messageId, format, args);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageFormatById(long messageId, @NotNull String format, @NotNull Object... args) {
        return getTextChannel().editMessageFormatById(messageId, format, args);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(@NotNull String messageId, @NotNull MessageEmbed... newEmbeds) {
        return getTextChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(long messageId, @NotNull MessageEmbed... newEmbeds) {
        return getTextChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(@NotNull String messageId, @NotNull Collection<? extends MessageEmbed> newEmbeds) {
        return getTextChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageEmbedsById(long messageId, @NotNull Collection<? extends MessageEmbed> newEmbeds) {
        return getTextChannel().editMessageEmbedsById(messageId, newEmbeds);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(@NotNull String messageId, @NotNull Collection<? extends ComponentLayout> components) {
        return getTextChannel().editMessageComponentsById(messageId, components);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(long messageId, @NotNull Collection<? extends ComponentLayout> components) {
        return getTextChannel().editMessageComponentsById(messageId, components);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(@NotNull String messageId, @NotNull ComponentLayout... components) {
        return getTextChannel().editMessageComponentsById(messageId, components);
    }

    @CheckReturnValue
    @Nonnull
    public MessageAction editMessageComponentsById(long messageId, @NotNull ComponentLayout... components) {
        return getTextChannel().editMessageComponentsById(messageId, components);
    }

    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        getTextChannel().formatTo(formatter, flags, width, precision);
    }

}

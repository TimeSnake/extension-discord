/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import de.timesnake.basic.proxy.util.Network;
import de.timesnake.basic.proxy.util.chat.Argument;
import de.timesnake.basic.proxy.util.chat.CommandListener;
import de.timesnake.basic.proxy.util.chat.Completion;
import de.timesnake.basic.proxy.util.chat.Sender;
import de.timesnake.basic.proxy.util.user.User;
import de.timesnake.database.util.Database;
import de.timesnake.library.basic.util.Tuple;
import de.timesnake.library.chat.Chat;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class RegistrationCmd extends ListenerAdapter implements CommandListener {

  private final HashMap<UUID, Tuple<UUID, String>> openRegistrationsByUUID = new HashMap<>();

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    if (!args.isLengthLowerEquals(1, true)) {
      return;
    }

    if (!sender.isPlayer(true)) {
      return;
    }

    User user = sender.getUser();

    // Discord information
    if (args.length() == 0) {

      Component component = Chat.getSenderPlugin(Plugin.SERVER)
          .append(Component.text("Join our discord: ", ExTextColor.PUBLIC))
          .append(Component.text("https://discord.gg/YRCZhFVE9z", ExTextColor.VALUE))
          .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to open link")))
          .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,
              "https://discord.gg/YRCZhFVE9z"));

      if (sender.hasPermission("discord.info")) {
        for (User u : Network.getUsers()) {
          u.getPlayer().sendMessage(component);
        }
      } else {
        user.getPlayer().sendMessage(component);
      }
      return;
    }

    // Discord registration
    if (args.length() == 1) {
      if (args.get(0).equalsIgnoreCase("login")) {

        Long currentDiscordID = Database.getUsers().getUser(user.getUniqueId())
            .getDiscordId();
        if (currentDiscordID != null) {
          sender.sendPluginMessage(
              Component.text("Your account is already linked. By continuing, you " +
                  "will unlink your current discord account.", ExTextColor.WARNING));
        }

        String code;
        if (openRegistrationsByUUID.containsKey(user.getUniqueId())) {
          code = openRegistrationsByUUID.get(user.getUniqueId()).getB();
        } else {
          Random rnd = new Random();
          code = String.valueOf(rnd.nextInt(100000 - 10000) + 10000);
          openRegistrationsByUUID.put(user.getUniqueId(), new Tuple<>(user.getUniqueId(), code));
        }

        Component component = Chat.getSenderPlugin(Plugin.SERVER)
            .append(Component.text("Please send the following ", ExTextColor.PERSONAL))
            .append(Component.text("code via private message to our bot: ",
                ExTextColor.PERSONAL))
            .append(Component.text(code, ExTextColor.VALUE))
            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                Component.text("Click to copy")))
            .clickEvent(
                ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code));

        user.getPlayer().sendMessage(component);

      } else if (args.get(0).equalsIgnoreCase("ping")) {
        if (!sender.hasPermission("discord.ping")) {
          return;
        }
        TimeSnakeGuild.getApi().getCategoriesByName("Kanäle", true).get(0).getTextChannels()
            .stream().filter(t -> t.getName().equalsIgnoreCase("allgemein")).findFirst()
            .get().sendMessage("@ping-vanilla Server is online");
      } else if (args.get(0).equalsIgnoreCase("help")) {
        sender.sendTDMessageCommandHelp("Get the link to our discord", "discord");
        sender.sendTDMessageCommandHelp("Register your discord account", "discord login");
      } else {
        sender.sendMessageUseHelp("discord help");
      }
      return;
    }

  }

  @Override
  public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
    PrivateChannel channel = event.getChannel();
    net.dv8tion.jda.api.entities.User user = event.getAuthor();
    String message = event.getMessage().getContentRaw();

    if (user.getIdLong() == TimeSnakeGuild.getApi().getSelfUser().getIdLong()) {
      return;
    }

    String providedCode;
    try {
      providedCode = String.valueOf(Integer.parseInt(message));
    } catch (NumberFormatException e) {
      channel.sendMessage(
          "Your message does not have the format of a registration id.\nPlease use the command "
              + "\"/discord login\" and copy the code by clicking on it.").complete();
      return;
    }

    for (Tuple<UUID, String> value : openRegistrationsByUUID.values()) {
      if (value.getB().equals(providedCode)) {
        UUID uuid = value.getA();
        long userID = user.getIdLong();
        Database.getUsers().getUser(uuid).setDiscordId(userID);
        openRegistrationsByUUID.remove(uuid);
        channel.sendMessage("Your discord account has been successfully linked to your Minecraft account ("
            + Database.getUsers().getUser(uuid).getName() + ")!").complete();
        return;
      }
    }

    // Code not found
    channel.sendMessage("The entered code does not exist. Please use the command \"/discord login\" and follow "
        + "the instructions.").complete();

  }

  @Override
  public Completion getTabCompletion() {
    return new Completion()
        .addArgument(new Completion("login", "help"));
  }

  @Override
  public String getPermission() {
    return null;
  }
}

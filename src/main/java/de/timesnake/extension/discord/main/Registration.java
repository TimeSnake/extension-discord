package de.timesnake.extension.discord.main;

import de.timesnake.basic.proxy.util.Network;
import de.timesnake.basic.proxy.util.chat.Argument;
import de.timesnake.basic.proxy.util.chat.ChatColor;
import de.timesnake.basic.proxy.util.chat.Sender;
import de.timesnake.basic.proxy.util.user.User;
import de.timesnake.database.util.Database;
import de.timesnake.extension.discord.wrapper.ExPrivateChannel;
import de.timesnake.extension.discord.wrapper.ExUser;
import de.timesnake.library.basic.util.Tuple;
import de.timesnake.library.extension.util.chat.Chat;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.CommandListener;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Registration extends ListenerAdapter implements CommandListener<Sender, Argument> {

    public static Registration getInstance() {
        if (instance == null) {
            instance = new Registration();
        }
        return instance;
    }

    // Singleton setup /////////////////////////////
    protected static Registration instance;
    private HashMap<UUID, Tuple<UUID, String>> openRegistrationsByUUID = new HashMap<>();

    protected Registration() {

    }
    ////////////////////////////////////////////////

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!args.isLengthLowerEquals(1, true)) {
            return;
        }

        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();

        // Discord information
        if (args.length() == 0) {

            TextComponent text = new TextComponent();
            text.setText(Chat.getSenderPlugin(Plugin.DISCORD) + ChatColor.PUBLIC + "Join our discord: " + ChatColor.VALUE + "https://discord.gg/YRCZhFVE9z");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to open link")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/YRCZhFVE9z"));

            if (sender.hasPermission("exsupport.discord")) {
                for (User u : Network.getUsers()) {
                    u.getPlayer().sendMessage(text);
                }
            } else {
                user.getPlayer().sendMessage(text);
            }
            return;
        }

        // Discord registration
        if (args.length() == 1) {
            if (args.get(0).equalsIgnoreCase("login")) {

                Long currentDiscordID = Database.getUsers().getUser(user.getUniqueId()).getDiscordId();
                if (currentDiscordID != null) {
                    sender.sendPluginMessage(ChatColor.WARNING + "Your account is already linked. By continuing, you " +
                            "will unlink your current discord account.");
                }

                String code;
                if (openRegistrationsByUUID.containsKey(user.getUniqueId())) {
                    code = openRegistrationsByUUID.get(user.getUniqueId()).getB();
                } else {
                    Random rnd = new Random();
                    code = String.valueOf(rnd.nextInt(100000 - 10000) + 10000);
                    openRegistrationsByUUID.put(user.getUniqueId(), new Tuple<>(user.getUniqueId(), code));
                }

                TextComponent text = new TextComponent();
                text.setText(Chat.getSenderPlugin(Plugin.DISCORD) + ChatColor.PERSONAL + "Please send the following " +
                        "code via private message to our bot: " + ChatColor.VALUE + code);
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy")));
                text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code));

                user.getPlayer().sendMessage(text);

            } else if (args.get(0).equalsIgnoreCase("help")) {
                sender.sendMessageCommandHelp("Get the link to our discord", "discord");
                sender.sendMessageCommandHelp("Register your discord account", "discord login");
            } else {
                sender.sendMessageUseHelp("discord help");
            }
            return;
        }

    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        ExPrivateChannel channel = new ExPrivateChannel(event.getChannel());
        ExUser user = new ExUser(event.getAuthor());
        String message = event.getMessage().getContentRaw();

        if (user.getID() == TimeSnakeGuild.getApi().getSelfUser().getIdLong()) {
            return;
        }

        String providedCode;
        try {
            providedCode = String.valueOf(Integer.parseInt(message));
        } catch (NumberFormatException e) {
            channel.sendMessage("Your message does not have the format of a registration id.\nPlease use the command " +
                    "\"/discord login\" and copy the code by clicking on it.").complete();
            return;
        }

        for (Tuple<UUID, String> value : openRegistrationsByUUID.values()) {
            if (value.getB().equals(providedCode)) {
                UUID uuid = value.getA();
                long userID = user.getID();
                Database.getUsers().getUser(uuid).setDiscordId(userID);
                openRegistrationsByUUID.remove(uuid);
                channel.sendMessage("Your discord account has been successfully linked to your Minecraft account (" + Database.getUsers().getUser(uuid).getName() + ")!").complete();
                return;
            }
        }

        // Code not found
        channel.sendMessage("The entered code does not exist. Please use the command \"/discord login\" and follow " +
                "the instructions.").complete();

    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (args.length() == 1) {
            return List.of("login", "help");
        }
        return List.of();
    }
}

/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import de.timesnake.basic.proxy.util.Network;
import de.timesnake.extension.discord.util.ConfigFile;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;


@com.velocitypowered.api.plugin.Plugin(id = "extension-discord", name = "ExDiscord", version = "1.0-SNAPSHOT",
        url = "https://git.timesnake.de", authors = {"MarkusNils"},
        dependencies = {
                @Dependency(id = "basic-proxy")
        })
public class ExDiscord {

    public static ExDiscord getPlugin() {
        return plugin;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static EventManager getEventManager() {
        return server.getEventManager();
    }

    public static PluginManager getPluginManager() {
        return server.getPluginManager();
    }

    public static CommandManager getCommandManager() {
        return server.getCommandManager();
    }

    private static void registerListeners(JDA api) {
        api.addEventListener(Registration.getInstance());
    }

    public static ConfigFile configFile = new ConfigFile();
    private static ExDiscord plugin;
    private static ProxyServer server;
    private static Logger logger;

    @Inject
    public ExDiscord(ProxyServer server, Logger logger) {
        ExDiscord.server = server;
        ExDiscord.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        plugin = this;

        Network.getCommandManager().addCommand(this, "discord", Registration.getInstance(),
                de.timesnake.extension.discord.main.Plugin.DISCORD);

        new ChannelManager();

        JDABuilder builder = JDABuilder.createDefault(configFile.getToken());
        JDA api = null;

        try {
            api = builder.build();
            api.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (api != null) {

            // Init
            TimeSnakeGuild.initialize(api, configFile.getGuildID());
            registerListeners(api);
            api.getPresence().setPresence(Activity.watching("TimeSnake.de"), false);

        } else {
            Network.printWarning(de.timesnake.extension.discord.main.Plugin.DISCORD,
                    "The api could not be " +
                            "initialized.");
        }
    }

    public void onProxyShutdown(ProxyShutdownEvent event) {
        TimeSnakeGuild.getApi().shutdown();
    }
}

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
import de.timesnake.library.chat.Plugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;


@com.velocitypowered.api.plugin.Plugin(id = "extension-discord", name = "ExDiscord", version = "1.0-SNAPSHOT",
    url = "https://git.timesnake.de", authors = {"timesnake"},
    dependencies = {
        @Dependency(id = "basic-proxy")
    })
public class ExDiscord {

  public static final Plugin PLUGIN = new Plugin("Discord", "XDC");

  public static ExDiscord getPlugin() {
    return plugin;
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

  public static ConfigFile configFile = new ConfigFile();
  private static RegistrationCmd registrationCmd;
  private static ExDiscord plugin;
  private static ProxyServer server;

  private final Logger logger = LogManager.getLogger("discord.manager");

  @Inject
  public ExDiscord(ProxyServer server) {
    ExDiscord.server = server;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    plugin = this;

    registrationCmd = new RegistrationCmd();

    Network.getCommandManager().addCommand(this, "discord", registrationCmd, PLUGIN);

    new ChannelManager();

    JDABuilder builder = JDABuilder.createDefault(configFile.getToken());
    JDA api = null;

    try {
      api = builder.build();
      api.awaitReady();
    } catch (LoginException | InterruptedException ignored) {
    }

    if (api != null) {

      // Init
      new ExGuild(api, configFile.getGuildID());
      api.addEventListener(registrationCmd);
      api.getPresence().setPresence(Activity.watching("TimeSnake.de"), false);

    } else {
      this.logger.error("API could not be initialized");
    }
  }

  public void onProxyShutdown(ProxyShutdownEvent event) {
    ExGuild.getInstance().getApi().shutdown();
  }
}

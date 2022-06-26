package de.timesnake.extension.discord.main;

import de.timesnake.basic.proxy.util.Network;
import de.timesnake.extension.discord.util.ConfigFile;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import javax.security.auth.login.LoginException;


public class ExDiscord extends Plugin {

    private static void registerListeners(JDA api) {
        api.addEventListener(Registration.getInstance());
    }
    public static ConfigFile configFile = new ConfigFile();

    @Override
    public void onEnable() {

        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        Network.getCommandHandler().addCommand(this, pm, "discord", Registration.getInstance(),
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
            Network.printWarning(de.timesnake.extension.discord.main.Plugin.DISCORD, "The api could not be " +
                    "initialized.");
        }
    }

    @Override
    public void onDisable() {
        TimeSnakeGuild.getApi().shutdown();
    }
}

package de.timesnake.extension.discord.util;

import de.timesnake.basic.proxy.util.file.ExFile;

public class ConfigFile extends ExFile {

    private String token;
    private Long guildID;
    private Long fallbackChannel;

    public ConfigFile() {
        super("extension-discord", "config");
    }

    public String getToken() {
        if (token == null) {
            token = super.getString("token");
        }
        return token;
    }

    public long getGuildID() {
        if (guildID == null) {
            guildID = super.getLong("guildID");
        }
        return guildID;
    }

    public long getFallbackChannel() {
        if (fallbackChannel == null) {
            fallbackChannel = super.getLong("fallbackChannel");
        }
        return fallbackChannel;
    }


}

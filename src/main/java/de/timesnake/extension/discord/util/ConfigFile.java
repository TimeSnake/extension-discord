/*
 * extension-discord.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.extension.discord.util;

import de.timesnake.basic.proxy.util.file.ExFile;

public class ConfigFile extends ExFile {

    private String token;
    private Long guildID;
    private Long fallbackChannel;

    public ConfigFile() {
        super("extension-discord", "config.toml");
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

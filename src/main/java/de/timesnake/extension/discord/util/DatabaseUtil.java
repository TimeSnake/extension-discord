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

import de.timesnake.database.util.Database;
import de.timesnake.extension.discord.main.TimeSnakeGuild;
import de.timesnake.extension.discord.wrapper.ExMember;
import de.timesnake.extension.discord.wrapper.ExUser;

import java.util.UUID;

public class DatabaseUtil {

    public static ExMember getMemberFromUUID(UUID uuid) {
        Long userID = Database.getUsers().getUser(uuid).getDiscordId();
        if (userID == null) { // User not registered
            return null;
        }

        ExUser user = new ExUser(userID);
        return TimeSnakeGuild.getMember(user);
    }

    public static ExUser getUserFromUUID(UUID uuid) {
        Long userID = Database.getUsers().getUser(uuid).getDiscordId();
        if (userID == null) { // User not registered
            return null;
        }

        return new ExUser(userID);
    }

}

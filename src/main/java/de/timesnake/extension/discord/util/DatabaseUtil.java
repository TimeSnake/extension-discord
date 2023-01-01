/*
 * Copyright (C) 2023 timesnake
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

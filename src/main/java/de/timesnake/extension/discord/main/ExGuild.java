/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

import de.timesnake.database.util.Database;
import de.timesnake.database.util.user.DbUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ExGuild extends DelegatedGuild {

  public static ExGuild getInstance() {
    return instance;
  }

  protected static ExGuild instance;
  private final Map<UUID, Long> discordIdByUuid = new ConcurrentHashMap<>();
  private final VoiceChannel fallbackChannel;

  protected ExGuild(JDA api, long guildID) {
    super(api, guildID);

    instance = this;

    this.fallbackChannel = getVoiceChannelById(ExDiscord.configFile.getFallbackChannel());

    for (DbUser user : Database.getUsers().getUsers()) {
      Long discordId = user.getDiscordId();
      if (discordId != null) {
        this.discordIdByUuid.put(user.getUniqueId(), discordId);
      }
    }
  }

  public Member getMemberByUuid(UUID uuid) {
    if (!this.discordIdByUuid.containsKey(uuid)) {
      DbUser user = Database.getUsers().getUser(uuid);
      Long discordId = user.getDiscordId();
      if (discordId != null) {
        this.discordIdByUuid.put(user.getUniqueId(), discordId);
      } else {
        return null;
      }
    }

    return getMemberById(this.discordIdByUuid.get(uuid));
  }

  protected VoiceChannel getFallbackChannel() {
    return this.fallbackChannel;
  }
}

/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.discord.main;

public class Plugin extends de.timesnake.basic.proxy.util.chat.Plugin {

  public static final Plugin DISCORD = new Plugin("Discord", "XDC");

  protected Plugin(String name, String code) {
    super(name, code);
  }

}

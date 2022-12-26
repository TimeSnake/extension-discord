/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.extension.discord.main;

import de.timesnake.library.basic.util.LogHelper;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin extends de.timesnake.basic.proxy.util.chat.Plugin {

    public static final Plugin DISCORD = new Plugin("Discord", "XDC", LogHelper.getLogger("Discord", Level.INFO));

    protected Plugin(String name, String code, Logger logger) {
        super(name, code, logger);
    }

}

package com.enginarupdate.plugin.lang;

import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Minimal English/Turkish message resolver based on the player's client locale.
 * English is the default; Turkish is used when the player's client language is Turkish.
 */
public final class Lang {

    private Lang() {
    }

    public static boolean isTurkish(Player player) {
        Locale locale = player.locale();
        return locale != null && "tr".equalsIgnoreCase(locale.getLanguage());
    }

    public static String pick(Player player, String english, String turkish) {
        return isTurkish(player) ? turkish : english;
    }
}

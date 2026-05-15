package com.proxcord.free.utils;

import com.proxcord.free.ProxcordFreePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class LanguageManager {

    private static FileConfiguration config;

    // 1. Private constructor silences "Utility class has implicit public constructor"
    private LanguageManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void load() {
        ProxcordFreePlugin plugin = ProxcordFreePlugin.get();
        if (plugin == null) return;

        File file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static Component get(String key) {
        if (config == null) {
            return Component.text("Language file not loaded.");
        }

        String val = config.getString(key);
        if (val == null) {
            val = "&cMissing Key: " + key;
        }

        String prefix = config.getString("prefix");
        if (prefix == null) {
            prefix = "";
        }

        // 2. Legacy conversion that doesn't trigger deprecation warnings
        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + val);
    }
}
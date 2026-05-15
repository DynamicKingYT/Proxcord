package com.proxcord.free.voice;

import com.proxcord.free.ProxcordFreePlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class VoiceModeManager {

    private static VoiceMode mode = VoiceMode.GLOBAL;

    public enum VoiceMode {
        GLOBAL,
        PROXIMITY
    }

    public static void reloadConfig() {
        FileConfiguration cfg = ProxcordFreePlugin.get().getConfig();
        String str = cfg.getString("voice-mode", "global").toLowerCase();

        try {
            mode = VoiceMode.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            mode = VoiceMode.GLOBAL;
        }
    }

    public static VoiceMode getMode() {
        return mode;
    }

    public static boolean isGlobal() {
        return mode == VoiceMode.GLOBAL;
    }
}

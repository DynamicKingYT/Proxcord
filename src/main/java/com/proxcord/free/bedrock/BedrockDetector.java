package com.proxcord.free.bedrock;

import org.bukkit.entity.Player;

public class BedrockDetector {

    /**
     * Very common Floodgate/Geyser pattern:
     * Bedrock UUIDs start with "00000000-0000-0000-0000".
     * This keeps Java players out of the Bedrock-only GUI.
     */
    public static boolean isBedrock(Player player) {
        String uuid = player.getUniqueId().toString();
        return uuid.startsWith("00000000-0000-0000-0000");
    }
}

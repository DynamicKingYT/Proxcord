package com.proxcord.free.linking;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;
import java.util.UUID;

public final class LinkingManager {

    private LinkingManager() {}

    public static String getDiscordId(Player player) {
        return player == null ? null : getDiscordId(player.getUniqueId());
    }

    public static String getDiscordId(UUID uuid) {
        try {
            // Checks DiscordSRV's internal link storage
            return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
        } catch (Exception e) {
            return null;
        }
    }
}
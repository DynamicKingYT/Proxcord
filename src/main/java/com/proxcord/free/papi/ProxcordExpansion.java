package com.proxcord.free.papi;

import com.proxcord.free.ProxcordFreePlugin;
import com.proxcord.free.discord.DiscordBot;
import com.proxcord.free.utils.LanguageManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProxcordExpansion extends PlaceholderExpansion {

    private final ProxcordFreePlugin plugin;

    public ProxcordExpansion(ProxcordFreePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "proxcord";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ProxcordDev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Keep the hook alive even if PAPI reloads
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // %proxcord_status% -> Returns "Linked" or "Not Linked" (Localized)
        if (params.equalsIgnoreCase("status")) {
            String id = DiscordBot.getLinkedDiscordId(player);
            if (id != null) {
                // We convert the Component back to String for PAPI
                return LegacyComponentSerializer.legacySection().serialize(LanguageManager.get("status-linked"));
            } else {
                return LegacyComponentSerializer.legacySection().serialize(LanguageManager.get("status-unlinked"));
            }
        }

        // %proxcord_id% -> Returns the Discord ID or "None"
        if (params.equalsIgnoreCase("id")) {
            String id = DiscordBot.getLinkedDiscordId(player);
            return (id != null) ? id : "None";
        }

        return null; // Placeholder not found
    }
}
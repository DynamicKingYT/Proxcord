package com.proxcord.free.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when Proxcord successfully moves a player into the Proximity Voice Channel.
 */
// Suppress "Unused" warnings because this is an API class for other developers
@SuppressWarnings("unused")
public class ProxcordVoiceJoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String discordId;

    public ProxcordVoiceJoinEvent(Player player, String discordId) {
        this.player = player;
        this.discordId = discordId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getDiscordId() {
        return discordId;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
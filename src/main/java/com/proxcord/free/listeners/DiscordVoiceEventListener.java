package com.proxcord.free.listeners;

import com.proxcord.free.ProxcordFreePlugin;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;

public class DiscordVoiceEventListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() != null && event.getChannelJoined() == null) {
            // User left a channel voluntarily
            String userId = event.getMember().getId();
            ProxcordFreePlugin.get().getLogger().info("[Proxcord] User " + userId + " left voice manually.");
            // Optionally trigger resync immediately
        }
    }
}

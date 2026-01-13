package com.proxcord.free.voice;

import com.proxcord.free.ProxcordFreePlugin;
import com.proxcord.free.discord.DiscordBot;

// Shaded DiscordSRV/JDA Imports
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoiceChannelManager {

    private static final long COOLDOWN_MILLIS = 5000L;
    private static boolean configValid = false;
    private static long guildId = 0L;
    private static long nearChannelId = 0L;
    private static final Map<UUID, Long> lastMove = new HashMap<>();

    public static void reloadFromConfig() {
        ProxcordFreePlugin plugin = ProxcordFreePlugin.get();
        String guildStr = plugin.getConfig().getString("discord.guild-id", "");
        String nearStr = plugin.getConfig().getString("discord.near-channel-id", "");

        configValid = false;
        try {
            if (!guildStr.isBlank() && !nearStr.isBlank()) {
                guildId = Long.parseLong(guildStr);
                nearChannelId = Long.parseLong(nearStr);
                configValid = true;
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().severe("[Proxcord] Invalid ID in config.yml");
        }
    }

    public static void handleProximityState(Player player, boolean hasNearby) {
        if (!hasNearby) return;

        ProxcordFreePlugin plugin = ProxcordFreePlugin.get();
        if (!configValid) {
            reloadFromConfig();
            if (!configValid) return;
        }

        if (!DiscordBot.isReady()) return;

        String discordId = DiscordBot.getLinkedDiscordId(player);
        if (discordId == null) return;

        long now = System.currentTimeMillis();
        long last = lastMove.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < COOLDOWN_MILLIS) return;

        JDA jda = DiscordBot.getJda();
        if (jda == null) return;

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        Member member = guild.getMemberById(discordId);
        if (member == null) return;

        VoiceChannel nearChannel = guild.getVoiceChannelById(nearChannelId);
        if (nearChannel == null) return;

        if (member.getVoiceState() != null &&
                member.getVoiceState().getChannel() != null &&
                member.getVoiceState().getChannel().getIdLong() == nearChannelId) {
            return;
        }

        // FIX: Cast to VoiceChannel (the class), not nearChannel (the variable)
        guild.moveVoiceMember(member, (VoiceChannel) nearChannel).queue(
                success -> lastMove.put(player.getUniqueId(), System.currentTimeMillis()),
                error -> plugin.getLogger().warning("[Proxcord] Failed move: " + error.getMessage())
        );
    }
}
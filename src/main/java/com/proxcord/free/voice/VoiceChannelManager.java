package com.proxcord.free.voice;
import com.proxcord.free.ProxcordFreePlugin;
import com.proxcord.free.discord.DiscordBot;
import com.proxcord.free.api.events.ProxcordVoiceJoinEvent; // <--- NEW IMPORT

// Shaded DiscordSRV Imports
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VoiceChannelManager {

    private static final long COOLDOWN_MILLIS = 5000L;
    private static boolean configValid = false;
    private static long guildId = 0L;
    private static long nearChannelId = 0L;
    private static long globalChannelId = 0L;
    private static final Map<UUID, Long> lastMove = new HashMap<>();

    private VoiceChannelManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void reloadFromConfig() {
        ProxcordFreePlugin plugin = ProxcordFreePlugin.get();
        if (plugin == null) return;

        String guildStr = plugin.getConfig().getString("discord.guild-id", "");
        String nearStr = plugin.getConfig().getString("discord.near-channel-id", "");
        String globalStr = plugin.getConfig().getString("discord.global-channel-id", "");

        configValid = false;
        try {
            if (!guildStr.isBlank() && !nearStr.isBlank() && !globalStr.isBlank()) {
                guildId = Long.parseLong(guildStr);
                nearChannelId = Long.parseLong(nearStr);
                globalChannelId = Long.parseLong(globalStr);
                configValid = true;
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().severe("[Proxcord] Invalid ID in config.yml");
        }
    }

    public static void handleProximityState(Player player, boolean hasNearby) {
        if (!hasNearby) return;

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

        // ✅ THE API UPDATE
        guild.moveVoiceMember(member, nearChannel).queue(
                success -> {
                    lastMove.put(player.getUniqueId(), System.currentTimeMillis());

                    // 🔥 Fire the Custom Event safely on the main thread
                    ProxcordFreePlugin.get().getServer().getScheduler().runTask(ProxcordFreePlugin.get(), () -> {
                        ProxcordVoiceJoinEvent event = new ProxcordVoiceJoinEvent(player, discordId);
                        ProxcordFreePlugin.get().getServer().getPluginManager().callEvent(event);
                    });
                },
                error -> ProxcordFreePlugin.get().getLogger().warning("[Proxcord] Failed move: " + error.getMessage())
        );
    }

    public static long getGuildId() { return guildId; }
    public static long getNearChannelId() { return nearChannelId; }
    public static long getGlobalChannelId() { return globalChannelId; }
    public static boolean isGlobal() { return VoiceModeManager.isGlobal(); }

    public static void startGlobalVoiceLoop() {
        ProxcordFreePlugin plugin = ProxcordFreePlugin.get();
        if (plugin == null) return;

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!configValid || !DiscordBot.isReady()) return;

            JDA jda = DiscordBot.getJda();
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return;

            VoiceChannel targetChannel = null;

            if (VoiceModeManager.isGlobal()) {
                targetChannel = guild.getVoiceChannelById(globalChannelId);
            } else {
                targetChannel = guild.getVoiceChannelById(nearChannelId);
            }

            if (targetChannel == null) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                String discordId = DiscordBot.getLinkedDiscordId(player);
                if (discordId == null) continue;

                Member member = guild.getMemberById(discordId);
                if (member == null || member.getVoiceState() == null) continue;

                VoiceChannel currentVC = member.getVoiceState().getChannel();
                if (currentVC != null && currentVC.getIdLong() == targetChannel.getIdLong()) continue;

                guild.moveVoiceMember(member, targetChannel).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("[Proxcord] Failed to move " + player.getName() + ": " + error.getMessage())
                );
            }
        }, 40L, 100L);
    }
}
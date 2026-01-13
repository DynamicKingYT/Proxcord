package com.proxcord.free.proximity;

import com.proxcord.free.ProxcordFreePlugin;
import com.proxcord.free.voice.VoiceChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ProximityManager {

    private static Map<UUID, Set<UUID>> nearbyMap = new HashMap<>();

    public static void start() {
        ProxcordFreePlugin plugin = ProxcordFreePlugin.get();

        int range = plugin.getConfig().getInt("proximity.range", 15);
        int interval = plugin.getConfig().getInt("proximity.scan-interval-ticks", 20);

        plugin.getLogger().info("[Proxcord] Starting proximity scanner. Range=" + range
                + " blocks, Interval=" + interval + " ticks.");

        new BukkitRunnable() {
            @Override
            public void run() {
                scan(range);
            }
        }.runTaskTimer(plugin, 20L, interval); // start after 1s, then every interval
    }

    private static void scan(int range) {
        double rangeSquared = range * range;
        Map<UUID, Set<UUID>> newMap = new HashMap<>();

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player p : players) {
            UUID pu = p.getUniqueId();
            Set<UUID> nearby = new HashSet<>();

            for (Player other : players) {
                if (p == other) continue;
                if (!p.getWorld().equals(other.getWorld())) continue;

                double distSq = p.getLocation().distanceSquared(other.getLocation());
                if (distSq <= rangeSquared) {
                    nearby.add(other.getUniqueId());
                }
            }

            newMap.put(pu, nearby);
        }

        nearbyMap = newMap;

        // Tell VoiceChannelManager about each player's proximity state
        for (Player p : players) {
            Set<UUID> nearby = nearbyMap.getOrDefault(p.getUniqueId(), Collections.emptySet());
            boolean hasNearby = !nearby.isEmpty();
            VoiceChannelManager.handleProximityState(p, hasNearby);
        }
    }

    public static Set<UUID> getNearby(Player player) {
        return nearbyMap.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }
}

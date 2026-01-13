package com.proxcord.free;

import com.proxcord.free.discord.DiscordBot;
import com.proxcord.free.gui.VoiceGuiListener;
import com.proxcord.free.proximity.ProximityManager;
import com.proxcord.free.voice.VoiceChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ProxcordFreePlugin extends JavaPlugin {

    private static ProxcordFreePlugin instance;

    public static ProxcordFreePlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // 1. Hooking DiscordSRV with a delay to ensure it starts first
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
                try {
                    // We use the full package path to bypass "getApi" errors
                    github.scarsz.discordsrv.DiscordSRV dsrv = github.scarsz.discordsrv.DiscordSRV.getPlugin();
                    github.scarsz.discordsrv.dependencies.jda.api.JDA jda = dsrv.getJda();
                    String guildId = dsrv.getMainGuild().getId();

                    if (jda != null) {
                        DiscordBot.initialize(jda, guildId);
                        getLogger().info("[Proxcord] DiscordSRV hook successful!");
                    }
                } catch (Exception e) {
                    getLogger().warning("[Proxcord] Error hooking DiscordSRV: " + e.getMessage());
                }
            } else {
                getLogger().warning("[Proxcord] DiscordSRV not found! Muting features will not work.");
            }
        }, 40L); // 2 second delay

        // 2. Load Internal Systems
        VoiceChannelManager.reloadFromConfig();
        ProximityManager.start();

        // 3. Register Event Listeners
        Bukkit.getPluginManager().registerEvents(new VoiceGuiListener(this), this);

        getLogger().info("Proxcord FREE version enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Proxcord FREE version disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("voice")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            if (!player.hasPermission("proxcord.voice")) {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
            com.proxcord.free.gui.VoiceGui.openFor(player);
            return true;
        }
        return false;
    }
}
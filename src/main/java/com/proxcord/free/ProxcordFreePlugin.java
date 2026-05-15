package com.proxcord.free;

import com.proxcord.free.discord.DiscordBot;
import com.proxcord.free.gui.VoiceGui;
import com.proxcord.free.gui.VoiceGuiListener;
import com.proxcord.free.listeners.DiscordVoiceEventListener;
import com.proxcord.free.listeners.PlayerJoinVoiceListener;
import com.proxcord.free.proximity.ProximityManager;
import com.proxcord.free.utils.LanguageManager;
import com.proxcord.free.voice.VoiceChannelManager;
import com.proxcord.free.voice.VoiceModeManager;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import org.bstats.bukkit.Metrics;
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

        // 1. Load Language
        LanguageManager.load();

        // Register command executor
        getCommand("voice").setExecutor(this);

        // 2. bStats
        try {
            new Metrics(this, 29243);
        } catch (Exception ignored) {}

        // 3. Update Checker
        try {
            getServer().getPluginManager().registerEvents(new ModrinthUpdateChecker(this, "Hauz1asI"), this);
        } catch (Exception ignored) {}

        // 4. Events
        getServer().getPluginManager().registerEvents(new VoiceGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinVoiceListener(), this);

        // 5. PlaceholderAPI Hook (Tier 3.5 Feature)
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new com.proxcord.free.papi.ProxcordExpansion(this).register();
            getSLF4JLogger().info("PlaceholderAPI hooked!");
        }

        // 6. Hook DiscordSRV
        if (getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            getSLF4JLogger().info("DiscordSRV found! Waiting for connection...");
            DiscordSRV.api.subscribe(this);
        } else {
            getSLF4JLogger().error("DiscordSRV is missing!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            DiscordSRV.api.unsubscribe(this);
        }
        instance = null;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onDiscordReady(DiscordReadyEvent event) {
        getSLF4JLogger().info("DiscordSRV is ready! Hooking Proxcord now...");

        try {
            github.scarsz.discordsrv.DiscordSRV dsrv = github.scarsz.discordsrv.DiscordSRV.getPlugin();

            if (dsrv.getJda() != null && dsrv.getMainGuild() != null) {
                // Static initialization (No "new Class()")
                DiscordBot.initialize(dsrv.getJda(), dsrv.getMainGuild().getId());
                dsrv.getJda().addEventListener(new DiscordVoiceEventListener());
                VoiceChannelManager.reloadFromConfig();
                VoiceChannelManager.startGlobalVoiceLoop();
                ProximityManager.start();

                getSLF4JLogger().info("✅ Proxcord hooked!");
            }
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to initialize Discord hook.", e);
        }
    }

    // ✅ THE SILVER BULLET FIX (Zero Warnings)
    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        getLogger().info("Received command '" + command.getName() + "' from " + sender.getName());

        if (command.getName().equalsIgnoreCase("voice")) {
            getLogger().info("Handling '/voice' command...");

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("proxcord.admin")) {
                    sender.sendMessage(LanguageManager.get("no-permission"));
                    return true;
                }
                reloadConfig();
                LanguageManager.load();
                VoiceChannelManager.reloadFromConfig();

                sender.sendMessage(LanguageManager.get("reload-success"));
                return true;
            }

            if (args.length >= 2 && args[0].equalsIgnoreCase("global")) {
                if (!sender.hasPermission("proxcord.admin")) {
                    sender.sendMessage(LanguageManager.get("no-permission"));
                    return true;
                }

                String modeArg = args[1].toLowerCase();
                if ("on".equals(modeArg)) {
                    getConfig().set("voice-mode", "global");
                    saveConfig();
                    VoiceModeManager.reloadConfig();
                    sender.sendMessage(LanguageManager.get("global-mode-enabled"));
                } else if ("off".equals(modeArg)) {
                    getConfig().set("voice-mode", "proximity");
                    saveConfig();
                    VoiceModeManager.reloadConfig();
                    sender.sendMessage(LanguageManager.get("proximity-mode-enabled"));
                } else {
                    sender.sendMessage(LanguageManager.get("invalid-mode"));
                }
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage(LanguageManager.get("only-players"));
                return true;
            }

            if (!player.hasPermission("proxcord.voice")) {
                player.sendMessage(LanguageManager.get("no-permission"));
                return true;
            }

            getLogger().info("Opening voice GUI for player: " + player.getName());
            VoiceGui.openFor(player);
            return true;
        }
        return false;
    }
}
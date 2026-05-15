package com.proxcord.free.gui;

import com.proxcord.free.ProxcordFreePlugin;
import com.proxcord.free.discord.DiscordBot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VoiceGuiListener implements Listener {

    private final ProxcordFreePlugin plugin;

    public VoiceGuiListener(ProxcordFreePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        // ONLY react to player clicks
        if (!(e.getWhoClicked() instanceof Player player)) return;

        Inventory inv = e.getClickedInventory();
        if (inv == null) return;

        // Only handle our menu
        if (!e.getView().getTitle().equals(VoiceGui.TITLE)) return;

        // Block taking items
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;

        Material type = clicked.getType();

        // MUTE
        if (type == Material.RED_WOOL) {
            DiscordBot.mute(player);
            player.sendMessage(ChatColor.RED + "You are now muted in Discord.");
            return;
        }

        // UNMUTE
        if (type == Material.LIME_WOOL) {
            DiscordBot.unmute(player);
            player.sendMessage(ChatColor.GREEN + "You are now unmuted in Discord.");
        }
    }
}

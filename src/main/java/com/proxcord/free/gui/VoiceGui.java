package com.proxcord.free.gui;

import com.proxcord.free.bedrock.BedrockDetector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VoiceGui {

    public static final String TITLE = ChatColor.DARK_AQUA + "Proxcord Voice";

    public static void openFor(Player player) {
        if (!BedrockDetector.isBedrock(player)) {
            player.sendMessage(ChatColor.RED + "This menu is only for Bedrock players. Java players mute in Discord directly.");
            return;
        }

        Inventory inv = Bukkit.createInventory(player, 9, TITLE);

        // Mute button
        ItemStack mute = new ItemStack(Material.RED_WOOL);
        ItemMeta muteMeta = mute.getItemMeta();
        if (muteMeta != null) {
            muteMeta.setDisplayName(ChatColor.RED + "Mute in Discord");
            mute.setItemMeta(muteMeta);
        }

        // Unmute button
        ItemStack unmute = new ItemStack(Material.LIME_WOOL);
        ItemMeta unmuteMeta = unmute.getItemMeta();
        if (unmuteMeta != null) {
            unmuteMeta.setDisplayName(ChatColor.GREEN + "Unmute in Discord");
            unmute.setItemMeta(unmuteMeta);
        }

        inv.setItem(3, mute);
        inv.setItem(5, unmute);

        player.openInventory(inv);
    }
}

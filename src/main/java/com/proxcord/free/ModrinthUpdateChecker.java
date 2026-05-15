package com.proxcord.free;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI; // Required for the new URL fix
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class ModrinthUpdateChecker implements Listener {

    private final JavaPlugin plugin;
    private final String projectId;
    private String latestVersion;
    private boolean updateAvailable = false;

    public ModrinthUpdateChecker(JavaPlugin plugin, String projectId) {
        this.plugin = plugin;
        this.projectId = projectId;
        checkForUpdates();
    }

    @SuppressWarnings("deprecation") // Silences 'getDescription' warning safely
    private void checkForUpdates() {
        CompletableFuture.runAsync(() -> {
            try {
                // FIX 1: Use URI.create().toURL() instead of new URL() (Java 20+ requirement)
                URL url = URI.create("https://api.modrinth.com/v2/project/" + projectId + "/version").toURL();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // 'getDescription()' is deprecated in Paper but standard in Spigot.
                // The @SuppressWarnings above handles this line.
                String currentVersion = plugin.getDescription().getVersion();

                connection.setRequestProperty("User-Agent", "ProxcordDev/Proxcord/" + currentVersion);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {

                        JsonElement jsonElement = JsonParser.parseReader(reader);

                        if (jsonElement.isJsonArray()) {
                            JsonArray versions = jsonElement.getAsJsonArray();
                            if (!versions.isEmpty()) {
                                latestVersion = versions.get(0).getAsJsonObject().get("version_number").getAsString();

                                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                                    updateAvailable = true;

                                    // FIX 2: Use {} placeholders for logging instead of '+' (SLF4J Standard)
                                    plugin.getSLF4JLogger().warn("Proxcord Update Available: {}", latestVersion);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                // Keep it silent if check fails
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (updateAvailable && (event.getPlayer().isOp() || event.getPlayer().hasPermission("proxcord.admin"))) {
            event.getPlayer().sendMessage(Component.text("New Proxcord update: " + latestVersion, NamedTextColor.GREEN));
            event.getPlayer().sendMessage(Component.text("Download at Modrinth.", NamedTextColor.GRAY));
        }
    }
}
# 🎙️ Proxcord

**Proxcord** is a proximity-based voice chat bridge that allows **Minecraft Bedrock** and **Java Edition** players to communicate seamlessly using Discord. Designed specifically for Spigot, Paper, and Purpur servers, it bridges the gap in cross-play communities.

![Proxcord Logo](https://link-to-your-uploaded-image.png)

## ✨ Features
* **Cross-Platform Communication:** Allows Bedrock players (via Geyser/Floodgate) to talk to Java players.
* **Proximity Voice:** Volume adjusts dynamically based on the distance between players in-game.
* **Low Latency:** Offloads audio processing to Discord to keep your Minecraft server running smoothly.
* **Auto-Link:** Seamlessly connects Minecraft UUIDs to Discord IDs.

## 🛠️ Installation

### 1. Discord Bot Setup
1. Create a new application on the [Discord Developer Portal](https://discord.com/developers/applications).
2. Create a Bot, enable "Server Members Intent" and "Message Content Intent."
3. Invite the bot to your server with `Administrator` or `Manage Channels` permissions.
4. Copy your **Bot Token**.

### 2. Server Plugin Setup
1. Download the latest `.jar` from the [Releases](link-to-your-releases) page.
2. Drop it into your server's `/plugins` folder.
3. Restart your server to generate the configuration files.
4. Open `plugins/Proxcord/config.yml` and paste your Discord Bot Token and Voice Channel ID.
5. Restart or reload the plugin.

## 📋 Requirements
* **Java 17+**
* **Spigot/Paper/Purpur 1.16+**
* **Geyser & Floodgate** (Required for Bedrock support)

## ⚖️ License
This project is licensed under the **GNU GPL v3 License** - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request or open an issue for bug reports and feature suggestions.

# Proxcord Free Changelog

## v1.0.2 - 2024-01-20
### Added
- Update checker with direct download links
- Voice reload command (`/voice reload`) without server restart
- PlaceholderAPI integration for account linking status
- Version synchronization between Maven and plugin

### Fixed
- DiscordJDA hook errors (removed unnecessary dependency)
- Network optimization improvements

## v1.0.1 - 2024-01-15
### Added
- Network optimization improvements
- BStats implementation for usage analytics

### Removed
- DiscordJDA dependency (simplified architecture)

## v1.0.0 - 2024-01-10
### Added
- Initial release of Proxcord Free
- Cross-platform proximity voice chat for Bedrock & Java players
- DiscordSRV integration for seamless audio bridging
- GeyserMC & Floodgate compatibility
- Automatic Minecraft-Discord account linking
- Spatial audio with real-time distance-based volume adjustment
- Proximity detection system (15-block default range)
- Global and proximity voice modes
- Voice GUI for mute/unmute controls
- Basic cooldown system for voice switching

### Requirements
- Server: Spigot/Paper 1.16.5+
- Dependencies: GeyserMC, Floodgate, DiscordSRV
- Discord Server with Bot Token

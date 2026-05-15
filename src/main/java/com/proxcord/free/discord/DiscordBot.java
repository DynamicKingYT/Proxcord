package com.proxcord.free.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import org.bukkit.entity.Player;

public class DiscordBot
{

    private static JDA jda;
    private static String guildId;

    public static void initialize(JDA jdaInstance, String guild) {
        jda = jdaInstance;
        guildId = guild;
    }

    public static boolean isReady() {
        return jda != null;
    }

    public static JDA getJda() {
        return jda;
    }

    public static String getLinkedDiscordId(Player player)
    {
        return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
    }
    public static void mute(Player player) {
        String discordId = getLinkedDiscordId(player);
        if (discordId == null || jda == null) return;

        jda.getGuildById(guildId).retrieveMemberById(discordId).queue(member -> {
            if (member != null) member.mute(true).queue();
        });
    }

    public static void unmute(Player player) {
        String discordId = getLinkedDiscordId(player);
        if (discordId == null || jda == null) return;

        jda.getGuildById(guildId).retrieveMemberById(discordId).queue(member -> {
            if (member != null) member.mute(false).queue();
        });
    }
}
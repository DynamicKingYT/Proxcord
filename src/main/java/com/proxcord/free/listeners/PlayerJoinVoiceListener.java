package com.proxcord.free.listeners;

import com.proxcord.free.discord.DiscordBot;
import com.proxcord.free.voice.VoiceChannelManager;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinVoiceListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String discordId = DiscordBot.getLinkedDiscordId(player);

        if (discordId == null) return;

        JDA jda = DiscordBot.getJda();
        if (jda == null) return;

        Guild guild = jda.getGuildById(VoiceChannelManager.getGuildId());
        if (guild == null) return;

        Member member = guild.getMemberById(discordId);
        if (member == null) return;

        VoiceChannel targetChannel = guild.getVoiceChannelById(VoiceChannelManager.isGlobal() ?
                VoiceChannelManager.getGlobalChannelId() :
                VoiceChannelManager.getNearChannelId());

        if (targetChannel == null) return;

        guild.moveVoiceMember(member, targetChannel).queue();
    }
}

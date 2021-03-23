package me.koply.rae.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name = "Sırayı Gör",
        aliases = {"queue", "q", "list"},
        description = "Müzik sırasını görmeye yarar",
        guildOnly = true)
public final class QueueCommand extends JDACommand {

    public QueueCommand() {
        getInfo().setOnFalseCallback((e) -> e.getMessage().addReaction("🤔").queue());
    }

    @Override
    public final boolean handle(MessageReceivedEvent e) {
        if (Utilities.voiceCheck(e)) return false;

        final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        StringBuilder sb = new StringBuilder("```\n");
        sb.append("[X] ").append(manager.audioPlayer.getPlayingTrack().getInfo().title).append("\n");
        for (AudioTrack track : manager.scheduler.queue) {
            sb.append(track.getInfo().title).append("\n");
        }
        sb.append("```");
        e.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Utilities.randomColor())
                .setFooter(e.getJDA().getSelfUser().getName() + " by koply", e.getJDA().getSelfUser().getAvatarUrl())
                .setDescription(sb.toString())
                .setTitle("Müzik Sırası 🎶")
                .build()).queue();
        return true;
    }
}
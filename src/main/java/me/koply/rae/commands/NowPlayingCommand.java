package me.koply.rae.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name = "Çalan şarkı",
        aliases = {"np", "pn"},
        description = "Şu anda çalan şarkıyı görmenize yarar",
        guildOnly = true)
public final class NowPlayingCommand extends JDACommand {

    public NowPlayingCommand() {
        getInfo().setOnFalseCallback((e) -> e.getMessage().addReaction("🤔").queue());
    }

    @Override
    public final boolean handle(MessageReceivedEvent e) {
        if (Utilities.voiceCheck(e)) return false;
        final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        AudioTrack track = manager.audioPlayer.getPlayingTrack();
        long position = track.getPosition();

        e.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Şu an çalıyor 🎶")
                .setDescription("```\n" + track.getInfo().title + "\n-----> "+ Utilities.getKalanSure(position) + "/" + Utilities.getKalanSure(track.getDuration())+" <-----```")
                .setColor(Utilities.randomColor())
                .setFooter(e.getJDA().getSelfUser().getName() + " by koply", e.getJDA().getSelfUser().getAvatarUrl())
                .build()).queue();

        return true;
    }
}
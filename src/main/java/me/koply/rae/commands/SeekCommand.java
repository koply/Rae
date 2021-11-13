package me.koply.rae.commands;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name="Müziği Sar", aliases = {"seek", "se", "sar"}, description = "Müziği ileri geri sarabilirsiniz.", guildOnly = true)
public class SeekCommand extends JDACommand {

    public SeekCommand() {
        getInfo().setOnFalseCallback((e) -> e.getChannel()
                .sendMessage("Saniye olarak çalışır. " +
                        "Başına - koyarak geri alabilirsiniz. " +
                        "Başa sarmak için 0 yazmanız yeterlidir. " +
                        "Müzik çalıyor olmalıdır.").queue());
    }

    @Override
    public boolean handle(MessageReceivedEvent e, String[] args) {
        if (args.length == 1 || Utilities.voiceCheck(e)) return false;

        String ent = args[1];
        boolean negative = false;
        if (ent.startsWith("-")) {
            negative = true;
            ent = ent.substring(1);
        }

        Integer seconds = Utilities.parseInt(ent);
        if (seconds == null) return false;
        GuildMusicManager gmm = PlayerManager.getInstance().getMusicManager(e.getGuild());

        if (seconds==0) gmm.audioPlayer.getPlayingTrack().setPosition(0);

        long addition = seconds*1000;
        long current = gmm.audioPlayer.getPlayingTrack().getPosition();
        long duration = gmm.audioPlayer.getPlayingTrack().getDuration();

        if (negative) current-=addition;
        else current+=addition;

        gmm.audioPlayer.getPlayingTrack().setPosition(Math.min(current, duration));

        e.getMessage().addReaction("🤔").queue();
        return true;
    }
}
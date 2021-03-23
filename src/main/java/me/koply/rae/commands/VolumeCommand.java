package me.koply.rae.commands;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name = "Ses düzeyi",
        aliases = {"volume", "v", "ses"},
        description = "Ses düzeyini ayarlamaya yarar",
        guildOnly = true)
public final class VolumeCommand extends JDACommand {

    public VolumeCommand() {
        getInfo().setOnFalseCallback((e) -> e.getMessage().addReaction("🤔").queue());
    }

    @Override
    public final boolean handle(MessageReceivedEvent e, String[] args) {
        if (args.length == 1) return false;
        int num = 0;
        try {
            num = Integer.parseInt(args[1]);
        } catch (Throwable ignored) {
            return false;
        }

        if (num > 100 || num < 0) return false;
        if (Utilities.voiceCheck(e)) return false;

        final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());

        manager.audioPlayer.setVolume(num);
        e.getMessage().addReaction("👌").queue();

        return true;
    }
}
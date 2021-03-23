package me.koply.rae.commands;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Commando(name = "Müziği Geç",
            aliases = {"skip", "next", "geç", "gec"},
            description = "Müziği geçmenize yarar.",
            guildOnly = true)
public class NextCommand extends JDACommand {

    public NextCommand() {
        getInfo().setOnFalseCallback((e) -> e.getMessage().addReaction("🤔").queue());
    }

    @Override
    public boolean handle(MessageReceivedEvent e) {
        if (Utilities.voiceCheck(e)) return false;
        final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        if (manager.scheduler.queue.size() == 0) {
            e.getMessage().addReaction("🤔").queue();
        } else {
            manager.scheduler.nextTrack();
            e.getMessage().addReaction("😋").queue();
        }
        return true;
    }
}
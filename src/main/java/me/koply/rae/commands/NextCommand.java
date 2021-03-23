package me.koply.rae.commands;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
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
        GuildVoiceState memVoiceState = e.getMember().getVoiceState();
        GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();

        if (memVoiceState.inVoiceChannel() && selfVoiceState.inVoiceChannel()) {
            if (memVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
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

        return false;
    }
}
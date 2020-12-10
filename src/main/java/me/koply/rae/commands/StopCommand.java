package me.koply.rae.commands;

import me.koply.kcommando.internal.KRunnable;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.Commando;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Commando(name="Durdur", aliases={"stop", "durdur"}, description = "Çalan müziği kapatmanıza yarar.", guildOnly = true)
public final class StopCommand extends JDACommand {

    public StopCommand() {
        getInfo().setGuildOnlyCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("⛔").queue());
        getInfo().setOnFalseCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("⛔").queue());
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        GuildVoiceState memVoiceState = e.getMember().getVoiceState();
        GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();

        if (memVoiceState.inVoiceChannel() && selfVoiceState.inVoiceChannel()) {
            if (memVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
                final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
                manager.scheduler.player.stopTrack();
                manager.scheduler.queue.clear();
                e.getMessage().addReaction("👋").queue();
                e.getGuild().getAudioManager().closeAudioConnection();
                return true;
            }
        }

        return false;
    }
}
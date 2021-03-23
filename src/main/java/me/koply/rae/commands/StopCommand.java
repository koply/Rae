package me.koply.rae.commands;

import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.music.GuildMusicManager;
import me.koply.rae.music.PlayerManager;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Commando(name="Durdur", aliases={"stop", "durdur"}, description = "Çalan müziği kapatmanıza yarar.", guildOnly = true)
public final class StopCommand extends JDACommand {

    public StopCommand() {
        getInfo().setGuildOnlyCallback((e) -> e.getMessage().addReaction("⛔").queue());
        getInfo().setOnFalseCallback((e) -> e.getMessage().addReaction("⛔").queue());
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        if (Utilities.voiceCheck(e)) return false;
        final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        manager.scheduler.player.stopTrack();
        manager.scheduler.queue.clear();
        e.getMessage().addReaction("👋").queue();
        e.getGuild().getAudioManager().closeAudioConnection();
        return true;
    }
}
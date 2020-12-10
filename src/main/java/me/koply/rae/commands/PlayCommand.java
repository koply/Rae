package me.koply.rae.commands;

import me.koply.kcommando.internal.KRunnable;
import me.koply.rae.Main;
import me.koply.rae.music.PlayerManager;
import me.koply.rae.util.Utilities;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.Commando;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

@Commando(name="Müzik Çal", aliases={"play", "çal", "p", "oynat"}, description = "Verdiğiniz müziği çalmaya yarayan komuttur.", guildOnly = true)
public final class PlayCommand extends JDACommand {

    private final int prefixLength;
    public PlayCommand() {
        prefixLength = Main.getPrefix().length();
        getInfo().setGuildOnlyCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("⛔").queue());
        getInfo().setOnFalseCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("🤔").queue());
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        if (args.length == 1) return false;
        final GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();

        if (!e.getMember().getVoiceState().inVoiceChannel()) {
            e.getChannel().sendMessage(Utilities.embed("Müzik açabilmek için bir sesli odada olmanız gerekmektedir.")).queue();
            return false;
        }
        final boolean samechannel = e.getMember().getVoiceState().getChannel().getId().equals(selfVoiceState.getChannel().getId());
        if (selfVoiceState.inVoiceChannel() && !samechannel) {
            e.getChannel().sendMessage(Utilities.embed("Müzik açabilmek için benimle aynı odada olmalısın.")).queue();
            return false;
        }

        final AudioManager audioManager = e.getGuild().getAudioManager();
        final VoiceChannel channel = e.getMember().getVoiceState().getChannel();
        if (!selfVoiceState.inVoiceChannel()) {
            audioManager.openAudioConnection(channel);
        }

        String link = e.getMessage().getContentDisplay().substring(prefixLength + args[0].length()).trim();
        link = Utilities.isUrl(link) ? link : "ytsearch:"+link;

        PlayerManager.getInstance().loadAndPlay(e.getTextChannel(), audioManager, link);
        return true;
    }
}
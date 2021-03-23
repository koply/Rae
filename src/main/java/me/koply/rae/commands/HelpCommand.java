package me.koply.rae.commands;

import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.Main;
import me.koply.rae.util.Utilities;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Commando(name="Yardım",aliases={"help", "yardım"}, description = "Komut listesini görmenize yarar.")
public final class HelpCommand extends JDACommand {
    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e) {
        e.getChannel().sendMessage(Main.getHelpEmbed()
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                .setColor(Utilities.randomColor())
                .build()).queue();
        return true;
    }
}
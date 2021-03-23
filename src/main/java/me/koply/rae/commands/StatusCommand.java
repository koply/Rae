package me.koply.rae.commands;

import me.koply.kcommando.internal.annotations.Commando;
import me.koply.rae.Main;
import me.koply.rae.util.Utilities;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Commando(name = "Oynuyor", aliases={"oynuyor"}, description = "Bot sahibinin oynuyor kısmını değiştirmesine yarar.", ownerOnly = true)
public final class StatusCommand extends JDACommand {

    private final int prefixLength;
    public StatusCommand() {
        prefixLength = Main.getConfig().getString("prefix").length();
        getInfo().setOwnerOnlyCallback((e) -> e.getMessage().addReaction("⛔").queue());
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        e.getJDA().getPresence().setActivity(Activity.playing(e.getMessage().getContentDisplay().substring(prefixLength + args[0].length())));
        e.getMessage().addReaction(Utilities.OKEY).queue();
        return true;
    }
}
package me.koply.rae.events;

import me.koply.rae.Main;
import me.koply.rae.commands.MuteCommand;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public final class MessageListener extends ListenerAdapter {

    private final HashSet<String> bypassWords;
    private static String bypassText;
    private final boolean sendMsg;
    private static final HashMap<String, Integer> blockedAttempts = new HashMap<>();

    public MessageListener(HashSet<String> words) {
        bypassWords = words;
        bypassText = Main.getConfig().getString("yasakmesaji");
        sendMsg = Main.getConfig().getBoolean("yasakmesajigonder");
    }

    @Override
    public final void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        String[] args = e.getMessage().getContentDisplay().split(" ");
        for (String arg : args) {
            if (bypassWords.contains(arg)) {
                e.getMessage().delete().queue();
                final String key = e.getGuild().getId()+"-"+e.getAuthor().getId();
                int attempts = blockedAttempts.getOrDefault(key, -1);
                if (attempts == -1) blockedAttempts.put(key, 1);
                else if (attempts >= 2) {
                    MuteCommand.mute(e, e.getMember(), e.getGuild().getRolesByName(MuteCommand.getMuteRoleName(), false), System.currentTimeMillis(), 900_000);
                    blockedAttempts.put(key, 0);
                } else {
                    blockedAttempts.put(key, blockedAttempts.get(key) + 1);
                }
                if (sendMsg) e.getChannel().sendMessage(Utilities.embed(bypassText)).delay(4, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                break;
            }
        }
    }


}
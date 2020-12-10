package me.koply.rae.commands;

import me.koply.kcommando.internal.KRunnable;
import me.koply.rae.data.ReactData;
import me.koply.rae.events.ReactionListener;
import me.koply.rae.util.Utilities;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.Commando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Commando(name = "Temizle",
        aliases={"temizle","sil","clear"},
        description = "Toplu temizlik ♻️",
        guildOnly = true)
public final class ClearCommand extends JDACommand {

    // migrated from kcommando 2.4

    public ClearCommand() {
        getInfo().setGuildOnlyCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("⛔").queue());
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        if (!e.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            e.getMessage().addReaction("⛔").queue();
            return false;
        }
        if (!e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            e.getChannel().sendMessage("Bu işlemi yapabilmek için gereken yetkiye sahip değilim.").queue();
            return false;
        }
        if (args.length == 1) {
            e.getChannel().sendMessage(Utilities.embed("Lütfen geçerli bir sayı girin.")).queue();
            return false;
        }

        int num = 0;
        try {
            num = Integer.parseInt(args[1]);
        } catch (Throwable t) {
            t.printStackTrace();
            e.getChannel().sendMessage(Utilities.embed("Lütfen geçerli bir sayı girin.")).queue();
            return false;
        }

        final List<Message> list = e.getChannel().getHistory().retrievePast(num).complete();
        final Message msg = e.getChannel().sendMessage(new EmbedBuilder().setColor(Utilities.randomColor())
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                .setFooter(e.getJDA().getSelfUser().getName(), e.getJDA().getSelfUser().getAvatarUrl())
                .addField("🌠 Silinecek Olan Mesajlar", "Silinecek olan en eski mesaja gitmek için [tıklayın](" + list.get(list.size()-1).getJumpUrl() +")\nSilme işlemini onaylıyorsanız lütfen onaylama (" + Utilities.OKEY + ") tepkisine tıklayın.", false)
                .build()).complete();

        msg.addReaction(Utilities.OKEY).queue();
        msg.addReaction(Utilities.NOPE).queue();

        ReactionListener.getReactions().put(msg.getId(), new ReactData()
            .setMessage(msg)
            .setEventAuthorID(e.getAuthor().getId())
            .setReactRunnable(e1 -> {
                if (e1.getReactionEmote().getEmoji().equals(Utilities.OKEY)) {
                    e1.getTextChannel().purgeMessages(list);
                    ReactionListener.getReactions().get(e1.getMessageId()).getMessage().delete().queue();
                    e1.getChannel().sendMessage(new EmbedBuilder()
                            .setDescription("Sildim " + Utilities.OKEY)
                            .setColor(Color.GREEN).build()).delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                } else if (e1.getReactionEmote().getEmoji().equals(Utilities.NOPE)) {
                    msg.delete().queue();
                    ReactionListener.getReactions().remove(e1.getMessageId());
                }
            }));
        return true;
    }
}
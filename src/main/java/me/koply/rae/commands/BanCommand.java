package me.koply.rae.commands;

import me.koply.kcommando.internal.KRunnable;
import me.koply.rae.Main;
import me.koply.rae.util.Utilities;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.Commando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;

@Commando(name = "Yasakla",
        aliases={"ban", "yasakla"},
        description = "Kullanıcıyı sunucudan yasaklamanıza yarayan komuttur.",
        guildOnly = true)
public final class BanCommand extends JDACommand {

    // migrated command from kcommando 2.4

    private final int prefixLength;
    public BanCommand() {
        prefixLength = Main.getPrefix().length();
        getInfo().setGuildOnlyCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("⛔").queue());
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        if (!e.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            e.getMessage().addReaction("⛔").queue();
            return false;
        }
        if (!e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            e.getChannel().sendMessage("Bu işlemi yapabilmek için gereken yetkiye sahip değilim.").queue();
            return false;
        }
        if (args.length == 1) {
            e.getChannel().sendMessage(Utilities.embed("Lütfen banlanacak kişiyi etiketleyin.")).queue();
            return false;
        }

        final List<Member> mentioned = e.getMessage().getMentionedMembers();

        if (mentioned.isEmpty()) {
            Member mem = e.getGuild().getMemberById(args[1]);

            if (mem == null) {
                e.getChannel().sendMessage(Utilities.embed("Kişi bulunamadı. Etiketlemeyi deneyin.")).queue();
                return false;
            }

            final String reason = e.getMessage().getContentDisplay().substring(args[0].length() + args[1].length() + prefixLength + 1);
            ex(e, mem, reason);
        } else {
            Member mem = mentioned.get(0);
            final String memname = mem.getUser().getName();
            final String reason = e.getMessage().getContentDisplay().substring(args[0].length() + prefixLength + 1).replaceAll("@" + memname, "").trim();
            ex(e, mem, reason);
        }
        return true;
    }

    private void ex(MessageReceivedEvent e, Member mem, String reason) {
        if (mem == e.getMember()) {
            e.getChannel().sendMessage(Utilities.embed("⛔ Kendini banlayamazsın.")).queue();
        } else if (mem == e.getGuild().getSelfMember()) {
            e.getMessage().addReaction("⛔").queue();
        } else {
            if (!mem.getRoles().isEmpty()) {
                int memHighest = Objects.requireNonNull(Utilities.getHighestFrom(mem)).getPosition();
                if (Objects.requireNonNull(Utilities.getHighestFrom(e.getMember())).getPosition() > memHighest
                        && Objects.requireNonNull(Utilities.getHighestFrom(e.getGuild().getSelfMember())).getPosition() > memHighest) {
                    sendInformationAndBan(e, mem, reason);
                } else {
                    e.getChannel().sendMessage(new EmbedBuilder().setDescription(mem.getUser().getAsMention() + " kişisini banlayamazsın. Yetkisi senle eşit veya senden üstün.").setColor(new Color(234, 79, 66)).build()).queue();
                }
            } else {
                sendInformationAndBan(e, mem, reason);
            }
        }
    }

    private void sendInformationAndBan(MessageReceivedEvent e, Member mem, String reason) {
        e.getChannel().sendMessage(new EmbedBuilder().setColor(Utilities.randomColor())
                .addField("🔨 Üye Başarıyla Banlandı", "`" + reason + "`", false)
                .setFooter(e.getJDA().getSelfUser().getName(), e.getJDA().getSelfUser().getAvatarUrl())
                .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).build()).queue();

        e.getGuild().ban(mem, 0, reason).queue();
    }
}
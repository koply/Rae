package me.koply.rae.commands;

import me.koply.kcommando.internal.KRunnable;
import me.koply.rae.Main;
import me.koply.rae.data.DataManager;
import me.koply.rae.util.Utilities;
import me.koply.kcommando.CronService;
import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.Commando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Commando(name = "Sustur", aliases={"mute","sustur"}, description = "Kullanıcıları susturmanıza yarayan komut.", guildOnly = true)
public final class MuteCommand extends JDACommand {

    private static String muteRoleName;
    public static String getMuteRoleName() { return muteRoleName; }
    private final int prefixLength;

    public MuteCommand() {
        muteRoleName = Main.getConfig().getString("muterolename");
        prefixLength = Main.getPrefix().length();

        getInfo().setGuildOnlyCallback((KRunnable<MessageReceivedEvent>) (e) -> e.getMessage().addReaction("⛔").queue());

        CronService.getInstance().addRunnable(() -> {
            System.gc(); // for cleanup
            DataManager.getIns().saveAllDatas();
            final long ms = System.currentTimeMillis();
            int i = 0;
            for (Map.Entry<String, String> entry : Main.getMuteTimestamps().entrySet()) {
                if (Long.parseLong(entry.getValue())<ms) {
                    String[] ids = entry.getKey().split("-");
                    Guild g = Main.getJda().getGuildById(ids[0]);
                    if (g == null) continue;
                    List<Role> rs = g.getRolesByName(muteRoleName, false);
                    Role r = !rs.isEmpty() ? rs.get(0) : null;
                    if (r == null) continue;
                    g.removeRoleFromMember(ids[1], r).queue();
                    i++;
                    Main.getMuteTimestamps().remove(entry.getKey());
                }
            }
            if (i != 0) System.out.println(i+" kişinin susturulmasının süresi bitti.");
            Main.getMuteTimestamps().forEach((k,v)-> {
            });
        });
    }

    @Override
    public final boolean handle(@NotNull MessageReceivedEvent e, @NotNull String[] args) {
        if (muteRoleName==null || muteRoleName.equals("")) return false;
        if (!e.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            e.getMessage().addReaction("⛔").queue();
            return false;
        }

        if (!e.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            e.getChannel().sendMessage(Utilities.embed("Rol verme yetkim yok :(")).queue();
            return false;
        }

        if (args.length==1) {
            e.getChannel().sendMessage(
                    Utilities.embed("Lütfen susturulacak kişiyi ve süresini girin. Örn: " +
                            Main.getPrefix() + args[0] + " " + e.getJDA().getSelfUser().getAsMention() +
                            " 15m -> Beni 15 dakika susturur. Sınırsız susturmak için süre girmemelisiniz.")).queue();
            return false;
        }

        final List<Member> members = e.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            e.getChannel().sendMessage(Utilities.embed("Lütfen susturulacak kişiyi etiketleyin.")).queue();
            return false;
        }
        final Member mem = members.get(0);
        if (mem.hasPermission(Permission.ADMINISTRATOR)
                || mem.getIdLong() == e.getMember().getIdLong()
                || mem.getIdLong() == e.getGuild().getSelfMember().getIdLong()) {
            e.getMessage().addReaction("⛔").queue();
            return false;
        }

        final List<Role> roles =e.getGuild().getRolesByName(muteRoleName, false);
        if (roles.isEmpty()) {
            e.getChannel().sendMessage(Utilities.embed(muteRoleName + " isimli rol bulunamadı. " + Utilities.NOPE)).queue();
            return false;
        }
        final long currentMs = System.currentTimeMillis();
        long muteTimeMs = -1;
        final String muteTimeStr = e.getMessage().getContentDisplay().substring(args[0].length() + prefixLength + 1).replaceAll("@" + mem.getUser().getName(), "").trim();
        if (!muteTimeStr.equals("")) {
            muteTimeMs = getMillisFromString(muteTimeStr);
        }

        if (0 < muteTimeMs && muteTimeMs < 60000) {
            e.getChannel().sendMessage(Utilities.embed("En az 1 dakika susturabilirsiniz.")).queue();
            return false;
        }

        final Role memhighestrole = Utilities.getHighestFrom(mem);
        if (memhighestrole == null) {
            mute(e, mem, roles, currentMs, muteTimeMs);
        } else {
            if (memhighestrole.getPosition() >= Utilities.getHighestFrom(e.getGuild().getSelfMember()).getPosition()) {
                e.getChannel().sendMessage(Utilities.embed("Bu kişiyi susturabilmem için ondan üstün bir role sahip olmam gerekiyor.")).queue();
            } else if (memhighestrole.getPosition() >= roles.get(0).getPosition()) {
                e.getChannel().sendMessage(Utilities.embed(mem.getAsMention() + " kişisinin " + roles.get(0).getName() + " rolünden daha üstte rolü bulunuyor.")).queue();
            } else {
                mute(e, mem, roles, currentMs, muteTimeMs);
            }
        }
        return true;
    }

    public void mute(@NotNull MessageReceivedEvent e, Member mem, List<Role> roles, long currentMs, long muteTimeMs) {
        e.getGuild().addRoleToMember(mem, roles.get(0)).queue();
        e.getChannel().sendMessage(getEmbed(e.getAuthor(), e.getJDA().getSelfUser(), mem, muteTimeMs)).queue();

        if (muteTimeMs != -1) {
            Main.getMuteTimestamps().put(e.getGuild().getId()+"-"+mem.getId(), currentMs+muteTimeMs+"");
        }
    }

    private long getMillisFromString(String s) {
        final String str = s.toLowerCase();
        final char last = str.charAt(str.length()-1);
        long num = 0L;
        try {
            num = Long.parseLong(str.substring(0, str.length()-1));
        } catch (Throwable t) {
            return -1;
        }
        switch (last) {
            case 's':
                num*=1000;
                break;
            case 'm':
                num*=60_000;
                break;
            case 'h':
                num*=3_600_000;
                break;
            case 'd':
                num*=3_600_000*24;
                break;
            case 'w':
                num*=3_600_000*168;
                break;
        }
        return num;
    }

    private MessageEmbed getEmbed(User u, SelfUser self, Member muted, long left) {
        String leftStr = left == -1 ? " kişisinin susturulması ebediyete dek devam edecek!" : " kişisinin susturmasının bitmesine " + Utilities.getKalanSure(left) + " kaldı.";
        return new EmbedBuilder()
                .setAuthor(u.getName(), null, u.getAvatarUrl())
                .setFooter(self.getName() + " by koply", self.getAvatarUrl())
                .setColor(Utilities.randomColor())
                .addField("Başarıyla Susturuldu " + Utilities.OKEY, muted.getAsMention() + leftStr, false).build();
    }

    // for blacklisted texts
    public static void mute(@NotNull GuildMessageReceivedEvent e, Member mem, List<Role> roles, long currentMs, long muteTimeMs) {
        e.getGuild().addRoleToMember(mem, roles.get(0)).queue();
        if (muteTimeMs != -1) {
            Main.getMuteTimestamps().put(e.getGuild().getId()+"-"+mem.getId(), currentMs+muteTimeMs+"");
        }
    }
}
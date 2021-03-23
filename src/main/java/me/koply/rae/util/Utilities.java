package me.koply.rae.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public final class Utilities {

    public static final String NOPE = "❌";
    public static final String OKEY = "✅";

    public static String readFile(File file) {
        final StringBuilder sb = new StringBuilder();
        try {
            final FileInputStream fs = new FileInputStream(file);
            final InputStreamReader isr = new InputStreamReader(fs, StandardCharsets.UTF_8);
            final BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            fs.close();
            isr.close();
            br.close();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            return sb.toString();
        }
    }

    public static void writeFile(File file, String str) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static final Random random = new Random();
    public static Color randomColor() { return new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)); }

    public static MessageEmbed embed(String txt) {
        return new EmbedBuilder().setColor(randomColor())
                .setDescription(txt).build();
    }

    public static boolean voiceCheck(MessageReceivedEvent e) {
        GuildVoiceState memVoiceState = e.getMember().getVoiceState();
        GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();

        if (!memVoiceState.inVoiceChannel() && !selfVoiceState.inVoiceChannel()) return true;
        if (!memVoiceState.getChannel().equals(selfVoiceState.getChannel())) return true;
        return false;
    }

    /**
     * Get the role that is position highest in the role hierarchy for the given member.
     *
     * @param member The member whos roles should be used.
     * @return Possibly-null, if the user has any roles the role that is ranked highest in the role hierarchy will be returned.
     */
    public static Role getHighestFrom(@NotNull Member member) {
        Checks.notNull(member, "Member object can not be null");

        List<Role> roles = member.getRoles();
        if (roles.isEmpty()) {
            return null;
        }

        return roles.stream().min((first, second) -> {
            if (first.getPosition() == second.getPosition()) {
                return 0;
            }
            return first.getPosition() > second.getPosition() ? -1 : 1;
        }).orElseGet(null);
    }

    public static String getKalanSure(final long ms) {
        final long millis = ms % 1000;
        final long second = (ms / 1000) % 60;
        final long minute = (ms / 60_000) % 60;
        final long hour = (ms / 3_600_000) % 24;
        final long day = (ms/3_600_000) / 24;

        final StringBuilder sb = new StringBuilder();
        short k = 0;
        if (day != 0) {
            sb.append(day).append(" gün");
            k++;
        }
        if (hour != 0) {
            if (k!=0) sb.append(", ");
            sb.append(hour).append(" saat");
            k++;
        }
        if (minute != 0) {
            if (k!=0) sb.append(", ");
            sb.append(minute).append(" dakika");
            k++;
        }
        if (second != 0) {
            if (k!=0) sb.append(", ");
            sb.append(second).append(" saniye");
            k++;
        }
        if (millis != 0) {
            if (k!=0) sb.append(", ");
            sb.append(millis).append(" milisaniye");
        }
        return sb.toString();
    }

    public static boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
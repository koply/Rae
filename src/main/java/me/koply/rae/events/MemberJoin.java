package me.koply.rae.events;

import me.koply.rae.Main;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class MemberJoin extends ListenerAdapter {

    private final String text;
    public MemberJoin() {
        text = Main.getConfig().getString("hosgeldinmesaji");
    }

    @Override
    public final void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        e.getUser().openPrivateChannel().complete().sendMessage(text.replaceAll("\\{\\{member}}", e.getUser().getAsMention())).queue();
    }
}
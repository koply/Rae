package me.koply.rae.events;

import me.koply.rae.data.ReactData;
import me.koply.kcommando.CronService;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public final class ReactionListener extends ListenerAdapter {

    private static final ConcurrentHashMap<String, ReactData> reactions = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ReactData> getReactions() { return reactions; }

    public ReactionListener() {
        CronService.getInstance().addRunnable(() -> {
            long ms = System.currentTimeMillis();
            ReactionListener.getReactions().forEach((k, v) -> {
                int i = 0;
                if (ms-v.getTime()>=300_000) { // reactions are lives for 5 mins
                    ReactionListener.getReactions().remove(k);
                    i++;
                }
                if (i!=0) System.out.println("Cookie cleaner: " + i + " ReactData removed.");
            });
        });
    }

    @Override
    public final void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if (!reactions.containsKey(e.getMessageId())) return;
        if (e.getUser()==null) return; // stupidly
        if (e.getUser().isBot() || e.getUser().getId().equals(e.getJDA().getSelfUser().getId())) return;
        if (!reactions.get(e.getMessageId()).getEventAuthorID().equals(e.getUserId())) return;
        reactions.get(e.getMessageId()).getReactRunnable().run(e);
    }
}
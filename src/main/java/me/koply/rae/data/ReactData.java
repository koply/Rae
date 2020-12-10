package me.koply.rae.data;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public final class ReactData {
    private Message message;
    private IReact reactRunnable;
    private final long time;
    private String eventAuthorID;

    public interface IReact {
        void run(MessageReactionAddEvent e);
    }

    public ReactData() { time = System.currentTimeMillis(); }

    public final Message getMessage() { return message; }
    public final ReactData setMessage(Message message) {
        this.message = message;
        return this;
    }

    public final IReact getReactRunnable() { return reactRunnable; }
    public final ReactData setReactRunnable(IReact reactRunnable) {
        this.reactRunnable = reactRunnable;
        return this;
    }

    public final long getTime() { return time; }

    public final String getEventAuthorID() { return eventAuthorID; }
    public final ReactData setEventAuthorID(String eventAuthorID) {
        this.eventAuthorID = eventAuthorID;
        return this;
    }
}
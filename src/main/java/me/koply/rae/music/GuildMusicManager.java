package me.koply.rae.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public final class GuildMusicManager {
    public final AudioPlayer audioPlayer;
    public final TrackScheduler scheduler;
    private final AudioPlayerSendHandler handler;

    public GuildMusicManager(AudioPlayerManager manager) {
        audioPlayer = manager.createPlayer();
        scheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(scheduler);
        handler = new AudioPlayerSendHandler(audioPlayer);
    }

    public final AudioPlayerSendHandler getHandler() {
        return handler;
    }
}
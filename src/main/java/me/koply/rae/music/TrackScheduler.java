package me.koply.rae.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    private AudioManager audioManager;
    public void setAudioManager(AudioManager mana) { audioManager = mana; }
    public AudioManager getAudioManager() { return audioManager; }

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public final void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public final void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public final void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (queue.size() == 0) {
            player.destroy();
            audioManager.closeAudioConnection();
        } else if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
package me.koply.rae.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;
import java.nio.Buffer;

public final class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final ByteBuffer byteBuffer;
    private final MutableAudioFrame frame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.byteBuffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(byteBuffer);
    }

    @Override
    public final boolean canProvide() {
        return this.audioPlayer.provide(this.frame);
    }

    @Override
    public final ByteBuffer provide20MsAudio() {
        return ((ByteBuffer) ((Buffer) this.byteBuffer).flip());
    }

    @Override
    public final boolean isOpus() {
        return true;
    }
}
package me.koply.rae.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlayerManager {

    private static PlayerManager instance;
    public static PlayerManager getInstance() {
        if (instance == null) instance = new PlayerManager();
        return instance;
    }

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        musicManagers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getHandler());
            return guildMusicManager;
        });
    }

    public final void loadAndPlay(TextChannel c, AudioManager audioManager, String url, boolean isUrl) {
        final GuildMusicManager musicManager = getMusicManager(c.getGuild());
        musicManager.scheduler.setAudioManager(audioManager);
        audioPlayerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                c.sendMessage(getEmbed(audioTrack.getInfo(), c.getJDA().getSelfUser())).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist list) {
                final List<AudioTrack> tracks = list.getTracks();
                final AudioTrack audioTrack = tracks.get(0);
                if (!isUrl) {
                    musicManager.scheduler.queue(audioTrack);
                            c.sendMessage(getEmbed(audioTrack.getInfo(), c.getJDA().getSelfUser())).queue();
                } else {
                    long leftTime = 0;
                    for (AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                        leftTime += track.getInfo().length;
                    }
                    String title = "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")";
                            c.sendMessage(getListEmbed(leftTime, c.getJDA().getSelfUser(), tracks.size(), title)).queue();
                }
            }

            @Override
            public void noMatches() {
                c.sendMessage(Utilities.embed(Utilities.NOPE + " Müzik bulunamadı.")).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                c.sendMessage(Utilities.embed(Utilities.NOPE + " Müzik yüklenirken bir hata oluştu.")).queue();
            }
        });
    }

    private MessageEmbed getEmbed(AudioTrackInfo info, SelfUser u) {
        return new EmbedBuilder()
                .setTitle("Müzik sıraya eklendi! " + Utilities.OKEY )
                .setDescription("["+info.title+"]("+info.uri+") ["+ info.author +"] \n🕙 -> " + Utilities.getKalanSure(info.length))
                .setColor(new Color(60, 143, 62))
                .setFooter(u.getName() + " by koply", u.getAvatarUrl()).build();

    }

    private MessageEmbed getListEmbed(long length, SelfUser u, int count, String title) {
        return new EmbedBuilder()
                .setTitle("Müzikler sıraya eklendi! " + Utilities.OKEY )
		        .addField("🎶Şu an çalan", title, false)
		        .addField("Playlist Detayları", count + " adet müzik \n🕙 " + Utilities.getKalanSure(length), false)
                .setColor(new Color(60, 143, 62))
                .setFooter(u.getName() + " by koply", u.getAvatarUrl()).build();

    }
}

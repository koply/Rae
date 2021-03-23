package me.koply.rae;

import me.koply.rae.commands.HelpCommand;
import me.koply.rae.data.DataManager;
import me.koply.rae.events.ReactionListener;
import me.koply.rae.events.MemberJoin;
import me.koply.rae.events.MessageListener;
import me.koply.kcommando.CommandToRun;
import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Main extends JDAIntegration {

    // all constants
    private static          JSONObject config;
    public static           JSONObject getConfig()
                                    { return new JSONObject(config.toString()); }

    private static          String prefix;
    public static           String getPrefix()
                                    { return prefix; }

    private static          JSONObject database;
    public static           JSONObject getDatabase()
                                    { return database; }

    private static final    EmbedBuilder HELPEMBED = new EmbedBuilder();
    public static           EmbedBuilder getHelpEmbed()
                                    { return new EmbedBuilder(HELPEMBED); }

    private static final    HashSet<String> owners = new HashSet<>();
    public static           HashSet<String> getOwners()
                                    { return new HashSet<>(owners); }

    private static final    ConcurrentHashMap<String, String> muteTimestamps = new ConcurrentHashMap<>();
    public static           ConcurrentHashMap<String, String> getMuteTimestamps()
                                    { return muteTimestamps; }

    private static          JDA jda;
    public static           JDA getJda()
                                    { return jda; } // for unmute cron task

    public Main(JDA jda) {
        super(jda);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {

        // Config and data file initializing
        config = DataManager.getIns().readConfig();
        database = DataManager.getIns().readDatas();
        DataManager.getIns().initAllGuildDatas(muteTimestamps);

        // some configs
        prefix = config.getString("prefix");
        final JSONArray ownersJson = config.getJSONArray("owners");
        ownersJson.forEach((o) -> owners.add(o.toString()));

        // windows and linux shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> DataManager.getIns().saveAllDatas(), "TerminateProcess"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> DataManager.getIns().saveAllDatas(), "Shutdown-thread"));

        // bot initializes
        jda = JDABuilder.createDefault(config.getString("token"),
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableCache(CacheFlag.VOICE_STATE)
                .disableCache(CacheFlag.EMOTE)
                .setAutoReconnect(true).build();
        jda.awaitReady();

        new Main(jda).run();
    }

    private void run() {
        // owners array for kcommando
        final String[] tempowners = new String[owners.size()];
        int i = 0;
        for (String s : owners) {
            tempowners[i] = s;
            i++;
        }

        final KCommando<MessageReceivedEvent> kcm = new KCommando<>(this)
                .setPrefix(config.getString("prefix"))
                .setOwners(tempowners)
                .setPackage(HelpCommand.class.getPackage().getName())
                .setCooldown(config.getLong("cooldown"))
                .build();

        jda.addEventListener(new ReactionListener());

        final String yasaklikelimeler = config.getString("yasaklikelimeler");
        if (!yasaklikelimeler.isEmpty())
            jda.addEventListener(new MessageListener(new HashSet<>(Arrays.asList(yasaklikelimeler.split("-")))));

        final boolean hosgeldin = config.getBoolean("hosgeldinmesajigonder");
        if (hosgeldin) jda.addEventListener(new MemberJoin());

        initHelpEmbed(kcm.getParameters().getCommandMethods(), jda.getSelfUser());
    }

    private void initHelpEmbed(Map<String, CommandToRun<MessageReceivedEvent>> map, SelfUser self) {
        final HashSet<CommandToRun<MessageReceivedEvent>> set = new HashSet<>();
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, CommandToRun<MessageReceivedEvent>> entry : map.entrySet()) {
            if (set.contains(entry.getValue())) continue;
            sb.append("`").append(entry.getKey()).append("` -> ").append(entry.getValue().getClazz().getInfo().getDescription()).append("\n");
            set.add(entry.getValue());
        }
        HELPEMBED.addField("❯ Komutlar", sb.toString(), false)
                .setFooter(self.getName() + " by koply", self.getAvatarUrl());
    }
}
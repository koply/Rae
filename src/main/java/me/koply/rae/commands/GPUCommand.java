package me.koply.rae.commands;

import me.koply.kcommando.integration.impl.jda.JDACommand;
import me.koply.kcommando.internal.annotations.Commando;
import me.koply.kcommando.util.StringUtil;
import me.koply.rae.data.CSVManager;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static me.koply.rae.data.CSVManager.*;

@Commando(name = "GPU", aliases = "gpu", description = "GPU Sorgulama")
public final class GPUCommand extends JDACommand {

    public GPUCommand() {
        getInfo().setOnFalseCallback((e) -> e.getMessage().addReaction("🤔").queue());
    }

    public static class GPUCommandProcessData {
        private EmbedBuilder embed;
        private Message message;
        private boolean outerOK = false;
        private boolean innerOK = false;

        private String modelUrl = null;
    }
    // !gpu nvidia
    @Override
    public boolean handle(MessageReceivedEvent e, String[] args, String prefix) {
        if (args.length < 2) return false;
        GPUCommandProcessData dataSom = new GPUCommandProcessData();
        String text = e.getMessage().getContentDisplay().substring(args[0].length() + prefix.length() + 1);
        Thread th = new Thread(() -> {
            EmbedBuilder embed = findSimilar(text, e, dataSom);
            if (dataSom.outerOK) {
                lastDo(embed, dataSom.message, dataSom.modelUrl);
            } else {
                dataSom.embed = embed;
                dataSom.innerOK = true;
            }
        });
        th.start();
        Message msg = e.getChannel().sendMessage("arıyom bi dk..").complete();
        if (dataSom.innerOK) {
            lastDo(dataSom.embed, msg, dataSom.modelUrl);
        } else {
            dataSom.outerOK = true;
            dataSom.message = msg;
        }
        return true;
    }

    public static class DataNode {
        public String name;
        public String minValue;
        public String avgValue;
        public String maxValue;
    }

    private void lastDo(EmbedBuilder embed, Message message, String url) {
        if (embed != null) {
            message.editMessage(new MessageBuilder()
                    .setEmbed(embed.build())
                    .setContent("**Detaylı veriler yükleniyor...**").build()).queue();

            List<DataNode> nodes = CSVManager.getDetailsGPU(url);
            if (nodes == null) {
                message.editMessage(new MessageBuilder()
                        .setEmbed(embed.build())
                        .setContent("**Detaylı veri bulamadım. Bulduklarım şöyle:**").build()).queue();
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (DataNode no : nodes) {
                sb.append("`").append(no.name).append("`: ").append(no.minValue).append(" - ").append(no.avgValue).append(" - ").append(no.maxValue).append("\n");
            }
            embed.addField("__FPS Türü:__ MIN - ORT - MAX", sb.toString(), false);
            message.editMessage(new MessageBuilder()
                    .setEmbed(embed.build())
                    .setContent("**Bulduğum veriler şöyle:**").build()).queue();
        } else {
            message.editMessage("bulamadım :(").queue();
        }
    }

    private EmbedBuilder findSimilar(String text, MessageReceivedEvent e, GPUCommandProcessData dataSom) {
        // tüm cümle ile benzerlik arayınca sıkıntı çıkıyor, bunu daha temiz bir hale getir
        // sadece model olan kısımlarda arama yapmak lazım
        int mostSimilarityIndex = 0;
        double lastSimilarity = 0;

        for (int i = 0; i<GPU_NAMES.length; i++) {
            double similarity = StringUtil.similarity(GPU_NAMES[i], text);
            if (similarity > lastSimilarity) {
                mostSimilarityIndex = i;
                lastSimilarity = similarity;
            }
        }
        if (mostSimilarityIndex == 0) return null;
        //- 1url 2sample 3bench 4rank 5model 6brand
        String[] line = LINES.get(mostSimilarityIndex);
        dataSom.modelUrl = line[line.length-1];
        return new EmbedBuilder()
                .setAuthor(line[line.length-6] + " - " + line[line.length-5], null, e.getAuthor().getAvatarUrl())
                .addField("Sıralaması", line[line.length-4], true)
                .addField("Benchmark Puanı", line[line.length-3], true)
                .setFooter(line[line.length-2] + " adet test yapıldı. (" + lastSimilarity + ")", e.getJDA().getSelfUser().getAvatarUrl())
                .setColor(Utilities.randomColor());




    }
}
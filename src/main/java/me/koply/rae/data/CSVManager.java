package me.koply.rae.data;

import me.koply.rae.commands.GPUCommand;
import me.koply.rae.util.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CSVManager {
    private static final String GPU_URL = "https://www.userbenchmark.com/resources/download/csv/GPU_UserBenchmarks.csv";
    public static final List<String[]> LINES = new ArrayList<>();
    public static String[] GPU_NAMES;

    static {
        String gpuString = Utilities.readUrl(GPU_URL);
        if (gpuString == null) {
            System.err.println("gpuString null??");
        } else {
            String[] gpuLines = gpuString.split("\n");
            String[] tempNames = new String[gpuLines.length];
            for (int i = 0; i< gpuLines.length; i++) {
                String[] splitted = gpuLines[i].split(",");
                LINES.add(splitted);
                tempNames[i] = splitted[splitted.length-5];
            }
            GPU_NAMES = tempNames;
        }
    }

    public static List<GPUCommand.DataNode> getDetailsGPU(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".para-m-t thead tr td[style='vertical-align:bottom']");

            List<GPUCommand.DataNode> nodes = new ArrayList<>();
            for (Element current : elements) {
                Elements lines = current.select("table tbody tr");
                for (int j = 0; j < lines.size() - 1; j++) {
                    Element selectedTR = lines.get(j);
                    Elements innerTDs = selectedTR.select("td");

                    GPUCommand.DataNode dataNode = new GPUCommand.DataNode();

                    dataNode.minValue = innerTDs.get(0).text();
                    dataNode.maxValue = innerTDs.get(2).text();
                    String[] temp = innerTDs.get(1).text().split(" ");
                    dataNode.name = temp[0];
                    dataNode.avgValue = temp[1];
                    nodes.add(dataNode);
                }
            }
            return nodes;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
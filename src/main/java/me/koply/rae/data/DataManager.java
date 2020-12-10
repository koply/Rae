package me.koply.rae.data;

import me.koply.rae.Main;
import me.koply.rae.util.Utilities;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DataManager {
    private final File[] files = new File[] {new File("config.json"), new File("data.dat")};
    private static DataManager instance;
    public static DataManager getIns() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    private static final String context = "{\n" +
            "\n" +
            "  \"token\": \"TOKEN\",\n" +
            "  \"prefix\": \"PREFIX\",\n" +
            "  \"cooldown\": 1000,\n" +
            "  \"owners\": [\"ID\"],\n" +
            "  \"yasaklikelimeler\": \"\",\n" +
            "  \"yasakmesaji\": \"⛔ Küfür yasak!\",\n" +
            "  \"yasakmesajigonder\": false,\n" +
            "  \"hosgeldinmesaji\": \"Hoş geldin {{member}}! \",\n" +
            "  \"hosgeldinmesajigonder\": false,\n" +
            "  \"muterolename\": \"Susturulmuş\"\n" +
            "\n" +
            "}";

    public DataManager() {
        for (File file : files) {
            if (file.exists()) continue;
            try {
                if (file.createNewFile()) {
                    System.out.println(file.getName() + " dosyası oluşturuldu.");
                    if (file.getName().equals("config.json")) System.out.println("Config dosyasının içini doldurduktan sonra projeyi tekrardan açmanız gerekmektedir.");
                }
            } catch (Throwable t) { t.printStackTrace();
                System.out.println(file.getName() + " dosyası bir hatadan ötürü oluşturulamadı. Lütfen yapımcı ile iletişime geçin.");
            }
        }
    }

    public JSONObject readConfig() {
        final String configStr = Utilities.readFile(files[0]);
        if (configStr.isEmpty()) {
            System.out.println("Config dosyasının içi boş.");
            Utilities.writeFile(files[0], context);
            System.exit(-1);
        }
        JSONObject tempconfig = null;
        try {
            tempconfig = new JSONObject(configStr);
            System.out.println("Config dosyası başarıyla okundu.");
            return tempconfig;
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Config dosyası okunurken hatayla karşılaşıldı. Config dosyasının yedeğini aldıktan sonra silip tekrar botu açmayı deneyebilirsiniz.");
            System.exit(-1);
        } finally {
            return tempconfig;
        }
    }

    public JSONObject readDatas() {
        final String dataStr = Utilities.readFile(files[1]);
        if (dataStr.isEmpty()) {
            return new JSONObject();
        } else {
            JSONObject tempdata = new JSONObject();
            try {
                tempdata = new JSONObject(dataStr);
                System.out.println("Data dosyası başarıyla okundu.");
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println("Data dosyası okunurken bir hatayla karşılaşıldı. Lütfen yapımcı ile iletişime geçin.");
            } finally {
                return tempdata;
            }
        }
    }

    public void saveAllDatas() {
        final JSONObject main = Main.getDatabase();
        final JSONObject mutes = main.getJSONObject("mutes");
        for (Map.Entry<String, String> entry : Main.getMuteTimestamps().entrySet()) {
            mutes.put(entry.getKey(), entry.getValue());
        }
        Utilities.writeFile(files[1], main.toString());
    }

    public void initAllGuildDatas(ConcurrentHashMap<String, String> map) {
        final JSONObject main = Main.getDatabase();
        final JSONObject mutes = main.optJSONObject("mutes");
        if (mutes == null) {
            main.put("mutes", new JSONObject());
        } else {
            final Iterator<String> keys = mutes.keys();
            while (keys.hasNext()){
                final String key = keys.next();
                map.put(key, mutes.getString(key));
                // map from main
            }
        }
    }
}
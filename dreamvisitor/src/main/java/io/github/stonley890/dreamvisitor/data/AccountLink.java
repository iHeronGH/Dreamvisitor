package io.github.stonley890.dreamvisitor.data;

import io.github.stonley890.dreamvisitor.Dreamvisitor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.stonley890.dreamvisitor.Dreamvisitor.debug;

public class AccountLink {

    static Dreamvisitor plugin = Dreamvisitor.getPlugin();
    static File accountFile = new File(plugin.getDataFolder().getPath() + "/accountLink.txt");

    static Map<String, String> uuidToDiscordIdMap = new HashMap<>();
    static Map<String, String> discordIdToUuidMap = new HashMap<>();

    public static void init() {
        // If file does not exist, create one
        if (!accountFile.exists()) {
            debug("accountLink.txt does not exist. Creating one now...");
            try {
                if (!accountFile.createNewFile()) {
                    Bukkit.getLogger().warning("Unable to create accountLink.txt!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadFromFile();
    }

    private static void loadFromFile() {
        debug("Loading accountLink.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(accountFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String uuid = parts[0];
                    String discordID = parts[1];
                    uuidToDiscordIdMap.put(uuid, discordID);
                    discordIdToUuidMap.put(discordID, uuid);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountFile))) {
            for (Map.Entry<String, String> entry : uuidToDiscordIdMap.entrySet()) {
                String uuid = entry.getKey();
                String discordId = entry.getValue();
                writer.write(uuid + ":" + discordId);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void linkAccounts(String minecraftUUID, String discordId) {
        loadFromFile();
        uuidToDiscordIdMap.put(minecraftUUID.replaceAll("-",""), discordId);
        discordIdToUuidMap.put(discordId, minecraftUUID);
        saveFile();
    }

    public static String getDiscordId(String minecraftUUID) {
        loadFromFile();
        return uuidToDiscordIdMap.get(minecraftUUID.replaceAll("-",""));
    }

    public static String getUuid(String discordId) {
        loadFromFile();
        return discordIdToUuidMap.get(discordId);
    }
}

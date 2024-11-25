package fr.utt.lo02.data;

import com.google.gson.internal.Streams;
import fr.utt.lo02.core.IllegalGameStateExeceptions;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class DataManipulator {
    private static Properties mapProperties = loadMapProperties();
    private static Properties configProperties = loadConfig();
    private static Properties SectorProperties = loadSectorProperties();
    public static Properties getMapProperties() {
        return mapProperties;
    }
    public static Properties getConfigProperties() {
        return configProperties;
    }

    private static String defaultMapProperties =
            "0=14,15,19,24,21,26,23,28,32,33\n" +
                    "1=2,7\n" +
                    "2=1,3,7,8\n" +
                    "3=2,4,8,9\n" +
                    "4=3,5,9,10\n" +
                    "5=4,6,10,11\n" +
                    "6=5,11\n" +
                    "7=1,2,8,12,13\n" +
                    "8=2,3,7,9,13,14\n" +
                    "9=3,4,8,10,14,15\n" +
                    "10=4,5,9,11,15,16\n" +
                    "11=5,6,10,16,17\n" +
                    "12=7,13,18\n" +
                    "13=7,8,12,14,18,19\n" +
                    "14=8,9,13,15,19,0\n" +
                    "15=9,10,14,16,0,24\n" +
                    "16=10,11,15,17,24,25\n" +
                    "17=11,16,25\n" +
                    "18=12,13,19,20,21\n" +
                    "19=13,14,18,0,21\n" +
                    "20=18,21,22\n" +
                    "21=18,19,20,22,23,0\n" +
                    "22=20,21,23,30,31\n" +
                    "23=21,22,31,32,0\n" +
                    "24=15,16,25,26,0\n" +
                    "25=16,17,24,26,27\n" +
                    "26=24,25,27,28,0,29\n" +
                    "27=25,26,29\n" +
                    "28=26,29,33,34,0\n" +
                    "29=26,27,28,34,35\n" +
                    "30=22,31,36\n" +
                    "31=22,23,30,32,36,37\n" +
                    "32=23,31,33,37,38,0\n" +
                    "33=0,28,32,38,39\n" +
                    "34=28,29,33,35,39,40\n" +
                    "35=29,34,40\n" +
                    "36=30,31,37,41,42\n" +
                    "37=31,32,36,38,42,43\n" +
                    "38=32,33,37,39,43,44\n" +
                    "39=33,34,38,40,44,45\n" +
                    "40=34,35,39,45,46\n" +
                    "41=36,42\n" +
                    "42=36,37,41,43\n" +
                    "43=37,38,42,44\n" +
                    "44=38,39,43,45\n" +
                    "45=39,40,44,46\n" +
                    "46=40,45";

    private static Properties writeDefaultProperties() {
        String path = "src/ressources/map.properties";
        Properties properties = new Properties();
        String[] lines = defaultMapProperties.split("\n");
        for (String line : lines) {
            String[] parts = line.split("=");
            properties.setProperty(parts[0], parts[1]);
        }
        // save to file
        try {
            properties.store(new java.io.FileOutputStream(path), null);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return properties;
    }

    private static Properties loadMapProperties() {
        String path = "src/ressources/map.properties";
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            // e.printStackTrace();
            properties = writeDefaultProperties();
        }

        // check properties integrity
        // contains all numbers from 0 to n without missing or duplicate
        int n = properties.size();
        for (int i = 0; i < n; i++) {
            if (!properties.containsKey(String.valueOf(i))) {
                properties = writeDefaultProperties();
                break;
            }
        }

        // only integers in values
        for (Object value : properties.values()) {
            String[] parts = value.toString().split(",");
            for (String part : parts) {
                try {
                    Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    properties = writeDefaultProperties();
                    break;
                }
            }
        }

        // only list of integers in values
        for (Object value : properties.values()) {
            String[] parts = value.toString().split(",");
            for (String part : parts) {
                if (part.isEmpty()) {
                    properties = writeDefaultProperties();
                    break;
                }
            }
        }

        return properties;
    }

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/ressources/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check if numberShipsPerPlayer is an integer and if it exists
        try {
            Integer.parseInt(properties.getProperty("numberShipsPerPlayer"));
        } catch (NumberFormatException e) {
            properties.setProperty("numberShipsPerPlayer", "15");
        }

        return properties;
    }


    public static Set<String> getSavesList() {
        // read all files names in saves folder
        File folder = new File("src/ressources/saves");
        File[] listOfFiles = folder.listFiles();
        Set<String> saves = new HashSet<>();
        if (listOfFiles == null) {
            throw new IllegalGameStateExeceptions("No saves folder found");
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String[] name = file.getName().split("\\.");
                if (name.length == 2 && name[1].equals("json")) {
                    saves.add(name[0]);
                }
            }
        }
        return saves;
    }
    public static String loadSave(String name) {
        File file = new File("src/ressources/saves/" + name + ".json");
        if (file.exists()) {
            List<String> lines;
            try {
                lines = Files.readAllLines(Paths.get(file.getPath()));
                if (lines.size() == 1) {
                    return lines.get(0);
                }
                throw new IllegalGameStateExeceptions("Save file corrupted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalGameStateExeceptions("Save file not found");
    }

    public static Properties getSectorProperties() {
        return SectorProperties;
    }
    public static Properties loadSectorProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/ressources/sector.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check properties integrity
        // contains numberTriPrimeSector, numberMiddleSector, numberBorderSector
        if (!properties.containsKey("numberTriPrimeSector")) {
            properties.setProperty("numberTriPrimeSector", "1");
        }
        if (!properties.containsKey("numberMiddleSector")) {
            properties.setProperty("numberMiddleSector", "2");
        }
        if (!properties.containsKey("numberBorderSector")) {
            properties.setProperty("numberBorderSector", "6");
        }

        // contains SectorSystemsLevel1, SectorSystemsLevel2
        if (!properties.containsKey("SectorSystemsLevel1")) {
            properties.setProperty("SectorSystemsLevel1", "2");
        }
        if (!properties.containsKey("SectorSystemsLevel2")) {
            properties.setProperty("SectorSystemsLevel2", "1");
        }

        return properties;
    }
}
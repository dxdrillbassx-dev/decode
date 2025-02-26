package net.dxdrillbassx.decode.command.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.phys.Vec3;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class HomeData {
    private static final File HOME_FILE = new File("config/homes.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type HOME_TYPE = new TypeToken<Map<UUID, Map<String, Vec3>>>() {}.getType();

    public static void saveHomes(Map<UUID, Map<String, Vec3>> homePoints) {
        try (FileWriter writer = new FileWriter(HOME_FILE)) {
            GSON.toJson(homePoints, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadHomes(Map<UUID, Map<String, Vec3>> homePoints) {
        if (!HOME_FILE.exists()) return;
        try (FileReader reader = new FileReader(HOME_FILE)) {
            Map<UUID, Map<String, Vec3>> loadedHomes = GSON.fromJson(reader, HOME_TYPE);
            if (loadedHomes != null) {
                homePoints.putAll(loadedHomes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

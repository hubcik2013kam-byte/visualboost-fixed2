package pl.visualboost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Ustawienia moda (opcje gry zapisuje sama gra w options.txt). */
public class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("visualboost.json");
	public static Config I = new Config();

	// --- obraz ---
	public boolean fullbright = false;
	public double fullbrightLevel = 10.0; // 2..15
	public int zoomFactor = 4;            // 2..10

	// --- HUD ---
	public boolean hudEnabled = true;
	public boolean hudShowInScreens = false;
	public boolean hudSnap = true;
	public boolean hudGrid = false;
	public boolean invFull = false;
	public String hudLabel = "Visual Boost";
	public List<String> friends = new ArrayList<>();
	public List<String> trackedItems = new ArrayList<>(List.of(
			"minecraft:ender_pearl", "minecraft:golden_apple", "minecraft:experience_bottle"));
	public Map<String, PanelCfg> panels = new LinkedHashMap<>();

	public PanelCfg panel(String id) {
		if (panels == null) panels = new LinkedHashMap<>();
		return panels.computeIfAbsent(id, PanelCfg::defaultFor);
	}

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				Config c = GSON.fromJson(Files.readString(FILE), Config.class);
				if (c != null) I = c;
			}
		} catch (Exception e) {
			VisualBoostClient.LOGGER.warn("Config load failed", e);
		}
		if (I.friends == null) I.friends = new ArrayList<>();
		if (I.trackedItems == null) I.trackedItems = new ArrayList<>();
		if (I.hudLabel == null) I.hudLabel = "";
	}

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(I));
		} catch (Exception e) {
			VisualBoostClient.LOGGER.warn("Config save failed", e);
		}
	}
}

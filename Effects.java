package pl.visualboost;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

/**
 * Fullbright i zoom realizowane przez tymczasowa zmiane opcji gry (gamma / FOV).
 * Poprzednie wartosci sa zapamietywane i przywracane, zeby nie psuc options.txt.
 */
public final class Effects {
	private Effects() { }

	private static boolean fullbrightActive;
	private static double savedGamma;
	private static boolean zooming;
	private static int savedFov;

	public static void tick(Minecraft mc, KeyMapping zoomKey) {
		Options o = mc.options;
		if (o == null) return;
		Config cfg = Config.I;

		// --- fullbright ---
		if (cfg.fullbright) {
			if (!fullbrightActive) {
				savedGamma = o.gamma().get();
				fullbrightActive = true;
			}
			if (Math.abs(o.gamma().get() - cfg.fullbrightLevel) > 1e-6) o.gamma().set(cfg.fullbrightLevel);
		} else if (fullbrightActive) {
			o.gamma().set(savedGamma);
			fullbrightActive = false;
		}

		// --- zoom (przytrzymaj klawisz, tylko gdy zaden ekran nie jest otwarty) ---
		boolean down = zoomKey.isDown() && mc.screen == null;
		if (down) {
			if (!zooming) {
				savedFov = o.fov().get();
				zooming = true;
			}
			int target = Math.max(5, Math.round(savedFov / (float) cfg.zoomFactor));
			if (o.fov().get() != target) o.fov().set(target);
		} else if (zooming) {
			o.fov().set(savedFov);
			zooming = false;
		}
	}

	/** Przywraca opcje przed zamknieciem gry. */
	public static void restore(Minecraft mc) {
		Options o = mc.options;
		if (o == null) return;
		if (fullbrightActive) {
			o.gamma().set(savedGamma);
			fullbrightActive = false;
		}
		if (zooming) {
			o.fov().set(savedFov);
			zooming = false;
		}
	}
}

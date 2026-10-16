package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import pl.visualboost.Config;

/** Wspolrzedne XYZ i predkosc w blokach na sekunde (BPS). */
public class CoordsPanel extends Panel {
	private String line1 = "";
	private String line2 = "";
	private double lastX, lastZ;
	private long lastTime = 0;
	private double bps = 0;

	public CoordsPanel() { super("coords", "Pozycja"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		if (mc.player == null) return;
		double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
		long now = System.currentTimeMillis();
		if (lastTime != 0 && now > lastTime) {
			double dt = (now - lastTime) / 1000.0;
			double dist = Math.hypot(x - lastX, z - lastZ);
			double inst = dist / dt;
			if (inst > 200) inst = 0; // teleport / zmiana wymiaru
			bps = bps * 0.5 + inst * 0.5;
		}
		lastX = x; lastZ = z; lastTime = now;

		line1 = String.format("XYZ: %.0f, %.0f, %.0f", x, y, z);
		line2 = String.format("BPS: %.1f", bps);
		contentW = Math.max(mc.font.width(line1), mc.font.width(line2));
		contentH = 19;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		g.drawString(mc.font, line1, 0, 0, 0xFFFFFFFF, true);
		g.drawString(mc.font, line2, 0, 10, 0xFFCCCCCC, true);
	}
}

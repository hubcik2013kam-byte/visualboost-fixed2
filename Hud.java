package pl.visualboost.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import pl.visualboost.Config;
import pl.visualboost.PanelCfg;
import pl.visualboost.gui.HudEditScreen;

import java.util.List;

/** Rejestr paneli, odswiezanie danych i rysowanie. */
public final class Hud {
	private Hud() { }

	public static final List<Panel> PANELS = List.of(
			new InfoPanel(), new CoordsPanel(), new EffectsPanel(), new CooldownsPanel(),
			new ArmorPanel(), new InventoryPanel(), new KeystrokesPanel(), new PlayerCardPanel(),
			new FriendsPanel(), new ResourcesPanel());

	private static long lastSlowUpdate = 0;

	/** Wywolywane przez Fabric HUD API. */
	public static void render(GuiGraphics g, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		Config cfg = Config.I;
		if (!cfg.hudEnabled || mc.player == null || mc.level == null) return;
		if (mc.screen instanceof HudEditScreen) return; // ekran edycji rysuje panele sam
		if (mc.screen != null && !cfg.hudShowInScreens) return;

		updateAll(mc, cfg, false);
		for (Panel p : PANELS) {
			if (cfg.panel(p.id).enabled) renderPanel(g, mc, p);
		}
	}

	/** includeDisabled = true w trybie edycji, zeby wylaczone panele tez mialy rozmiar. */
	public static void updateAll(Minecraft mc, Config cfg, boolean includeDisabled) {
		long now = System.currentTimeMillis();
		boolean slow = now - lastSlowUpdate >= 250;
		if (slow) lastSlowUpdate = now;
		for (Panel p : PANELS) {
			if (!includeDisabled && !cfg.panel(p.id).enabled) continue;
			if (slow || p.updateEveryFrame() || p.totalW == 0) {
				p.update(mc, cfg);
				p.measure(mc.font);
			}
		}
	}

	/** {x, y, szerokosc, wysokosc} w pikselach ekranu (po uwzglednieniu skali). */
	public static int[] bounds(Minecraft mc, Panel p) {
		PanelCfg pc = Config.I.panel(p.id);
		int sw = mc.getWindow().getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();
		int w = Math.round(p.totalW * pc.scale);
		int h = Math.round(p.totalH * pc.scale);
		int x = Math.round(pc.x * Math.max(0, sw - w));
		int y = Math.round(pc.y * Math.max(0, sh - h));
		return new int[]{x, y, w, h};
	}

	public static void renderPanel(GuiGraphics g, Minecraft mc, Panel p) {
		PanelCfg pc = Config.I.panel(p.id);
		int[] b = bounds(mc, p);
		int w = p.totalW, h = p.totalH;
		int border = Draw.argb(pc.accent, 0.5f);

		var pose = g.pose();
		pose.pushMatrix();
		pose.translate((float) b[0], (float) b[1]);
		pose.scale(pc.scale, pc.scale);

		Draw.roundedFill(g, 0, 0, w, h, 4, Draw.argb(pc.bg, pc.bgAlpha));
		g.hLine(4, w - 5, 0, border);
		g.hLine(4, w - 5, h - 1, border);
		g.vLine(0, 4, h - 5, border);
		g.vLine(w - 1, 4, h - 5, border);

		if (p.hasHeader()) {
			g.drawString(mc.font, p.title, Panel.PAD, Panel.PAD, Draw.opaque(pc.accent), false);
			g.hLine(Panel.PAD, w - Panel.PAD - 1, Panel.PAD + 9, Draw.argb(pc.accent, 0.35f));
		}

		pose.pushMatrix();
		pose.translate((float) Panel.PAD, (float) p.contentTop());
		p.drawContent(g, mc, pc.accent);
		pose.popMatrix();

		pose.popMatrix();
	}
}

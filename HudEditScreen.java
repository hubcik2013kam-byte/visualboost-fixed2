package pl.visualboost.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.visualboost.Config;
import pl.visualboost.PanelCfg;
import pl.visualboost.hud.Hud;
import pl.visualboost.hud.Panel;

/**
 * Tryb edycji HUD: panele przeciaga sie myszka (stan myszy odczytywany w render,
 * wiec nie zalezy od sygnatur zdarzen wejscia). Klikniecie panelu otwiera jego ustawienia.
 */
public class HudEditScreen extends Screen {
	private static final int SIDE_W = 150, SNAP_DIST = 5, GRID = 8;

	private String selected;
	private Panel dragging;
	private int offX, offY;
	private boolean wasDown;
	private boolean needsRebuild;

	public HudEditScreen() {
		super(Component.literal("Edycja HUD"));
	}

	@Override
	protected void init() {
		Config c = Config.I;
		int cx = this.width / 2;

		// pasek narzedzi
		int bw = 82, gap = 4, x0 = cx - (4 * bw + 3 * gap) / 2;
		addRenderableWidget(Button.builder(onOff("Snap", c.hudSnap), b -> {
			c.hudSnap = !c.hudSnap;
			b.setMessage(onOff("Snap", c.hudSnap));
		}).bounds(x0, 6, bw, 20).build());
		addRenderableWidget(Button.builder(onOff("Siatka", c.hudGrid), b -> {
			c.hudGrid = !c.hudGrid;
			b.setMessage(onOff("Siatka", c.hudGrid));
		}).bounds(x0 + (bw + gap), 6, bw, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Reset ukladu"), b -> {
			c.panels.clear();
			rebuildWidgets();
		}).bounds(x0 + 2 * (bw + gap), 6, bw, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Gotowe"), b -> onClose())
				.bounds(x0 + 3 * (bw + gap), 6, bw, 20).build());

		// ustawienia wybranego panelu (prawa kolumna)
		if (selected != null) {
			PanelCfg pc = c.panel(selected);
			int sx = this.width - SIDE_W - 6;
			addRenderableWidget(Button.builder(removeLabel(pc.enabled), b -> {
				pc.enabled = !pc.enabled;
				b.setMessage(removeLabel(pc.enabled));
			}).bounds(sx, 46, SIDE_W, 20).build());
			addRenderableWidget(new ValueSlider(sx, 68, SIDE_W, 20, "Skala %", 50, 200, pc.scale * 100,
					v -> pc.scale = (float) v / 100f));
			addRenderableWidget(new ValueSlider(sx, 90, SIDE_W, 20, "Tlo %", 0, 100, pc.bgAlpha * 100,
					v -> pc.bgAlpha = (float) v / 100f));
			addHex(sx, 122, pc.bg, v -> pc.bg = v);
			addHex(sx, 158, pc.accent, v -> pc.accent = v);
		}
	}

	private void addHex(int x, int y, int value, java.util.function.IntConsumer set) {
		EditBox box = new EditBox(font, x, y, SIDE_W, 20, Component.literal("hex"));
		box.setMaxLength(7);
		box.setValue(String.format("#%06X", value));
		box.setResponder(s -> {
			String t = s.startsWith("#") ? s.substring(1) : s;
			if (t.length() != 6) return;
			try {
				set.accept(Integer.parseInt(t, 16));
			} catch (NumberFormatException ignored) {
				// niepelny HEX - ignorujemy
			}
		});
		addRenderableWidget(box);
	}

	private static Component removeLabel(boolean enabled) {
		return Component.literal(enabled ? "Usun panel" : "Dodaj panel");
	}

	private static Component onOff(String label, boolean on) {
		return Component.literal(label + ": " + (on ? "WL" : "wyl"));
	}

	@Override
	public void tick() {
		if (needsRebuild) {
			needsRebuild = false;
			rebuildWidgets();
		}
	}

	private void select(String id) {
		if ((id == null && selected != null) || (id != null && !id.equals(selected))) {
			selected = id;
			needsRebuild = true;
		}
	}

	private boolean overWidget(int mx, int my) {
		for (GuiEventListener l : this.children()) {
			if (l.isMouseOver(mx, my)) return true;
		}
		return false;
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float delta) {
		super.render(g, mx, my, delta);
		Minecraft mc = Minecraft.getInstance();
		Config cfg = Config.I;
		Hud.updateAll(mc, cfg, true);

		if (cfg.hudGrid) {
			for (int x = 0; x < this.width; x += GRID) g.vLine(x, 0, this.height, 0x14FFFFFF);
			for (int y = 0; y < this.height; y += GRID) g.hLine(0, this.width, y, 0x14FFFFFF);
		}

		handleMouse(mc, cfg, mx, my);

		for (Panel p : Hud.PANELS) {
			PanelCfg pc = cfg.panel(p.id);
			int[] b = Hud.bounds(mc, p);
			boolean sel = p.id.equals(selected);
			if (pc.enabled && cfg.hudEnabled) {
				Hud.renderPanel(g, mc, p);
			} else {
				// wylaczony panel: sam obrys z nazwa, zeby dalo sie go znalezc i wlaczyc
				g.fill(b[0], b[1], b[0] + b[2], b[1] + b[3], 0x22000000);
				g.drawString(font, p.title + " (wyl.)", b[0] + 3, b[1] + 3, 0xFF888888, false);
			}
			int outline = sel ? 0xFFFFFFFF : 0x33FFFFFF;
			g.renderOutline(b[0] - 1, b[1] - 1, b[2] + 2, b[3] + 2, outline);
		}

		if (selected != null) {
			g.drawString(font, "Panel: " + selected, this.width - SIDE_W - 6, 34, 0xFFFFFFFF, true);
			g.drawString(font, "Kolor tla (hex)", this.width - SIDE_W - 6, 112, 0xFFCCCCCC, true);
			g.drawString(font, "Kolor akcentu (hex)", this.width - SIDE_W - 6, 148, 0xFFCCCCCC, true);
		} else {
			g.drawCenteredString(font, Component.literal("Przeciagnij panel myszka. Kliknij panel, zeby edytowac."),
					this.width / 2, 32, 0xFFFFFFFF);
		}
	}

	private void handleMouse(Minecraft mc, Config cfg, int mx, int my) {
		boolean down = mc.mouseHandler.isLeftPressed();

		if (down && !wasDown && !overWidget(mx, my)) {
			Panel hit = null;
			int[] hb = null;
			for (int i = Hud.PANELS.size() - 1; i >= 0; i--) {
				Panel p = Hud.PANELS.get(i);
				int[] b = Hud.bounds(mc, p);
				if (mx >= b[0] && mx < b[0] + b[2] && my >= b[1] && my < b[1] + b[3]) {
					hit = p;
					hb = b;
					break;
				}
			}
			if (hit != null) {
				dragging = hit;
				offX = mx - hb[0];
				offY = my - hb[1];
				select(hit.id);
			} else {
				select(null);
			}
		}

		if (down && dragging != null) {
			PanelCfg pc = cfg.panel(dragging.id);
			int[] b = Hud.bounds(mc, dragging);
			int nx = snap(mx - offX, b[2], this.width, true, dragging, mc, cfg);
			int ny = snap(my - offY, b[3], this.height, false, dragging, mc, cfg);
			int availX = this.width - b[2], availY = this.height - b[3];
			pc.x = availX > 0 ? nx / (float) availX : 0f;
			pc.y = availY > 0 ? ny / (float) availY : 0f;
		}
		if (!down) dragging = null;
		wasDown = down;
	}

	/** Przyciaganie do siatki, krawedzi ekranu, srodka i krawedzi innych paneli. */
	private int snap(int pos, int size, int screen, boolean horizontal, Panel self, Minecraft mc, Config cfg) {
		int max = Math.max(0, screen - size);
		if (cfg.hudGrid) return clamp(Math.round(pos / (float) GRID) * GRID, 0, max);
		if (!cfg.hudSnap) return clamp(pos, 0, max);

		int best = pos;
		int bestDist = SNAP_DIST + 1;
		int[] fixed = {0, max, max / 2};
		for (int cand : fixed) {
			int d = Math.abs(cand - pos);
			if (d < bestDist) { bestDist = d; best = cand; }
		}
		for (Panel o : Hud.PANELS) {
			if (o == self || !cfg.panel(o.id).enabled) continue;
			int[] ob = Hud.bounds(mc, o);
			int start = horizontal ? ob[0] : ob[1];
			int len = horizontal ? ob[2] : ob[3];
			int[] cands = {start + len, start - size, start, start + len - size};
			for (int cand : cands) {
				int d = Math.abs(cand - pos);
				if (d < bestDist) { bestDist = d; best = cand; }
			}
		}
		return clamp(best, 0, max);
	}

	private static int clamp(int v, int lo, int hi) {
		return Math.max(lo, Math.min(hi, v));
	}

	@Override
	public void removed() {
		Config.save();
	}
}

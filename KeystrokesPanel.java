package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import pl.visualboost.Config;

import java.util.ArrayDeque;
import java.util.Deque;

/** Klawisze WASD, spacja, shift, LMB/RMB z podswietleniem i licznikiem CPS. */
public class KeystrokesPanel extends Panel {
	private final Deque<Long> leftClicks = new ArrayDeque<>();
	private final Deque<Long> rightClicks = new ArrayDeque<>();
	private boolean lastL, lastR;
	private boolean w, a, s, d, space, shift, l, r;
	private String cpsL = "0 CPS", cpsR = "0 CPS";
	private int lastCpsL = -1, lastCpsR = -1;

	public KeystrokesPanel() { super("keys", "Klawisze"); }

	@Override public boolean updateEveryFrame() { return true; }

	@Override
	public void update(Minecraft mc, Config cfg) {
		var o = mc.options;
		w = o.keyUp.isDown(); a = o.keyLeft.isDown(); s = o.keyDown.isDown(); d = o.keyRight.isDown();
		space = o.keyJump.isDown(); shift = o.keyShift.isDown();
		l = o.keyAttack.isDown(); r = o.keyUse.isDown();

		long now = System.currentTimeMillis();
		if (l && !lastL) leftClicks.addLast(now);
		if (r && !lastR) rightClicks.addLast(now);
		lastL = l; lastR = r;
		while (!leftClicks.isEmpty() && now - leftClicks.peekFirst() > 1000) leftClicks.pollFirst();
		while (!rightClicks.isEmpty() && now - rightClicks.peekFirst() > 1000) rightClicks.pollFirst();
		if (leftClicks.size() != lastCpsL) { lastCpsL = leftClicks.size(); cpsL = lastCpsL + " CPS"; }
		if (rightClicks.size() != lastCpsR) { lastCpsR = rightClicks.size(); cpsR = lastCpsR + " CPS"; }

		contentW = 70;
		contentH = 94;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		Font f = mc.font;
		key(g, f, 24, 0, 22, 18, "W", w, accent, null);
		key(g, f, 0, 20, 22, 18, "A", a, accent, null);
		key(g, f, 24, 20, 22, 18, "S", s, accent, null);
		key(g, f, 48, 20, 22, 18, "D", d, accent, null);
		key(g, f, 0, 40, 34, 22, "LMB", l, accent, cpsL);
		key(g, f, 36, 40, 34, 22, "RMB", r, accent, cpsR);
		key(g, f, 0, 64, 70, 14, "SPACE", space, accent, null);
		key(g, f, 0, 80, 70, 14, "SHIFT", shift, accent, null);
	}

	private static void key(GuiGraphics g, Font f, int x, int y, int w, int h, String label,
							boolean pressed, int accent, String sub) {
		Draw.roundedFill(g, x, y, w, h, 2, pressed ? Draw.argb(accent, 0.85f) : 0x55000000);
		int color = pressed ? 0xFF101010 : 0xFFFFFFFF;
		if (sub == null) {
			g.drawString(f, label, x + (w - f.width(label)) / 2, y + (h - 8) / 2 + 1, color, false);
		} else {
			g.drawString(f, label, x + (w - f.width(label)) / 2, y + 3, color, false);
			g.drawString(f, sub, x + (w - f.width(sub)) / 2, y + 12, pressed ? 0xFF303030 : 0xFFAAAAAA, false);
		}
	}
}

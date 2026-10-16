package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import pl.visualboost.Config;

/** Pojedynczy panel HUD. Dane sa odswiezane w update(), a draw tylko rysuje to, co jest w cache. */
public abstract class Panel {
	public static final int PAD = 5;
	public static final int HEADER = 12;

	public final String id;
	public final String title;
	public int totalW, totalH;
	protected int contentW, contentH;

	protected Panel(String id, String title) {
		this.id = id;
		this.title = title;
	}

	/** Odswieza cache i ustawia contentW / contentH. */
	public abstract void update(Minecraft mc, Config cfg);

	/** Rysuje zawartosc z poczatkiem ukladu w lewym gornym rogu obszaru tresci. */
	public abstract void drawContent(GuiGraphics g, Minecraft mc, int accent);

	public boolean hasHeader() { return true; }

	public boolean updateEveryFrame() { return false; }

	public final int contentTop() { return PAD + (hasHeader() ? HEADER : 0); }

	public final void measure(Font font) {
		int titleW = hasHeader() ? font.width(title) : 0;
		totalW = Math.max(contentW, titleW) + PAD * 2;
		totalH = contentTop() + contentH + PAD;
	}
}

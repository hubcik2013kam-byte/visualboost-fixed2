package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import pl.visualboost.Config;

import java.util.ArrayList;
import java.util.List;

/** Lokalna lista nickow z oznaczeniem, czy gracz jest teraz na serwerze. */
public class FriendsPanel extends Panel {
	private final List<String> names = new ArrayList<>();
	private final List<Boolean> online = new ArrayList<>();
	private boolean empty;

	public FriendsPanel() { super("friends", "Znajomi"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		names.clear(); online.clear();
		ClientPacketListener conn = mc.getConnection();
		for (String n : cfg.friends) {
			if (names.size() >= 10) break;
			names.add(n);
			online.add(conn != null && conn.getPlayerInfo(n) != null);
		}
		empty = names.isEmpty();
		int w = mc.font.width("dodaj w menu");
		for (String n : names) w = Math.max(w, 9 + mc.font.width(n));
		contentW = w;
		contentH = empty ? 9 : names.size() * 11 - 2;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		if (empty) {
			g.drawString(mc.font, "dodaj w menu", 0, 0, 0xFF888888, true);
			return;
		}
		for (int i = 0; i < names.size(); i++) {
			int y = i * 11;
			g.fill(0, y + 1, 6, y + 7, online.get(i) ? 0xFF55FF55 : 0xFF666666);
			g.drawString(mc.font, names.get(i), 9, y, online.get(i) ? 0xFFFFFFFF : 0xFF999999, true);
		}
	}
}

package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import pl.visualboost.Config;

/** Pasek: wlasny napis, FPS, ping, czas w grze. (TPS pomijamy - klient go nie zna.) */
public class InfoPanel extends Panel {
	private String label = "";
	private String rest = "";
	private int labelW;

	public InfoPanel() { super("info", "Info"); }

	@Override public boolean hasHeader() { return false; }

	@Override
	public void update(Minecraft mc, Config cfg) {
		label = cfg.hudLabel == null ? "" : cfg.hudLabel;
		StringBuilder sb = new StringBuilder();
		sb.append("FPS ").append(mc.getFps());

		ClientPacketListener conn = mc.getConnection();
		if (conn != null && mc.player != null) {
			PlayerInfo info = conn.getPlayerInfo(mc.player.getUUID());
			if (info != null) sb.append("  |  Ping ").append(info.getLatency()).append(" ms");
		}
		if (mc.level != null) {
			long t = mc.level.getDayTime() % 24000L;
			int hours = (int) ((t / 1000 + 6) % 24);
			int minutes = (int) ((t % 1000) * 60 / 1000);
			sb.append("  |  ").append(String.format("%02d:%02d", hours, minutes));
		}
		rest = sb.toString();
		labelW = label.isEmpty() ? 0 : mc.font.width(label) + 8;
		contentW = labelW + mc.font.width(rest);
		contentH = 9;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		if (!label.isEmpty()) g.drawString(mc.font, label, 0, 0, Draw.opaque(accent), true);
		g.drawString(mc.font, rest, labelW, 0, 0xFFFFFFFF, true);
	}
}

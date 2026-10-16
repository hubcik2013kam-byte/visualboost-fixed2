package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import pl.visualboost.Config;

/** Glowa skina, nick, pasek zycia i glodu. */
public class PlayerCardPanel extends Panel {
	private static final int BAR_X = 28, BAR_W = 60;
	private String name = "";
	private float hp, maxHp = 20f;
	private int food;
	private String hpText = "", foodText = "";

	public PlayerCardPanel() { super("player", "Gracz"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		if (mc.player == null) return;
		name = mc.player.getName().getString();
		hp = mc.player.getHealth();
		maxHp = Math.max(1f, mc.player.getMaxHealth());
		food = mc.player.getFoodData().getFoodLevel();
		hpText = String.valueOf(Math.round(hp));
		foodText = String.valueOf(food);
		contentW = BAR_X + BAR_W + 18;
		contentH = 24;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		if (mc.player == null) return;
		PlayerFaceRenderer.draw(g, mc.player.getSkin(), 0, 0, 24);
		g.drawString(mc.font, name, BAR_X, 0, 0xFFFFFFFF, true);

		bar(g, 11, Math.min(1f, hp / maxHp), 0xFFE04050);
		g.drawString(mc.font, hpText, BAR_X + BAR_W + 4, 9, 0xFFFFB0B8, true);
		bar(g, 19, Math.min(1f, food / 20f), 0xFFE0A030);
		g.drawString(mc.font, foodText, BAR_X + BAR_W + 4, 17, 0xFFFFD590, true);
	}

	private static void bar(GuiGraphics g, int y, float ratio, int color) {
		Draw.roundedFill(g, BAR_X, y, BAR_W, 5, 2, 0x66000000);
		int w = Math.round(BAR_W * ratio);
		if (w > 0) Draw.roundedFill(g, BAR_X, y, Math.max(3, w), 5, 2, color);
	}
}

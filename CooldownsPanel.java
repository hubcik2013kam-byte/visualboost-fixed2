package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import pl.visualboost.Config;

import java.util.ArrayList;
import java.util.List;

/** Cooldowny popularnych przedmiotow (procent pozostalego czasu). */
public class CooldownsPanel extends Panel {
	private static final ItemStack[] TRACKED = {
			new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
			new ItemStack(Items.SHIELD), new ItemStack(Items.WIND_CHARGE)
	};
	private static final int ROW = 18, BAR_W = 44;

	private final List<ItemStack> shown = new ArrayList<>();
	private final List<Float> percents = new ArrayList<>();
	private final List<String> texts = new ArrayList<>();
	private boolean empty;

	public CooldownsPanel() { super("cooldowns", "Cooldowny"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		shown.clear(); percents.clear(); texts.clear();
		if (mc.player != null) {
			for (ItemStack s : TRACKED) {
				float p = mc.player.getCooldowns().getCooldownPercent(s, 0f);
				if (p > 0f) {
					shown.add(s);
					percents.add(p);
					texts.add(Math.round(p * 100) + "%");
				}
			}
		}
		empty = shown.isEmpty();
		if (empty) {
			contentW = mc.font.width("brak");
			contentH = 9;
		} else {
			contentW = 20 + BAR_W;
			contentH = shown.size() * ROW - 2;
		}
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		if (empty) {
			g.drawString(mc.font, "brak", 0, 0, 0xFF888888, true);
			return;
		}
		for (int i = 0; i < shown.size(); i++) {
			int y = i * ROW;
			g.renderItem(shown.get(i), 0, y);
			g.drawString(mc.font, texts.get(i), 20, y + 1, 0xFFFFFFFF, true);
			g.fill(20, y + 11, 20 + BAR_W, y + 14, 0x66000000);
			g.fill(20, y + 11, 20 + Math.round(BAR_W * percents.get(i)), y + 14, Draw.opaque(accent));
		}
	}
}

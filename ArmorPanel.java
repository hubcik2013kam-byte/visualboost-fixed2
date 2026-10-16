package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import pl.visualboost.Config;

/** Cztery czesci pancerza z ikona i procentem wytrzymalosci. */
public class ArmorPanel extends Panel {
	private static final EquipmentSlot[] SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	private final ItemStack[] stacks = new ItemStack[4];
	private final String[] pct = new String[4];
	private final int[] pctColor = new int[4];

	public ArmorPanel() { super("armor", "Pancerz"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		for (int i = 0; i < 4; i++) {
			ItemStack s = mc.player == null ? ItemStack.EMPTY : mc.player.getItemBySlot(SLOTS[i]);
			stacks[i] = s;
			if (!s.isEmpty() && s.isDamageableItem()) {
				int p = 100 - s.getDamageValue() * 100 / Math.max(1, s.getMaxDamage());
				pct[i] = p + "%";
				pctColor[i] = p > 50 ? 0xFF7CFC7C : p > 25 ? 0xFFFFD25A : 0xFFFF6060;
			} else {
				pct[i] = "";
			}
		}
		contentW = 4 * 20 - 4;
		contentH = 27;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		for (int i = 0; i < 4; i++) {
			int x = i * 20;
			if (stacks[i].isEmpty()) {
				g.fill(x, 0, x + 16, 16, 0x22FFFFFF);
			} else {
				g.renderItem(stacks[i], x, 0);
			}
			if (!pct[i].isEmpty()) {
				g.drawString(mc.font, pct[i], x + 8 - mc.font.width(pct[i]) / 2, 18, pctColor[i], true);
			}
		}
	}
}

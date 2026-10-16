package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import pl.visualboost.Config;

/** Hotbar albo caly ekwipunek jako siatka ikon. */
public class InventoryPanel extends Panel {
	private static final int CELL = 18;
	private boolean full;

	public InventoryPanel() { super("inventory", "Ekwipunek"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		full = cfg.invFull;
		contentW = 9 * CELL - 2;
		contentH = full ? 4 * CELL + 2 - 2 : CELL - 2;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		if (mc.player == null) return;
		var inv = mc.player.getInventory();
		int rows = full ? 4 : 1;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < 9; c++) {
				// pelny: 3 rzedy glownego ekwipunku (sloty 9-35), potem hotbar (0-8)
				int slot = full ? (r < 3 ? 9 + r * 9 + c : c) : c;
				int x = c * CELL;
				int y = r * CELL + (full && r == 3 ? 2 : 0);
				g.fill(x, y, x + 16, y + 16, 0x22FFFFFF);
				ItemStack s = inv.getItem(slot);
				if (!s.isEmpty()) {
					g.renderItem(s, x, y);
					g.renderItemDecorations(mc.font, s, x, y);
				}
			}
		}
	}
}

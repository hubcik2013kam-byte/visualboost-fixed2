package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import pl.visualboost.Config;

import java.util.ArrayList;
import java.util.List;

/** Liczniki wybranych przedmiotow w ekwipunku (lista w menu, zakladka Teksty). */
public class ResourcesPanel extends Panel {
	private static final int ROW = 18;
	private final List<ItemStack> icons = new ArrayList<>();
	private final List<Item> items = new ArrayList<>();
	private final List<String> counts = new ArrayList<>();
	private String lastSource = null;

	public ResourcesPanel() { super("resources", "Zasoby"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		String source = String.join(",", cfg.trackedItems);
		if (!source.equals(lastSource)) {
			lastSource = source;
			icons.clear(); items.clear();
			for (String s : cfg.trackedItems) {
				if (items.size() >= 8) break;
				Identifier id = Identifier.tryParse(s.trim());
				if (id == null) continue;
				BuiltInRegistries.ITEM.getOptional(id).ifPresent(it -> {
					items.add(it);
					icons.add(new ItemStack(it));
				});
			}
		}
		counts.clear();
		int w = mc.font.width("brak");
		for (Item it : items) {
			int n = 0;
			if (mc.player != null) {
				var inv = mc.player.getInventory();
				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack s = inv.getItem(i);
					if (s.is(it)) n += s.getCount();
				}
			}
			String t = String.valueOf(n);
			counts.add(t);
			w = Math.max(w, 20 + mc.font.width(t));
		}
		contentW = w;
		contentH = items.isEmpty() ? 9 : items.size() * ROW - 2;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		if (items.isEmpty()) {
			g.drawString(mc.font, "brak", 0, 0, 0xFF888888, true);
			return;
		}
		for (int i = 0; i < items.size(); i++) {
			int y = i * ROW;
			g.renderItem(icons.get(i), 0, y);
			g.drawString(mc.font, counts.get(i), 20, y + 4, 0xFFFFFFFF, true);
		}
	}
}

package pl.visualboost.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import pl.visualboost.Config;

import java.util.ArrayList;
import java.util.List;

/** Aktywne efekty: kolorowa kropka, nazwa z poziomem, pozostaly czas. */
public class EffectsPanel extends Panel {
	private final List<String> names = new ArrayList<>();
	private final List<String> times = new ArrayList<>();
	private final List<Integer> colors = new ArrayList<>();
	private int nameW;

	public EffectsPanel() { super("effects", "Efekty"); }

	@Override
	public void update(Minecraft mc, Config cfg) {
		names.clear(); times.clear(); colors.clear();
		if (mc.player != null) {
			for (MobEffectInstance inst : mc.player.getActiveEffects()) {
				String name = inst.getEffect().value().getDisplayName().getString();
				if (inst.getAmplifier() > 0) name += " " + Draw.roman(inst.getAmplifier() + 1);
				names.add(name);
				if (inst.isInfiniteDuration()) {
					times.add("inf");
				} else {
					int sec = inst.getDuration() / 20;
					times.add(String.format("%02d:%02d", sec / 60, sec % 60));
				}
				colors.add(inst.getEffect().value().getColor());
			}
		}
		if (names.isEmpty()) {
			names.add("brak efektow");
			times.add("");
			colors.add(0x666666);
		}
		nameW = 0;
		int timeW = 0;
		for (int i = 0; i < names.size(); i++) {
			nameW = Math.max(nameW, mc.font.width(names.get(i)));
			timeW = Math.max(timeW, mc.font.width(times.get(i)));
		}
		contentW = 9 + nameW + 10 + timeW;
		contentH = names.size() * 11 - 2;
	}

	@Override
	public void drawContent(GuiGraphics g, Minecraft mc, int accent) {
		for (int i = 0; i < names.size(); i++) {
			int y = i * 11;
			g.fill(0, y + 1, 6, y + 7, Draw.opaque(colors.get(i)));
			g.drawString(mc.font, names.get(i), 9, y, 0xFFFFFFFF, true);
			String t = times.get(i);
			g.drawString(mc.font, t, contentW - mc.font.width(t), y, 0xFFBBBBBB, true);
		}
	}
}

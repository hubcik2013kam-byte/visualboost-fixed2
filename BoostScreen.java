package pl.visualboost.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.visualboost.Config;
import pl.visualboost.hud.Hud;
import pl.visualboost.hud.Panel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Menu Visual Boost: zakladki Obraz, Kamera, Wydajn., HUD, Teksty. Zmiany dzialaja od razu. */
public class BoostScreen extends Screen {
	private static final int W = 150, H = 20, GAP = 22;
	private static final String[] TABS = {"Obraz", "Kamera", "Wydajn.", "HUD", "Teksty"};
	private static int lastTab = 0;

	private int tab = lastTab;

	public BoostScreen() {
		super(Component.literal("Visual Boost"));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int tabW = 58, tabGap = 4;
		int startX = cx - (TABS.length * tabW + (TABS.length - 1) * tabGap) / 2;
		for (int i = 0; i < TABS.length; i++) {
			final int idx = i;
			String label = i == tab ? "[" + TABS[i] + "]" : TABS[i];
			addRenderableWidget(Button.builder(Component.literal(label), b -> {
				lastTab = idx;
				tab = idx;
				rebuildWidgets();
			}).bounds(startX + i * (tabW + tabGap), 14, tabW, H).build());
		}

		Options o = Minecraft.getInstance().options;
		Config c = Config.I;
		switch (tab) {
			case 0 -> initImage(o, c, cx);
			case 1 -> initCamera(o, cx);
			case 2 -> initPerformance(o, cx);
			case 3 -> initHud(c, cx);
			default -> initTexts(c, cx);
		}

		addRenderableWidget(Button.builder(Component.literal("Gotowe"), b -> onClose())
				.bounds(cx - W / 2, this.height - 28, W, H).build());
	}

	// ---------------- zakladki ----------------

	private void initImage(Options o, Config c, int cx) {
		int left = cx - W - 6, right = cx + 6, top = 44;
		addRenderableWidget(toggle(left, top, W, "Fullbright", () -> c.fullbright, v -> c.fullbright = v));
		addRenderableWidget(new ValueSlider(left, top + GAP, W, H, "Jasnosc", 2, 15, c.fullbrightLevel,
				v -> c.fullbrightLevel = v));
		addRenderableWidget(optToggle(left, top + GAP * 2, "Cienie postaci", o.entityShadows()));
		addRenderableWidget(new ValueSlider(right, top, W, H, "FOV", 30, 140, o.fov().get(),
				v -> o.fov().set((int) Math.round(v))));
		addRenderableWidget(new ValueSlider(right, top + GAP, W, H, "Zoom x", 2, 10, c.zoomFactor,
				v -> c.zoomFactor = (int) Math.round(v)));
	}

	private void initCamera(Options o, int cx) {
		int left = cx - W - 6, right = cx + 6, top = 44;
		addRenderableWidget(optToggle(left, top, W, "Bujanie kamery", o.bobView()));
		addRenderableWidget(percent(left, top + GAP, "Przechyl przy ranie", o.damageTiltStrength()));
		addRenderableWidget(percent(left, top + GAP * 2, "Efekt FOV (sprint)", o.fovEffectScale()));
		addRenderableWidget(percent(right, top, "Znieksztalcenia", o.screenEffectScale()));
		addRenderableWidget(optToggle(right, top + GAP, W, "Sprint: przelacznik", o.toggleSprint()));
		addRenderableWidget(optToggle(right, top + GAP * 2, W, "Kucanie: przelacznik", o.toggleCrouch()));
	}

	private void initPerformance(Options o, int cx) {
		int left = cx - W - 6, right = cx + 6, top = 44;
		addRenderableWidget(new ValueSlider(left, top, W, H, "Zasieg", 2, 32, o.renderDistance().get(),
				v -> o.renderDistance().set((int) Math.round(v))));
		addRenderableWidget(new ValueSlider(left, top + GAP, W, H, "Max FPS", 10, 260, o.framerateLimit().get(),
				v -> o.framerateLimit().set((int) Math.round(v))));
		addRenderableWidget(new ValueSlider(left, top + GAP * 2, W, H, "Mieszanie biomow", 0, 7,
				o.biomeBlendRadius().get(), v -> o.biomeBlendRadius().set((int) Math.round(v))));
		addRenderableWidget(Button.builder(particleLabel(o), b -> {
			ParticleStatus[] all = ParticleStatus.values();
			o.particles().set(all[(o.particles().get().ordinal() + 1) % all.length]);
			b.setMessage(particleLabel(o));
		}).bounds(right, top, W, H).build());
		addRenderableWidget(Button.builder(Component.literal("Preset: Jakosc"), b -> {
			applyQuality(o);
			rebuildWidgets();
		}).bounds(right, top + GAP, W, H).build());
		addRenderableWidget(Button.builder(Component.literal("Preset: Wydajnosc"), b -> {
			applyPerformance(o);
			rebuildWidgets();
		}).bounds(right, top + GAP * 2, W, H).build());
	}

	private void initHud(Config c, int cx) {
		int colW = 100, colGap = 3, top = 44;
		int x0 = cx - (3 * colW + 2 * colGap) / 2;

		// 12 przelacznikow w 3 kolumnach (wylaczenie = usuniecie panelu z ekranu)
		String[] names = {"HUD", "W menu", "Info", "Pozycja", "Efekty", "Cooldown",
				"Pancerz", "Ekwip.", "Klawisze", "Gracz", "Znajomi", "Zasoby"};
		for (int i = 0; i < names.length; i++) {
			int x = x0 + (i % 3) * (colW + colGap);
			int y = top + (i / 3) * GAP;
			switch (i) {
				case 0 -> addRenderableWidget(toggle(x, y, colW, names[i], () -> c.hudEnabled, v -> c.hudEnabled = v));
				case 1 -> addRenderableWidget(toggle(x, y, colW, names[i], () -> c.hudShowInScreens,
						v -> c.hudShowInScreens = v));
				default -> {
					Panel p = Hud.PANELS.get(i - 2);
					addRenderableWidget(toggle(x, y, colW, names[i], () -> c.panel(p.id).enabled,
							v -> c.panel(p.id).enabled = v));
				}
			}
		}

		int rowY = top + 4 * GAP + 2;
		addRenderableWidget(Button.builder(Component.literal("Edytuj uklad (przeciaganie)"),
				b -> this.minecraft.setScreen(new HudEditScreen())).bounds(cx - W - 6, rowY, W, H).build());
		addRenderableWidget(Button.builder(Component.literal("Ekwipunek: " + (c.invFull ? "pelny" : "hotbar")), b -> {
			c.invFull = !c.invFull;
			b.setMessage(Component.literal("Ekwipunek: " + (c.invFull ? "pelny" : "hotbar")));
		}).bounds(cx + 6, rowY, W, H).build());
	}

	private void initTexts(Config c, int cx) {
		int w = 306, x = cx - w / 2, top = 52;
		addText(x, top, w, c.hudLabel, "Napis na pasku info", s -> c.hudLabel = s);
		addText(x, top + 42, w, String.join(", ", c.friends), "Znajomi (nicki po przecinku)",
				s -> c.friends = splitList(s));
		addText(x, top + 84, w, String.join(", ", c.trackedItems),
				"Zasoby (id przedmiotow po przecinku, np. minecraft:gold_ingot)", s -> c.trackedItems = splitList(s));
	}

	private void addText(int x, int y, int w, String value, String hint, Consumer<String> onChange) {
		EditBox box = new EditBox(font, x, y, w, H, Component.literal(hint));
		box.setMaxLength(256);
		box.setValue(value);
		box.setResponder(onChange);
		box.setHint(Component.literal(hint));
		addRenderableWidget(box);
	}

	private static List<String> splitList(String s) {
		List<String> out = new ArrayList<>();
		for (String part : s.split(",")) {
			String t = part.trim();
			if (!t.isEmpty()) out.add(t);
		}
		return out;
	}

	// ---------------- presety ----------------

	private static void applyQuality(Options o) {
		o.renderDistance().set(16);
		o.biomeBlendRadius().set(5);
		o.entityShadows().set(true);
		o.particles().set(ParticleStatus.ALL);
		o.fovEffectScale().set(1.0);
	}

	private static void applyPerformance(Options o) {
		o.renderDistance().set(8);
		o.biomeBlendRadius().set(1);
		o.entityShadows().set(false);
		o.particles().set(ParticleStatus.DECREASED);
		o.framerateLimit().set(260);
	}

	// ---------------- pomocnicze widgety ----------------

	private static Component particleLabel(Options o) {
		String name = switch (o.particles().get()) {
			case ALL -> "wszystkie";
			case DECREASED -> "mniej";
			case MINIMAL -> "minimum";
		};
		return Component.literal("Czasteczki: " + name);
	}

	private static Button toggle(int x, int y, int w, String label, BooleanSupplier get, Consumer<Boolean> set) {
		return Button.builder(toggleText(label, get.getAsBoolean()), b -> {
			boolean v = !get.getAsBoolean();
			set.accept(v);
			b.setMessage(toggleText(label, v));
		}).bounds(x, y, w, H).build();
	}

	private static Button optToggle(int x, int y, int w, String label, OptionInstance<Boolean> opt) {
		return toggle(x, y, w, label, opt::get, opt::set);
	}

	private static Button optToggle(int x, int y, String label, OptionInstance<Boolean> opt) {
		return optToggle(x, y, W, label, opt);
	}

	private static ValueSlider percent(int x, int y, String label, OptionInstance<Double> opt) {
		return new ValueSlider(x, y, W, H, label, 0, 100, opt.get() * 100, v -> opt.set(v / 100.0));
	}

	private static Component toggleText(String label, boolean on) {
		return Component.literal(label + ": " + (on ? "WL" : "wyl"));
	}

	// ---------------- rysowanie ----------------

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		int cx = this.width / 2;
		switch (tab) {
			case 0 -> g.drawCenteredString(font, Component.literal("Zoom: przytrzymaj klawisz (zmienisz w Sterowanie)"),
					cx, 44 + GAP * 3 + 4, 0xFFAAAAAA);
			case 2 -> g.drawCenteredString(font, Component.literal("Max FPS 260 = bez limitu"),
					cx, 44 + GAP * 3 + 4, 0xFFAAAAAA);
			case 3 -> g.drawCenteredString(font,
					Component.literal("Wylaczenie panelu = usuniecie z ekranu. Dodasz go tu albo w edytorze."),
					cx, 44 + GAP * 5 + 6, 0xFFAAAAAA);
			case 4 -> {
				int x = cx - 153, top = 52;
				g.drawString(font, "Napis na pasku info", x, top - 11, 0xFFFFFFFF, true);
				g.drawString(font, "Znajomi (nicki po przecinku)", x, top + 31, 0xFFFFFFFF, true);
				g.drawString(font, "Zasoby (id przedmiotow, np. minecraft:gold_ingot)", x, top + 73, 0xFFFFFFFF, true);
			}
			default -> { }
		}
	}

	@Override
	public void removed() {
		Config.save();
		Minecraft.getInstance().options.save();
	}
}

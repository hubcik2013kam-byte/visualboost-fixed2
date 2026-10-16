package pl.visualboost;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.visualboost.gui.BoostScreen;
import pl.visualboost.gui.HudEditScreen;
import pl.visualboost.hud.Hud;

public class VisualBoostClient implements ClientModInitializer {
	public static final String MOD_ID = "visualboost";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyMapping openMenuKey;
	private static KeyMapping zoomKey;
	private static KeyMapping editHudKey;

	@Override
	public void onInitializeClient() {
		Config.load();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.visualboost.open_menu",
				InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));
		zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.visualboost.zoom",
				InputConstants.Type.KEYSYM, InputConstants.KEY_C, category));
		editHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.visualboost.edit_hud",
				InputConstants.Type.KEYSYM, InputConstants.KEY_H, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenuKey.consumeClick()) {
				if (client.screen == null) client.setScreen(new BoostScreen());
			}
			while (editHudKey.consumeClick()) {
				if (client.screen == null) client.setScreen(new HudEditScreen());
			}
			Effects.tick(client, zoomKey);
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(Effects::restore);

		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(MOD_ID, "hud"), Hud::render);
	}
}

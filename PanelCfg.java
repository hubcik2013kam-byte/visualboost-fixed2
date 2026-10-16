package pl.visualboost;

/** Ustawienia jednego panelu HUD. x/y to ulamek dostepnego miejsca na ekranie (0..1). */
public class PanelCfg {
	public boolean enabled = true;
	public float x = 0f;
	public float y = 0f;
	public float scale = 1f;
	public float bgAlpha = 0.55f;
	public int bg = 0x0F1018;
	public int accent = 0xFFB7C5;

	public static PanelCfg defaultFor(String id) {
		PanelCfg c = new PanelCfg();
		switch (id) {
			case "info" -> { c.x = 0.5f; c.y = 0.01f; }
			case "coords" -> { c.x = 0.01f; c.y = 0.01f; }
			case "effects" -> { c.x = 0.01f; c.y = 0.18f; }
			case "cooldowns" -> { c.x = 0.01f; c.y = 0.45f; }
			case "armor" -> { c.x = 0.99f; c.y = 0.30f; c.enabled = false; }
			case "inventory" -> { c.x = 0.99f; c.y = 0.55f; c.enabled = false; }
			case "keys" -> { c.x = 0.99f; c.y = 0.99f; }
			case "player" -> { c.x = 0.01f; c.y = 0.99f; }
			case "friends" -> { c.x = 0.99f; c.y = 0.01f; c.enabled = false; }
			case "resources" -> { c.x = 0.01f; c.y = 0.70f; c.enabled = false; }
			default -> { }
		}
		return c;
	}
}

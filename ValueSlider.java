package pl.visualboost.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;

/** Suwak z etykieta i zakresem min..max. */
public class ValueSlider extends AbstractSliderButton {
	private final String label;
	private final double min, max;
	private final DoubleConsumer onChange;

	public ValueSlider(int x, int y, int w, int h, String label, double min, double max, double cur, DoubleConsumer cb) {
		super(x, y, w, h, Component.empty(), (Math.max(min, Math.min(max, cur)) - min) / (max - min));
		this.label = label;
		this.min = min;
		this.max = max;
		this.onChange = cb;
		updateMessage();
	}

	@Override
	protected void updateMessage() {
		if (label == null) return;
		setMessage(Component.literal(label + ": " + Math.round(min + (max - min) * this.value)));
	}

	@Override
	protected void applyValue() {
		onChange.accept(min + (max - min) * this.value);
	}
}

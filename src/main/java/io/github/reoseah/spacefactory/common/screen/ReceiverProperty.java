package io.github.reoseah.spacefactory.common.screen;

import net.minecraft.screen.Property;

import java.util.function.IntConsumer;

/**
 * Write-only {@link Property} that can be initialized with lambda-function.
 *
 * @see SenderProperty
 */
public class ReceiverProperty extends Property {
	protected final IntConsumer consumer;

	public ReceiverProperty(IntConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void set(int value) {
		this.consumer.accept(value);
	}

	@Override
	public int get() {
		return 0;
	}
}

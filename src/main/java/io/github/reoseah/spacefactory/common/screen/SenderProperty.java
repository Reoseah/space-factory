package io.github.reoseah.spacefactory.common.screen;

import net.minecraft.screen.Property;

import java.util.function.IntSupplier;

/**
 * Read-only {@link Property} that can be initialized with lambda-function.
 * <p>
 * Properties are used only to sync screen handler data to client
 * and this logic is naturally closer to screen handler than block entity.
 * So, by high cohesion principle, instead of doing it Mojank way with property delegates,
 * property logic should be moved to screen handler.
 * {@link SenderProperty} on server paired with matching {@link ReceiverProperty} on client allow to achieve this like so:
 * <pre>
 *     // on server
 *     this.addProperty(new SenderProperty(() -> be.getEnergy()));
 *     // on client
 *     this.addProperty(new ReceiverProperty(value -> this.energy = value));
 * </pre>
 */
public class SenderProperty extends Property {
    protected final IntSupplier supplier;

    public SenderProperty(IntSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public int get() {
        return this.supplier.getAsInt();
    }

    @Override
    public void set(int value) {
        throw new UnsupportedOperationException();
    }
}

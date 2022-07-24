package io.github.reoseah.spacefactory.feature.machine.grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.screen.OutputSlot;
import io.github.reoseah.spacefactory.common.screen.ReceiverProperty;
import io.github.reoseah.spacefactory.common.screen.SenderProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public abstract class GrinderScreenHandler extends ScreenHandler {
    protected final Inventory inventory;

    protected GrinderScreenHandler(int syncId, Inventory inventory, PlayerInventory playerInventory) {
        super(SpaceFactory.ScreenHandlerTypes.GRINDER, syncId);

        this.inventory = inventory;

        this.addSlot(new Slot(inventory, GrinderProps.INPUT_SLOT, 44, 36));
        this.addSlot(new Slot(inventory, GrinderProps.ENERGY_SLOT, 8, 52));
        this.addSlot(new OutputSlot(inventory, GrinderProps.OUTPUT_SLOTS_START, 107, 27));
        this.addSlot(new OutputSlot(inventory, GrinderProps.OUTPUT_SLOTS_START + 1, 125, 27));
        this.addSlot(new OutputSlot(inventory, GrinderProps.OUTPUT_SLOTS_START + 2, 107, 45));
        this.addSlot(new OutputSlot(inventory, GrinderProps.OUTPUT_SLOTS_START + 3, 125, 45));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public static class Server extends GrinderScreenHandler {
        public Server(int syncId, GrinderBlockEntity inventory, PlayerInventory playerInventory) {
            super(syncId, inventory, playerInventory);

            this.addProperty(new SenderProperty(() -> inventory.energy));
            this.addProperty(new SenderProperty(() -> inventory.progress));
            this.addProperty(new SenderProperty(() -> inventory.lastRecipe != null && inventory.lastRecipe.isPresent() ? inventory.lastRecipe.get().energy : 0));
        }
    }

    public static class Client extends GrinderScreenHandler {
        private int energy;
        private int progress, total;

        public Client(int syncId, PlayerInventory playerInventory) {
            super(syncId, new SimpleInventory(GrinderProps.INVENTORY_SIZE), playerInventory);
            this.addProperty(new ReceiverProperty(value -> this.energy = value));
            this.addProperty(new ReceiverProperty(value -> this.progress = value));
            this.addProperty(new ReceiverProperty(value -> this.total = value));
        }

        public int getEnergy() {
            return this.energy;
        }

        public int getEnergyDisplay() {
            return this.energy * 20 / GrinderProps.CAPACITY;
        }

        public int getProgress() {
            return this.progress;
        }

        public int getProgressDisplay() {
            int duration = this.total == 0 ? 1000 : this.total;
            return this.progress * 24 / duration;
        }
    }
}

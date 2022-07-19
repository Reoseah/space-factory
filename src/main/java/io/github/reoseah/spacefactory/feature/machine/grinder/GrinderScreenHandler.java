package io.github.reoseah.spacefactory.feature.machine.grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.screen.OutputSlot;
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
        }
    }

    public static class Client extends GrinderScreenHandler {
        public Client(int syncId, PlayerInventory playerInventory) {
            super(syncId, new SimpleInventory(GrinderProps.SLOTS), playerInventory);
        }
    }
}

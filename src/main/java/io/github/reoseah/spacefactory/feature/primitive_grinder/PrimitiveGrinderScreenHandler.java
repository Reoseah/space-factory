package io.github.reoseah.spacefactory.feature.primitive_grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.screen.ReceiverProperty;
import io.github.reoseah.spacefactory.common.screen.SenderProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class PrimitiveGrinderScreenHandler extends ScreenHandler {
    protected final Inventory inventory;

    protected PrimitiveGrinderScreenHandler(int syncId, Inventory inventory, PlayerInventory playerInventory) {
        super(SpaceFactory.ScreenHandlerTypes.PRIMITIVE_GRINDER, syncId);

        this.inventory = inventory;

        this.addSlot(new Slot(inventory, 0, 44, 36));
        this.addSlot(new ResultSlot(playerInventory.player, inventory, 1, 107, 27));
        this.addSlot(new ResultSlot(playerInventory.player, inventory, 2, 125, 27));
        this.addSlot(new ResultSlot(playerInventory.player, inventory, 3, 107, 45));
        this.addSlot(new ResultSlot(playerInventory.player, inventory, 4, 125, 45));

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

    public static class Server extends PrimitiveGrinderScreenHandler {
        Server(int syncId, PrimitiveGrinderBlockEntity inventory, PlayerInventory playerInventory) {
            super(syncId, inventory, playerInventory);

            this.addProperty(new SenderProperty(() -> inventory.progress));
            this.addProperty(new SenderProperty(() -> inventory.duration));
        }
    }

    public static class Client extends PrimitiveGrinderScreenHandler {
        private int progress, total;

        public Client(int syncId, PlayerInventory playerInventory) {
            super(syncId, new SimpleInventory(5), playerInventory);
            this.addProperty(new ReceiverProperty(value -> this.progress = value));
            this.addProperty(new ReceiverProperty(value -> this.total = value));
        }

        public int getProgress() {
            return this.progress;
        }

        public int getProgressDisplay() {
            int duration = this.total == 0 ? 1000 : this.total;
            return this.progress * 24 / duration;
        }
    }

    public static class ResultSlot extends Slot {
        protected final PlayerEntity player;

        public ResultSlot(PlayerEntity player, Inventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            this.player = player;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            super.onTakeItem(player, stack);
        }

        protected void onCrafted(ItemStack stack, int amount) {
            this.onCrafted(stack);
        }

        protected void onCrafted(ItemStack stack) {
            if (!this.player.world.isClient && this.inventory instanceof PrimitiveGrinderBlockEntity be) {
                be.dropExperienceForRecipesUsed((ServerPlayerEntity) this.player);
            }
        }
    }
}

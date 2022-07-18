package io.github.reoseah.spacefactory.mixin.client;

import io.github.reoseah.spacefactory.SF;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(CreativeInventoryScreen.class)
@Environment(value = EnvType.CLIENT)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    private CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(at = @At("HEAD"), method = "renderTabTooltipIfHovered", cancellable = true)
    private void renderSpaceFactoryTooltips(MatrixStack matrices, ItemGroup group, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (group == SF.TECHNOLOGY || group == SF.DECORATION) {
            int column = group.getColumn();
            int x = 28 * column;
            int y = 0;
            if (column > 0) {
                x += column;
            }
            if (group.isTopRow()) {
                y -= 32;
            } else {
                y += this.backgroundHeight;
            }
            if (this.isPointWithinBounds(x + 3, y + 3, 23, 27, mouseX, mouseY)) {
                List<Text> tooltip = Arrays.asList(Text.translatable("spacefactory"), Text.translatable(group.getName()).formatted(Formatting.GRAY));
                this.renderTooltip(matrices, tooltip, mouseX, mouseY);
                cir.setReturnValue(true);
            }
        }
    }
}

package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.feature.machine.electric_furnace.ElectricFurnaceScreen;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderScreen;
import io.github.reoseah.spacefactory.feature.primitive_grinder.CrankBlockEntityRenderer;
import io.github.reoseah.spacefactory.feature.primitive_grinder.PrimitiveGrinderScreen;
import io.github.reoseah.spacefactory.feature.tool.Wrenchable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SpaceFactoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        UnclampedModelPredicateProvider isWrenchOpen = (ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int i) -> {
            if (entity instanceof PlayerEntity player) {
                if ((player.getMainHandStack() == stack) || (player.getOffHandStack() == stack)
                        && MinecraftClient.getInstance().interactionManager != null) {
                    float reachDistance = MinecraftClient.getInstance().interactionManager.getReachDistance();
                    HitResult hit = player.raycast(reachDistance, 0, false);
                    if (hit instanceof BlockHitResult blockHit) {
                        BlockPos pos = blockHit.getBlockPos();
                        if (entity.getEntityWorld().getBlockState(pos).getBlock() instanceof Wrenchable) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        };
        ModelPredicateProviderRegistry.register(SpaceFactory.Items.REFINED_IRON_WRENCH, SpaceFactory.id("open"), isWrenchOpen);
        ModelPredicateProviderRegistry.register(SpaceFactory.Items.NEOSTEEL_WRENCH, SpaceFactory.id("open"), isWrenchOpen);

        BlockEntityRendererRegistry.register(SpaceFactory.BlockEntityTypes.CRANK, CrankBlockEntityRenderer::new);

        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.GRINDER, GrinderScreen::new);
        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.PRIMITIVE_GRINDER, PrimitiveGrinderScreen::new);
    }
}

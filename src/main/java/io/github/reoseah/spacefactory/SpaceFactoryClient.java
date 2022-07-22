package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.feature.machine.electric_furnace.ElectricFurnaceScreen;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderScreen;
import io.github.reoseah.spacefactory.feature.primitive_grinder.CrankBlockEntityRenderer;
import io.github.reoseah.spacefactory.feature.primitive_grinder.PrimitiveGrinderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class SpaceFactoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), SpaceFactory.Blocks.RUBBER_ROOT);

        BlockEntityRendererRegistry.register(SpaceFactory.BlockEntityTypes.CRANK, CrankBlockEntityRenderer::new);

        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.GRINDER, GrinderScreen::new);
        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.PRIMITIVE_GRINDER, PrimitiveGrinderScreen::new);
    }
}

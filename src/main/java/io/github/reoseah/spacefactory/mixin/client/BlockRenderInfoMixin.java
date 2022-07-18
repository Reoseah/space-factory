package io.github.reoseah.spacefactory.mixin.client;

import io.github.reoseah.spacefactory.SpaceFactory;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderInfo.class)
public class BlockRenderInfoMixin {
    @Shadow(remap = false)
    boolean defaultAo;

    @Inject(method = "prepareForBlock", at = @At("RETURN"), remap = false, require = 0)
    public void after_prepareForBlock(BlockState state, BlockPos pos, boolean modelAO, CallbackInfo ci) {
        if (state.getMaterial() == SpaceFactory.Blocks.Materials.MACHINE) {
            this.defaultAo = modelAO && MinecraftClient.isAmbientOcclusionEnabled();
        }
    }
}

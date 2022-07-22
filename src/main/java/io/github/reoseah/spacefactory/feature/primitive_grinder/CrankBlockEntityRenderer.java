package io.github.reoseah.spacefactory.feature.primitive_grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class CrankBlockEntityRenderer implements BlockEntityRenderer<CrankBlockEntity> {
    private static final ModelIdentifier MODEL_ID = new ModelIdentifier(SpaceFactory.id("crank"), "");

    public CrankBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CrankBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BakedModel model = mc.getBakedModelManager().getModel(MODEL_ID);
        BlockPos pos = entity.getPos();
        matrices.push();
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(Vec3f.NEGATIVE_Y.getRadialQuaternion(entity.angle + (entity.ticksToRotate == 0 ? 0 : (float) (3.6 * tickDelta / 180F * Math.PI))));

        matrices.translate(-0.5F, -0.5F, -0.5F);
        mc.getBlockRenderManager().getModelRenderer().render(entity.getWorld(), model, entity.getCachedState(), pos, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, entity.getWorld().getRandom(), 1, 0xFFFFFF);
        matrices.pop();
    }
}

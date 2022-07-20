package io.github.reoseah.spacefactory.feature.machine.grinder;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.reoseah.spacefactory.SpaceFactory;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GrinderScreen extends HandledScreen<GrinderScreenHandler.Client> {
    private static final Identifier TEXTURE = SpaceFactory.id("textures/gui/grinder.png");

    public GrinderScreen(GrinderScreenHandler.Client handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        int leftX = (this.width - this.backgroundWidth) / 2;
        int topY = (this.height - this.backgroundHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawTexture(matrices, leftX, topY, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.handler.getEnergy() > 0) {
            int energy = this.handler.getEnergyDisplay();
            this.drawTexture(matrices, leftX + 11, topY + 44 - energy, 176, 38 - energy, 14, energy + 1);
        }

        int recipeArrowProgress = this.handler.getProgressDisplay();
        if (this.handler.getProgress() > 0) {
            this.drawTexture(matrices, leftX + 70, topY + 34, 176, 0, recipeArrowProgress + 1, 16);
        }
    }
}

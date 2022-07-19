package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SpaceFactoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(SpaceFactory.ScreenHandlerTypes.GRINDER, GrinderScreen::new);
    }
}

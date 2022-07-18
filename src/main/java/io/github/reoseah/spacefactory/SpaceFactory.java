package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.feature.generator.fuel.GeneratorBlock;
import io.github.reoseah.spacefactory.feature.generator.solar.SolarPanelBlock;
import io.github.reoseah.spacefactory.feature.machine.electric_furnace.ElectricFurnaceBlock;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceFactory implements ModInitializer {
    public static final String ID = "spacefactory";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final ItemGroup TECHNOLOGY = FabricItemGroupBuilder.build(id("technology"), () -> new ItemStack(Items.REFINED_IRON_INGOT));
    public static final ItemGroup DECORATION = FabricItemGroupBuilder.build(id("decoration"), () -> new ItemStack(Items.REFINED_IRON_BLOCK));

    @Override
    public void onInitialize() {
        LOGGER.info("initializing {}", ID);

        Blocks.register();
        Items.register();
    }

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    public static class Blocks {
        public static class Materials {
            /**
             * Machine material. Having luminance > 0 on machines won't disable ambient occlusion.
             *
             * @see io.github.reoseah.spacefactory.mixin.client.BlockRenderInfoMixin
             */
            public static final Material MACHINE = new FabricMaterialBuilder(MapColor.IRON_GRAY).build();
        }

        public static final Block GENERATOR = new GeneratorBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 14 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(machineSettings().mapColor(MapColor.BLUE));

        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block GRINDER = new GrinderBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block EXTRACTOR = new GrinderBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));

        public static final Block REFINED_IRON_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL).strength(3F, 6F).sounds(BlockSoundGroup.METAL));

        private static AbstractBlock.Settings machineSettings() {
            return AbstractBlock.Settings.of(Materials.MACHINE).strength(3F).sounds(BlockSoundGroup.METAL);
        }

        public static void register() {
            register("refined_iron_block", REFINED_IRON_BLOCK);

            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);

            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("extractor", EXTRACTOR);
        }

        private static void register(String name, Block entry) {
            Registry.register(Registry.BLOCK, id(name), entry);
        }
    }

    public static class Items {
        public static final Item GENERATOR = new BlockItem(Blocks.GENERATOR, settings(TECHNOLOGY));
        public static final Item SOLAR_PANEL = new BlockItem(Blocks.SOLAR_PANEL, settings(TECHNOLOGY));
        public static final Item ELECTRIC_FURNACE = new BlockItem(Blocks.ELECTRIC_FURNACE, settings(TECHNOLOGY));
        public static final Item GRINDER = new BlockItem(Blocks.GRINDER, settings(TECHNOLOGY));
        public static final Item EXTRACTOR = new BlockItem(Blocks.EXTRACTOR, settings(TECHNOLOGY));

        public static final Item REFINED_IRON_INGOT = new Item(settings(TECHNOLOGY));

        public static final Item REFINED_IRON_BLOCK = new BlockItem(Blocks.REFINED_IRON_BLOCK, settings(DECORATION));

        public static void register() {
            register("refined_iron_block", REFINED_IRON_BLOCK);
            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);

            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("extractor", EXTRACTOR);

            register("refined_iron_ingot", REFINED_IRON_INGOT);
        }

        private static FabricItemSettings settings(ItemGroup group) {
            return new FabricItemSettings().group(group);
        }

        private static void register(String name, Item entry) {
            Registry.register(Registry.ITEM, id(name), entry);
        }
    }
}
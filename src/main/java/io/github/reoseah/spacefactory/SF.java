package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.feature.generator.fuel.GeneratorBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SF implements ModInitializer {
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
        public static final Block REFINED_IRON_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL).strength(3F, 6F).sounds(BlockSoundGroup.METAL).requiresTool());

        public static final Block GENERATOR = new GeneratorBlock(AbstractBlock.Settings.of(Material.METAL).strength(3F).sounds(BlockSoundGroup.METAL).requiresTool());
        public static final Block SOLAR_PANEL = new GeneratorBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.BLUE).strength(3F).sounds(BlockSoundGroup.METAL).requiresTool());

        public static void register() {
            register("refined_iron_block", REFINED_IRON_BLOCK);

            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);
        }

        private static void register(String name, Block entry) {
            Registry.register(Registry.BLOCK, id(name), entry);
        }
    }

    public static class Items {
        public static final Item REFINED_IRON_BLOCK = new BlockItem(Blocks.REFINED_IRON_BLOCK, settings(DECORATION));

        public static final Item GENERATOR = new BlockItem(Blocks.GENERATOR, settings(TECHNOLOGY));
        public static final Item SOLAR_PANEL = new BlockItem(Blocks.SOLAR_PANEL, settings(TECHNOLOGY));

        public static final Item REFINED_IRON_INGOT = new Item(settings(TECHNOLOGY));

        public static void register() {
            register("refined_iron_block", REFINED_IRON_BLOCK);
            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);

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

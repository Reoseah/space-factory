package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.feature.generator.fuel.GeneratorBlock;
import io.github.reoseah.spacefactory.feature.generator.solar.SolarPanelBlock;
import io.github.reoseah.spacefactory.feature.machine.electric_furnace.ElectricFurnaceBlock;
import io.github.reoseah.spacefactory.feature.machine.electric_furnace.ElectricFurnaceBlockEntity;
import io.github.reoseah.spacefactory.feature.machine.electric_furnace.ElectricFurnaceScreenHandler;
import io.github.reoseah.spacefactory.feature.machine.extractor.ExtractorBlock;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderBlock;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderBlockEntity;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderScreenHandler;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrindingRecipe;
import io.github.reoseah.spacefactory.feature.primitive_grinder.*;
import io.github.reoseah.spacefactory.feature.rubber_root.RubberRootBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
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
        BlockEntityTypes.register();
        Items.register();
        ScreenHandlerTypes.register();
        RecipeTypes.register();
        RecipeSerializers.register();
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
            public static final Material MACHINE = new FabricMaterialBuilder(MapColor.WHITE_GRAY).build();
        }

        public static final Block GENERATOR = new GeneratorBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 14 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(machineSettings().mapColor(MapColor.BLUE));

        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block GRINDER = new GrinderBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block EXTRACTOR = new ExtractorBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));

        public static final Block PRIMITIVE_GRINDER = new PrimitiveGrinderBlock(FabricBlockSettings.of(Material.STONE).strength(3.5F, 3.5F));
        public static final Block CRANK = new CrankBlock(FabricBlockSettings.of(Material.WOOD, MapColor.CLEAR).strength(2.0F, 2.0F));

        public static final Block REFINED_IRON_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.WHITE_GRAY).strength(3F, 6F).sounds(BlockSoundGroup.METAL));
        public static final Block REFINED_COPPER_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.ORANGE).strength(3F, 6F).sounds(BlockSoundGroup.METAL));

        public static final Block RUBBER_ROOT = new RubberRootBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));

        private static AbstractBlock.Settings machineSettings() {
            return AbstractBlock.Settings.of(Materials.MACHINE).strength(3F).sounds(BlockSoundGroup.METAL);
        }

        public static void register() {
            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);

            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("extractor", EXTRACTOR);

            register("primitive_grinder", PRIMITIVE_GRINDER);
            register("crank", CRANK);

            register("refined_iron_block", REFINED_IRON_BLOCK);
            register("refined_copper_block", REFINED_COPPER_BLOCK);

            register("rubber_root", RUBBER_ROOT);
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
        public static final Item PRIMITIVE_GRINDER = new BlockItem(Blocks.PRIMITIVE_GRINDER, settings(TECHNOLOGY));
        public static final Item CRANK = new BlockItem(Blocks.CRANK, settings(TECHNOLOGY));

        public static final Item REFINED_IRON_INGOT = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_COPPER_INGOT = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_IRON_NUGGET = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_COPPER_NUGGET = new Item(settings(TECHNOLOGY));

        public static final Item RUBBER_ROOT_SEEDS = new AliasedBlockItem(Blocks.RUBBER_ROOT, settings(TECHNOLOGY));
        public static final Item RUBBER_ROOT = new Item(settings(TECHNOLOGY));
        public static final Item RAW_RUBBER = new Item(settings(TECHNOLOGY));

        public static final Item REFINED_IRON_BLOCK = new BlockItem(Blocks.REFINED_IRON_BLOCK, settings(DECORATION));
        public static final Item REFINED_COPPER_BLOCK = new BlockItem(Blocks.REFINED_COPPER_BLOCK, settings(DECORATION));

        public static void register() {
            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);

            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("extractor", EXTRACTOR);
            register("primitive_grinder", PRIMITIVE_GRINDER);
            register("crank", CRANK);

            register("refined_iron_ingot", REFINED_IRON_INGOT);
            register("refined_copper_ingot", REFINED_COPPER_INGOT);
            register("refined_iron_nugget", REFINED_IRON_NUGGET);
            register("refined_copper_nugget", REFINED_COPPER_NUGGET);

            register("rubber_root_seeds", RUBBER_ROOT_SEEDS);
            register("rubber_root", RUBBER_ROOT);
            register("raw_rubber", RAW_RUBBER);

            register("refined_iron_block", REFINED_IRON_BLOCK);
            register("refined_copper_block", REFINED_COPPER_BLOCK);
        }

        private static FabricItemSettings settings(ItemGroup group) {
            return new FabricItemSettings().group(group);
        }

        private static void register(String name, Item entry) {
            Registry.register(Registry.ITEM, id(name), entry);
        }
    }

    public static class BlockEntityTypes {
        public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricFurnaceBlockEntity::new, Blocks.ELECTRIC_FURNACE).build();
        public static final BlockEntityType<GrinderBlockEntity> GRINDER = FabricBlockEntityTypeBuilder.create(GrinderBlockEntity::new, Blocks.GRINDER).build();
        public static final BlockEntityType<PrimitiveGrinderBlockEntity> PRIMITIVE_GRINDER = FabricBlockEntityTypeBuilder.create(PrimitiveGrinderBlockEntity::new, Blocks.PRIMITIVE_GRINDER).build();
        public static final BlockEntityType<CrankBlockEntity> CRANK = FabricBlockEntityTypeBuilder.create(CrankBlockEntity::new, Blocks.CRANK).build();

        public static void register() {
            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("primitive_grinder", PRIMITIVE_GRINDER);
            register("crank", CRANK);
        }

        private static void register(String name, BlockEntityType<?> entry) {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, id(name), entry);
        }
    }

    public static class ScreenHandlerTypes {
        public static final ScreenHandlerType<ElectricFurnaceScreenHandler.Client> ELECTRIC_FURNACE = new ScreenHandlerType<>(ElectricFurnaceScreenHandler.Client::new);
        public static final ScreenHandlerType<GrinderScreenHandler.Client> GRINDER = new ScreenHandlerType<>(GrinderScreenHandler.Client::new);
        public static final ScreenHandlerType<PrimitiveGrinderScreenHandler.Client> PRIMITIVE_GRINDER = new ScreenHandlerType<>(PrimitiveGrinderScreenHandler.Client::new);

        public static void register() {
            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("primitive_grinder", PRIMITIVE_GRINDER);
        }

        private static void register(String name, ScreenHandlerType<?> entry) {
            Registry.register(Registry.SCREEN_HANDLER, id(name), entry);
        }
    }

    public static class RecipeTypes {
        public static final RecipeType<GrindingRecipe> GRINDING = new SpaceFactoryType<>("grinding");

        public static void register() {
            register("grinding", GRINDING);
        }

        private static void register(String name, RecipeType<?> entry) {
            Registry.register(Registry.RECIPE_TYPE, id(name), entry);
        }

        private static final class SpaceFactoryType<T extends Recipe<?>> implements RecipeType<T> {
            private final String name;

            private SpaceFactoryType(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return ID + ":" + this.name;
            }
        }
    }

    public static class RecipeSerializers {
        public static final RecipeSerializer<GrindingRecipe> GRINDING = new GrindingRecipe.Serializer();

        public static void register() {
            register("grinding", GRINDING);
        }

        private static void register(String name, RecipeSerializer<?> entry) {
            Registry.register(Registry.RECIPE_SERIALIZER, id(name), entry);
        }
    }
}

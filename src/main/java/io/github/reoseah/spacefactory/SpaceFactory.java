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
import io.github.reoseah.spacefactory.feature.tool.MacheteItem;
import io.github.reoseah.spacefactory.feature.tool.UnicutterItem;
import io.github.reoseah.spacefactory.feature.tool.WrenchItem;
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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
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

        public static final Block REFINED_IRON_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.WHITE_GRAY).strength(3F, 6F).sounds(BlockSoundGroup.METAL));
        public static final Block REFINED_COPPER_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.ORANGE).strength(3F, 6F).sounds(BlockSoundGroup.METAL));
        public static final Block NEOSTEEL_BLOCK = new Block(FabricBlockSettings.of(Material.METAL, MapColor.DARK_AQUA).strength(7F, 15F).sounds(BlockSoundGroup.METAL));
        public static final Block IRIDIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL, MapColor.WHITE).strength(7F, 15F).sounds(BlockSoundGroup.METAL));

        public static final Block PRIMITIVE_GRINDER = new PrimitiveGrinderBlock(FabricBlockSettings.of(Material.STONE).strength(3.5F, 3.5F));
        public static final Block CRANK = new CrankBlock(FabricBlockSettings.of(Material.WOOD, MapColor.CLEAR).strength(2.0F, 2.0F));

        public static final Block GENERATOR = new GeneratorBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 14 : 0));
        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block GRINDER = new GrinderBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block EXTRACTOR = new ExtractorBlock(machineSettings().luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(machineSettings().mapColor(MapColor.BLUE));

        private static AbstractBlock.Settings machineSettings() {
            return AbstractBlock.Settings.of(Materials.MACHINE).strength(3F).sounds(BlockSoundGroup.METAL);
        }

        public static void register() {
            register("refined_iron_block", REFINED_IRON_BLOCK);
            register("refined_copper_block", REFINED_COPPER_BLOCK);
            register("neosteel_block", NEOSTEEL_BLOCK);
            register("iridium_block", IRIDIUM_BLOCK);

            register("primitive_grinder", PRIMITIVE_GRINDER);
            register("crank", CRANK);

            register("generator", GENERATOR);
            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("extractor", EXTRACTOR);
            register("solar_panel", SOLAR_PANEL);
        }

        private static void register(String name, Block entry) {
            Registry.register(Registry.BLOCK, id(name), entry);
        }
    }

    public static class Items {
        public static final Item REFINED_IRON_BLOCK = new BlockItem(Blocks.REFINED_IRON_BLOCK, settings(DECORATION));
        public static final Item REFINED_COPPER_BLOCK = new BlockItem(Blocks.REFINED_COPPER_BLOCK, settings(DECORATION));
        public static final Item NEOSTEEL_BLOCK = new BlockItem(Blocks.NEOSTEEL_BLOCK, settings(DECORATION).rarity(Rarity.RARE));
        public static final Item IRIDIUM_BLOCK = new BlockItem(Blocks.IRIDIUM_BLOCK, settings(DECORATION).rarity(Rarity.UNCOMMON));

        public static final Item PRIMITIVE_GRINDER = new BlockItem(Blocks.PRIMITIVE_GRINDER, settings(TECHNOLOGY));
        public static final Item CRANK = new BlockItem(Blocks.CRANK, settings(TECHNOLOGY));

        public static final Item GENERATOR = new BlockItem(Blocks.GENERATOR, settings(TECHNOLOGY));
        public static final Item ELECTRIC_FURNACE = new BlockItem(Blocks.ELECTRIC_FURNACE, settings(TECHNOLOGY));
        public static final Item GRINDER = new BlockItem(Blocks.GRINDER, settings(TECHNOLOGY));
        public static final Item EXTRACTOR = new BlockItem(Blocks.EXTRACTOR, settings(TECHNOLOGY));
        public static final Item SOLAR_PANEL = new BlockItem(Blocks.SOLAR_PANEL, settings(TECHNOLOGY));

        public static final Item COPPER_NUGGET = new Item(settings(TECHNOLOGY));

        public static final Item STONE_DUST = new Item(settings(TECHNOLOGY));
        public static final Item COAL_DUST = new Item(settings(TECHNOLOGY));
        public static final Item COPPER_DUST = new Item(settings(TECHNOLOGY));
        public static final Item IRON_DUST = new Item(settings(TECHNOLOGY));
        public static final Item GOLD_DUST = new Item(settings(TECHNOLOGY));
        public static final Item DIAMOND_DUST = new Item(settings(TECHNOLOGY));
        public static final Item ENDER_PEARL_DUST = new Item(settings(TECHNOLOGY));
        public static final Item NETHERITE_SCRAP_DUST = new Item(settings(TECHNOLOGY));
        public static final Item NETHER_QUARTZ_DUST = new Item(settings(TECHNOLOGY));

        public static final Item REFINED_IRON_INGOT = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_IRON_DUST = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_IRON_NUGGET = new Item(settings(TECHNOLOGY));

        public static final Item REFINED_COPPER_INGOT = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_COPPER_DUST = new Item(settings(TECHNOLOGY));
        public static final Item REFINED_COPPER_NUGGET = new Item(settings(TECHNOLOGY));

        public static final Item SILICON_INGOT = new Item(settings(TECHNOLOGY));

        public static final Item NEOSTEEL_INGOT = new Item(settings(TECHNOLOGY).rarity(Rarity.RARE));

        public static final Item RAW_IRIDIUM = new Item(settings(TECHNOLOGY).rarity(Rarity.UNCOMMON));
        public static final Item IRIDIUM_INGOT = new Item(settings(TECHNOLOGY).rarity(Rarity.UNCOMMON));
        public static final Item IRIDIUM_DUST = new Item(settings(TECHNOLOGY).rarity(Rarity.UNCOMMON));
        public static final Item IRIDIUM_NUGGET = new Item(settings(TECHNOLOGY).rarity(Rarity.UNCOMMON));
        public static final Item SMALL_IRIDIUM_DUST = new Item(settings(TECHNOLOGY).rarity(Rarity.UNCOMMON));

        public static final Item RAW_RUBBER = new Item(settings(TECHNOLOGY));
        public static final Item RUBBER = new Item(settings(TECHNOLOGY));
        public static final Item CIRCUIT = new Item(settings(TECHNOLOGY));

//        public static final Item COPPER_WIRE = new BlockItem(Blocks.COPPER_WIRE, settings());
//        public static final Item COPPER_CABLE = new BlockItem(Blocks.COPPER_CABLE, settings());
//        public static final Item COPPER_BUS_BAR = new BlockItem(Blocks.COPPER_BUS_BAR, settings());

        public static final Item FLAK_VEST = new ArmorItem(ArmorMaterials.FLAK, EquipmentSlot.CHEST, settings(TECHNOLOGY).rarity(Rarity.UNCOMMON));
        public static final Item REFINED_IRON_MACHETE = new MacheteItem(ToolMaterials.REFINED_IRON, 2, -2.2F, settings(TECHNOLOGY));
        public static final Item REFINED_IRON_WRENCH = new WrenchItem(ToolMaterials.REFINED_IRON, 0, -1.8F, settings(TECHNOLOGY));
        public static final Item REFINED_IRON_UNICUTTER = new UnicutterItem(ToolMaterials.REFINED_IRON, -1, -1F, settings(TECHNOLOGY).maxDamage(250));
        public static final Item NEOSTEEL_MACHETE = new MacheteItem(ToolMaterials.NEOSTEEL, 2, -2.2F, settings(TECHNOLOGY).rarity(Rarity.RARE));
        public static final Item NEOSTEEL_WRENCH = new WrenchItem(ToolMaterials.NEOSTEEL, 0, -1.8F, settings(TECHNOLOGY).maxDamage(700).rarity(Rarity.RARE));
        public static final Item NEOSTEEL_UNICUTTER = new UnicutterItem(ToolMaterials.NEOSTEEL, -1, -1F, settings(TECHNOLOGY).maxDamage(700).rarity(Rarity.RARE));

        public static void register() {
            register("refined_iron_block", REFINED_IRON_BLOCK);
            register("refined_copper_block", REFINED_COPPER_BLOCK);
            register("neosteel_block", NEOSTEEL_BLOCK);
            register("iridium_block", IRIDIUM_BLOCK);

            register("primitive_grinder", PRIMITIVE_GRINDER);
            register("crank", CRANK);
            register("generator", GENERATOR);
            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
            register("extractor", EXTRACTOR);
            register("solar_panel", SOLAR_PANEL);

            register("copper_nugget", COPPER_NUGGET);

            register("stone_dust", STONE_DUST);
            register("coal_dust", COAL_DUST);
            register("copper_dust", COPPER_DUST);
            register("iron_dust", IRON_DUST);
            register("gold_dust", GOLD_DUST);
            register("diamond_dust", DIAMOND_DUST);
            register("ender_pearl_dust", ENDER_PEARL_DUST);
            register("netherite_scrap_dust", NETHERITE_SCRAP_DUST);
            register("nether_quartz_dust", NETHER_QUARTZ_DUST);

            register("refined_iron_ingot", REFINED_IRON_INGOT);
            register("refined_iron_dust", REFINED_IRON_DUST);
            register("refined_iron_nugget", REFINED_IRON_NUGGET);

            register("refined_copper_ingot", REFINED_COPPER_INGOT);
            register("refined_copper_dust", REFINED_COPPER_DUST);
            register("refined_copper_nugget", REFINED_COPPER_NUGGET);

            register("silicon_ingot", SILICON_INGOT);
            register("neosteel_ingot", NEOSTEEL_INGOT);

            register("raw_iridium", RAW_IRIDIUM);
            register("iridium_ingot", IRIDIUM_INGOT);
            register("iridium_dust", IRIDIUM_DUST);
            register("iridium_nugget", IRIDIUM_NUGGET);
            register("small_iridium_dust", SMALL_IRIDIUM_DUST);

            register("raw_rubber", RAW_RUBBER);
            register("rubber", RUBBER);
            register("circuit", CIRCUIT);

            register("flak_vest", FLAK_VEST);
            register("refined_iron_machete", REFINED_IRON_MACHETE);
            register("refined_iron_wrench", REFINED_IRON_WRENCH);
            register("refined_iron_unicutter", REFINED_IRON_UNICUTTER);
            register("neosteel_machete", NEOSTEEL_MACHETE);
            register("neosteel_wrench", NEOSTEEL_WRENCH);
            register("neosteel_unicutter", NEOSTEEL_UNICUTTER);
        }

        private static FabricItemSettings settings(ItemGroup group) {
            return new FabricItemSettings().group(group);
        }

        private static void register(String name, Item entry) {
            Registry.register(Registry.ITEM, id(name), entry);
        }
    }

    public static class BlockEntityTypes {
        public static final BlockEntityType<PrimitiveGrinderBlockEntity> PRIMITIVE_GRINDER = FabricBlockEntityTypeBuilder.create(PrimitiveGrinderBlockEntity::new, Blocks.PRIMITIVE_GRINDER).build();
        public static final BlockEntityType<CrankBlockEntity> CRANK = FabricBlockEntityTypeBuilder.create(CrankBlockEntity::new, Blocks.CRANK).build();
        public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricFurnaceBlockEntity::new, Blocks.ELECTRIC_FURNACE).build();
        public static final BlockEntityType<GrinderBlockEntity> GRINDER = FabricBlockEntityTypeBuilder.create(GrinderBlockEntity::new, Blocks.GRINDER).build();

        public static void register() {
            register("primitive_grinder", PRIMITIVE_GRINDER);
            register("crank", CRANK);
            register("electric_furnace", ELECTRIC_FURNACE);
            register("grinder", GRINDER);
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

    public enum ArmorMaterials implements ArmorMaterial {
        FLAK("flak", 10, new int[]{3, 6, 8, 3}, 8, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, 0.1F, Ingredient.empty());

        private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

        private final String name;
        private final int durabilityMultiplier;
        private final int[] protectionAmounts;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final Ingredient repairIngredient;

        ArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Ingredient repairIngredient) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.protectionAmounts = protectionAmounts;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredient = repairIngredient;
        }

        @Override
        public int getDurability(EquipmentSlot slot) {
            return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
        }

        @Override
        public int getProtectionAmount(EquipmentSlot slot) {
            return this.protectionAmounts[slot.getEntitySlotId()];
        }

        @Override
        public int getEnchantability() {
            return this.enchantability;
        }

        @Override
        public SoundEvent getEquipSound() {
            return this.equipSound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public float getToughness() {
            return this.toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }

    public enum ToolMaterials implements ToolMaterial {
        REFINED_IRON(2, 750, 6.5F, 3F, 8, Ingredient.ofItems(Items.REFINED_IRON_INGOT)), //
        NEOSTEEL(3, 1500, 7.5F, 4F, 0, Ingredient.ofItems(Items.NEOSTEEL_INGOT));

        private final int miningLevel;
        private final int itemDurability;
        private final float miningSpeed;
        private final float attackDamage;
        private final int enchantability;
        private final Ingredient repairIngredient;

        ToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Ingredient repairIngredient) {
            this.miningLevel = miningLevel;
            this.itemDurability = itemDurability;
            this.miningSpeed = miningSpeed;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
            this.repairIngredient = repairIngredient;
        }

        @Override
        public int getDurability() {
            return this.itemDurability;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return this.miningSpeed;
        }

        @Override
        public float getAttackDamage() {
            return this.attackDamage;
        }

        @Override
        public int getMiningLevel() {
            return this.miningLevel;
        }

        @Override
        public int getEnchantability() {
            return this.enchantability;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient;
        }
    }
}

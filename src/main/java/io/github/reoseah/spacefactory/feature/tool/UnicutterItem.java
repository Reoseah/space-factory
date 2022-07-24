package io.github.reoseah.spacefactory.feature.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// TODO make it cut chains? allow to add other blocks through a tag like vanilla tools?
public class UnicutterItem extends ShearsItem {
	private final ToolMaterial material;
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	public UnicutterItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(settings.maxDamageIfAbsent(material.getDurability()));
		this.material = material;
		this.attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder() //
				.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", attackDamage + material.getAttackDamage(), EntityAttributeModifier.Operation.ADDITION)) //
				.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION)) //
				.build();
	}

	public ToolMaterial getMaterial() {
		return this.material;
	}

	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
	}

	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB) || state.isIn(BlockTags.LEAVES)) {
			return 15.0F;
		}
		if (state.isIn(BlockTags.WOOL))
			return 5.0F;

		return state.isOf(Blocks.VINE) || state.isOf(Blocks.GLOW_LICHEN) ? 2.0F : 0.25F;
	}


	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();

		if (player != null && world.canPlayerModifyAt(player, pos)) {
			ItemStack stack = context.getStack();
			Hand hand = context.getHand();

			BlockState state = world.getBlockState(pos);

//			if (state.isOf(SpaceFactory.Blocks.COPPER_WIRE)) {
//				for (ItemStack rubberStack : player.getInventory().main) {
//					if (rubberStack.isIn(SpaceFactory.Items.RUBBERS)) {
//						if (world.setBlockState(pos, SpaceFactory.Blocks.COPPER_CABLE.getStateWithProperties(state))) {
//							rubberStack.decrement(1);
//							this.damageToolAndPlaySoundAndIncrementUsageCounter(stack, player, hand, pos, world);
//							return ActionResult.SUCCESS;
//						}
//					}
//				}
//				return ActionResult.PASS;
//			}
//			if (state.isOf(SpaceFactory.Blocks.COPPER_CABLE)) {
//				if (world.setBlockState(pos, SpaceFactory.Blocks.COPPER_WIRE.getStateWithProperties(state))) {
//					player.getInventory().insertStack(new ItemStack(SpaceFactory.Items.RUBBER));
//					this.damageToolAndPlaySoundAndIncrementUsageCounter(stack, player, hand, pos, world);
//				}
//				return ActionResult.SUCCESS;
//			}
		}
		return super.useOnBlock(context);
	}

	private void damageToolAndPlaySoundAndIncrementUsageCounter(ItemStack stack, PlayerEntity player, Hand hand, BlockPos pos, World world) {
		stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
		world.playSound(player, pos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);
		if (player instanceof ServerPlayerEntity) {
			Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
		}
	}
}

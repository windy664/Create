package com.simibubi.create.content.contraptions.components.deployer;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.processing.ItemApplicationRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.utility.BlockHelper;

import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ManualApplicationRecipe extends ItemApplicationRecipe {

	public static InteractionResult manualApplicationRecipesApplyInWorld(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);
		BlockPos pos = hitResult.getBlockPos();
		BlockState blockState = level.getBlockState(pos);

		if (level.isClientSide())
			return InteractionResult.PASS;
		if (heldItem.isEmpty())
			return InteractionResult.PASS;
		if (blockState.isAir())
			return InteractionResult.PASS;

		RecipeType<Recipe<RecipeWrapper>> type = AllRecipeTypes.ITEM_APPLICATION.getType();
		Optional<Recipe<RecipeWrapper>> foundRecipe = level.getRecipeManager()
			.getAllRecipesFor(type)
			.stream()
			.filter(r -> {
				ManualApplicationRecipe mar = (ManualApplicationRecipe) r;
				return mar.testBlock(blockState) && mar.ingredients.get(1)
					.test(heldItem);
			})
			.findFirst();

		if (foundRecipe.isEmpty())
			return InteractionResult.PASS;

		level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1.45f);
		ManualApplicationRecipe recipe = (ManualApplicationRecipe) foundRecipe.get();
		level.destroyBlock(pos, false);
		level.setBlock(pos, recipe.transformBlock(blockState), 3);
		recipe.rollResults()
			.forEach(stack -> Block.popResource(level, pos, stack));

		boolean unbreakable = heldItem.hasTag() && heldItem.getTag()
			.getBoolean("Unbreakable");
		boolean keepHeld = recipe.shouldKeepHeldItem();

		if (!unbreakable && !keepHeld) {
			if (heldItem.isDamageableItem())
				heldItem.hurtAndBreak(1, player, s -> s.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			else
				heldItem.shrink(1);
		}

		return InteractionResult.SUCCESS;
	}

	public ManualApplicationRecipe(ProcessingRecipeParams params) {
		super(AllRecipeTypes.ITEM_APPLICATION, params);
	}

	public static DeployerApplicationRecipe asDeploying(Recipe<?> recipe) {
		ManualApplicationRecipe mar = (ManualApplicationRecipe) recipe;
		ProcessingRecipeBuilder<DeployerApplicationRecipe> builder =
			new ProcessingRecipeBuilder<>(DeployerApplicationRecipe::new,
				new ResourceLocation(mar.id.getNamespace(), mar.id.getPath() + "_using_deployer"))
					.require(mar.ingredients.get(0))
					.require(mar.ingredients.get(1));
		for (ProcessingOutput output : mar.results)
			builder.output(output);
		if (mar.shouldKeepHeldItem())
			builder.toolNotConsumed();
		return builder.build();
	}

	public boolean testBlock(BlockState in) {
		return ingredients.get(0)
			.test(new ItemStack(in.getBlock()
				.asItem()));
	}

	public BlockState transformBlock(BlockState in) {
		ProcessingOutput mainOutput = results.get(0);
		ItemStack output = mainOutput.rollOutput();
		if (output.getItem()instanceof BlockItem bi)
			return BlockHelper.copyProperties(in, bi.getBlock()
				.defaultBlockState());
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public List<ItemStack> rollResults() {
		return rollResults(getRollableResultsExceptBlock());
	}

	public List<ProcessingOutput> getRollableResultsExceptBlock() {
		ProcessingOutput mainOutput = results.get(0);
		if (mainOutput.getStack()
			.getItem() instanceof BlockItem)
			return results.subList(1, results.size());
		return results;
	}

}

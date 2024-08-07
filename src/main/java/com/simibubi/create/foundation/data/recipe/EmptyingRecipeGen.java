package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;

import io.github.tropheusj.milk.Milk;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

public class EmptyingRecipeGen extends ProcessingRecipeGen {

	/*
	 * potion/water bottles are handled internally
	 */

	GeneratedRecipe

	HONEY_BOTTLE = create("honey_bottle", b -> b.require(Items.HONEY_BOTTLE)
		.output(AllFluids.HONEY.get(), AllFluids.HONEY_BOTTLE_AMOUNT)
		.output(Items.GLASS_BOTTLE)),

		BUILDERS_TEA = create("builders_tea", b -> b.require(AllItems.BUILDERS_TEA.get())
			.output(AllFluids.TEA.get(), FluidConstants.BOTTLE)
			.output(Items.GLASS_BOTTLE)),

		FD_MILK = create(Mods.FD.recipeId("milk_bottle"), b -> b.require(Mods.FD, "milk_bottle")
			.output(Milk.FLOWING_MILK, FluidConstants.BOTTLE)
			.output(Items.GLASS_BOTTLE)
			.whenModLoaded(Mods.FD.getId())),

		AM_LAVA = create(Mods.AM.recipeId("lava_bottle"), b -> b.require(Mods.AM, "lava_bottle")
				.output(Items.GLASS_BOTTLE)
				.output(Fluids.LAVA, 250)
				.whenModLoaded(Mods.AM.getId())),

		NEO_MILK = create(Mods.NEA.recipeId("milk_bottle"), b -> b.require(Mods.FD, "milk_bottle")
				.output(Milk.STILL_MILK, 250)
				.output(Items.GLASS_BOTTLE)
				.whenModLoaded(Mods.NEA.getId()))

	;

	public EmptyingRecipeGen(FabricDataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.EMPTYING;
	}

}

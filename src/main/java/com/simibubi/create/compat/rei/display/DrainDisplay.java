package com.simibubi.create.compat.rei.display;

import java.util.List;

import com.simibubi.create.Create;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class DrainDisplay extends CreateDisplay<EmptyingRecipe> {
	public static final CategoryIdentifier<CreateDisplay<EmptyingRecipe>> ID = CategoryIdentifier.of(Create.asResource("draining"));

	public DrainDisplay(EmptyingRecipe recipe) {
		super(
				recipe,
				ID,
				EntryIngredients.ofIngredients(recipe.getIngredients()),
				List.of(
						EntryIngredients.ofItemStacks(recipe.getRollableResultsAsItemStacks()),
						EntryIngredients.of(VanillaEntryTypes.FLUID, CreateRecipeCategory.convertToREIFluids(recipe.getFluidResults()))
				)
		);
	}
}

package com.simibubi.create.foundation.recipe;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;

public class AllIngredients {
	public static void register() {
		CraftingHelper.register(Create.asResource("block_tag_ingredient"), BlockTagIngredient.Serializer.INSTANCE);
	}
}

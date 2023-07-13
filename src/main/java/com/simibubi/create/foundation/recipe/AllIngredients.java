package com.simibubi.create.foundation.recipe;

import com.simibubi.create.Create;

public class AllIngredients {
	public static void register() {
		CraftingHelper.register(Create.asResource("block_tag_ingredient"), BlockTagIngredient.Serializer.INSTANCE);
	}
}

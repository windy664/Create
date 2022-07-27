package com.simibubi.create.compat.emi.recipes;

import com.simibubi.create.compat.emi.CreateEmiPlugin;
import com.simibubi.create.content.contraptions.processing.ItemApplicationRecipe;

import com.simibubi.create.foundation.gui.AllGuiTextures;

import dev.emi.emi.api.widget.WidgetHolder;

public class ItemApplicationEmiRecipe extends CreateEmiRecipe<ItemApplicationRecipe> {
	public ItemApplicationEmiRecipe(ItemApplicationRecipe recipe) {
		super(CreateEmiPlugin.ITEM_APPLICATION, recipe, 177, 60);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addTexture(widgets, AllGuiTextures.JEI_SLOT, 50, 4);
		addTexture(widgets, AllGuiTextures.JEI_SLOT, 26, 37);
		addTexture(widgets, AllGuiTextures.JEI_SHADOW, 62, 47);
		addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 74, 10);
		// TODO ONCE STUFF COMPILES AGAIN
	}
}

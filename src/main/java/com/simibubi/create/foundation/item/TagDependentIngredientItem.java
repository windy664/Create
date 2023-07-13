package com.simibubi.create.foundation.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagDependentIngredientItem extends Item {

	private TagKey<Item> tag;

	public TagDependentIngredientItem(Properties properties, TagKey<Item> tag) {
		super(properties);
		this.tag = tag;
	}

	public boolean shouldHide() {
		boolean tagMissing = !Registry.ITEM.isKnownTagName(this.tag);
		boolean tagEmpty = tagMissing || !Registry.ITEM.getTagOrEmpty(this.tag).iterator().hasNext();
		return tagMissing || tagEmpty;
	}

}

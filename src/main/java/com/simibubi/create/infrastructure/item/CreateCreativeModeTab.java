package com.simibubi.create.infrastructure.item;

import java.util.Collection;

import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.RegistryEntry;

import io.github.fabricators_of_create.porting_lib.util.ItemGroupUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class CreateCreativeModeTab extends CreativeModeTab {
	public CreateCreativeModeTab(String id) {
		super(ItemGroupUtil.expandArrayAndGetId(), Create.ID + "." + id);
	}

	@Override
	public void fillItemList(NonNullList<ItemStack> items) {
		addItems(items, true);
		addBlocks(items);
		addItems(items, false);
	}

	protected Collection<RegistryEntry<Item>> registeredItems() {
		return Create.REGISTRATE.getAll(Registry.ITEM_REGISTRY);
	}

	public void addBlocks(NonNullList<ItemStack> items) {
		for (RegistryEntry<Item> entry : registeredItems())
			if (entry.get() instanceof BlockItem blockItem)
				blockItem.fillItemCategory(this, items);
	}

	public void addItems(NonNullList<ItemStack> items, boolean specialItems) {
		ItemRenderer itemRenderer = Minecraft.getInstance()
			.getItemRenderer();

		for (RegistryEntry<Item> entry : registeredItems()) {
			Item item = entry.get();
			if (item instanceof BlockItem)
				continue;
			ItemStack stack = new ItemStack(item);
			BakedModel model = itemRenderer.getModel(stack, null, null, 0);
			if (model.isGui3d() == specialItems)
				item.fillItemCategory(this, items);
		}
	}
}

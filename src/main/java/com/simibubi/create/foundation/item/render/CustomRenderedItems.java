package com.simibubi.create.foundation.item.render;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CustomRenderedItems {

	private static final Set<Item> ITEMS = new ReferenceOpenHashSet<>();
	private static boolean itemsFiltered = false;

	/**
	 * Track an item that uses a subclass of {@link CustomRenderedItemModelRenderer} as its custom renderer
	 * to automatically wrap its model with {@link CustomRenderedItemModel}.
	 * @param item The item that should have its model swapped.
	 */
	public static void register(Item item) {
		ITEMS.add(item);
	}

	/**
	 * This method must not be called before item registration is finished!
	 */
	public static void forEach(Consumer<Item> consumer) {
		if (!itemsFiltered) {
			Iterator<Item> iterator = ITEMS.iterator();
			while (iterator.hasNext()) {
				Item item = iterator.next();
				if (!(RenderProperties.get(item).getItemStackRenderer() instanceof CustomRenderedItemModelRenderer)) {
					iterator.remove();
				}
			}
			itemsFiltered = true;
		}
		ITEMS.forEach(consumer);
	}

}

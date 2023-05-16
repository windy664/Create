package com.simibubi.create.foundation.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.AttachedRegistry;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public interface TooltipModifier {
	AttachedRegistry<Item, TooltipModifier> REGISTRY = new AttachedRegistry<>(Registry.ITEM);

	TooltipModifier EMPTY = new TooltipModifier() {
		@Override
		public void modify(ItemStack stack, TooltipFlag flags, List<Component> tooltip) {
		}

		@Override
		public TooltipModifier andThen(TooltipModifier after) {
			return after;
		}
	};

	void modify(ItemStack stack, TooltipFlag flags, List<Component> tooltip);

	default TooltipModifier andThen(TooltipModifier after) {
		if (after == EMPTY) {
			return this;
		}
		return (stack, flags, tooltip) -> {
			modify(stack, flags, tooltip);
			after.modify(stack, flags, tooltip);
		};
	}

	static TooltipModifier mapNull(@Nullable TooltipModifier modifier) {
		if (modifier == null) {
			return EMPTY;
		}
		return modifier;
	}
}

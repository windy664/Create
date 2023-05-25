package com.simibubi.create.foundation.item;

import java.util.List;

import net.minecraft.world.entity.player.Player;

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
		public void modify(ItemStack stack, TooltipFlag flags, List<Component> tooltip, Player player) {
		}

		@Override
		public TooltipModifier andThen(TooltipModifier after) {
			return after;
		}
	};

	void modify(ItemStack stack, TooltipFlag flags, List<Component> tooltip, Player player);

	default TooltipModifier andThen(TooltipModifier after) {
		if (after == EMPTY) {
			return this;
		}
		return (stack, flags, tooltip, player) -> {
			modify(stack, flags, tooltip, player);
			after.modify(stack, flags, tooltip, player);
		};
	}

	static TooltipModifier mapNull(@Nullable TooltipModifier modifier) {
		if (modifier == null) {
			return EMPTY;
		}
		return modifier;
	}
}

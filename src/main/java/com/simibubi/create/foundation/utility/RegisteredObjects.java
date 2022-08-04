package com.simibubi.create.foundation.utility;

import net.minecraft.core.Registry;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public final class RegisteredObjects {
	// registry argument for easier porting to 1.19
	@NotNull
	public static <V> ResourceLocation getKeyOrThrow(Registry<V> registry, V value) {
		ResourceLocation key = registry.getKey(value);
		if (key == null) {
			throw new IllegalArgumentException("Could not get key for value " + value + "!");
		}
		return key;
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(Block value) {
		return getKeyOrThrow(Registry.BLOCK, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(Item value) {
		return getKeyOrThrow(Registry.ITEM, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(Fluid value) {
		return getKeyOrThrow(Registry.FLUID, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(EntityType<?> value) {
		return getKeyOrThrow(Registry.ENTITY_TYPE, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(BlockEntityType<?> value) {
		return getKeyOrThrow(Registry.BLOCK_ENTITY_TYPE, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(Potion value) {
		return getKeyOrThrow(Registry.POTION, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(ParticleType<?> value) {
		return getKeyOrThrow(Registry.PARTICLE_TYPE, value);
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(RecipeSerializer<?> value) {
		return getKeyOrThrow(Registry.RECIPE_SERIALIZER, value);
	}
}

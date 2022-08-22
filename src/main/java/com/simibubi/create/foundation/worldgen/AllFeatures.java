package com.simibubi.create.foundation.worldgen;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;

public class AllFeatures {
	private static final LazyRegistrar<Feature<?>> REGISTER = LazyRegistrar.create(Registry.FEATURE, Create.ID);

	public static final RegistryObject<StandardOreFeature> STANDARD_ORE = REGISTER.register("standard_ore", () -> new StandardOreFeature());
	public static final RegistryObject<LayeredOreFeature> LAYERED_ORE = REGISTER.register("layered_ore", () -> new LayeredOreFeature());

	public static void register() {
		REGISTER.register();
	}
}

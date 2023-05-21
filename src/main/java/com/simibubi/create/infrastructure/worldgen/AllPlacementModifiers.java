package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class AllPlacementModifiers {
	private static final LazyRegistrar<PlacementModifierType<?>> REGISTER = LazyRegistrar.create(Registry.PLACEMENT_MODIFIERS, Create.ID);

	public static final RegistryObject<PlacementModifierType<ConfigDrivenPlacement>> CONFIG_DRIVEN = REGISTER.register("config_driven", () -> () -> ConfigDrivenPlacement.CODEC);

	public static void register() {
		REGISTER.register();
	}
}

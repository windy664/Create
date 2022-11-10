package com.simibubi.create.foundation.worldgen;

import java.util.Map;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.worldgen.OreFeatureConfigEntry.DatagenExtension;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BuiltinRegistration {
	private static final LazyRegistrar<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE_REGISTER = LazyRegistrar.create(BuiltinRegistries.CONFIGURED_FEATURE, Create.ID);
	private static final LazyRegistrar<PlacedFeature> PLACED_FEATURE_REGISTER = LazyRegistrar.create(BuiltinRegistries.PLACED_FEATURE, Create.ID);

	static {
		for (Map.Entry<ResourceLocation, OreFeatureConfigEntry> entry : OreFeatureConfigEntry.ALL.entrySet()) {
			ResourceLocation id = entry.getKey();
			if (id.getNamespace().equals(Create.ID)) {
				DatagenExtension datagenExt = entry.getValue().datagenExt();
				if (datagenExt != null) {
					CONFIGURED_FEATURE_REGISTER.register(id.getPath(), () -> datagenExt.createConfiguredFeature(BuiltinRegistries.ACCESS));
					PLACED_FEATURE_REGISTER.register(id.getPath(), () -> datagenExt.createPlacedFeature(BuiltinRegistries.ACCESS));
				}
			}
		}
	}

	public static void register() {
		CONFIGURED_FEATURE_REGISTER.register();
		PLACED_FEATURE_REGISTER.register();
	}
}

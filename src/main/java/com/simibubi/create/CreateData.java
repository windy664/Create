package com.simibubi.create;

import com.simibubi.create.compat.archEx.ArchExCompat;
import com.simibubi.create.compat.archEx.GroupProvider;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

public class CreateData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
		Pack pack = generator.createPack();
		Create.REGISTRATE.setupDatagen(pack, helper);
		Create.gatherData(pack, helper);
		ArchExCompat.init(pack);
	}
}

package com.simibubi.create;

import com.simibubi.create.content.logistics.trains.entity.CarriageSyncDataSerializer;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.network.syncher.EntityDataSerializers;

public class AllEntityDataSerializers {
	private static final LazyRegistrar<DataSerializerEntry> REGISTER = LazyRegistrar.create(ForgeRegistries.Keys.DATA_SERIALIZERS, Create.ID);

	public static final CarriageSyncDataSerializer CARRIAGE_DATA = new CarriageSyncDataSerializer();

	public static final RegistryObject<DataSerializerEntry> CARRIAGE_DATA_ENTRY = REGISTER.register("carriage_data", () -> new DataSerializerEntry(CARRIAGE_DATA));

	public static void register() {
		REGISTER.register();
	}
}

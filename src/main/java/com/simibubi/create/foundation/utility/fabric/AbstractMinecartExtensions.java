package com.simibubi.create.foundation.utility.fabric;

import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

public interface AbstractMinecartExtensions {
	CapabilityMinecartController getCap();

	void setCap(CapabilityMinecartController cap);

	LazyOptional<MinecartController> lazyController();

	MinecartController getController();

	String CAP_KEY = "Controller";

	static void minecartSpawn(Entity entity, Level level) {
		if (entity instanceof AbstractMinecart cart)
			CapabilityMinecartController.attach(cart);
	}

	static void minecartRead(Entity entity, CompoundTag data) {
		if (entity instanceof AbstractMinecart cart && data.contains(CAP_KEY)) {
			CompoundTag cap = data.getCompound(CAP_KEY);
			cart.getCap().deserializeNBT(cap);
		}
	}

	static void minecartWrite(Entity entity, CompoundTag data) {
		if (entity instanceof AbstractMinecart cart && cart.getCap() != null) {
			CompoundTag capTag = cart.getCap().serializeNBT();
			data.put(CAP_KEY, capTag);
		}
	}

	static void minecartRemove(Entity entity, Level level) {
		if (entity instanceof AbstractMinecart cart) {
			CapabilityMinecartController.onCartRemoved(level, cart);
			cart.lazyController().invalidate();
		}
	}
}

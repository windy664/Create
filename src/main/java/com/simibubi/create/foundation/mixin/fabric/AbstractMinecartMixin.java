package com.simibubi.create.foundation.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import com.simibubi.create.foundation.utility.fabric.AbstractMinecartExtensions;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements AbstractMinecartExtensions {
	@Unique
	public CapabilityMinecartController create$controllerCap = null;

	@Override
	public CapabilityMinecartController getCap() {
		return create$controllerCap;
	}

	@Override
	public void setCap(CapabilityMinecartController cap) {
		this.create$controllerCap = cap;
	}

	@Override
	public LazyOptional<MinecartController> lazyController() {
		return create$controllerCap.cap;
	}

	@Override
	public MinecartController getController() {
		return create$controllerCap.handler;
	}
}

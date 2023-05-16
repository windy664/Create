package com.simibubi.create.content.logistics.trains.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.trains.BogeyRenderer;
import com.simibubi.create.content.logistics.trains.BogeyRenderer.CommonRenderer;
import com.simibubi.create.content.logistics.trains.BogeySizes;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;


public class BogeyStyle {

	public final ResourceLocation name;
	public final ResourceLocation cycleGroup;
	public final Component displayName;
	public final ResourceLocation soundType;
	public final ParticleOptions contactParticle;
	public final ParticleOptions smokeParticle;
	public final CompoundTag defaultData;

	private Optional<Supplier<? extends CommonRenderer>> commonRendererFactory;

	@Environment(EnvType.CLIENT)
	private Map<BogeySizes.BogeySize, SizeData> sizes;

	@Environment(EnvType.CLIENT)
	private Optional<CommonRenderer> commonRenderer;

	public BogeyStyle(ResourceLocation name, ResourceLocation cycleGroup, Component displayName, ResourceLocation soundType, ParticleOptions contactParticle, ParticleOptions smokeParticle,
					  CompoundTag defaultData, Map<BogeySizes.BogeySize, Supplier<SizeData>> sizes, Optional<Supplier<? extends CommonRenderer>> commonRenderer) {
		this.name = name;
		this.cycleGroup = cycleGroup;
		this.displayName = displayName;
		this.soundType = soundType;
		this.contactParticle = contactParticle;
		this.smokeParticle = smokeParticle;
		this.defaultData = defaultData;

		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			this.sizes = new HashMap<>();
			sizes.forEach((k, v) -> this.sizes.put(k, v.get()));

			this.commonRendererFactory = commonRenderer;
			this.commonRenderer = commonRenderer.map(Supplier::get);
		});
	}

	public Map<ResourceLocation, BogeyStyle> getCycleGroup() {
		return AllBogeyStyles.getCycleGroup(cycleGroup);
	}

	public Block getNextBlock(BogeySizes.BogeySize currentSize) {
		return Stream.iterate(currentSize.increment(), BogeySizes.BogeySize::increment)
				.filter(sizes::containsKey)
				.findFirst()
				.map(size -> Registry.BLOCK.get(sizes.get(size).block()))
				.orElse(Registry.BLOCK.get(sizes.get(currentSize).block()));
	}

	public Block getBlockOfSize(BogeySizes.BogeySize size) {
		return Registry.BLOCK.get(sizes.get(size).block());
	}

	public Set<BogeySizes.BogeySize> validSizes() {
		return sizes.keySet();
	}

	@NotNull
	public SoundEvent getSoundType() {
		AllSoundEvents.SoundEntry entry = AllSoundEvents.ALL.get(this.soundType);
		if (entry == null || entry.getMainEvent() == null) entry = AllSoundEvents.TRAIN2;
		return entry.getMainEvent();
	}

	@Environment(EnvType.CLIENT)
	public BogeyRenderer createRendererInstance(BogeySizes.BogeySize size) {
		return this.sizes.get(size).createRenderInstance();
	}

	@Environment(EnvType.CLIENT)
	public BogeyRenderer getInWorldRenderInstance(BogeySizes.BogeySize size) {
		SizeData sizeData = this.sizes.get(size);
		return sizeData != null ? sizeData.getInWorldInstance() : BackupBogeyRenderer.INSTANCE;
	}

	public Optional<CommonRenderer> getInWorldCommonRenderInstance() {
		return this.commonRenderer;
	}

	public Optional<CommonRenderer> getNewCommonRenderInstance() {
		return this.commonRendererFactory.map(Supplier::get);
	}

	public BogeyInstance createInstance(CarriageBogey bogey, BogeySizes.BogeySize size, MaterialManager materialManager) {
		return new BogeyInstance(bogey, this, size, materialManager);
	}

	@Environment(EnvType.CLIENT)
	public record SizeData(ResourceLocation block, Supplier<? extends BogeyRenderer> rendererFactory, BogeyRenderer instance) {
		public BogeyRenderer createRenderInstance() {
			return rendererFactory.get();
		}

		public BogeyRenderer getInWorldInstance() {
			return instance;
		}
	}
}

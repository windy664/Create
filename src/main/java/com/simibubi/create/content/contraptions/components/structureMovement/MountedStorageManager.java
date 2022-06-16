package com.simibubi.create.content.contraptions.components.structureMovement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption.ContraptionInvWrapper;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.NBTHelper;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class MountedStorageManager {

	protected ContraptionInvWrapper inventory;
	protected ContraptionInvWrapper fuelInventory;
	protected CombinedTankWrapper fluidInventory;
	protected Map<BlockPos, MountedStorage> storage;
	protected Map<BlockPos, MountedFluidStorage> fluidStorage;

	public MountedStorageManager() {
		storage = new HashMap<>();
		fluidStorage = new HashMap<>();
	}

	public void entityTick(AbstractContraptionEntity entity) {
		fluidStorage.forEach((pos, mfs) -> mfs.tick(entity, pos, entity.level.isClientSide));
	}

	public void createHandlers() {
		Collection<MountedStorage> itemHandlers = storage.values();

		inventory = wrapItems(itemHandlers.stream()
			.map(MountedStorage::getItemHandler)
			.toList(), false);

		fuelInventory = wrapItems(itemHandlers.stream()
			.filter(MountedStorage::canUseForFuel)
			.map(MountedStorage::getItemHandler)
			.toList(), true);

		fluidInventory = wrapFluids(fluidStorage.values()
			.stream()
			.map(MountedFluidStorage::getFluidHandler)
			.collect(Collectors.toList()));
	}

	protected ContraptionInvWrapper wrapItems(Collection<Storage<ItemVariant>> list, boolean fuel) {
		return new ContraptionInvWrapper(Arrays.copyOf(list.toArray(), list.size(), Storage[].class));
	}

	protected CombinedTankWrapper wrapFluids(Collection<Storage<FluidVariant>> list) {
		return new CombinedTankWrapper(Arrays.copyOf(list.toArray(), list.size(), SmartFluidTank[].class));
	}

	public void addBlock(BlockPos localPos, BlockEntity te) {
		if (te != null && MountedStorage.canUseAsStorage(te))
			storage.put(localPos, new MountedStorage(te));
		if (te != null && MountedFluidStorage.canUseAsStorage(te))
			fluidStorage.put(localPos, new MountedFluidStorage(te));
	}

	public void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentTileEntities, boolean clientPacket) {
		storage.clear();
		NBTHelper.iterateCompoundList(nbt.getList("Storage", Tag.TAG_COMPOUND), c -> storage
			.put(NbtUtils.readBlockPos(c.getCompound("Pos")), MountedStorage.deserialize(c.getCompound("Data"))));

		fluidStorage.clear();
		NBTHelper.iterateCompoundList(nbt.getList("FluidStorage", Tag.TAG_COMPOUND), c -> fluidStorage
			.put(NbtUtils.readBlockPos(c.getCompound("Pos")), MountedFluidStorage.deserialize(c.getCompound("Data"))));

		if (clientPacket && presentTileEntities != null)
			bindTanks(presentTileEntities);

		List<Storage<ItemVariant>> handlers = new ArrayList<>();
		List<Storage<ItemVariant>> fuelHandlers = new ArrayList<>();
		for (MountedStorage mountedStorage : storage.values()) {
			Storage<ItemVariant> itemHandler = mountedStorage.getItemHandler();
			handlers.add(itemHandler);
			if (mountedStorage.canUseForFuel())
				fuelHandlers.add(itemHandler);
		}

		inventory = wrapItems(handlers, false);
		fuelInventory = wrapItems(fuelHandlers, true);
		fluidInventory = wrapFluids(fluidStorage.values()
			.stream()
			.map(MountedFluidStorage::getFluidHandler)
			.map(tank -> (Storage<FluidVariant>) tank)
			.toList());
	}

	public void bindTanks(Map<BlockPos, BlockEntity> presentTileEntities) {
		fluidStorage.forEach((pos, mfs) -> {
			BlockEntity tileEntity = presentTileEntities.get(pos);
			if (!(tileEntity instanceof FluidTankTileEntity))
				return;
			FluidTankTileEntity tank = (FluidTankTileEntity) tileEntity;
			FluidTank tankInventory = tank.getTankInventory();
			if (tankInventory instanceof FluidTank)
				((FluidTank) tankInventory).setFluid(mfs.tank.getFluid());
			tank.getFluidLevel()
				.startWithValue(tank.getFillState());
			mfs.assignTileEntity(tank);
		});
	}

	public void write(CompoundTag nbt, boolean clientPacket) {
		ListTag storageNBT = new ListTag();
		if (!clientPacket)
			for (BlockPos pos : storage.keySet()) {
				CompoundTag c = new CompoundTag();
				MountedStorage mountedStorage = storage.get(pos);
				if (!mountedStorage.isValid())
					continue;
				c.put("Pos", NbtUtils.writeBlockPos(pos));
				c.put("Data", mountedStorage.serialize());
				storageNBT.add(c);
			}

		ListTag fluidStorageNBT = new ListTag();
		for (BlockPos pos : fluidStorage.keySet()) {
			CompoundTag c = new CompoundTag();
			MountedFluidStorage mountedStorage = fluidStorage.get(pos);
			if (!mountedStorage.isValid())
				continue;
			c.put("Pos", NbtUtils.writeBlockPos(pos));
			c.put("Data", mountedStorage.serialize());
			fluidStorageNBT.add(c);
		}

		nbt.put("Storage", storageNBT);
		nbt.put("FluidStorage", fluidStorageNBT);
	}

	public void removeStorageFromWorld() {
		storage.values()
			.forEach(MountedStorage::removeStorageFromWorld);
		fluidStorage.values()
			.forEach(MountedFluidStorage::removeStorageFromWorld);
	}

	public void addStorageToWorld(StructureBlockInfo block, BlockEntity tileEntity) {
		if (storage.containsKey(block.pos)) {
			MountedStorage mountedStorage = storage.get(block.pos);
			if (mountedStorage.isValid())
				mountedStorage.addStorageToWorld(tileEntity);
		}

		if (fluidStorage.containsKey(block.pos)) {
			MountedFluidStorage mountedStorage = fluidStorage.get(block.pos);
			if (mountedStorage.isValid())
				mountedStorage.addStorageToWorld(tileEntity);
		}
	}

	public void clear() {
		for (Storage<ItemVariant> storage : inventory.parts) {
			if (!(storage instanceof ContraptionInvWrapper wrapper) || !wrapper.isExternal) {
				TransferUtil.clearStorage(storage);
			}
		}
		TransferUtil.clearStorage(fluidInventory);
	}

	public void updateContainedFluid(BlockPos localPos, FluidStack containedFluid) {
		MountedFluidStorage mountedFluidStorage = fluidStorage.get(localPos);
		if (mountedFluidStorage != null)
			mountedFluidStorage.updateFluid(containedFluid);
	}

	public void attachExternal(Storage<ItemVariant> externalStorage) {
		inventory = new ContraptionInvWrapper(externalStorage, inventory);
		fuelInventory = new ContraptionInvWrapper(externalStorage, fuelInventory);
	}

	public ContraptionInvWrapper getItems() {
		return inventory;
	}

	public ContraptionInvWrapper getFuelItems() {
		return fuelInventory;
	}

	public CombinedTankWrapper getFluids() {
		return fluidInventory;
	}

}

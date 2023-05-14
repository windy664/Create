package com.simibubi.create.content.contraptions.components.actors;

import io.github.fabricators_of_create.porting_lib.transfer.WrappedStorage;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortableFluidInterfaceBlockEntity extends PortableStorageInterfaceBlockEntity {

public class PortableFluidInterfaceTileEntity extends PortableStorageInterfaceTileEntity implements FluidTransferable {

	protected InterfaceFluidHandler capability;

	public PortableFluidInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		capability = createEmptyHandler();
	}

	@Override
	public void startTransferringTo(Contraption contraption, float distance) {
		capability.setWrapped(contraption.getSharedFluidTanks());
		super.startTransferringTo(contraption, distance);
	}

	@Override
	protected void invalidateCapability() {
		capability.setWrapped(Storage.empty());
	}

	@Override
	protected void stopTransferring() {
		capability.setWrapped(Storage.empty());
		super.stopTransferring();
	}

	private InterfaceFluidHandler createEmptyHandler() {
		return new InterfaceFluidHandler(Storage.empty());
	}

	@Override
	public Storage<FluidVariant> getFluidStorage(@Nullable Direction face) {
		return capability;
	}

	public class InterfaceFluidHandler extends WrappedStorage<FluidVariant> {

		public InterfaceFluidHandler(Storage<FluidVariant> wrapped) {
			super(wrapped);
		}

		@Override
		public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			if (!isConnected())
				return 0;
			long fill = wrapped.insert(resource, maxAmount, transaction);
			if (fill > 0)
				TransactionCallback.onSuccess(transaction, this::keepAlive);
			return fill;
		}

		@Override
		public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			if (!canTransfer())
				return 0;
			long drain = wrapped.extract(resource, maxAmount, transaction);
			if (drain != 0)
				TransactionCallback.onSuccess(transaction, this::keepAlive);
			return drain;
		}

		@Override
		public @Nullable StorageView<FluidVariant> exactView(TransactionContext transaction, FluidVariant resource) {
			TransactionCallback.onSuccess(transaction, this::keepAlive);
			return super.exactView(transaction, resource);
		}

		@Override
		public Iterator<? extends StorageView<FluidVariant>> iterator(TransactionContext transaction) {
			TransactionCallback.onSuccess(transaction, this::keepAlive);
			return super.iterator(transaction);
		}

		@Override
		public Iterable<? extends StorageView<FluidVariant>> iterable(TransactionContext transaction) {
			TransactionCallback.onSuccess(transaction, this::keepAlive);
			return super.iterable(transaction);
		}

		public void keepAlive() {
			onContentTransferred();
		}

		private void setWrapped(Storage<FluidVariant> wrapped) {
			this.wrapped = wrapped;
		}
	}

}

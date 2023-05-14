package com.simibubi.create.content.contraptions.components.actors;

import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortableItemInterfaceBlockEntity extends PortableStorageInterfaceBlockEntity {

public class PortableItemInterfaceTileEntity extends PortableStorageInterfaceTileEntity implements ItemTransferable {

	protected InterfaceItemHandler capability;

	public PortableItemInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		capability = createEmptyHandler();
	}

	@Override
	public void startTransferringTo(Contraption contraption, float distance) {
		capability.setWrapped(contraption.getSharedInventory());
		super.startTransferringTo(contraption, distance);
	}

	@Override
	protected void stopTransferring() {
		capability.setWrapped(Storage.empty());
		super.stopTransferring();
	}

	private InterfaceItemHandler createEmptyHandler() {
		return new InterfaceItemHandler(Storage.empty());
	}

	@Override
	protected void invalidateCapability() {
		capability.setWrapped(Storage.empty());
	}

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
		return capability;
	}

	class InterfaceItemHandler extends ItemHandlerWrapper {

		public InterfaceItemHandler(Storage<ItemVariant> wrapped) {
			super(wrapped);
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (!canTransfer())
				return 0;
			long extracted = super.extract(resource, maxAmount, transaction);
			if (extracted != 0) {
				TransactionCallback.onSuccess(transaction, PortableItemInterfaceTileEntity.this::onContentTransferred);
			}
			return extracted;
		}

		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (!canTransfer())
				return 0;
			long inserted = super.insert(resource, maxAmount, transaction);
			if (inserted != 0) {
				TransactionCallback.onSuccess(transaction, PortableItemInterfaceTileEntity.this::onContentTransferred);
			}
			return inserted;
		}

		@Override
		public @Nullable StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
			TransactionCallback.onSuccess(transaction, PortableItemInterfaceTileEntity.this::onContentTransferred);
			return super.exactView(transaction, resource);
		}

		@Override
		public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction) {
			TransactionCallback.onSuccess(transaction, PortableItemInterfaceTileEntity.this::onContentTransferred);
			return super.iterator(transaction);
		}

		@Override
		public Iterable<? extends StorageView<ItemVariant>> iterable(TransactionContext transaction) {
			TransactionCallback.onSuccess(transaction, PortableItemInterfaceTileEntity.this::onContentTransferred);
			return super.iterable(transaction);
		}

		private void setWrapped(Storage<ItemVariant> wrapped) {
			this.wrapped = wrapped;
		}
	}
}

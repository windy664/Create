package com.simibubi.create.content.logistics.block.depot;

import com.google.common.collect.Iterators;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class DepotItemHandler extends SnapshotParticipant<Unit> implements Storage<ItemVariant> {

	private static final int MAIN_SLOT = 0;
	private DepotBehaviour te;

	public DepotItemHandler(DepotBehaviour te) {
		this.te = te;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!te.getHeldItemStack().isEmpty() && !te.canMergeItems())
			return 0;
		if (!te.isOutputEmpty() && !te.canMergeItems())
			return 0;
		int toInsert = Math.min((int) maxAmount, resource.getItem().getMaxStackSize());
		ItemStack stack = resource.toStack(toInsert);
		ItemStack remainder = te.insert(new TransportedItemStack(stack), transaction);
		return stack.getCount() - remainder.getCount();
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long extracted = te.processingOutputBuffer.extract(resource, maxAmount, transaction);
		if (extracted == maxAmount)
			return extracted;
		extracted += extractFromMain(resource, maxAmount - extracted, transaction);
		return extracted;
	}

	public long extractFromMain(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		TransportedItemStack held = te.heldItem;
		if (held == null)
			return 0;
		ItemStack stack = held.stack;
		if (!resource.matches(stack))
			return 0;
		int toExtract = Math.min((int) maxAmount, Math.min(stack.getCount(), te.maxStackSize.get()));
		stack = stack.copy();
		stack.shrink(toExtract);
		te.snapshotParticipant.updateSnapshots(transaction);
		te.heldItem.stack = stack;
		if (stack.isEmpty())
			te.heldItem = null;
		return toExtract;
	}

	@Override
	public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return Iterators.concat(Iterators.singletonIterator(new MainSlotView()), te.processingOutputBuffer.iterator(transaction));
	}

	@Override
	protected Unit createSnapshot() {
		return Unit.INSTANCE;
	}

	@Override
	protected void readSnapshot(Unit snapshot) {
	}

	@Override
	protected void onFinalCommit() {
		super.onFinalCommit();
		te.tileEntity.notifyUpdate();
	}

	public class MainSlotView implements StorageView<ItemVariant> {

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return extractFromMain(resource, maxAmount, transaction);
		}

		@Override
		public boolean isResourceBlank() {
			return getResource().isBlank();
		}

		@Override
		public ItemVariant getResource() {
			return ItemVariant.of(getStack());
		}

		@Override
		public long getAmount() {
			ItemStack stack = getStack();
			return stack.isEmpty() ? 0 : stack.getCount();
		}

		@Override
		public long getCapacity() {
			ItemStack stack = getStack();
			return stack.isEmpty() ? te.maxStackSize.get() : Math.min(stack.getMaxStackSize(), te.maxStackSize.get());
		}

		public ItemStack getStack() {
			TransportedItemStack held = te.heldItem;
			if (held == null || held.stack == null || held.stack.isEmpty())
				return ItemStack.EMPTY;
			return held.stack;
		}
	}
}

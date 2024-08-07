package com.simibubi.create.content.processing.basin;

import com.simibubi.create.foundation.item.SmartInventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class BasinInventory extends SmartInventory {

	private BasinBlockEntity blockEntity;

	public BasinInventory(int slots, BasinBlockEntity be) {
		super(slots, be, 16, true);
		this.blockEntity = be;
		this.whenContentsChanged(be::notifyChangeOfContents);
	}

	@Override
	public SmartInventory whenContentsChanged(Runnable updateCallback) {
		return super.whenContentsChanged(() -> {
			updateCallback.run();
			blockEntity.notifyChangeOfContents();
		});
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		int firstFreeSlot = -1;

		for (int i = 0; i < getSlots(); i++) {
			// Only insert if no other slot already has a stack of this item
			if (i != slot && ItemHandlerHelper.canItemStacksStack(stack, inv.getStackInSlot(i)))
				return stack;
			if (inv.getStackInSlot(i)
				.isEmpty() && firstFreeSlot == -1)
				firstFreeSlot = i;
		}

		// Only insert if this is the first empty slot, prevents overfilling in the
		// simulation pass
		if (inv.getStackInSlot(slot)
			.isEmpty() && firstFreeSlot != slot)
			return stack;

		return super.insertItem(slot, stack, simulate);
	}

	@Override
	protected void onFinalCommit() {
		super.onFinalCommit();
		blockEntity.notifyChangeOfContents();
	}
}

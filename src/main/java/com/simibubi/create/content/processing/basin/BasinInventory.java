package com.simibubi.create.content.processing.basin;

import com.simibubi.create.foundation.item.SmartInventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class BasinInventory extends SmartInventory {

	private BasinBlockEntity blockEntity;

	public BasinInventory(int slots, BasinBlockEntity be) {
		super(slots, be, 16, true);
		this.blockEntity = be;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		// Only insert if no other slot already has a stack of this item
		for (int i = 0; i < getSlots(); i++)
			if (i != slot && ItemHandlerHelper.canItemStacksStack(stack, inv.getStackInSlot(i)))
				return stack;
		return super.insert(resource, maxAmount, transaction);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack extractItem = super.extractItem(slot, amount, simulate);
		if (!simulate && !extractItem.isEmpty())
			blockEntity.notifyChangeOfContents();
		return extractItem;
	}

	// old code:
	// 	@Override
	//	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
	//		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
	//		if (!insertionAllowed)
	//			return 0;
	//		// Only insert if no other slot already has a stack of this item
	//		try (Transaction test = transaction.openNested()) {
	//			long contained = this.extract(resource, Long.MAX_VALUE, test);
	//			if (contained != 0) {
	//				// already have this item. can we stack?
	//				long maxStackSize = Math.min(stackSize, resource.getItem().getMaxStackSize());
	//				long space = Math.max(0, maxStackSize - contained);
	//				if (space <= 0) {
	//					// nope.
	//					return 0;
	//				} else {
	//					// yes!
	//					maxAmount = Math.min(space, maxAmount);
	//				}
	//			}
	//		}
	//		return super.insert(resource, maxAmount, transaction);
	//	}

}

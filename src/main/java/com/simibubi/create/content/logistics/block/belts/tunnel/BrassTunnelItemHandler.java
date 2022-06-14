package com.simibubi.create.content.logistics.block.belts.tunnel;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public class BrassTunnelItemHandler implements SingleSlotStorage<ItemVariant> {

	private BrassTunnelTileEntity te;

	public BrassTunnelItemHandler(BrassTunnelTileEntity te) {
		this.te = te;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!te.hasDistributionBehaviour()) {
			Storage<ItemVariant> beltCapability = te.getBeltCapability();
			if (beltCapability == null)
				return 0;
			return beltCapability.insert(resource, maxAmount, transaction);
		}

		if (!te.canTakeItems())
			return 0;
		int toInsert = Math.min((int) maxAmount, resource.getItem().getMaxStackSize());

		te.setStackToDistribute(resource.toStack(toInsert), transaction);
		return toInsert;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		Storage<ItemVariant> beltCapability = te.getBeltCapability();
		if (beltCapability == null)
			return 0;
		return beltCapability.extract(resource, maxAmount, transaction);
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
		return getStack().getMaxStackSize();
	}

	public ItemStack getStack() {
		ItemStack stack = te.stackToDistribute;
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		return stack;
	}
}

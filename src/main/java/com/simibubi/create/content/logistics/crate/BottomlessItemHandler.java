package com.simibubi.create.content.logistics.crate;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BottomlessItemHandler extends ItemStackHandler implements SingleSlotStorage<ItemVariant> { // must extend ItemStackHandler for mounted storages

	private Supplier<ItemStack> suppliedItemStack;

	public BottomlessItemHandler(Supplier<ItemStack> suppliedItemStack) {
		super(0);
		this.suppliedItemStack = suppliedItemStack;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return maxAmount;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		ItemStack stack = suppliedItemStack.get();
		if (stack == null || !resource.matches(stack))
			return 0;
		if (!stack.isEmpty())
			return Math.min(stack.getMaxStackSize(), maxAmount);
		return 0;
	}

	protected ItemStack getStack() {
		ItemStack stack = suppliedItemStack.get();
		return stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack;
	}

	@Override
	public boolean isResourceBlank() {
		return getStack().isEmpty();
	}

	@Override
	public ItemVariant getResource() {
		return isResourceBlank() ? ItemVariant.blank() : ItemVariant.of(suppliedItemStack.get());
	}

	@Override
	public long getAmount() {
		return Long.MAX_VALUE;
	}

	@Override
	public long getCapacity() {
		return Long.MAX_VALUE;
	}

	// avoid the slot list

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getStack();
	}

	@Override
	public ItemVariant getVariantInSlot(int slot) {
		return getResource();
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return SingleSlotStorage.super.iterator(); // singleton iterator on this
	}

	@Override
	public Iterable<StorageView<ItemVariant>> nonEmptyViews() {
		return this::nonEmptyIterator;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
		return isResourceBlank() ? Collections.emptyIterator() : iterator();
	}
}

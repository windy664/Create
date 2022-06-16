package com.simibubi.create.foundation.utility.fabric;

import java.util.List;
import java.util.function.Supplier;

public record ListEntrySupplier<T>(int index, List<T> list) implements Supplier<T> {
	@Override
	public T get() {
		return list.get(index);
	}
}

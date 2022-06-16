package com.simibubi.create.foundation.utility.fabric;

import java.util.List;
import java.util.function.Consumer;

public record ListEntryConsumer<T>(int index, List<T> list) implements Consumer<T> {
	@Override
	public void accept(T t) {
		list.set(index, t);
	}
}

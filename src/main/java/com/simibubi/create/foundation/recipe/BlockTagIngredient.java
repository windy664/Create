package com.simibubi.create.foundation.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.crafting.AbstractIngredient;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockTagIngredient extends AbstractIngredient {
	protected final TagKey<Block> tag;

	@Nullable
	protected ItemStack[] itemStacks;
	@Nullable
	protected IntList stackingIds;

	protected BlockTagIngredient(TagKey<Block> tag) {
		this.tag = tag;
	}

	public static BlockTagIngredient create(TagKey<Block> tag) {
		return new BlockTagIngredient(tag);
	}

	protected void dissolve() {
		if (itemStacks == null) {
			List<ItemStack> list = new ArrayList<>();
			for (Holder<Block> block : Registry.BLOCK.getTag(tag).get()) {
				ItemStack stack = new ItemStack(block.value());
				if (!stack.isEmpty()) {
					list.add(stack);
				}
			}
			itemStacks = list.toArray(ItemStack[]::new);
		}
	}

	@Override
	public ItemStack[] getItems() {
		dissolve();
		return itemStacks;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) {
			return false;
		}

		dissolve();
		if (itemStacks.length == 0) {
			return stack.isEmpty();
		}

		for (ItemStack itemStack : itemStacks) {
			if (itemStack.is(stack.getItem())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public IntList getStackingIds() {
		if (stackingIds == null) {
			dissolve();
			stackingIds = new IntArrayList(itemStacks.length);

			for (ItemStack stack : itemStacks) {
				stackingIds.add(StackedContents.getStackingIndex(stack));
			}

			stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return stackingIds;
	}

	public TagKey<Block> getTag() {
		return tag;
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
		json.addProperty("tag", tag.location().toString());
		return json;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(CraftingHelper.getID(Serializer.INSTANCE));
		TagKey<Block> tag = getTag();
		buffer.writeResourceLocation(tag.location());
	}

	public static class Serializer implements IngredientDeserializer {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public BlockTagIngredient fromJson(JsonObject json) {
			ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
			TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, rl);
			return new BlockTagIngredient(tag);
		}

		@Override
		public BlockTagIngredient fromNetwork(FriendlyByteBuf buffer) {
			ResourceLocation rl = buffer.readResourceLocation();
			TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, rl);
			return new BlockTagIngredient(tag);
		}
	}
}

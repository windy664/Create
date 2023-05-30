package com.simibubi.create.foundation.utility.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.crafting.AbstractIngredient;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class BlockTagIngredient extends AbstractIngredient {
	protected final TagKey<Block> tag;

	protected BlockTagIngredient(TagKey<Block> tag) {
		this.tag = tag;
	}

	public static BlockTagIngredient create(TagKey<Block> tag) {
		return new BlockTagIngredient(tag);
	}

	@Override
	public void dissolve() {
		if (itemStacks == null) {
			List<ItemStack> list = new ArrayList<>();
			for (Holder<Block> holder : Registry.BLOCK.getTagOrEmpty(tag)) {
				Block block = holder.value();
				ItemStack stack = new ItemStack(block);
				if (!stack.isEmpty()) {
					list.add(stack);
				}
			}
			itemStacks = list.toArray(ItemStack[]::new);
		}
	}

	public TagKey<Block> getTag() {
		return tag;
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
		buffer.writeResourceLocation(Create.asResource("block_tag_ingredient"));
		buffer.writeResourceLocation(tag.location());
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements IngredientDeserializer {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public Ingredient fromJson(JsonObject json) {
			ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
			TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, rl);
			return new BlockTagIngredient(tag);
		}

		@Override
		public Ingredient fromNetwork(FriendlyByteBuf buffer) {
			ResourceLocation rl = buffer.readResourceLocation();
			TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, rl);
			return new BlockTagIngredient(tag);
		}
	}
}

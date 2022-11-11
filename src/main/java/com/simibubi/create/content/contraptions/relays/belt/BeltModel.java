package com.simibubi.create.content.contraptions.relays.belt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.relays.belt.BeltTileEntity.CasingType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BeltModel extends ForwardingBakedModel {

	private static final SpriteShiftEntry SPRITE_SHIFT = AllSpriteShifts.ANDESIDE_BELT_CASING;

	public BeltModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		boolean applyTransform = false;
		if (blockView instanceof RenderAttachedBlockView attachmentView) {
			if (attachmentView.getBlockEntityRenderAttachment(pos) instanceof CasingType type) {
				if (type != CasingType.NONE && type != CasingType.BRASS) {
					applyTransform = true;
				}
			}
		}

		if (applyTransform) {
			SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));
			context.pushTransform(quad -> {
				TextureAtlasSprite sprite = spriteFinder.find(quad, 0);
				if (sprite == SPRITE_SHIFT.getOriginal()) {
					for (int vertex = 0; vertex < 4; vertex++) {
						float u = quad.spriteU(vertex, 0);
						float v = quad.spriteV(vertex, 0);
						quad.sprite(vertex, 0,
								SPRITE_SHIFT.getTargetU(u),
								SPRITE_SHIFT.getTargetV(v)
						);
					}
				}
				return true;
			});
		}
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		if (applyTransform) {
			context.popTransform();
		}
	}

}

package com.simibubi.create.content.kinetics.belt;

import java.util.Random;
import java.util.function.Supplier;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity.CasingType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;

public class BeltModel extends ForwardingBakedModel {

	public static final ModelProperty<CasingType> CASING_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Boolean> COVER_PROPERTY = new ModelProperty<>();

	private static final SpriteShiftEntry SPRITE_SHIFT = AllSpriteShifts.ANDESIDE_BELT_CASING;

	public BeltModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public TextureAtlasSprite getParticleIcon(IModelData data) {
		if (!data.hasProperty(CASING_PROPERTY))
			return super.getParticleIcon(data);
		CasingType type = data.getData(CASING_PROPERTY);
		if (type == CasingType.NONE || type == CasingType.BRASS)
			return super.getParticleIcon(data);
		return AllSpriteShifts.ANDESITE_CASING.getOriginal();
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	// TODO PORT 0.5.1
	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
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

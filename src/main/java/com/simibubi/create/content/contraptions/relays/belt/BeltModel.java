package com.simibubi.create.content.contraptions.relays.belt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.relays.belt.BeltTileEntity.CasingType;
import com.simibubi.create.foundation.block.render.QuadHelper;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import io.github.fabricators_of_create.porting_lib.model.IModelData;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BeltModel extends ForwardingBakedModel {

	private static final SpriteShiftEntry SPRITE_SHIFT = AllSpriteShifts.ANDESIDE_BELT_CASING;

	public BeltModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		List<BakedQuad> quads = super.getQuads(state, side, rand, extraData);
		if (!extraData.hasProperty(CASING_PROPERTY))
			return quads;
		CasingType type = extraData.getData(CASING_PROPERTY);
		if (type == CasingType.NONE || type == CasingType.BRASS)
			return quads;

		quads = new ArrayList<>(quads);

		for (int i = 0; i < quads.size(); i++) {
			BakedQuad quad = quads.get(i);
			TextureAtlasSprite original = quad.getSprite();
			if (original != SPRITE_SHIFT.getOriginal())
				continue;

			BakedQuad newQuad = QuadHelper.clone(quad);
			int[] vertexData = newQuad.getVertices();

			for (int vertex = 0; vertex < 4; vertex++) {
				float u = QuadHelper.getU(vertexData, vertex);
				float v = QuadHelper.getV(vertexData, vertex);
				QuadHelper.setU(vertexData, vertex, SPRITE_SHIFT.getTargetU(u));
				QuadHelper.setV(vertexData, vertex, SPRITE_SHIFT.getTargetV(v));
			}
		}
		if (applyTransform) {
			TextureAtlasSprite original = AllSpriteShifts.ANDESIDE_BELT_CASING.getOriginal();
			TextureAtlasSprite target = AllSpriteShifts.ANDESIDE_BELT_CASING.getTarget();
			SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));
			context.pushTransform(quad -> {
				TextureAtlasSprite sprite = spriteFinder.find(quad, 0);
				if (sprite == original) {
					for (int vertex = 0; vertex < 4; vertex++) {
						float u = quad.spriteU(vertex, 0);
						float v = quad.spriteV(vertex, 0);
						quad.sprite(vertex, 0,
								target.getU(SuperByteBuffer.getUnInterpolatedU(original, u)),
								target.getV(SuperByteBuffer.getUnInterpolatedV(original, v))
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

package com.simibubi.create.content.decoration.copycat;

import java.util.Random;
import java.util.function.Supplier;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.Iterate;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CopycatModel extends ForwardingBakedModel {

	public CopycatModel(BakedModel originalModel) {
		wrapped = originalModel;
	}

	private void gatherOcclusionData(BlockAndTintGetter world, BlockPos pos, BlockState state, BlockState material,
		OcclusionData occlusionData, CopycatBlock copycatBlock) {
		MutableBlockPos mutablePos = new MutableBlockPos();
		for (Direction face : Iterate.directions) {
			if (!copycatBlock.canFaceBeOccluded(state, face))
				continue;
			MutableBlockPos neighbourPos = mutablePos.setWithOffset(pos, face);
			if (!Block.shouldRenderFace(material, world, pos, face, neighbourPos))
				occlusionData.occlude(face);
		}
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState material;
		if (blockView instanceof RenderAttachedBlockView attachmentView
				&& attachmentView.getBlockEntityRenderAttachment(pos) instanceof BlockState material1) {
			material = material1;
		} else {
			material = AllBlocks.COPYCAT_BASE.getDefaultState();
		}

		OcclusionData occlusionData = new OcclusionData();
		if (state.getBlock() instanceof CopycatBlock copycatBlock) {
			gatherOcclusionData(blockView, pos, state, material, occlusionData, copycatBlock);
		}

		CullFaceRemovalData cullFaceRemovalData = new CullFaceRemovalData();
		if (state.getBlock() instanceof CopycatBlock copycatBlock) {
			for (Direction cullFace : Iterate.directions) {
				if (copycatBlock.shouldFaceAlwaysRender(state, cullFace)) {
					cullFaceRemovalData.remove(cullFace);
				}
			}
		}

		emitBlockQuadsInner(blockView, state, pos, randomSupplier, context, material, cullFaceRemovalData, occlusionData);
	}

	protected abstract void emitBlockQuadsInner(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, BlockState material, CullFaceRemovalData cullFaceRemovalData, OcclusionData occlusionData);

	// TODO
//	@Override
//	public TextureAtlasSprite getParticleIcon(IModelData data) {
//		BlockState material = getMaterial(data);
//
//		if (material == null)
//			return super.getParticleIcon(data);
//
//		IModelData wrappedData = data.getData(WRAPPED_DATA_PROPERTY);
//		if (wrappedData == null)
//			wrappedData = EmptyModelData.INSTANCE;
//
//		return getModelOf(material).getParticleIcon(wrappedData);
//	}
//
//	@Nullable
//	public static BlockState getMaterial(IModelData data) {
//		BlockState material = data.getData(MATERIAL_PROPERTY);
//		return material == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : material;
//	}

	public static BakedModel getModelOf(BlockState state) {
		return Minecraft.getInstance()
			.getBlockRenderer()
			.getBlockModel(state);
	}

	protected static class OcclusionData {
		private final boolean[] occluded;

		public OcclusionData() {
			occluded = new boolean[6];
		}

		public void occlude(Direction face) {
			occluded[face.get3DDataValue()] = true;
		}

		public boolean isOccluded(Direction face) {
			return face == null ? false : occluded[face.get3DDataValue()];
		}
	}

	protected static class CullFaceRemovalData {
		private final boolean[] shouldRemove;

		public CullFaceRemovalData() {
			shouldRemove = new boolean[6];
		}

		public void remove(Direction face) {
			shouldRemove[face.get3DDataValue()] = true;
		}

		public boolean shouldRemove(Direction face) {
			return face == null ? false : shouldRemove[face.get3DDataValue()];
		}
	}

}

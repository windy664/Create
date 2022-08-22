package com.simibubi.create.content.contraptions.fluids;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour.AttachmentTypes.ComponentPartials;
import com.simibubi.create.content.contraptions.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedTileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PipeAttachmentModel extends ForwardingBakedModel {

	private boolean hideAttachmentConnector;

	public static PipeAttachmentModel opaque(BakedModel template) {
		return new PipeAttachmentModel(template, false);
	}

	public static PipeAttachmentModel transparent(BakedModel template) {
		return new PipeAttachmentModel(template, true);
	}

	public PipeAttachmentModel(BakedModel template, boolean hideAttachmentConnector) {
		wrapped = template;
		this.hideAttachmentConnector = hideAttachmentConnector;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		PipeModelData data = new PipeModelData();
		FluidTransportBehaviour transport = TileEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
		BracketedTileEntityBehaviour bracket = TileEntityBehaviour.get(world, pos, BracketedTileEntityBehaviour.TYPE);

		if (transport != null)
			for (Direction d : Iterate.directions)
				data.putAttachment(d, transport.getRenderedRimAttachment(world, pos, state, d));
		if (bracket != null)
			data.putBracket(bracket.getBracket());

		data.setEncased(FluidPipeBlock.shouldDrawCasing(world, pos, state));

		context.pushTransform(quad -> {
			Direction cullFace = quad.cullFace();
			if (cullFace != null) {
				return !data.hasRim(cullFace);
			}
			return true;
		});
		super.emitBlockQuads(world, state, pos, randomSupplier, context);
		context.popTransform();

		BakedModel bracketModel = data.getBracket();
		if (bracketModel != null)
			((FabricBakedModel) bracketModel).emitBlockQuads(world, state, pos, randomSupplier, context);
		if (hideAttachmentConnector)
			context.pushTransform(quad -> quad.cullFace() != Direction.UP);
		for (Direction d : Iterate.directions) {
			AttachmentTypes type = data.getAttachment(d);
			for (ComponentPartials partial : type.partials) {
				((FabricBakedModel) AllBlockPartials.PIPE_ATTACHMENTS.get(partial)
						.get(d)
						.get())
						.emitBlockQuads(world, state, pos, randomSupplier, context);
			}
		}
		if (data.isEncased())
			((FabricBakedModel) AllBlockPartials.FLUID_PIPE_CASING.get())
				.emitBlockQuads(world, state, pos, randomSupplier, context);
		if (hideAttachmentConnector)
			context.popTransform();
	}

	private static class PipeModelData {
		private AttachmentTypes[] attachments;
		private boolean encased;
		private BakedModel bracket;

		public PipeModelData() {
			attachments = new AttachmentTypes[6];
			Arrays.fill(attachments, AttachmentTypes.NONE);
		}

		public void putBracket(BlockState state) {
			if (state != null) {
				this.bracket = Minecraft.getInstance()
					.getBlockRenderer()
					.getBlockModel(state);
			}
		}

		public BakedModel getBracket() {
			return bracket;
		}

		public void putAttachment(Direction face, AttachmentTypes rim) {
			attachments[face.get3DDataValue()] = rim;
		}

		public AttachmentTypes getAttachment(Direction face) {
			return attachments[face.get3DDataValue()];
		}

		public void setEncased(boolean encased) {
			this.encased = encased;
		}

		public boolean isEncased() {
			return encased;
		}
	}

}

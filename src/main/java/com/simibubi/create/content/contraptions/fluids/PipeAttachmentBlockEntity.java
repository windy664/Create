package com.simibubi.create.content.contraptions.fluids;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Fabric: Implement this on any BlockEntity that uses {@link PipeAttachmentModel} and call {@link #getAttachments(BlockEntity)}
 */
public interface PipeAttachmentBlockEntity extends RenderAttachmentBlockEntity {
	@Nullable
	static AttachmentTypes[] getAttachments(BlockEntity be) {
		FluidTransportBehaviour behavior = TileEntityBehaviour.get(be, FluidTransportBehaviour.TYPE);
		if (behavior == null)
			return null;
		AttachmentTypes[] attachments = new AttachmentTypes[6];
		for (int i = 0; i < Iterate.directions.length; i++) {
			attachments[i] = behavior.getRenderedRimAttachment(
					be.getLevel(), be.getBlockPos(), be.getBlockState(), Iterate.directions[i]
			);
		}
		return attachments;
	}
}

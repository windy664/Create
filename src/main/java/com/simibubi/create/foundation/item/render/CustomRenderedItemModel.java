package com.simibubi.create.foundation.item.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public class CustomRenderedItemModel extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel {

	public CustomRenderedItemModel(BakedModel originalModel) {
		this.wrapped = originalModel;
	}

	@Override
	public boolean isCustomRenderer() {
		return true;
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat,
									 boolean leftHand) {
		// Super call returns originalModel, but we want to return this, else BEWLR
		// won't be used.
		TransformTypeDependentItemBakedModel.maybeApplyTransform(wrapped, cameraItemDisplayContext, mat, leftHand);
		return this;
	}

	public BakedModel getOriginalModel() {
		return wrapped;
	}

}

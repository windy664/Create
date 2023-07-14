package com.simibubi.create.content.equipment.goggles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public class GogglesModel extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel {

	public GogglesModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat, boolean leftHanded) {
		if (cameraItemDisplayContext == ItemDisplayContext.HEAD) {
			return TransformTypeDependentItemBakedModel.maybeApplyTransform(
					AllPartialModels.GOGGLES.get(), cameraItemDisplayContext, mat, leftHanded
			);
		}
		return TransformTypeDependentItemBakedModel.super.applyTransform(cameraItemDisplayContext, mat, leftHanded);
	}

}

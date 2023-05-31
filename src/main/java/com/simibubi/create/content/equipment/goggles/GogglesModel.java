package com.simibubi.create.content.equipment.goggles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public class GogglesModel extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel {

	public GogglesModel(BakedModel template) {
		wrapped = template;
	}

	@Override
	public BakedModel applyTransform(TransformType cameraTransformType, PoseStack mat, boolean leftHanded) {
		if (cameraTransformType == TransformType.HEAD)
			return AllPartialModels.GOGGLES.get()
				.applyTransform(cameraTransformType, mat, leftHanded);
		return super.applyTransform(cameraTransformType, mat, leftHanded);
	}

}

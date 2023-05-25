package com.simibubi.create.foundation.item;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.mixin.accessor.HumanoidArmorLayerAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public interface LayeredArmorItem extends CustomRenderedArmorItem {
	@Environment(EnvType.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	default void renderArmorPiece(HumanoidArmorLayer<?, ?, ?> layer, PoseStack poseStack,
			MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light,
			HumanoidModel<?> originalModel, ItemStack stack) {
		if (!(stack.getItem() instanceof ArmorItem item)) {
			return;
		}
		if (item.getSlot() != slot) {
			return;
		}

		HumanoidArmorLayerAccessor accessor = (HumanoidArmorLayerAccessor) layer;
		Map<String, ResourceLocation> locationCache = HumanoidArmorLayerAccessor.create$getArmorLocationCache();
		boolean glint = stack.hasFoil();

		HumanoidModel<?> innerModel = accessor.create$getInnerModel();
		layer.getParentModel().copyPropertiesTo((HumanoidModel) innerModel);
		accessor.create$callSetPartVisibility(innerModel, slot);
		String locationStr2 = getArmorTextureLocation(entity, slot, stack, 2);
		ResourceLocation location2 = locationCache.computeIfAbsent(locationStr2, ResourceLocation::new);
		renderModel(poseStack, bufferSource, light, glint, innerModel, 1.0F, 1.0F, 1.0F, location2);

		HumanoidModel<?> outerModel = accessor.create$getOuterModel();
		layer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
		accessor.create$callSetPartVisibility(outerModel, slot);
		String locationStr1 = getArmorTextureLocation(entity, slot, stack, 1);
		ResourceLocation location1 = locationCache.computeIfAbsent(locationStr1, ResourceLocation::new);
		renderModel(poseStack, bufferSource, light, glint, outerModel, 1.0F, 1.0F, 1.0F, location1);
	}

	// fabric: forge patches! yay!
	@Environment(EnvType.CLIENT)
	static void renderModel(PoseStack poseStack, MultiBufferSource buffers, int light, boolean glint,
							Model pModel, float r, float g, float b, ResourceLocation armorResource) {
		VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(armorResource), false, glint);
		pModel.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
	}

	String getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer);
}

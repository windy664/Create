package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.utility.RaycastHelper;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.concurrent.atomic.AtomicBoolean;

public class ValueSettingsInputHandler {

	public static InteractionResult onBlockActivated(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		BlockPos pos = hitResult.getBlockPos();

		if (!canInteract(player))
			return InteractionResult.PASS;
		if (AllBlocks.CLIPBOARD.isIn(player.getMainHandItem()))
			return InteractionResult.PASS;
		if (!(world.getBlockEntity(pos)instanceof SmartBlockEntity sbe))
			return InteractionResult.PASS;

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			AtomicBoolean shouldCancel = new AtomicBoolean(false);
			EnvExecutor.runWhenOn(EnvType.CLIENT,
					() -> () -> CreateClient.VALUE_SETTINGS_HANDLER.cancelIfWarmupAlreadyStarted(pos, shouldCancel));
			if (shouldCancel.get())
				return InteractionResult.FAIL;
		}

		for (BlockEntityBehaviour behaviour : sbe.getAllBehaviours()) {
			if (!(behaviour instanceof ValueSettingsBehaviour valueSettingsBehaviour))
				continue;

			BlockHitResult ray = RaycastHelper.rayTraceRange(world, player, 10);
			if (ray == null)
				return InteractionResult.PASS;
			if (behaviour instanceof SidedFilteringBehaviour) {
				behaviour = ((SidedFilteringBehaviour) behaviour).get(ray.getDirection());
				if (behaviour == null)
					continue;
			}

			if (!valueSettingsBehaviour.isActive())
				continue;
			if (valueSettingsBehaviour.onlyVisibleWithWrench()
				&& !AllItemTags.WRENCH.matches(player.getItemInHand(hand)))
				continue;
			if (valueSettingsBehaviour.getSlotPositioning()instanceof ValueBoxTransform.Sided sidedSlot) {
				if (!sidedSlot.isSideActive(sbe.getBlockState(), ray.getDirection()))
					continue;
				sidedSlot.fromSide(ray.getDirection());
			}

			boolean fakePlayer = player.isFake();
			if (!valueSettingsBehaviour.testHit(ray.getLocation()) && !fakePlayer)
				continue;

			if (!valueSettingsBehaviour.acceptsValueSettings() || fakePlayer) {
				valueSettingsBehaviour.onShortInteract(player, hand, ray.getDirection());
				return InteractionResult.SUCCESS;
			}

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				BehaviourType<?> type = behaviour.getType();
				EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> CreateClient.VALUE_SETTINGS_HANDLER
					.startInteractionWith(pos, type, hand, ray.getDirection()));
			}

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static boolean canInteract(Player player) {
		return player != null && !player.isSpectator() && !player.isShiftKeyDown();
	}

}

package com.simibubi.create.content.logistics.trains.management.schedule;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraption;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ScheduleItemEntityInteraction {

	public static InteractionResult removeScheduleFromConductor(Player player, InteractionHand hand, Entity entity) {
		if (player == null || entity == null)
			return null;
		if (player.isSpectator())
			return null;

		Entity rootVehicle = entity.getRootVehicle();
		if (!(rootVehicle instanceof CarriageContraptionEntity))
			return null;
		if (!(entity instanceof LivingEntity living))
			return null;
		if (player.getCooldowns()
			.isOnCooldown(AllItems.SCHEDULE.get()))
			return null;

		ItemStack itemStack = player.getItemInHand(hand);
		if (itemStack.getItem()instanceof ScheduleItem si) {
			InteractionResult result = si.handScheduleTo(itemStack, player, living, hand);
			if (result.consumesAction()) {
				player.getCooldowns()
					.addCooldown(AllItems.SCHEDULE.get(), 5);
				return result;
			}
		}

		if (hand == InteractionHand.OFF_HAND)
			return null;

		CarriageContraptionEntity cce = (CarriageContraptionEntity) rootVehicle;
		Contraption contraption = cce.getContraption();
		if (!(contraption instanceof CarriageContraption cc))
			return null;

		Train train = cce.getCarriage().train;
		if (train == null)
			return null;
		if (train.runtime.getSchedule() == null)
			return null;

		Integer seatIndex = contraption.getSeatMapping()
			.get(entity.getUUID());
		if (seatIndex == null)
			return null;
		BlockPos seatPos = contraption.getSeats()
			.get(seatIndex);
		Couple<Boolean> directions = cc.conductorSeats.get(seatPos);
		if (directions == null)
			return null;

		boolean onServer = !player.level.isClientSide;

		if (train.runtime.paused && !train.runtime.completed) {
			if (onServer) {
				train.runtime.paused = false;
				AllSoundEvents.CONFIRM.playOnServer(player.level, player.blockPosition(), 1, 1);
				player.displayClientMessage(Lang.translateDirect("schedule.continued"), true);
			}

			player.getCooldowns()
				.addCooldown(AllItems.SCHEDULE.get(), 5);
			return InteractionResult.SUCCESS;
		}

		ItemStack itemInHand = player.getItemInHand(hand);
		if (!itemInHand.isEmpty()) {
			if (onServer) {
				AllSoundEvents.DENY.playOnServer(player.level, player.blockPosition(), 1, 1);
				player.displayClientMessage(Lang.translateDirect("schedule.remove_with_empty_hand"), true);
			}
			return InteractionResult.SUCCESS;
		}

		if (onServer) {
			AllSoundEvents.playItemPickup(player);
			player.displayClientMessage(
				Lang.translateDirect(
					train.runtime.isAutoSchedule ? "schedule.auto_removed_from_train" : "schedule.removed_from_train"),
				true);

			player.getInventory()
				.placeItemBackInInventory(train.runtime.returnSchedule());
		}

		player.getCooldowns()
			.addCooldown(AllItems.SCHEDULE.get(), 5);
		return InteractionResult.SUCCESS;
	}

}

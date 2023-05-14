package com.simibubi.create.events;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionHandlerClient;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.controls.TrainHUD;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandlerClient;
import com.simibubi.create.content.logistics.item.LinkedControllerClientHandler;
import com.simibubi.create.content.logistics.trains.entity.TrainRelocator;
import com.simibubi.create.content.logistics.trains.track.CurvedTrackInteraction;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringHandler;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueHandler;
import io.github.fabricators_of_create.porting_lib.event.client.KeyInputCallback;
import io.github.fabricators_of_create.porting_lib.event.client.MouseButtonCallback;
import io.github.fabricators_of_create.porting_lib.event.client.MouseScrolledCallback;

import io.github.fabricators_of_create.porting_lib.event.client.OnStartUseItemCallback;
import io.github.fabricators_of_create.porting_lib.event.client.PickBlockCallback;
import io.github.fabricators_of_create.porting_lib.util.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class InputEvents {

	public static void onKeyInput(int key, int scancode, int action, int mods) {
		if (Minecraft.getInstance().screen != null)
			return;

		boolean pressed = !(action == 0);

		CreateClient.SCHEMATIC_HANDLER.onKeyInput(key, pressed);
		ToolboxHandlerClient.onKeyInput(key, pressed);
	}

	public static boolean onMouseScrolled(double delta) {
		if (Minecraft.getInstance().screen != null)
			return false;

//		CollisionDebugger.onScroll(delta);
		boolean cancelled = CreateClient.SCHEMATIC_HANDLER.mouseScrolled(delta)
			|| CreateClient.SCHEMATIC_AND_QUILL_HANDLER.mouseScrolled(delta) || TrainHUD.onScroll(delta)
			|| ElevatorControlsHandler.onScroll(delta);
		return cancelled;
	}

	public static InteractionResult onMouseInput(int button, int action, int mods) {
		if (Minecraft.getInstance().screen != null)
			return InteractionResult.PASS;

		boolean pressed = !(action == 0);

		CreateClient.SCHEMATIC_HANDLER.onMouseInput(button, pressed);
		CreateClient.SCHEMATIC_AND_QUILL_HANDLER.onMouseInput(button, pressed);
		return InteractionResult.PASS;
	}

	public static InteractionResult onClickInput(int button, int action, int mods) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen != null)
			return InteractionResult.PASS;

		int use = KeyBindingHelper.getKeyCode(mc.options.keyUse).getValue();
		int attack = KeyBindingHelper.getKeyCode(mc.options.keyAttack).getValue();
		boolean isUse = button == use;
		boolean isAttack = button == attack;

		// fabric: filter only presses
		if (action != 1)
			return InteractionResult.PASS;

		if (CurvedTrackInteraction.onClickInput(isUse, isAttack)) {
			return InteractionResult.SUCCESS;
		}


		if (isUse || isAttack) {
			if (CreateClient.GLUE_HANDLER.onMouseInput(isAttack))
				return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	public static InteractionResult onStartUseItem(InteractionHand hand) {
		if (Minecraft.getInstance().screen == null) {
			LinkedControllerClientHandler.deactivateInLectern();
			TrainRelocator.onClicked();
		}
		return InteractionResult.PASS;
	}

	public static boolean onPickBlock() {
		if (Minecraft.getInstance().screen != null)
			return false;
		return ToolboxHandlerClient.onPickItem();
	}

	public static void register() {
		KeyInputCallback.EVENT.register(InputEvents::onKeyInput);
		MouseScrolledCallback.EVENT.register(InputEvents::onMouseScrolled);
		MouseButtonCallback.EVENT.register(InputEvents::onMouseInput);
		MouseButtonCallback.EVENT.register(InputEvents::onClickInput);
		OnStartUseItemCallback.EVENT.register(InputEvents::onStartUseItem);
		PickBlockCallback.EVENT.register(InputEvents::onPickBlock);
	}

}

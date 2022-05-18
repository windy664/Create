package com.simibubi.create.events;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionHandlerClient;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandlerClient;
import com.simibubi.create.content.logistics.item.LinkedControllerClientHandler;
import com.simibubi.create.content.logistics.trains.entity.TrainRelocator;
import com.simibubi.create.content.logistics.trains.track.CurvedTrackInteraction;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringHandler;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueHandler;
import io.github.fabricators_of_create.porting_lib.event.client.KeyInputCallback;
import io.github.fabricators_of_create.porting_lib.event.client.MouseButtonCallback;
import io.github.fabricators_of_create.porting_lib.event.client.MouseScrolledCallback;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
			|| CreateClient.SCHEMATIC_AND_QUILL_HANDLER.mouseScrolled(delta) || FilteringHandler.onScroll(delta)
			|| ScrollValueHandler.onScroll(delta);
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

	@SubscribeEvent
	public static InteractionResult onClickInput(int button, int action, int mods) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen != null)
			return;

		if (CurvedTrackInteraction.onClickInput(event)) {
			event.setCanceled(true);
			return;
		}

		KeyMapping key = event.getKeyMapping();

		if (key == mc.options.keyUse || key == mc.options.keyAttack) {
			if (CreateClient.GLUE_HANDLER.onMouseInput(key == mc.options.keyAttack))
				event.setCanceled(true);
		}

		if (mc.options.keyPickItem.isDown()) {
			if (ToolboxHandlerClient.onPickItem())
				return InteractionResult.SUCCESS;
			return InteractionResult.PASS;
		}

		if (button != 1)
			return InteractionResult.PASS;
		LinkedControllerClientHandler.deactivateInLectern();
		TrainRelocator.onClicked(event);
	}

	public static void register() {
		KeyInputCallback.EVENT.register(InputEvents::onKeyInput);
		MouseScrolledCallback.EVENT.register(InputEvents::onMouseScrolled);
		MouseButtonCallback.EVENT.register(InputEvents::onMouseInput);
		MouseButtonCallback.EVENT.register(InputEvents::onClickInput);
	}

}

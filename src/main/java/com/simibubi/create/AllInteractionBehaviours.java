package com.simibubi.create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.contraptions.components.structureMovement.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.DoorMovingInteraction;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.LeverMovingInteraction;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.TrapdoorMovingInteraction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AllInteractionBehaviours {
	private static final Map<Block, MovingInteractionBehaviour> BLOCK_BEHAVIOURS = new HashMap<>();
	private static final List<BehaviourProvider> GLOBAL_BEHAVIOURS = new ArrayList<>();

	public static void registerBehaviour(Block block, MovingInteractionBehaviour provider) {
		BLOCK_BEHAVIOURS.put(block, provider);
	}

	public static void registerBehaviourProvider(BehaviourProvider provider) {
		GLOBAL_BEHAVIOURS.add(provider);
	}

	@Nullable
	public static MovingInteractionBehaviour getBehaviour(BlockState state) {
		MovingInteractionBehaviour behaviour = BLOCK_BEHAVIOURS.get(state.getBlock());
		if (behaviour != null) {
			return behaviour;
		}

		for (BehaviourProvider provider : GLOBAL_BEHAVIOURS) {
			behaviour = provider.getBehaviour(state);
			if (behaviour != null) {
				return behaviour;
			}
		}

		return null;
	}

	public static <B extends Block> NonNullConsumer<? super B> interactionBehaviour(
		MovingInteractionBehaviour behaviour) {
		return b -> registerBehaviour(b, behaviour);
	}

	static void registerDefaults() {
		registerBehaviour(Blocks.LEVER, new LeverMovingInteraction());

		DoorMovingInteraction doorBehaviour = new DoorMovingInteraction();
		registerBehaviourProvider(state -> {
			if (state.is(BlockTags.WOODEN_DOORS)) {
				return doorBehaviour;
			}
			return null;
		});

		TrapdoorMovingInteraction trapdoorBehaviour = new TrapdoorMovingInteraction();
		registerBehaviourProvider(state -> {
			if (state.is(BlockTags.WOODEN_TRAPDOORS)) {
				return trapdoorBehaviour;
			}
			return null;
		});
	}

	public interface BehaviourProvider {
		@Nullable
		MovingInteractionBehaviour getBehaviour(BlockState state);
	}
}

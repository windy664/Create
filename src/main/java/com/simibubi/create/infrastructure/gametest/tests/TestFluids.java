package com.simibubi.create.infrastructure.gametest.tests;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.infrastructure.gametest.CreateGameTestHelper;
import com.simibubi.create.infrastructure.gametest.GameTestGroup;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

@GameTestGroup(path = "fluids")
public class TestFluids {
	@GameTest(template = "hose_pulley_transfer", timeoutTicks = CreateGameTestHelper.TWENTY_SECONDS)
	public static void hosePulleyTransfer(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(7, 7, 5);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(15);
			// check filled
			BlockPos filledLowerCorner = new BlockPos(2, 3, 2);
			BlockPos filledUpperCorner = new BlockPos(4, 5, 4);
			BlockPos.betweenClosed(filledLowerCorner, filledUpperCorner)
					.forEach(pos -> helper.assertBlockPresent(Blocks.WATER, pos));
			// check emptied
			BlockPos emptiedLowerCorner = new BlockPos(8, 3, 2);
			BlockPos emptiedUpperCorner = new BlockPos(10, 5, 4);
			BlockPos.betweenClosed(emptiedLowerCorner, emptiedUpperCorner)
					.forEach(pos -> helper.assertBlockPresent(Blocks.AIR, pos));
			// check nothing left in pulley
			BlockPos pulleyPos = new BlockPos(4, 7, 3);
			Storage<FluidVariant> storage = helper.fluidStorageAt(pulleyPos);
			if (storage instanceof HosePulleyFluidHandler hose) {
				SmartFluidTank internalTank = hose.getInternalTank();
				if (!internalTank.isEmpty())
					helper.fail("Pulley not empty");
			} else {
				helper.fail("Not a pulley");
			}
		});
	}

	@GameTest(template = "in_world_pumping_out")
	public static void inWorldPumpingOut(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(4, 3, 3);
		BlockPos basin = new BlockPos(5, 2, 2);
		BlockPos output = new BlockPos(2, 2, 2);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			helper.assertBlockPresent(Blocks.WATER, output);
			helper.assertTankEmpty(basin);
		});
	}

	@GameTest(template = "in_world_pumping_in")
	public static void inWorldPumpingIn(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(4, 3, 3);
		BlockPos basin = new BlockPos(5, 2, 2);
		BlockPos water = new BlockPos(2, 2, 2);
		FluidStack expectedResult = new FluidStack(Fluids.WATER, FluidConstants.BUCKET);
		helper.pullLever(lever);
		helper.succeedWhen(() -> {
			helper.assertBlockPresent(Blocks.AIR, water);
			helper.assertFluidPresent(expectedResult, basin);
		});
	}

	@GameTest(template = "steam_engine")
	public static void steamEngine(CreateGameTestHelper helper) {
		BlockPos lever = new BlockPos(4, 3, 3);
		helper.pullLever(lever);
		BlockPos stressometer = new BlockPos(5, 2, 5);
		BlockPos speedometer = new BlockPos(4, 2, 5);
		helper.succeedWhen(() -> {
			StressGaugeBlockEntity stress = helper.getBlockEntity(AllBlockEntityTypes.STRESSOMETER.get(), stressometer);
			SpeedGaugeBlockEntity speed = helper.getBlockEntity(AllBlockEntityTypes.SPEEDOMETER.get(), speedometer);
			float capacity = stress.getNetworkCapacity();
			helper.assertCloseEnoughTo(capacity, 2048);
			float rotationSpeed = Mth.abs(speed.getSpeed());
			helper.assertCloseEnoughTo(rotationSpeed, 16);
		});
	}

	@GameTest(template = "3_pipe_combine", timeoutTicks = CreateGameTestHelper.TWENTY_SECONDS)
	public static void threePipeCombine(CreateGameTestHelper helper) {
		BlockPos tank1Pos = new BlockPos(5, 2, 1);
		BlockPos tank2Pos = tank1Pos.south();
		BlockPos tank3Pos = tank2Pos.south();
		long initialContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);

		BlockPos pumpPos = new BlockPos(2, 2, 2);
		helper.flipBlock(pumpPos);
		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(13);
			// make sure fully drained
			helper.assertTanksEmpty(tank1Pos, tank2Pos, tank3Pos);
			// and fully moved
			BlockPos outputTankPos = new BlockPos(1, 2, 2);
			long moved = helper.getFluidInTanks(outputTankPos);
			if (moved != initialContents)
				helper.fail("Wrong amount of fluid amount. expected [%s], got [%s]".formatted(initialContents, moved));
			// verify nothing was duped or deleted
		});
	}

	@GameTest(template = "3_pipe_split", timeoutTicks = CreateGameTestHelper.TEN_SECONDS)
	public static void threePipeSplit(CreateGameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(2, 2, 2);
		BlockPos tank1Pos = new BlockPos(5, 2, 1);
		BlockPos tank2Pos = tank1Pos.south();
		BlockPos tank3Pos = tank2Pos.south();
		BlockPos outputTankPos = new BlockPos(1, 2, 2);

		long totalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos, outputTankPos);
		helper.flipBlock(pumpPos);

		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(7);
			FluidStack contents = helper.getTankContents(outputTankPos);
			if (!contents.isEmpty()) {
				helper.fail("Tank not empty: " + contents.getAmount());
			}
			long newTotalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);
			if (newTotalContents != totalContents) {
				helper.fail("Wrong total fluid amount. expected [%s], got [%s]".formatted(totalContents, newTotalContents));
			}
		});
	}
}

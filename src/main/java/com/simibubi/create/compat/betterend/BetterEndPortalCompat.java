package com.simibubi.create.compat.betterend;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.simibubi.create.Create;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class BetterEndPortalCompat {
	/**
	 * Retrieves the adjusted {@link PortalInfo} for the Better End portal using reflection.
	 *
	 * @param targetLevel The target {@link ServerLevel} (dimension).
	 * @param entity      The probe {@link Entity} used for portal traversal calculations.
	 * @return The adjusted {@link PortalInfo} for the target dimension, or {@code null} if an error occurs.
	 */
	public static PortalInfo getBetterEndPortalInfo(ServerLevel targetLevel, Entity entity) {
		try {
			Class<?> travelerStateClass = Class.forName("org.betterx.betterend.portal.TravelerState");
			Constructor<?> constructor = travelerStateClass.getDeclaredConstructor(Entity.class);
			constructor.setAccessible(true);
			Object travelerState = constructor.newInstance(entity);

			// Set the private portalEntrancePos field to the entity's block position as assumed in TravelerState#findDimensionEntryPoint
			Field portalEntrancePosField = travelerStateClass.getDeclaredField("portalEntrancePos");
			portalEntrancePosField.setAccessible(true);
			portalEntrancePosField.set(travelerState, entity.blockPosition().immutable());

			Method findDimensionEntryPointMethod = travelerStateClass.getDeclaredMethod("findDimensionEntryPoint", ServerLevel.class);
			findDimensionEntryPointMethod.setAccessible(true);

			// We need to lower the result by 1 to align with the floor on the exit side
			PortalInfo otherSide = (PortalInfo) findDimensionEntryPointMethod.invoke(travelerState, targetLevel);
			return new PortalInfo(
					new Vec3(otherSide.pos.x, otherSide.pos.y - 1, otherSide.pos.z),
					otherSide.speed,
					otherSide.yRot,
					otherSide.xRot
			);
		} catch (Throwable e) {
			Create.LOGGER.error("Create's Better End Portal compat failed to initialize: ", e);
		}
		return null;
	}
}

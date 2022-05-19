package com.simibubi.create.foundation.utility.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import net.minecraft.world.entity.player.Player;

public class ReachUtil {
	public static double reach(Player p) {
		return ReachEntityAttributes.getReachDistance(p, p.isCreative() ? 5 : 4.5);
	}
}

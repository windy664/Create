package com.simibubi.create.foundation.networking;

import com.simibubi.create.events.CommonEvents;

import net.minecraft.network.FriendlyByteBuf;

public class LeftClickPacket extends SimplePacketBase {

	public LeftClickPacket() {}

	LeftClickPacket(FriendlyByteBuf buffer) {}

	@Override
	public void write(FriendlyByteBuf buffer) {}

	@Override
	public boolean handle(Context context) {
		if (context.getDirection() != NetworkDirection.PLAY_TO_SERVER)
			return false;
		context.enqueueWork(() -> CommonEvents.leftClickEmpty(context.getSender()));
		return true;
	}

}

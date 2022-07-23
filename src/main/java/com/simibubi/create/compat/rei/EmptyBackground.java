package com.simibubi.create.compat.rei;

import com.mojang.blaze3d.vertex.PoseStack;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;

public class EmptyBackground implements Renderer {

	private int width;
	private int height;

	public EmptyBackground(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}

	@Override
	public void render(PoseStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
	}

	@Override
	public int getZ() {
		return 0;
	}

	@Override
	public void setZ(int z) {

	}
}

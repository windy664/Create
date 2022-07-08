package com.simibubi.create.content.contraptions.goggles;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidUnit;
import io.github.fabricators_of_create.porting_lib.util.MinecraftClientUtil;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/*
* Implement this Interface in the TileEntity class that wants to add info to the screen
* */
public interface IHaveGoggleInformation {

	/**
	 * Use Lang.[...].forGoggles(list)
	 */
	String spacing = "    ";

	/**
	 * Use Lang.[...].forGoggles(list)
	 */
	@Deprecated
	Component componentSpacing = new TextComponent(spacing);

	/**
	 * this method will be called when looking at a TileEntity that implemented this
	 * interface
	 *
	 * @return {@code true} if the tooltip creation was successful and should be
	 *         displayed, or {@code false} if the overlay should not be displayed
	 */
	default boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}

	static String format(double d) {
		return numberFormat.get()
			.format(d).replace("\u00A0", " ");
	}

	default boolean containedFluidTooltip(List<Component> tooltip, boolean isPlayerSneaking, Storage<FluidVariant> handler) {
		tooltip.add(componentSpacing.plainCopy().append(Lang.translate("gui.goggles.fluid_container")));
		FluidUnit unit = AllConfigs.CLIENT.fluidUnitType.get();
		boolean simplify = AllConfigs.CLIENT.simplifyFluidUnit.get();
		TranslatableComponent mb = Lang.translate(unit.getTranslationKey());
		if (handler == null)
			return false;

		LangBuilder mb = Lang.translate("generic.unit.millibuckets");
		Lang.translate("gui.goggles.fluid_container")
			.forGoggles(tooltip);

		boolean isEmpty = true;
		try (Transaction t = TransferUtil.getTransaction()) {
			boolean moreThan1Tank = false;
			StorageView<FluidVariant> first = null;
			for (Iterator<? extends StorageView<FluidVariant>> iterator = handler.iterator(t); iterator.hasNext();) {
				StorageView<FluidVariant> view = iterator.next();
				if (!moreThan1Tank) first = view;
				moreThan1Tank |= iterator.hasNext();
				if (view.isResourceBlank()) continue;
				long amount = view.getAmount();

				if (amount == 0)
					continue;

			Lang.fluidName(fluidStack)
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip, 1);

				Lang.builder()
				.add(Lang.number(fluidStack.getAmount())
						.add(mb)
				.style(ChatFormatting.GOLD))
				.text(ChatFormatting.GRAY, " / ")
				.add(Lang.number(tank.getTankCapacity(i))
						.add(mb)
						.style(ChatFormatting.DARK_GRAY))
						.forGoggles(tooltip, 1);

				isEmpty = false;
			}

			if (moreThan1Tank) {
				if (isEmpty)
					tooltip.remove(tooltip.size() - 1);
				return true;
			}

			if (!isEmpty)
				return true;

		Lang.translate("gui.goggles.fluid_container.capacity")
			.add(Lang.number(tank.getTankCapacity(0))
				.add(mb)
				.style(ChatFormatting.GOLD))
			.style(ChatFormatting.GRAY)
			.forGoggles(tooltip, 1);


			return true;
		}
	}

}

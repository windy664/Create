package com.simibubi.create.compat.modmenu;

import com.simibubi.create.foundation.gui.CreateMainMenuScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class CreateModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return CreateMainMenuScreen::new;
	}
}

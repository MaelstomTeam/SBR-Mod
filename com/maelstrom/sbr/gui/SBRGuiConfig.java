package com.maelstrom.sbr.gui;

import com.maelstrom.sbr.SBRMod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.GuiConfig;

public class SBRGuiConfig extends GuiConfig {

	public SBRGuiConfig(GuiScreen g){
		super(g, new ConfigElement(SBRMod.configFile.getCategory("sbr")).getChildElements(), "sbr", false, false, "\"Save, Backup & Restore\" Config");
	}
}
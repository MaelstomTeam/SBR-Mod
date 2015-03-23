package com.maelstrom.sbr;

import java.io.IOException;
import java.nio.file.Files;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import com.maelstrom.sbr.gui.GuiSaveViewer;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid=SBRMod.modID, name="Save, Backup & Restore", version="Beta 0.0.1.134", useMetadata = true, guiFactory = "com.maelstrom.sbr.gui.GuiFactory")
public class SBRMod {
	public static final int version = 5; //config reseter because im lazy ;p
	
	
	public static Configuration configFile;
	public static int keyShow = Keyboard.KEY_LSHIFT;
	
	@Mod.Instance("sbr")
	public static SBRMod instance;
	
	public static boolean enableSaftyOnSaveScreen = true, onMainMenu = true;
	public static boolean backupJson = true;
	public static int configVersion = instance.version;


	public static final String modID = "sbr";
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
		configFile = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
		if(!GuiSaveViewer.checkDir())
			GuiSaveViewer.createDir();
		if(backupJson){
			GuiSaveViewer.backupJson();
		}
		GuiSaveViewer.getList();
	}
	
	@Mod.EventHandler
    public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new GuiSaveViewer());
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	public static void syncConfig(){
		enableSaftyOnSaveScreen = configFile.getBoolean("on WorldSaves", "sbr", enableSaftyOnSaveScreen, "safty on world save screen incase you're really that accident prone");
		onMainMenu = configFile.getBoolean("on MainMenu", "sbr", onMainMenu, "safty on Main Menu incase you're really that accident prone");
		configVersion = configFile.getInt("version", "ignore", version, 0, 9999, null);
		keyShow = configFile.getInt("show Key", "sbr", Keyboard.KEY_LSHIFT, 0, Keyboard.KEYBOARD_SIZE, "key bid for showing the save icon on the select world screen. not needed if safty is off.");
		backupJson = configFile.getBoolean("backup json at startup", "sbr", backupJson, "backup the .json file containing the extra data for SBR");
		
		if(configFile.hasChanged())
			configFile.save();
		
		//config reseter because im lazy ;p
		if(configVersion != version){
			try {
				Files.deleteIfExists(configFile.getConfigFile().toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			syncConfig();
			return;
		}
		
	}
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equals("sbr"))
			syncConfig();
	}
}
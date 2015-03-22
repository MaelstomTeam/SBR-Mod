package com.maelstrom.sbr.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maelstrom.sbr.SBRMod;
import com.maelstrom.sbr.Zipper;
import com.maelstrom.sbr.json.Backup;
import com.maelstrom.sbr.json.World;
import com.maelstrom.sbr.json.WorldArray;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiSaveViewer extends GuiScreen{
	
	Gui last;
	private GuiButton sbrWorldMenu = new SBRGuiButton(0, 2, 2), sbrWorldMenu2 = new SBRGuiButton(0,this.width / 2 - 100,2);
	public GuiScreen world;
	
    private boolean lastMouseEvent;
    private long timeSince;
    SBRGuiSlot slot;
    
//	@SubscribeEvent
//	public void guiButtonPress2(GuiScreenEvent.ActionPerformedEvent event){
//		System.out.println(event.button);
//	}
    
	@SubscribeEvent
	public void guiDraw(GuiScreenEvent.DrawScreenEvent event){
		if(event.gui instanceof GuiSelectWorld){
			this.mc = event.gui.mc;
	        int i = Mouse.getEventX() * event.gui.width / this.mc.displayWidth;
	        int j = Mouse.getEventY() * event.gui.height / this.mc.displayHeight - 1;
	        int k = Mouse.getEventButton();
			sbrWorldMenu.enabled = (Keyboard.isKeyDown(SBRMod.keyShow)) || !SBRMod.enableSaftyOnSaveScreen;
			sbrWorldMenu.drawButton(mc, i, event.gui.height - j);
			if (Mouse.getEventButtonState()) {
				if(!this.lastMouseEvent && k == 0){
					if(sbrWorldMenu.mousePressed(mc, i, event.gui.height - j)){
						world = event.gui;
						readJson();
						mc.displayGuiScreen(this);
						mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
					}
				}
				this.lastMouseEvent = Mouse.getEventButtonState();
	        }else
	        	this.lastMouseEvent = false;
		}else if(event.gui instanceof GuiMainMenu){
			sbrWorldMenu2 = new SBRGuiButton(0, event.gui.width / 2 - 124, event.gui.height / 4 + 48);
			this.mc = event.gui.mc;
	        int i = Mouse.getEventX() * event.gui.width / this.mc.displayWidth;
	        int j = Mouse.getEventY() * event.gui.height / this.mc.displayHeight - 1;
	        int k = Mouse.getEventButton();
	        sbrWorldMenu2.visible = SBRMod.onMainMenu;
			sbrWorldMenu2.drawButton(mc, i, event.gui.height - j);
			if (Mouse.getEventButtonState()) {
				if(!this.lastMouseEvent && k == 0){
					if(sbrWorldMenu2.mousePressed(mc, i, event.gui.height - j)){
						world = event.gui;
						readJson();
						mc.displayGuiScreen(this);
						mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
					}
				}
				this.lastMouseEvent = Mouse.getEventButtonState();
	        }else
	        	this.lastMouseEvent = false;
		}
	}
	
	/*
	 * #######################################
	 *           Actual GUI STUFF
	 * #######################################
	 */
	
	private GuiButton back;
	public GuiButton save;
	public GuiButton restore;
	public int selection;
	
	public void initGui(){
		slot = new SBRGuiSlot();
		this.buttonList.clear();
		this.buttonList.add(back = new GuiButton(0, this.width - 36, this.height - 26, 30, 20, I18n.format("gui.back", new Object[0])));
		this.buttonList.add(save = new GuiButton(1, this.width /2 - 40 - (72/2), this.height - 26, 72, 20, I18n.format("sbr.backup", new Object[0])));
		this.buttonList.add(restore = new GuiButton(2, this.width /2 + 40 - (72/2), this.height - 26, 72, 20, I18n.format("sbr.restore", new Object[0])));
	}
	
    
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
    	drawDefaultBackground();
        slot.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        this.drawCenteredString(this.fontRendererObj, I18n.format("sbr.title.mainmenu", new Object[0]), this.width / 2, 20, 16777215);

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
    
    protected void actionPerformed(GuiButton button) {
    	buttonPressed(button.id);
    }
    
    private void buttonPressed(int id){
    	try{
			List<SaveFormatComparator> saves = (List<SaveFormatComparator>)this.mc.getSaveLoader().getSaveList();
			switch(id){
			case 0:{
				mc.displayGuiScreen(world);
				writeJson(worldArray);
				break;
				}
			case 1:{
//		    	mc.displayGuiScreen(new GuiBackupScreen(selection, this));
				this.addBackup(saves.get(selection).getFileName(), "backup of " + saves.get(selection).getDisplayName());
				this.restore.enabled = true;
				break;
			}
			case 2:{
				mc.displayGuiScreen(new GuiRestoreSave(saves.get(selection).getFileName(), this));
				break;
			}
			}
    	}catch(Exception e){}
    }
    
    @Override
    protected void keyTyped(char charKey, int intKey) {
//    	switch(charKey){
//	    	case 'p': {
//				addBackup("New World", "Test file zipper");
//				writeJson(worldArray);
//				readJson();
//	    		break;
//	    	}
//	    	case 'o':{
//				String loc = ("/sbr/world backups/New World/210320150003473.zip");
//				Zipper.unZip(new File(Minecraft.getMinecraft().mcDataDir, loc));
//				addBackup("New World", "Test file zipper");
//				writeJson(worldArray);
//				readJson();
//	    		break;
//	    	}
//    	}
//    	System.out.println((int)charKey);
    	super.keyTyped(charKey, intKey);
    }
    
    
    /*
     * ================================ 
     *        JSON STUFF HERE
     * ================================
     */
    
    public static void getList(){
    	File file = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/SBR-Save.json");
    	temp:if(!file.exists()){
    		try {
    			System.out.println("SBR\nSBR File Not Found...\n    Creating new file!!");
				if(file.createNewFile()){
					break temp;
				}else{
					throw new Error("Unable to create file");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("SBR File Found...");
		}
		readJson();
    }
    
    public static WorldArray worldArray;
    
    public static void readJson(){
    	try{
	    	File file = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/SBR-Save.json");
	    	InputStream in = new FileInputStream(file);
	    	Reader reader = new InputStreamReader(in);
    		Gson gson = new GsonBuilder().create();
    		worldArray = gson.fromJson(reader, WorldArray.class);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void writeJson(WorldArray w){
    	File file = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/SBR-Save.json");
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	String json = gson.toJson(w);
    	try{
    		FileWriter writer = new FileWriter(file.getPath());
    		writer.write(json);
    		writer.close();
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    static void dup(File source, File target) {
		try {
	    	InputStream in = new FileInputStream(source);
	    	OutputStream out = new FileOutputStream(target);
	    	byte[] buf = new byte[1024];
	    	int len;
	    	while((len = in.read(buf)) > 0){
	    		out.write(buf, 0, len);
	    	}
	    	in.close();
	    	out.close();
		}catch(Exception e){}
    }
    
    public static void createDir(){
    	File file = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/SBR-Save.json");
    	File sbr = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/");
    	File jsonBackup = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/jsonBackup");
    	if(!sbr.exists())
    		sbr.mkdirs();
    	if(!jsonBackup.exists())
    		jsonBackup.mkdir();
    	if(!file.exists())
    		writeJson(new WorldArray());
    }

	public static void backupJson() {
    	File file = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/SBR-Save.json");
    	File file2 = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/jsonBackup/SBR-"+new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())+".json");
    	if(file.exists()){
        	dup(file, file2);
    	}
	}

	public static boolean checkDir() {
    	File file = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/SBR-Save.json");
    	File sbr = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/");
    	File jsonBackup = new File(Minecraft.getMinecraft().mcDataDir,"/sbr/jsonBackup");
    	if(!sbr.exists() || !jsonBackup.exists() || !file.exists())
    		return false;
    	return true;
	}
	
	public static void addBackup(String name, String desc){
		String nameFile = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
		World w = worldArray.getWorld(name);
		if(w == null){
			w = new World(name, "");
			w.addBackup(new Backup(desc, nameFile));
			worldArray.addWorld(w);
		}else{
			w.addBackup(new Backup(desc, nameFile));
		}
		
		String loc = ("/sbr/world backups/" + name + "/" + nameFile + ".zip");
		try {
			Zipper.zipFolder(new File(Minecraft.getMinecraft().mcDataDir, "/saves/" + w.getName()), new File(Minecraft.getMinecraft().mcDataDir, loc));
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeJson(worldArray);
		readJson();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class SBRGuiSlot extends GuiSlot
	{
	    private static final String __OBFID = "CL_00000712";
        String[] gamemode = new String[3];

	    String field_146637_u = "", anvil = "";
	    DateFormat dtf = new SimpleDateFormat();

		private List saves;
	    public SBRGuiSlot()
	    {
	        super(GuiSaveViewer.this.mc, GuiSaveViewer.this.width, GuiSaveViewer.this.height, 32, GuiSaveViewer.this.height - 32, 36);
	    	try {
				saves = GuiSaveViewer.this.mc.getSaveLoader().getSaveList();
			} catch (AnvilConverterException e) {
				e.printStackTrace();
			}
	        gamemode[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival", new Object[0]);
	        gamemode[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative", new Object[0]);
	        gamemode[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure", new Object[0]);
	    }

	    protected int getSize()
	    {
	    	try {
				saves = GuiSaveViewer.this.mc.getSaveLoader().getSaveList();
			} catch (AnvilConverterException e) {
				e.printStackTrace();
			}
	        return saves.size();
	    }
	    
	    protected void elementClicked(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
	    {
	    	GuiSaveViewer.this.selection = p_148144_1_;
	        boolean flag1 = GuiSaveViewer.this.selection >= 0 && GuiSaveViewer.this.selection < this.getSize();
	        boolean flag2 = flag1;
	        try{
		        flag2 = flag2 && GuiSaveViewer.this.worldArray.getWorld(((SaveFormatComparator)saves.get(p_148144_1_)).getFileName()) != null
		        		&& GuiSaveViewer.this.worldArray.getWorld(((SaveFormatComparator)saves.get(p_148144_1_)).getFileName()).getBackups() != null
		        		&& GuiSaveViewer.this.worldArray.getWorld(((SaveFormatComparator)saves.get(p_148144_1_)).getFileName()).getBackups().size() > 0;
	        }catch(Exception e){
	        	flag2 = false;
	        }
	    	GuiSaveViewer.this.restore.enabled = flag2;
	        GuiSaveViewer.this.save.enabled = flag1;
	    }
	    
	    protected boolean isSelected(int p_148131_1_)
	    {
	        return p_148131_1_ == GuiSaveViewer.this.selection;
	    }

	    protected int getContentHeight()
	    {
	        return saves.size() * 36;
	    }

	    protected void drawBackground()
	    {
	    	GuiSaveViewer.this.drawDefaultBackground();
	    }

	    protected void drawSlot(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
	    {
	        SaveFormatComparator saveformatcomparator = (SaveFormatComparator)saves.get(p_148126_1_);
	        String name = saveformatcomparator.getDisplayName();

	        if (name == null || MathHelper.stringNullOrLengthZero(name))
	        {
	            name = field_146637_u + " " + (p_148126_1_ + 1);
	        }

	        String time = saveformatcomparator.getFileName();
	        time = time + " (" + dtf.format(new Date(saveformatcomparator.getLastTimePlayed()));
	        time = time + ")";
	        String gameData = "";

	        if (saveformatcomparator.requiresConversion())
	        {
	            gameData = anvil + " " + gameData;
	        }
	        else
	        {
	            gameData = gamemode[saveformatcomparator.getEnumGameType().getID()];

	            if (saveformatcomparator.isHardcoreModeEnabled())
	            {
	                gameData = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET;
	            }

	            if (saveformatcomparator.getCheatsEnabled())
	            {
	                gameData = gameData + ", " + I18n.format("selectWorld.cheats", new Object[0]);
	            }
	        }

	        drawString(GuiSaveViewer.this.fontRendererObj, name, p_148126_2_ + 2, p_148126_3_ + 1, 16777215);
	        drawString(GuiSaveViewer.this.fontRendererObj, time, p_148126_2_ + 2, p_148126_3_ + 12, 8421504);
	        drawString(GuiSaveViewer.this.fontRendererObj, gameData, p_148126_2_ + 2, p_148126_3_ + 12 + 10, 8421504);
	        
	        String code = "";
	        
	        try{
	        if(worldArray.getWorld(saveformatcomparator.getFileName()) != null)
	        	code += "§a■";
	        else{
	        	code += "§c■";
	        }
	        if(worldArray.getWorld(saveformatcomparator.getFileName()).getBackups() != null)
	        	if(worldArray.getWorld(saveformatcomparator.getFileName()).getBackups().size() > 0)
	        		code += "§a" + worldArray.getWorld(saveformatcomparator.getFileName()).getBackups().size();
	        	else
	            	code += "§c0";
	        else
	        	code += "§c■";
	        }catch(Exception e){
	        	code = "§c■§c0";
	        }
			GL11.glPushMatrix();
	        GL11.glScaled(2, 2, 2);
	    	drawString(GuiSaveViewer.this.fontRendererObj, code, p_148126_2_ / 2 + 94, p_148126_3_ / 2 + 3, 8421504);
			GL11.glPopMatrix();
	    }
	}
}
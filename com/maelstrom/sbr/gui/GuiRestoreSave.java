package com.maelstrom.sbr.gui;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormatComparator;

import org.lwjgl.opengl.GL11;

import com.maelstrom.sbr.Zipper;
import com.maelstrom.sbr.json.Backup;
import com.maelstrom.sbr.json.WorldArray;

public class GuiRestoreSave extends GuiScreen {
	private int selection;
	private GuiSaveViewer guiSave;
	private WorldArray worldArray;
	private SBRGuiSlot slot;
	public GuiButton restore;
	private GuiButton back;
	private String worldname;
	public GuiRestoreSave(String selection, GuiSaveViewer guiSave) {
		this.selection = 0;
		worldname = selection;
		this.guiSave = guiSave;
		worldArray = guiSave.worldArray;
	}
	
	public void initGui(){
		slot = new SBRGuiSlot();
		this.buttonList.add(back = new GuiButton(0, this.width - 36, this.height - 26, 30, 20, I18n.format("gui.back", new Object[0])));
		this.buttonList.add(restore = new GuiButton(2, this.width /2 - (72/2), this.height - 26, 72, 20, I18n.format("sbr.restore", new Object[0])));
	}
	
    protected void actionPerformed(GuiButton button) {
    	buttonPressed(button.id);
    }
	
    private void buttonPressed(int id){
    	switch(id){
    	case 0:{
    		mc.displayGuiScreen(guiSave);
    		break;
    		}
    	case 2:{
			String loc = ("/sbr/world backups/" + worldname + "/" + worldArray.getWorld(GuiRestoreSave.this.worldname).getBackups().get(selection).getFileName() + ".zip");
			Zipper.unZip(new File(Minecraft.getMinecraft().mcDataDir, loc));
    		mc.displayGuiScreen(guiSave.world);
    	}
    	}
    }
    
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
    	drawDefaultBackground();
        slot.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        this.drawCenteredString(this.fontRendererObj, I18n.format("sbr.tilte.restore", new Object[0]), this.width / 2, 20, 16777215);

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
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
	        super(GuiRestoreSave.this.mc, GuiRestoreSave.this.width, GuiRestoreSave.this.height, 32, GuiRestoreSave.this.height - 32, 36);
	    	try {
				saves = GuiRestoreSave.this.mc.getSaveLoader().getSaveList();
			} catch (AnvilConverterException e) {
				e.printStackTrace();
			}
	        gamemode[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival", new Object[0]);
	        gamemode[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative", new Object[0]);
	        gamemode[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure", new Object[0]);
	    }

	    protected int getSize()
	    {
	        return GuiRestoreSave.this.worldArray.getWorld(GuiRestoreSave.this.worldname).getBackups().size();
	    }
	    
	    protected void elementClicked(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
	    {
	    	GuiRestoreSave.this.selection = p_148144_1_;
	        boolean flag1 = GuiRestoreSave.this.selection >= 0 && GuiRestoreSave.this.selection < this.getSize();
	    	GuiRestoreSave.this.restore.enabled = flag1;
	    }
	    
	    protected boolean isSelected(int p_148131_1_)
	    {
	        return p_148131_1_ == GuiRestoreSave.this.selection;
	    }

	    protected int getContentHeight()
	    {
	        return GuiRestoreSave.this.worldArray.getWorld(GuiRestoreSave.this.worldname).getBackups().size() * 36;
	    }

	    protected void drawBackground()
	    {
	    	GuiRestoreSave.this.drawDefaultBackground();
	    }

	    protected void drawSlot(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
	    {
	    	Backup world = GuiRestoreSave.this.worldArray.getWorld(GuiRestoreSave.this.worldname).getBackups().get(p_148126_1_);
	        String name = world.getFileName();

	        if (name == null || MathHelper.stringNullOrLengthZero(name))
	        {
	            name = field_146637_u + " " + (p_148126_1_ + 1);
	        }

	        String time = world.getDescription();
	        String gameData = "(" + world.getDate() + " " + world.getTime() + ")";

	        drawString(GuiRestoreSave.this.fontRendererObj, name, p_148126_2_ + 2, p_148126_3_ + 1, 16777215);
	        drawString(GuiRestoreSave.this.fontRendererObj, time, p_148126_2_ + 2, p_148126_3_ + 12, 8421504);
	        drawString(GuiRestoreSave.this.fontRendererObj, gameData, p_148126_2_ + 2, p_148126_3_ + 12 + 10, 8421504);
	    }
	}
}

package com.maelstrom.sbr.gui;

import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Color;

import com.maelstrom.sbr.Zipper;
import com.maelstrom.sbr.gui.GuiRestoreSave.SBRGuiSlot;
import com.maelstrom.sbr.json.World;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiRenameWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.SaveFormatComparator;

public class GuiBackupScreen extends GuiScreen {

	private SaveFormatComparator worldSave;
	private GuiScreen parent;
	private World worldSaveJSON;
	private GuiButton back;
	private GuiButton save;
	private GuiTextField metaName;
	private GuiTextField description;

	public GuiBackupScreen(SaveFormatComparator save, GuiSaveViewer guiSaveViewer) {
		worldSave = save;
		parent = guiSaveViewer;
		worldSaveJSON = guiSaveViewer.worldArray.getWorld(worldSave.getFileName());
	}

	
	public void initGui(){
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
		this.buttonList.add(back = new GuiButton(0, this.width - 36, this.height - 26, 30, 20, I18n.format("gui.back", new Object[0])));
		this.buttonList.add(save = new GuiButton(1, this.width /2 - (72/2), this.height - 26, 72, 20, I18n.format("sbr.save", new Object[0])));
		this.metaName = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
		this.description = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 90, 200, 20);
		save.enabled = false;
	}
	
    protected void actionPerformed(GuiButton button) {
    	buttonPressed(button.id);
    }
	
    private void buttonPressed(int id){
    	switch(id){
    	case 0:{
    		mc.displayGuiScreen(parent);
    		break;
    		}
    	case 1:{
    		String metaNameText = metaName.getText();
    		String descriptionText = description.getText();
    		if (descriptionText.trim().length() <= 0)
    			descriptionText = metaNameText;
    		GuiSaveViewer.addBackup(worldSaveJSON, metaNameText, descriptionText);
    		mc.displayGuiScreen(parent);
    		break;
    	}
    	}
    }
    
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.metaName.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.description.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }


    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }
    
    protected void keyTyped(char character, int keyCode)
    {
    	if(metaName.isFocused())
            this.metaName.textboxKeyTyped(character, keyCode);
    	if(description.isFocused())
            this.description.textboxKeyTyped(character, keyCode);
    	save.enabled = this.metaName.getText().length() > 0;

        if (keyCode == 28 || keyCode == 156){
            this.actionPerformed(save);
        }
    }
    
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
    	drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("sbr.tilte.backup", new Object[0]), this.width / 2, 20, 16777215);
        /*
         * TITLE
         * DESCRIPTION
         * DATE
         * OTHER
         * JUST JSON STUFF
         */
        metaName.drawTextBox();
        description.drawTextBox();
        if(!metaName.isFocused() && metaName.getText().length() == 0)
        	this.drawString(Minecraft.getMinecraft().fontRenderer, "Backup Name", this.width / 2 - 96, 66, Color.DKGREY.hashCode());
        if(!description.isFocused() && description.getText().length() == 0)
        	this.drawString(Minecraft.getMinecraft().fontRenderer, "Backup Description", this.width / 2 - 96, 96, Color.DKGREY.hashCode());
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
	
}

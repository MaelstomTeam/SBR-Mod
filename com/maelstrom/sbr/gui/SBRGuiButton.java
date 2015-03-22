package com.maelstrom.sbr.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class SBRGuiButton extends GuiButton {
    private ResourceLocation buttonTexture = new ResourceLocation("sbr","textures/gui/buttons.png");;
	public SBRGuiButton(int p_i1041_1_, int p_i1041_2_, int p_i1041_3_)
    {
        super(p_i1041_1_, p_i1041_2_, p_i1041_3_, 20, 20, "");
    }
	public void drawButton(Minecraft mc, int x, int y)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(buttonTexture );
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            int k = 106;
            if(!enabled)
            	k-= this.height;
            else if(flag)
                k += this.height;

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, k, this.width, this.height);
        }
    }
}

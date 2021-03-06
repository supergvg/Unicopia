package com.sollace.unicopia.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.sollace.unicopia.client.model.ModelCloud;
import com.sollace.unicopia.entity.EntityCloud;

public class RenderCloud extends RenderLiving {
    private static final ResourceLocation cloud = new ResourceLocation("unicopia", "textures/entity/clouds.png");
    private static final ResourceLocation rainCloud = new ResourceLocation("unicopia", "textures/entity/clouds_storm.png");
    
    public RenderCloud(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelCloud(), 1f);
    }
    
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
    	scaleCloud((EntityCloud)par1EntityLivingBase, par2);
    }
    
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        if (!p_77036_1_.isDead) {
	    	GL11.glEnable(GL11.GL_BLEND);
	    	if (!((EntityCloud)p_77036_1_).getOpaque()) {
	    		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
	    	}
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    	super.renderModel(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    
    protected void scaleCloud(EntityCloud entity, float par2) {
    	float scale = (float)entity.getCloudSize();
    	GL11.glScalef(scale, scale, scale);
    }
    
    protected ResourceLocation getEntityTexture(EntityCloud entity) {
    	if (entity.getIsRaining() && entity.getIsThundering()) {
    		return rainCloud;
    	}
        return cloud;
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return getEntityTexture((EntityCloud)par1Entity);
    }
    
    protected int getColorMultiplier(EntityLivingBase par1EntityLivingBase, float yaw, float pitch) {
        return 25;
    }
    
    protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase) {
        return 0.0F;
    }
}

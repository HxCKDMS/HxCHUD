package hxckdms.hxchud.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hxckdms.hxchud.Configuration;
import hxckdms.hxchud.libraries.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.HashMap;

import static hxckdms.hxchud.Configuration.*;

@SuppressWarnings({"unused"})
public class RenderHPEvent {
    private Minecraft mc = Minecraft.getMinecraft();

    private float healthLast = 0, healthLast2 = 0, mountHealthLast = 0, mountHealthLast2 = 0;
    private long lastSysTime = System.currentTimeMillis();
    private static final ResourceLocation uiHealthBar = new ResourceLocation(Configuration.customResourceLocation);
    private static final ResourceLocation uiZedsHealthBar = new ResourceLocation(Constants.MOD_ID, "textures/gui/ZedsHealthBar.png");
    static HashMap<String, ResourceLocation> WidgetImages = new HashMap<>();

    public static void init() {
        widgets.forEach((name, widget) -> {
            if (!widget.imagePath.isEmpty() && widget.imagePath.length() > 5)
                WidgetImages.put(name, new ResourceLocation(widget.imagePath));
        });
    }

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHealthBar(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ARMOR || event.type == RenderGameOverlayEvent.ElementType.FOOD || event.type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT)
            event.setCanceled(true);

        if (event.type == RenderGameOverlayEvent.ElementType.HEALTH) {
            ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int scaledWidth = scaledresolution.getScaledWidth();
            int scaledHeight = scaledresolution.getScaledHeight();
            int xBasePos = scaledWidth / 2 - 91;
            int yBasePos = scaledHeight - 40;
            int xSize = 88;
            int ySize = 10;
            int xSpacer = 6;
            mc.getTextureManager().bindTexture(Configuration.useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);

            IAttributeInstance attrMaxHealth = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
            float healthMax = (float) attrMaxHealth.getAttributeValue();
            float absorb = mc.thePlayer.getAbsorptionAmount();
            float currentValue = (float) health / healthMax;
            int foodVal = mc.thePlayer.getFoodStats().getFoodLevel();
            float saturationVal = mc.thePlayer.getFoodStats().getSaturationLevel();

            boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1 && mc.thePlayer.hurtResistantTime >= 10;

            if (healthLast2 == 0 || health >= healthMax)
                healthLast2 = healthMax;
            if (health > healthLast)
                healthLast2 = health;

            if (healthLast2 > health && System.currentTimeMillis() - lastSysTime >= Configuration.previousHealthDecayTimer) {
                healthLast2--;
                lastSysTime = System.currentTimeMillis();
            }

            if (healthLast2 > healthLast && healthLast > health && showOnlyLastHit)
                healthLast2 = healthLast;

            if (mc.thePlayer.isRidingHorse() && mc.thePlayer.ridingEntity instanceof EntityLiving) {
                EntityLiving mount = (EntityLiving) mc.thePlayer.ridingEntity;
                if (mountHealthLast2 == 0 || mount.getHealth() >= mount.getMaxHealth())
                    mountHealthLast2 = mount.getMaxHealth();
                if (mount.getHealth() > mountHealthLast)
                    mountHealthLast2 = mount.getHealth();

                if (mountHealthLast2 > mount.getHealth() && System.currentTimeMillis() - lastSysTime >= Configuration.previousHealthDecayTimer) {
                    healthLast2--;
                    lastSysTime = System.currentTimeMillis();
                }

                if (mountHealthLast2 > mountHealthLast && mountHealthLast > mount.getHealth() && Configuration.showOnlyLastHit)
                    mountHealthLast2 = mountHealthLast;
            }

            Configuration.hudWidget healthBar = widgets.get("HealthBar");
            Configuration.hudWidget foodBar = widgets.get("FoodBar");
            Configuration.hudWidget satBar = widgets.get("SaturationBar");
            Configuration.hudWidget armorBar = widgets.get("ArmorBar");
            Configuration.hudWidget horseHealthBar = widgets.get("HorseHealthBar");
            Configuration.hudWidget horseArmorBar = widgets.get("HorseArmorBar");
            Configuration.hudWidget healthBarBackground = widgets.get("HealthBarBackground");
            Configuration.hudWidget foodBarBackground = widgets.get("FoodBarBackground");
            Configuration.hudWidget satBarBackground = widgets.get("SaturationBarBackground");
            Configuration.hudWidget armorBarBackground = widgets.get("ArmorBarBackground");
            Configuration.hudWidget horseHealthBarBackground = widgets.get("HorseHealthBarBackground");
            Configuration.hudWidget horseArmorBarBackground = widgets.get("HorseArmorBarBackground");

            if (Configuration.useCustomPosition) {
                boolean verticle = healthBar.verticle;
                /*
                * 
                *  GET HEALTHBAR BACKGROUND RESOURCE IF EXISTS 
                * 
                * */
                if (WidgetImages.keySet().contains("HealthBarBackground")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("HealthBarBackground"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }
                
                /*
                * 
                *  DRAW BACKGROUND WITH HIGHLIGHT IF NECESSARY
                * 
                * */
                if (highlight && Configuration.highlightOnDamage) {
                    drawBar(healthBarBackground, 1, "Highlight");
                } else {
                    drawBar(healthBarBackground, 1);
                }


                /*
                * 
                *  GET HEALTHBAR RESOURCE IF EXISTS 
                * 
                * */

                if (WidgetImages.keySet().contains("HealthBar")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("HealthBar"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }

                /*
                * 
                *  PREVIOUS HEALTH 
                * 
                * */
                if (Configuration.showPreviousHealth) {
                    drawBar(healthBar, Math.round(healthLast2 / healthMax), "PastHealth");
                }

                /*
                * 
                *  WITHER POTION HEALTH 
                * 
                * */
                if (mc.thePlayer.isPotionActive(Potion.wither)) {
                    drawBar(healthBar, currentValue, "WitherHealth");

                /*
                * 
                *  POISON HEALTH 
                * 
                * */
                } else if (mc.thePlayer.isPotionActive(Potion.poison)) {
                    drawBar(healthBar, currentValue, "PoisonHealth");

                /*
                * 
                *  REGENERATION HEALTH 
                * 
                * */
                } else if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
                    drawBar(healthBar, currentValue, "RegenHealth");

                /*
                * 
                *  NORMAL HEALTH 
                * 
                * */
                } else {
                    if (health / healthMax > .75f) {
                        drawBar(healthBar, currentValue);
                    } else if (health / healthMax > .25f) {
                        drawBar(healthBar, currentValue, "MidHealth");
                    } else {
                        drawBar(healthBar, currentValue, "LowHealth");
                    }
                }

                healthLast = health;

                /*
                *
                *  GET FOODBAR RESOURCE IF EXISTS
                *
                * */
                if (WidgetImages.keySet().contains("FoodBarBackground")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("FoodBarBackground"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }


                /*
                *
                *  DRAW FOODBAR BACKGROUND
                *
                * */
                drawBar(foodBarBackground, 1);

                /*
                *
                *  GET FOODBAR RESOURCE IF EXISTS
                *
                * */
                if (WidgetImages.keySet().contains("FoodBar")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("FoodBar"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }


                /*
                *
                *  DRAW FOODBAR
                *
                * */
                drawBar(foodBar, foodVal / 20f);

                if (satBar.alwaysShow) {
                    /*
                    *
                    *  GET SATURATIONBAR BACKGROUND RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("SaturationBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("SaturationBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                    }


                    /*
                    *
                    *  DRAW SATURATION BAR BACKGROUND
                    *
                    * */
                    drawBar(satBarBackground, 1);


                    /*
                    *
                    *  GET SATURATIONBAR RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("SaturationBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("SaturationBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                    }

                    drawBar(satBar, saturationVal / 20f);
                }


                /*
                *
                *  SHOW ARMOR BAR
                *
                * */
                if (mc.thePlayer.getTotalArmorValue() > 0 || armorBar.alwaysShow) {
                    /*
                    *
                    *  GET ARMOR BAR BACKGROUND RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("ArmorBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("ArmorBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW ARMOR BAR
                    *
                    * */
                    drawBar(armorBarBackground, 1);

                    /*
                    *
                    *  GET ARMOR BAR RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("ArmorBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("ArmorBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW ARMOR BAR
                    *
                    * */
                    drawBar(armorBar, 1);
                }

                /*
                *
                *  IF MOUNTED
                *
                * */
                if (mc.thePlayer.isRidingHorse() && mc.thePlayer.ridingEntity instanceof EntityLiving) {
                    /*
                    *
                    *  GET MOUNT HEALTH BACKGROUND RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("HorseHealthBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("HorseHealthBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW MOUNT HEALTH BACKGROUND
                    *
                    * */
                    drawBar(horseHealthBarBackground, 1);

                    /*
                    *
                    *  GET MOUNT HEALTH RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("HorseHealthBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("HorseHealthBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW MOUNT HEALTH
                    *
                    * */
                    drawBar(horseHealthBar, mountHealthLast2 / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth(), "PastHealth");
                    if (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() > .75f) {
                        drawBar(horseHealthBar, ((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth());
                    } else if (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() > .25f) {
                        drawBar(horseHealthBar, ((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth(), "MidHealth");
                    } else {
                        drawBar(horseHealthBar, ((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth(), "LowHealth");
                    }
                    mountHealthLast = ((EntityLiving)mc.thePlayer.ridingEntity).getHealth();
                }

                if (mc.thePlayer.isRidingHorse() && mc.thePlayer.ridingEntity instanceof EntityLiving && ((EntityLivingBase) mc.thePlayer.ridingEntity).getTotalArmorValue() > 0) {
                    /*
                    *
                    *  GET MOUNT ARMOR BACKGROUND RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("HorseArmorBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("HorseArmorBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW MOUNT ARMOR BACKGROUND
                    *
                    * */
                    drawBar(horseArmorBarBackground, 1);

                    /*
                    *
                    *  GET MOUNT ARMOR RESOURCE IF EXISTS
                    *
                    * */
                    if (WidgetImages.keySet().contains("HorseArmorBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("HorseArmorBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW MOUNT ARMOR
                    *
                    * */
                    drawBar(horseArmorBar, ((EntityLiving) mc.thePlayer.ridingEntity).getTotalArmorValue() / maxArmor);
                }

                /*
                *
                *  DRAW STRINGS IF ENABLED
                *
                * */
                if (Configuration.showValues) {
//                    GL11.glScalef(healthBar.fontSize, healthBar.fontSize, healthBar.fontSize);
                    if (healthBar.showValue)
                        mc.fontRenderer.drawString((health + absorb) + "/" + healthMax, healthBar.textX + (healthBar.centered ? (mc.fontRenderer.getStringWidth(health + "/" + healthMax) / 2) : 0), healthBar.textY, Integer.parseInt(healthBar.color.replace("0x", ""), 16), false);
//                    GL11.glScalef(foodBar.fontSize, foodBar.fontSize, foodBar.fontSize);
                    if (foodBar.showValue)
                        mc.fontRenderer.drawString(foodVal + "/" + 20, foodBar.textX - (foodBar.centered ? (mc.fontRenderer.getStringWidth(foodVal + "/" + 20) / 2) : 0), foodBar.textY, Integer.parseInt(foodBar.color.replace("0x", ""), 16), false);
//                    GL11.glScalef(armorBar.fontSize, armorBar.fontSize, armorBar.fontSize);
                    if (armorBar.showValue && (mc.thePlayer.getTotalArmorValue() > 0 || armorBar.alwaysShow))
                        mc.fontRenderer.drawString(mc.thePlayer.getTotalArmorValue() + "/" + Configuration.maxArmor, armorBar.textX + (armorBar.centered ? (mc.fontRenderer.getStringWidth(mc.thePlayer.getTotalArmorValue() + "/" + Configuration.maxArmor) / 2) : 0), armorBar.textY, Integer.parseInt(armorBar.color.replace("0x", ""), 16), false);
//                    GL11.glScalef(horseHealthBar.fontSize, horseHealthBar.fontSize, horseHealthBar.fontSize);
                    if (mc.thePlayer.isRidingHorse() && horseHealthBar.showValue)
                        mc.fontRenderer.drawString(((EntityLivingBase) mc.thePlayer.ridingEntity).getHealth() + "/" + ((EntityLivingBase) mc.thePlayer.ridingEntity).getMaxHealth(), horseHealthBar.textX + (horseHealthBar.centered ? (mc.fontRenderer.getStringWidth(((EntityLivingBase) mc.thePlayer.ridingEntity).getHealth() + "/" + ((EntityLivingBase) mc.thePlayer.ridingEntity).getMaxHealth()) / 2) : 0), horseHealthBar.textY, Integer.parseInt(horseHealthBar.color.replace("0x", ""), 16), false);
//                    GL11.glScalef(horseArmorBar.fontSize, horseArmorBar.fontSize, horseArmorBar.fontSize);
                    if (mc.thePlayer.isRidingHorse() && horseArmorBar.showValue)
                        mc.fontRenderer.drawString(((EntityLivingBase) mc.thePlayer.ridingEntity).getTotalArmorValue() + "/" + Configuration.maxArmor, horseArmorBar.textX + (horseArmorBar.centered ? (mc.fontRenderer.getStringWidth(((EntityLivingBase) mc.thePlayer.ridingEntity).getTotalArmorValue() + "/" + Configuration.maxArmor) / 2) : 0), horseArmorBar.textY, Integer.parseInt(horseArmorBar.color.replace("0x", ""), 16), false);
                }
//                float scale = (float) mc.displayWidth / (float) scaledWidth;
//                GL11.glScalef(scale, scale, scale);
            } else {
                if (highlight && Configuration.highlightOnDamage) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 11, xSize, ySize);
                } else {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 0, xSize, ySize);
                }

                if (Configuration.showPreviousHealth)
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 55, Math.min(Math.round((healthLast2 / healthMax) * xSize), xSize), ySize);

                if (mc.thePlayer.isPotionActive(Potion.wither)) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 88, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else if (mc.thePlayer.isPotionActive(Potion.poison)) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 77, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 66, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else {
                    if (health / healthMax > .75f) {
                        drawTexturedModalRect(xBasePos, yBasePos, 0, 22, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                    } else if (health / healthMax > .25f) {
                        drawTexturedModalRect(xBasePos, yBasePos, 0, 33, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                    } else {
                        drawTexturedModalRect(xBasePos, yBasePos, 0, 44, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                    }
                }

                healthLast = health;

                drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos, 0, 0, xSize, ySize); //Food Bar

                drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos, 0, 99, Math.min((Math.round(foodVal / 20f * xSize)), xSize), ySize);
                if (satBar.alwaysShow)
                    drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos, 0, 110, Math.min((Math.round(saturationVal / 20f * xSize)), xSize), ySize);

                if (mc.thePlayer.getTotalArmorValue() > 0 || armorBar.alwaysShow) {
                    drawTexturedModalRect(xBasePos, yBasePos - 12, 0, 0, xSize, ySize);
                    drawTexturedModalRect(xBasePos, yBasePos - 12, 0, 121, Math.min((Math.round((float) mc.thePlayer.getTotalArmorValue() / (float) Configuration.maxArmor * xSize)), xSize), ySize);
                }

                if (mc.thePlayer.isRidingHorse() && mc.thePlayer.ridingEntity instanceof EntityLiving) {
                    drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 0, xSize, ySize);
                    if (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() > .75f) {
                        drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 22, Math.min((int) (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() * xSize), xSize), ySize);
                    } else if (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() > .25f) {
                        drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 33, Math.min((int) (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() * xSize), xSize), ySize);
                    } else {
                        drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 44, Math.min((int) (((EntityLiving) mc.thePlayer.ridingEntity).getHealth() / ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth() * xSize), xSize), ySize);
                    }
                    mountHealthLast = ((EntityLiving)mc.thePlayer.ridingEntity).getHealth();
                }

                if (mc.thePlayer.isRidingHorse() && mc.thePlayer.ridingEntity instanceof EntityLiving && ((EntityLivingBase) mc.thePlayer.ridingEntity).getTotalArmorValue() > 0) {
                    drawTexturedModalRect(xBasePos + xSize + 6, yBasePos - 24, horseArmorBarBackground.texturePosX, horseArmorBarBackground.texturePosY, xSize, ySize);
                    drawTexturedModalRect(xBasePos + xSize + 6, yBasePos - 24, horseArmorBar.texturePosX, horseArmorBar.texturePosY, Math.min((Math.round((float) ((EntityLivingBase) mc.thePlayer.ridingEntity).getTotalArmorValue() / (float) Configuration.maxArmor * xSize)), xSize), ySize);
                }

                if (Configuration.showValues) {
                    if (healthBar.showValue)
                        mc.fontRenderer.drawString((health + absorb) + "/" + healthMax, xBasePos + 40 - (mc.fontRenderer.getStringWidth(health + "/" + healthMax) / 2), yBasePos + 1, Integer.parseInt(healthBar.color.replace("0x", ""), 16), false);
                    if (foodBar.showValue)
                        mc.fontRenderer.drawString(foodVal + "/" + 20, xBasePos + xSize + 50 - (mc.fontRenderer.getStringWidth(foodVal + "/" + 20) / 2), yBasePos + 1, Integer.parseInt(foodBar.color.replace("0x", ""), 16), false);
                    if (armorBar.showValue && (mc.thePlayer.getTotalArmorValue() > 0 || armorBar.alwaysShow))
                        mc.fontRenderer.drawString(mc.thePlayer.getTotalArmorValue() + "/" + Configuration.maxArmor, xBasePos + 40 - (mc.fontRenderer.getStringWidth(mc.thePlayer.getTotalArmorValue() + "/" + Configuration.maxArmor) / 2), yBasePos - 11, Integer.parseInt(armorBar.color.replace("0x", ""), 16), false);
                    if (mc.thePlayer.isRidingHorse() && horseHealthBar.showValue)
                        mc.fontRenderer.drawString(((EntityLiving) mc.thePlayer.ridingEntity).getHealth() + "/" + ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth(), xBasePos + xSize + 50 - (mc.fontRenderer.getStringWidth(((EntityLiving) mc.thePlayer.ridingEntity).getHealth() + "/" + ((EntityLiving) mc.thePlayer.ridingEntity).getMaxHealth()) / 2), yBasePos - 11, Integer.parseInt(horseHealthBar.color.replace("0x", ""), 16), false);
                    if (mc.thePlayer.isRidingHorse() && horseArmorBar.showValue)
                        mc.fontRenderer.drawString(((EntityLiving) mc.thePlayer.ridingEntity).getTotalArmorValue() + "/" + Configuration.maxArmor, xBasePos + xSize + 50 - (mc.fontRenderer.getStringWidth(((EntityLivingBase) mc.thePlayer.ridingEntity).getTotalArmorValue() + "/" + Configuration.maxArmor) / 2), yBasePos - 11, Integer.parseInt(horseArmorBar.color.replace("0x", ""), 16), false);
                }
            }

            event.setCanceled(true);
        }
    }
    private void drawBar(Configuration.hudWidget bar, float currentPercent) {
        drawTexturedModalRect(bar.uiPosX,
                bar.verticle ? ((bar.uiPosY + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) : bar.uiPosY, bar.texturePosX,
                bar.verticle ? ((bar.texturePosY + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) :  bar.texturePosY,
                bar.verticle ? bar.textureSizeX : Math.min(Math.round(currentPercent * bar.textureSizeX), bar.textureSizeX),
                bar.verticle ? Math.min(Math.round(currentPercent * bar.textureSizeY), bar.textureSizeY): bar.textureSizeY);
    }

    private void drawBar(Configuration.hudWidget bar, float currentPercent, String subElement) {
        drawTexturedModalRect(bar.uiPosX,
                bar.verticle ? ((bar.uiPosY + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) : bar.uiPosY, bar.subElements.get(subElement).get(0),
                bar.verticle ? ((bar.subElements.get(subElement).get(1) + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) :  bar.subElements.get(subElement).get(1),
                bar.verticle ? bar.textureSizeX : Math.min(Math.round(currentPercent * bar.textureSizeX), bar.textureSizeX),
                bar.verticle ? Math.min(Math.round(currentPercent * bar.textureSizeY), bar.textureSizeY): bar.textureSizeY);
    }

    private void drawTexturedModalRect(int x, int y, int u, int v, int xSize, int ySize) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + ySize, 0.0, (u + 0) * f, (v + ySize) * f1);
        tessellator.addVertexWithUV(x + xSize, y + ySize, 0.0, (u + xSize) * f, (v + ySize) * f1);
        tessellator.addVertexWithUV(x + xSize, y + 0, 0.0, (u + xSize) * f, (v + 0) * f1);
        tessellator.addVertexWithUV(x + 0, y + 0, 0.0, (u + 0) * f, (v + 0) * f1);
        tessellator.draw();
    }

}

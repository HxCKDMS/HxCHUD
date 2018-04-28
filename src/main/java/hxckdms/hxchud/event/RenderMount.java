package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static hxckdms.hxchud.Configuration.*;

public class RenderMount {
    private static float mountHealthLast = 0, mountHealthLast2 = 0;


    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderMountBars(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT) {
            ScaledResolution scaledresolution = new ScaledResolution(Helper.mc);

            Configuration.hudWidget horseHealthBar = widgets.get("HorseHealthBar");
            Configuration.hudWidget horseArmorBar = widgets.get("HorseArmorBar");
            Configuration.hudWidget horseHealthBarBackground = widgets.get("HorseHealthBarBackground");
            Configuration.hudWidget horseArmorBarBackground = widgets.get("HorseArmorBarBackground");

            if (Configuration.useCustomPosition && !customHotbarPosOnly) {
                if (Helper.mc.player.isRidingHorse() && Helper.mc.player.getRidingEntity() instanceof EntityLiving) {
                    EntityLiving mount = (EntityLiving) Helper.mc.player.getRidingEntity();
                    if (mountHealthLast2 == 0 || mount.getHealth() >= mount.getMaxHealth())
                        mountHealthLast2 = mount.getMaxHealth();
                    if (mount.getHealth() > mountHealthLast)
                        mountHealthLast2 = mount.getHealth();

                    if (mountHealthLast2 > mount.getHealth() && System.currentTimeMillis() - Helper.lastSysTime >= Configuration.previousHealthDecayTimer) {
                        mountHealthLast2--;
                        Helper.lastSysTime = System.currentTimeMillis();
                    }

                    if (mountHealthLast2 > mountHealthLast && mountHealthLast > mount.getHealth() && Configuration.showOnlyLastHit)
                        mountHealthLast2 = mountHealthLast;
                }

                /* IF MOUNTED */
                if (Helper.mc.player.isRidingHorse() && Helper.mc.player.getRidingEntity() instanceof EntityLiving) {
                    /* GET MOUNT HEALTH BACKGROUND RESOURCE IF EXISTS */
                    if (Helper.WidgetImages.keySet().contains("HorseHealthBarBackground")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("HorseHealthBarBackground"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    /* DRAW MOUNT HEALTH BACKGROUND */
                    if (Helper.previousValues.get("HorseHealthBar") != ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() + ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth()) {
                        Helper.drawBar(horseHealthBarBackground, 1, true);
                    } else {
                        Helper.drawBar(horseHealthBarBackground, 1, false);
                    }

                    /* GET MOUNT HEALTH RESOURCE IF EXISTS */
                    if (Helper.WidgetImages.keySet().contains("HorseHealthBar")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("HorseHealthBar"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    /* DRAW MOUNT HEALTH */
                    if (Helper.previousValues.get("HorseHealthBar") != ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() + ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth()) {
                        Helper.drawBar(horseHealthBar, mountHealthLast2 / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), true, "PastHealth");
                        if (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() > .75f) {
                            Helper.drawBar(horseHealthBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), true);
                        } else if (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() > .25f) {
                            Helper.drawBar(horseHealthBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), true, "MidHealth");
                        } else {
                            Helper.drawBar(horseHealthBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), true, "LowHealth");
                        }
                    } else {
                        Helper.drawBar(horseHealthBar, mountHealthLast2 / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), false, "PastHealth");
                        if (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() > .75f) {
                            Helper.drawBar(horseHealthBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), false);
                        } else if (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() > .25f) {
                            Helper.drawBar(horseHealthBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), false, "MidHealth");
                        } else {
                            Helper.drawBar(horseHealthBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth(), false, "LowHealth");
                        }
                    }
                    mountHealthLast = ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth();
                    Helper.previousValues.put(horseHealthBar.elementName, ((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() + ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth());
                }

                if (Helper.mc.player.isRidingHorse() && Helper.mc.player.getRidingEntity() instanceof EntityLiving && ((EntityLivingBase) Helper.mc.player.getRidingEntity()).getTotalArmorValue() > 0) {
                    /*
                    *
                    *  GET MOUNT ARMOR BACKGROUND RESOURCE IF EXISTS
                    *
                    * */
                    if (Helper.WidgetImages.keySet().contains("HorseArmorBarBackground")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("HorseArmorBarBackground"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW MOUNT ARMOR BACKGROUND
                    *
                    * */
                    if (Helper.previousValues.get("HorseArmorBar") != ((EntityLiving) Helper.mc.player.getRidingEntity()).getTotalArmorValue() + maxArmor) {
                        Helper.drawBar(horseArmorBarBackground, 1, true);
                    } else {
                        Helper.drawBar(horseArmorBarBackground, 1, false);
                    }

                    /*
                    *
                    *  GET MOUNT ARMOR RESOURCE IF EXISTS
                    *
                    * */
                    if (Helper.WidgetImages.keySet().contains("HorseArmorBar")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("HorseArmorBar"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    /*
                    *
                    *  DRAW MOUNT ARMOR
                    *
                    * */
                    if (Helper.previousValues.get("HorseArmorBar") != ((EntityLiving) Helper.mc.player.getRidingEntity()).getTotalArmorValue() + maxArmor) {
                        Helper.drawBar(horseArmorBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getTotalArmorValue() / maxArmor, true);
                    } else {
                        Helper.drawBar(horseArmorBar, ((EntityLiving) Helper.mc.player.getRidingEntity()).getTotalArmorValue() / maxArmor, false);
                    }
                    Helper.previousValues.put(horseArmorBar.elementName, ((EntityLiving) Helper.mc.player.getRidingEntity()).getTotalArmorValue() + (float) maxArmor);
                }
            } else {
                int scaledWidth = scaledresolution.getScaledWidth();
                int scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 40;
                int xSize = 88;
                int ySize = 10;
                int xSpacer = 6;


                if (Helper.mc.player.isRidingHorse() && Helper.mc.player.getRidingEntity() instanceof EntityLiving) {
                    Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 0, xSize, ySize);
                    if (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() > .75f) {
                        Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 22, Math.min((int) (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() * xSize), xSize), ySize);
                    } else if (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() > .25f) {
                        Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 33, Math.min((int) (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() * xSize), xSize), ySize);
                    } else {
                        Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 44, Math.min((int) (((EntityLiving) Helper.mc.player.getRidingEntity()).getHealth() / ((EntityLiving) Helper.mc.player.getRidingEntity()).getMaxHealth() * xSize), xSize), ySize);
                    }
                    mountHealthLast = ((EntityLiving)Helper.mc.player.getRidingEntity()).getHealth();
                }

                if (Helper.mc.player.isRidingHorse() && Helper.mc.player.getRidingEntity() instanceof EntityLiving && ((EntityLivingBase) Helper.mc.player.getRidingEntity()).getTotalArmorValue() > 0) {
                    Helper.drawTexturedModalRect(xBasePos + xSize + 6, yBasePos - 24, horseArmorBarBackground.texturePosX, horseArmorBarBackground.texturePosY, xSize, ySize);
                    Helper.drawTexturedModalRect(xBasePos + xSize + 6, yBasePos - 24, horseArmorBar.texturePosX, horseArmorBar.texturePosY, Math.min((Math.round((float) ((EntityLivingBase) Helper.mc.player.getRidingEntity()).getTotalArmorValue() / (float) Configuration.maxArmor * xSize)), xSize), ySize);
                }

            }


            if (Configuration.showValues) {
                GL11.glPushMatrix();
                GL11.glScalef(horseHealthBar.fontSize, horseHealthBar.fontSize, horseHealthBar.fontSize);
                if (Helper.mc.player.isRidingHorse() && horseHealthBar.showValue)
                    Helper.mc.fontRenderer.drawString(((EntityLivingBase) Helper.mc.player.getRidingEntity()).getHealth() + "/" + ((EntityLivingBase) Helper.mc.player.getRidingEntity()).getMaxHealth(), horseHealthBar.textX + (horseHealthBar.centered ? (Helper.mc.fontRenderer.getStringWidth(((EntityLivingBase) Helper.mc.player.getRidingEntity()).getHealth() + "/" + ((EntityLivingBase) Helper.mc.player.getRidingEntity()).getMaxHealth()) / 2) : 0), horseHealthBar.textY, Integer.parseInt(horseHealthBar.color.replace("0x", ""), 16), false);
                GL11.glScalef(horseArmorBar.fontSize, horseArmorBar.fontSize, horseArmorBar.fontSize);
                if (Helper.mc.player.isRidingHorse() && horseArmorBar.showValue)
                    Helper.mc.fontRenderer.drawString(((EntityLivingBase) Helper.mc.player.getRidingEntity()).getTotalArmorValue() + "/" + Configuration.maxArmor, horseArmorBar.textX + (horseArmorBar.centered ? (Helper.mc.fontRenderer.getStringWidth(((EntityLivingBase) Helper.mc.player.getRidingEntity()).getTotalArmorValue() + "/" + Configuration.maxArmor) / 2) : 0), horseArmorBar.textY, Integer.parseInt(horseArmorBar.color.replace("0x", ""), 16), false);
                GL11.glPopMatrix();
            }
        }
    }
}

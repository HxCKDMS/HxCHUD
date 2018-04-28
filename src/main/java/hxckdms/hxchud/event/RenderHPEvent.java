package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static hxckdms.hxchud.Configuration.*;

@SuppressWarnings({"unused"})
public class RenderHPEvent {
    private float healthLast = 0, healthLast2 = 0;

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHealthBar(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            ScaledResolution scaledresolution = new ScaledResolution(Helper.mc);
            Helper.xRes = scaledresolution.getScaledWidth();
            Helper.yRes = scaledresolution.getScaledHeight();

            Helper.mc.getTextureManager().bindTexture(Configuration.useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);

            IAttributeInstance attrMaxHealth = Helper.mc.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            int health = MathHelper.ceil(Helper.mc.player.getHealth());
            float healthMax = (float) attrMaxHealth.getAttributeValue();
            float absorb = Helper.mc.player.getAbsorptionAmount();
            float currentValue = (float) health / healthMax;

            boolean highlight = Helper.mc.player.hurtResistantTime / 3 % 2 == 1 && Helper.mc.player.hurtResistantTime >= 10;

            if (healthLast2 == 0 || health >= healthMax)
                healthLast2 = healthMax;
            if (health > healthLast)
                healthLast2 = health;

            if (healthLast2 > health && System.currentTimeMillis() - Helper.lastSysTime >= Configuration.previousHealthDecayTimer) {
                healthLast2--;
                Helper.lastSysTime = System.currentTimeMillis();
            }

            if (healthLast2 > healthLast && healthLast > health && showOnlyLastHit)
                healthLast2 = healthLast;


            Configuration.hudWidget healthBar = widgets.get("HealthBar");
            Configuration.hudWidget healthBarBackground = widgets.get("HealthBarBackground");

            if (Configuration.useCustomPosition && !customHotbarPosOnly) {
                boolean verticle = healthBar.verticle;
                /* GET HEALTHBAR BACKGROUND RESOURCE IF EXISTS */
                if (Helper.WidgetImages.keySet().contains("HealthBarBackground")) {
                    Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("HealthBarBackground"));
                } else {
                    Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                }
                
                /* DRAW BACKGROUND WITH HIGHLIGHT IF NECESSARY */
                if (Helper.previousValues.get("HealthBar") != health + healthMax) {
                    if (highlight && Configuration.highlightOnDamage) {
                        Helper.drawBar(healthBarBackground, 1, true, "Highlight");
                    } else {
                        Helper.drawBar(healthBarBackground, 1, true);
                    }
                } else {
                    if (highlight && Configuration.highlightOnDamage) {
                        Helper.drawBar(healthBarBackground, 1, false, "Highlight");
                    } else {
                        Helper.drawBar(healthBarBackground, 1, false);
                    }
                }


                /* GET HEALTHBAR RESOURCE IF EXISTS */
                if (Helper.WidgetImages.keySet().contains("HealthBar")) {
                    Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("HealthBar"));
                } else {
                    Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                }

                /* PREVIOUS HEALTH */
                if (Helper.previousValues.get("HealthBar") != health + healthMax) {
                    if (Configuration.showPreviousHealth) {
                        Helper.drawBar(healthBar, Math.round(healthLast2 / healthMax), true, "PastHealth");
                    }
                } else {
                    if (Configuration.showPreviousHealth) {
                        Helper.drawBar(healthBar, Math.round(healthLast2 / healthMax), false, "PastHealth");
                    }
                }

                if (Helper.previousValues.get("HealthBar") != health + healthMax) {
                    if (Helper.mc.player.isPotionActive(Potion.getPotionById(20))) { /* WITHER POTION HEALTH */
                        Helper.drawBar(healthBar, currentValue, true, "WitherHealth");
                    } else if (Helper.mc.player.isPotionActive(Potion.getPotionById(19))) { /* POISON HEALTH */
                        Helper.drawBar(healthBar, currentValue, true, "PoisonHealth");
                    } else if (Helper.mc.player.isPotionActive(Potion.getPotionById(10))) { /* REGENERATION HEALTH  */
                        Helper.drawBar(healthBar, currentValue, true, "RegenHealth");

                    /* NORMAL HEALTH */
                    } else {
                        if (health / healthMax > .75f) {
                            Helper.drawBar(healthBar, currentValue, true);
                        } else if (health / healthMax > .25f) {
                            Helper.drawBar(healthBar, currentValue, true, "MidHealth");
                        } else {
                            Helper.drawBar(healthBar, currentValue, true, "LowHealth");
                        }
                    }
                } else {
                    if (Helper.mc.player.isPotionActive(Potion.getPotionById(20))) { /* WITHER POTION HEALTH */
                        Helper.drawBar(healthBar, currentValue, false, "WitherHealth");
                    } else if (Helper.mc.player.isPotionActive(Potion.getPotionById(19))) { /* POISON HEALTH */
                        Helper.drawBar(healthBar, currentValue, false, "PoisonHealth");
                    } else if (Helper.mc.player.isPotionActive(Potion.getPotionById(10))) { /* REGENERATION HEALTH  */
                        Helper.drawBar(healthBar, currentValue, false, "RegenHealth");

                    /* NORMAL HEALTH */
                    } else {
                        if (health / healthMax > .75f) {
                            Helper.drawBar(healthBar, currentValue, false);
                        } else if (health / healthMax > .25f) {
                            Helper.drawBar(healthBar, currentValue, false, "MidHealth");
                        } else {
                            Helper.drawBar(healthBar, currentValue, false, "LowHealth");
                        }
                    }
                }

                healthLast = health;
                Helper.previousValues.replace(healthBar.elementName, health + healthMax);

                /*
                *
                *  DRAW STRINGS IF ENABLED
                *
                * */
                if (Configuration.showValues) {
                    GL11.glPushMatrix();
                    GL11.glScalef(healthBar.fontSize, healthBar.fontSize, healthBar.fontSize);
                    if (healthBar.showValue)
                        Helper.mc.fontRenderer.drawString((health + absorb) + "/" + healthMax, healthBar.textX + (healthBar.centered ? (Helper.mc.fontRenderer.getStringWidth(health + "/" + healthMax) / 2) : 0), healthBar.textY, Integer.parseInt(healthBar.color.replace("0x", ""), 16), false);
                    GL11.glPopMatrix();
                }
            } else {
                int scaledWidth = scaledresolution.getScaledWidth();
                int scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 40;
                int xSize = 88;
                int ySize = 10;
                int xSpacer = 6;


                if (highlight && Configuration.highlightOnDamage) {
                    Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 11, xSize, ySize);
                } else {
                    Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 0, xSize, ySize);
                }

                if (Configuration.showPreviousHealth)
                    Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 55, Math.min(Math.round((healthLast2 / healthMax) * xSize), xSize), ySize);

                if (Helper.mc.player.isPotionActive(Potion.getPotionById(20))) {
                    Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 88, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else if (Helper.mc.player.isPotionActive(Potion.getPotionById(19))) {
                    Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 77, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else if (Helper.mc.player.isPotionActive(Potion.getPotionById(10))) {
                    Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 66, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else {
                    if (health / healthMax > .75f) {
                        Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 22, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                    } else if (health / healthMax > .25f) {
                        Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 33, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                    } else {
                        Helper.drawTexturedModalRect(xBasePos, yBasePos, 0, 44, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                    }
                }

                healthLast = health;

                if (Configuration.showValues) {
                    if (healthBar.showValue)
                        Helper.mc.fontRenderer.drawString((health + absorb) + "/" + healthMax, xBasePos + 40 - (Helper.mc.fontRenderer.getStringWidth(health + "/" + healthMax) / 2), yBasePos + 1, Integer.parseInt(healthBar.color.replace("0x", ""), 16), false);
                }
            }

            event.setCanceled(true);
        }
    }

}

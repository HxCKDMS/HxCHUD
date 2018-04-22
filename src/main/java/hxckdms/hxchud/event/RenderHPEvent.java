package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import hxckdms.hxchud.libraries.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

import static hxckdms.hxchud.Configuration.*;

@SuppressWarnings({"unused"})
public class RenderHPEvent {
    private Minecraft mc = Minecraft.getMinecraft();

    private float healthLast = 0, healthLast2 = 0, mountHealthLast = 0, mountHealthLast2 = 0;
    private long lastSysTime = System.currentTimeMillis();
    private static final ResourceLocation uiHealthBar = new ResourceLocation(Configuration.customResourceLocation);
    private static final ResourceLocation uiZedsHealthBar = new ResourceLocation(Constants.MOD_ID, "textures/gui/zedshealthbar.png");
    static HashMap<String, ResourceLocation> WidgetImages = new HashMap<>();
    static HashMap<String, Integer> hideTimers = new HashMap<>();
    private static HashMap<String, Integer> hiddenXs = new HashMap<>(), hiddenYs = new HashMap<>();
    private static HashMap<String, Float> previousValues = new HashMap<>();
    private static int xRes = 480, yRes = 265;

    private static HashMap<String, Integer> timers = new HashMap<>();
    public static void init() {
        widgets.forEach((name, widget) -> {
            if (!widget.imagePath.isEmpty() && widget.imagePath.length() > 5)
                WidgetImages.put(name, new ResourceLocation(widget.imagePath));
            hideTimers.put(name, 0);
            hiddenXs.put(name, 0);
            hiddenYs.put(name, 0);
            previousValues.put(name, 0f);
            timers.put(name, 0);
        });
    }

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHealthBar(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT)
            event.setCanceled(true);

        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int scaledWidth = scaledresolution.getScaledWidth();
            int scaledHeight = scaledresolution.getScaledHeight();
            xRes = scaledresolution.getScaledWidth();
            yRes = scaledresolution.getScaledHeight();
            int xBasePos = scaledWidth / 2 - 91;
            int yBasePos = scaledHeight - 40;
            int xSize = 88;
            int ySize = 10;
            int xSpacer = 6;
            mc.getTextureManager().bindTexture(Configuration.useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);

            IAttributeInstance attrMaxHealth = mc.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            int health = MathHelper.ceil(mc.player.getHealth());
            float healthMax = (float) attrMaxHealth.getAttributeValue();
            float absorb = mc.player.getAbsorptionAmount();
            float currentValue = (float) health / healthMax;
            int foodVal = mc.player.getFoodStats().getFoodLevel();
            float saturationVal = mc.player.getFoodStats().getSaturationLevel();

            boolean highlight = mc.player.hurtResistantTime / 3 % 2 == 1 && mc.player.hurtResistantTime >= 10;

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

            if (mc.player.isRidingHorse() && mc.player.getRidingEntity() instanceof EntityLiving) {
                EntityLiving mount = (EntityLiving) mc.player.getRidingEntity();
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

            if (Configuration.useCustomPosition && !customHotbarPosOnly) {
                boolean verticle = healthBar.verticle;
                /* GET HEALTHBAR BACKGROUND RESOURCE IF EXISTS */
                if (WidgetImages.keySet().contains("HealthBarBackground")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("HealthBarBackground"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }
                
                /* DRAW BACKGROUND WITH HIGHLIGHT IF NECESSARY */
                if (previousValues.get("HealthBar") != health + healthMax) {
                    if (highlight && Configuration.highlightOnDamage) {
                        drawBar(healthBarBackground, 1, true, "Highlight");
                    } else {
                        drawBar(healthBarBackground, 1, true);
                    }
                } else {
                    if (highlight && Configuration.highlightOnDamage) {
                        drawBar(healthBarBackground, 1, false, "Highlight");
                    } else {
                        drawBar(healthBarBackground, 1, false);
                    }
                }


                /* GET HEALTHBAR RESOURCE IF EXISTS */
                if (WidgetImages.keySet().contains("HealthBar")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("HealthBar"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }

                /* PREVIOUS HEALTH */
                if (previousValues.get("HealthBar") != health + healthMax) {
                    if (Configuration.showPreviousHealth) {
                        drawBar(healthBar, Math.round(healthLast2 / healthMax), true, "PastHealth");
                    }
                } else {
                    if (Configuration.showPreviousHealth) {
                        drawBar(healthBar, Math.round(healthLast2 / healthMax), false, "PastHealth");
                    }
                }

                if (previousValues.get("HealthBar") != health + healthMax) {
                    if (mc.player.isPotionActive(Potion.getPotionById(20))) { /* WITHER POTION HEALTH */
                        drawBar(healthBar, currentValue, true, "WitherHealth");
                    } else if (mc.player.isPotionActive(Potion.getPotionById(19))) { /* POISON HEALTH */
                        drawBar(healthBar, currentValue, true, "PoisonHealth");
                    } else if (mc.player.isPotionActive(Potion.getPotionById(10))) { /* REGENERATION HEALTH  */
                        drawBar(healthBar, currentValue, true, "RegenHealth");

                    /* NORMAL HEALTH */
                    } else {
                        if (health / healthMax > .75f) {
                            drawBar(healthBar, currentValue, true);
                        } else if (health / healthMax > .25f) {
                            drawBar(healthBar, currentValue, true, "MidHealth");
                        } else {
                            drawBar(healthBar, currentValue, true, "LowHealth");
                        }
                    }
                } else {
                    if (mc.player.isPotionActive(Potion.getPotionById(20))) { /* WITHER POTION HEALTH */
                        drawBar(healthBar, currentValue, false, "WitherHealth");
                    } else if (mc.player.isPotionActive(Potion.getPotionById(19))) { /* POISON HEALTH */
                        drawBar(healthBar, currentValue, false, "PoisonHealth");
                    } else if (mc.player.isPotionActive(Potion.getPotionById(10))) { /* REGENERATION HEALTH  */
                        drawBar(healthBar, currentValue, false, "RegenHealth");

                    /* NORMAL HEALTH */
                    } else {
                        if (health / healthMax > .75f) {
                            drawBar(healthBar, currentValue, false);
                        } else if (health / healthMax > .25f) {
                            drawBar(healthBar, currentValue, false, "MidHealth");
                        } else {
                            drawBar(healthBar, currentValue, false, "LowHealth");
                        }
                    }
                }

                healthLast = health;
                previousValues.replace(healthBar.elementName, health + healthMax);

                /* GET FOODBAR RESOURCE IF EXISTS */
                if (WidgetImages.keySet().contains("FoodBarBackground")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("FoodBarBackground"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }


                /* DRAW FOODBAR BACKGROUND */
                if (previousValues.get("FoodBar") != foodVal + 20) {
                    drawBar(foodBarBackground, 1, true);
                } else {
                    drawBar(foodBarBackground, 1, false);
                }

                /* GET FOODBAR RESOURCE IF EXISTS */
                if (WidgetImages.keySet().contains("FoodBar")) {
                    mc.getTextureManager().bindTexture(WidgetImages.get("FoodBar"));
                } else {
                    mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                }


                /* DRAW FOODBAR */
                if (previousValues.get("FoodBar") != foodVal + 20) {
                    drawBar(foodBar, foodVal / 20f, true);
                } else {
                    drawBar(foodBar, foodVal / 20f, false);
                }

                if (satBar.alwaysShow) {
                    /* GET SATURATIONBAR BACKGROUND RESOURCE IF EXISTS */
                    if (WidgetImages.keySet().contains("SaturationBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("SaturationBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                    }


                    /* DRAW SATURATION BAR BACKGROUND */
                    if (previousValues.get(satBar.elementName) != saturationVal + 20) {
                        drawBar(satBarBackground, 1, true);
                    } else {
                        drawBar(satBarBackground, 1, false);
                    }


                    /* GET SATURATIONBAR RESOURCE IF EXISTS */
                    if (WidgetImages.keySet().contains("SaturationBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("SaturationBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar : uiHealthBar);
                    }

                    if (previousValues.get(satBar.elementName) != saturationVal + 20) {
                        drawBar(satBar, saturationVal / 20f, true);
                    } else {
                        drawBar(satBar, saturationVal / 20f, false);
                    }
                }
                previousValues.put(foodBar.elementName, foodVal + 20f);
                previousValues.put(satBar.elementName, saturationVal + 20);

                /* SHOW ARMOR BAR */
                if (mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow) {
                    /* GET ARMOR BAR BACKGROUND RESOURCE IF EXISTS */
                    if (WidgetImages.keySet().contains("ArmorBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("ArmorBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /* DRAW ARMOR BAR */
                    if (previousValues.get("ArmorBar") != mc.player.getTotalArmorValue() + maxArmor) {
                        drawBar(armorBarBackground, 1, true);
                    } else {
                        drawBar(armorBarBackground, 1, false);
                    }

                    /* GET ARMOR BAR RESOURCE IF EXISTS */
                    if (WidgetImages.keySet().contains("ArmorBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("ArmorBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /* DRAW ARMOR BAR */
                    if (previousValues.get("ArmorBar") != mc.player.getTotalArmorValue() + maxArmor) {
                        drawBar(armorBar, mc.player.getTotalArmorValue() / (float) maxArmor, true);
                    } else {
                        drawBar(armorBar, mc.player.getTotalArmorValue() / (float) maxArmor, false);
                    }
                }
                previousValues.put(armorBar.elementName, mc.player.getTotalArmorValue() + (float) maxArmor);

                /* IF MOUNTED */
                if (mc.player.isRidingHorse() && mc.player.getRidingEntity() instanceof EntityLiving) {
                    /* GET MOUNT HEALTH BACKGROUND RESOURCE IF EXISTS */
                    if (WidgetImages.keySet().contains("HorseHealthBarBackground")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("HorseHealthBarBackground"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /* DRAW MOUNT HEALTH BACKGROUND */
                    if (previousValues.get("HorseHealthBar") != ((EntityLiving) mc.player.getRidingEntity()).getHealth() + ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth()) {
                        drawBar(horseHealthBarBackground, 1, true);
                    } else {
                        drawBar(horseHealthBarBackground, 1, false);
                    }

                    /* GET MOUNT HEALTH RESOURCE IF EXISTS */
                    if (WidgetImages.keySet().contains("HorseHealthBar")) {
                        mc.getTextureManager().bindTexture(WidgetImages.get("HorseHealthBar"));
                    } else {
                        mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? uiZedsHealthBar: uiHealthBar);
                    }

                    /* DRAW MOUNT HEALTH */
                    if (previousValues.get("HorseHealthBar") != ((EntityLiving) mc.player.getRidingEntity()).getHealth() + ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth()) {
                        drawBar(horseHealthBar, mountHealthLast2 / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), true, "PastHealth");
                        if (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() > .75f) {
                            drawBar(horseHealthBar, ((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), true);
                        } else if (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() > .25f) {
                            drawBar(horseHealthBar, ((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), true, "MidHealth");
                        } else {
                            drawBar(horseHealthBar, ((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), true, "LowHealth");
                        }
                    } else {
                        drawBar(horseHealthBar, mountHealthLast2 / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), false, "PastHealth");
                        if (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() > .75f) {
                            drawBar(horseHealthBar, ((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), false);
                        } else if (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() > .25f) {
                            drawBar(horseHealthBar, ((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), false, "MidHealth");
                        } else {
                            drawBar(horseHealthBar, ((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), false, "LowHealth");
                        }
                    }
                    mountHealthLast = ((EntityLiving)mc.player.getRidingEntity()).getHealth();
                    previousValues.put(horseHealthBar.elementName, ((EntityLiving) mc.player.getRidingEntity()).getHealth() + ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth());
                }

                if (mc.player.isRidingHorse() && mc.player.getRidingEntity() instanceof EntityLiving && ((EntityLivingBase) mc.player.getRidingEntity()).getTotalArmorValue() > 0) {
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
                    if (previousValues.get("HorseArmorBar") != ((EntityLiving) mc.player.getRidingEntity()).getTotalArmorValue() + maxArmor) {
                        drawBar(horseArmorBarBackground, 1, true);
                    } else {
                        drawBar(horseArmorBarBackground, 1, false);
                    }

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
                    if (previousValues.get("HorseArmorBar") != ((EntityLiving) mc.player.getRidingEntity()).getTotalArmorValue() + maxArmor) {
                        drawBar(horseArmorBar, ((EntityLiving) mc.player.getRidingEntity()).getTotalArmorValue() / maxArmor, true);
                    } else {
                        drawBar(horseArmorBar, ((EntityLiving) mc.player.getRidingEntity()).getTotalArmorValue() / maxArmor, false);
                    }
                    previousValues.put(horseArmorBar.elementName, ((EntityLiving) mc.player.getRidingEntity()).getTotalArmorValue() + (float) maxArmor);
                }

                /*
                *
                *  DRAW STRINGS IF ENABLED
                *
                * */
                if (Configuration.showValues) {
                    GL11.glPushMatrix();
                    GL11.glScalef(healthBar.fontSize, healthBar.fontSize, healthBar.fontSize);
                    if (healthBar.showValue)
                        mc.fontRenderer.drawString((health + absorb) + "/" + healthMax, healthBar.textX + (healthBar.centered ? (mc.fontRenderer.getStringWidth(health + "/" + healthMax) / 2) : 0), healthBar.textY, Integer.parseInt(healthBar.color.replace("0x", ""), 16), false);
                    GL11.glScalef(foodBar.fontSize, foodBar.fontSize, foodBar.fontSize);
                    if (foodBar.showValue)
                        mc.fontRenderer.drawString(foodVal + "/" + 20, foodBar.textX - (foodBar.centered ? (mc.fontRenderer.getStringWidth(foodVal + "/" + 20) / 2) : 0), foodBar.textY, Integer.parseInt(foodBar.color.replace("0x", ""), 16), false);
                    GL11.glScalef(armorBar.fontSize, armorBar.fontSize, armorBar.fontSize);
                    if (armorBar.showValue && (mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow))
                        mc.fontRenderer.drawString(mc.player.getTotalArmorValue() + "/" + Configuration.maxArmor, armorBar.textX + (armorBar.centered ? (mc.fontRenderer.getStringWidth(mc.player.getTotalArmorValue() + "/" + Configuration.maxArmor) / 2) : 0), armorBar.textY, Integer.parseInt(armorBar.color.replace("0x", ""), 16), false);
                    GL11.glScalef(horseHealthBar.fontSize, horseHealthBar.fontSize, horseHealthBar.fontSize);
                    if (mc.player.isRidingHorse() && horseHealthBar.showValue)
                        mc.fontRenderer.drawString(((EntityLivingBase) mc.player.getRidingEntity()).getHealth() + "/" + ((EntityLivingBase) mc.player.getRidingEntity()).getMaxHealth(), horseHealthBar.textX + (horseHealthBar.centered ? (mc.fontRenderer.getStringWidth(((EntityLivingBase) mc.player.getRidingEntity()).getHealth() + "/" + ((EntityLivingBase) mc.player.getRidingEntity()).getMaxHealth()) / 2) : 0), horseHealthBar.textY, Integer.parseInt(horseHealthBar.color.replace("0x", ""), 16), false);
                    GL11.glScalef(horseArmorBar.fontSize, horseArmorBar.fontSize, horseArmorBar.fontSize);
                    if (mc.player.isRidingHorse() && horseArmorBar.showValue)
                        mc.fontRenderer.drawString(((EntityLivingBase) mc.player.getRidingEntity()).getTotalArmorValue() + "/" + Configuration.maxArmor, horseArmorBar.textX + (horseArmorBar.centered ? (mc.fontRenderer.getStringWidth(((EntityLivingBase) mc.player.getRidingEntity()).getTotalArmorValue() + "/" + Configuration.maxArmor) / 2) : 0), horseArmorBar.textY, Integer.parseInt(horseArmorBar.color.replace("0x", ""), 16), false);
                    GL11.glPopMatrix();
                }
            } else {
                if (highlight && Configuration.highlightOnDamage) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 11, xSize, ySize);
                } else {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 0, xSize, ySize);
                }

                if (Configuration.showPreviousHealth)
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 55, Math.min(Math.round((healthLast2 / healthMax) * xSize), xSize), ySize);

                if (mc.player.isPotionActive(Potion.getPotionById(20))) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 88, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else if (mc.player.isPotionActive(Potion.getPotionById(19))) {
                    drawTexturedModalRect(xBasePos, yBasePos, 0, 77, Math.min(Math.round(currentValue * xSize), xSize), ySize);
                } else if (mc.player.isPotionActive(Potion.getPotionById(10))) {
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

                if (mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow) {
                    drawTexturedModalRect(xBasePos, yBasePos - 12, 0, 0, xSize, ySize);
                    drawTexturedModalRect(xBasePos, yBasePos - 12, 0, 121, Math.min((Math.round((float) mc.player.getTotalArmorValue() / (float) Configuration.maxArmor * xSize)), xSize), ySize);
                }

                if (mc.player.isRidingHorse() && mc.player.getRidingEntity() instanceof EntityLiving) {
                    drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 0, xSize, ySize);
                    if (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() > .75f) {
                        drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 22, Math.min((int) (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() * xSize), xSize), ySize);
                    } else if (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() > .25f) {
                        drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 33, Math.min((int) (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() * xSize), xSize), ySize);
                    } else {
                        drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos - 12, 0, 44, Math.min((int) (((EntityLiving) mc.player.getRidingEntity()).getHealth() / ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth() * xSize), xSize), ySize);
                    }
                    mountHealthLast = ((EntityLiving)mc.player.getRidingEntity()).getHealth();
                }

                if (mc.player.isRidingHorse() && mc.player.getRidingEntity() instanceof EntityLiving && ((EntityLivingBase) mc.player.getRidingEntity()).getTotalArmorValue() > 0) {
                    drawTexturedModalRect(xBasePos + xSize + 6, yBasePos - 24, horseArmorBarBackground.texturePosX, horseArmorBarBackground.texturePosY, xSize, ySize);
                    drawTexturedModalRect(xBasePos + xSize + 6, yBasePos - 24, horseArmorBar.texturePosX, horseArmorBar.texturePosY, Math.min((Math.round((float) ((EntityLivingBase) mc.player.getRidingEntity()).getTotalArmorValue() / (float) Configuration.maxArmor * xSize)), xSize), ySize);
                }

                if (Configuration.showValues) {
                    if (healthBar.showValue)
                        mc.fontRenderer.drawString((health + absorb) + "/" + healthMax, xBasePos + 40 - (mc.fontRenderer.getStringWidth(health + "/" + healthMax) / 2), yBasePos + 1, Integer.parseInt(healthBar.color.replace("0x", ""), 16), false);
                    if (foodBar.showValue)
                        mc.fontRenderer.drawString(foodVal + "/" + 20, xBasePos + xSize + 50 - (mc.fontRenderer.getStringWidth(foodVal + "/" + 20) / 2), yBasePos + 1, Integer.parseInt(foodBar.color.replace("0x", ""), 16), false);
                    if (armorBar.showValue && (mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow))
                        mc.fontRenderer.drawString(mc.player.getTotalArmorValue() + "/" + Configuration.maxArmor, xBasePos + 40 - (mc.fontRenderer.getStringWidth(mc.player.getTotalArmorValue() + "/" + Configuration.maxArmor) / 2), yBasePos - 11, Integer.parseInt(armorBar.color.replace("0x", ""), 16), false);
                    if (mc.player.isRidingHorse() && horseHealthBar.showValue)
                        mc.fontRenderer.drawString(((EntityLiving) mc.player.getRidingEntity()).getHealth() + "/" + ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth(), xBasePos + xSize + 50 - (mc.fontRenderer.getStringWidth(((EntityLiving) mc.player.getRidingEntity()).getHealth() + "/" + ((EntityLiving) mc.player.getRidingEntity()).getMaxHealth()) / 2), yBasePos - 11, Integer.parseInt(horseHealthBar.color.replace("0x", ""), 16), false);
                    if (mc.player.isRidingHorse() && horseArmorBar.showValue)
                        mc.fontRenderer.drawString(((EntityLiving) mc.player.getRidingEntity()).getTotalArmorValue() + "/" + Configuration.maxArmor, xBasePos + xSize + 50 - (mc.fontRenderer.getStringWidth(((EntityLivingBase) mc.player.getRidingEntity()).getTotalArmorValue() + "/" + Configuration.maxArmor) / 2), yBasePos - 11, Integer.parseInt(horseArmorBar.color.replace("0x", ""), 16), false);
                }
            }

            event.setCanceled(true);
        }
    }
    
    private void drawBar(Configuration.hudWidget bar, float currentPercent, boolean positive) {
        int[] dispos = getCalculatedHiddenPos(bar, positive);
        drawTexturedModalRect(bar.uiPosX + dispos[0],
                bar.verticle ? ((bar.uiPosY + dispos[1] + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) : bar.uiPosY + dispos[1], bar.texturePosX,
                bar.verticle ? ((bar.texturePosY + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) :  bar.texturePosY,
                bar.verticle ? bar.textureSizeX : Math.min(Math.round(currentPercent * bar.textureSizeX), bar.textureSizeX),
                bar.verticle ? Math.min(Math.round(currentPercent * bar.textureSizeY), bar.textureSizeY): bar.textureSizeY);
    }

    private void drawBar(Configuration.hudWidget bar, float currentPercent, boolean positive, String subElement) {
        int[] dispos = getCalculatedHiddenPos(bar, positive);
        drawTexturedModalRect(bar.uiPosX + dispos[0],
                bar.verticle ? ((bar.uiPosY + dispos[1] + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) : bar.uiPosY + dispos[1], bar.subElements.get(subElement).get(0),
                bar.verticle ? ((bar.subElements.get(subElement).get(1) + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) :  bar.subElements.get(subElement).get(1),
                bar.verticle ? bar.textureSizeX : Math.min(Math.round(currentPercent * bar.textureSizeX), bar.textureSizeX),
                bar.verticle ? Math.min(Math.round(currentPercent * bar.textureSizeY), bar.textureSizeY): bar.textureSizeY);
    }

    private long lastTime = 0;
    @SuppressWarnings("all")
    private int[] getCalculatedHiddenPos(Configuration.hudWidget bar, boolean positive) {
        if (!Configuration.widgets.get(bar.elementName.replace("Background", "")).hideWhenUnchanged) return new int[]{0, 0};

        if ((System.currentTimeMillis() - lastTime > Configuration.widgets.get(bar.elementName.replace("Background", "")).msHidingSpeed) || positive) {
            if (!positive && timers.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) < 0) {
                if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX < Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX < 100 && hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) > -(Configuration.widgets.get(bar.elementName.replace("Background", "")).textureSizeX + Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX)) { // NEGATIVE X DIRECTION
                    hiddenXs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) - Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                } else if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX > Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX > 170 && hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) < (Configuration.widgets.get(bar.elementName.replace("Background", "")).textureSizeX - (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX - xRes))) { // POSITIVE X DIRECTION
                    hiddenXs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) + Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                } else if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX > Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY < 100 && hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) > -(Configuration.widgets.get(bar.elementName.replace("Background", "")).textureSizeY + Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY)) { // NEGATIVE Y DIRECTION
                    hiddenYs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) - Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                } else if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX < Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY > 170 && hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) < (Configuration.widgets.get(bar.elementName.replace("Background", "")).textureSizeY - (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY - yRes))) { // POSITIVE Y DIRECTION
                    hiddenYs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) + Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                }
            } else if (positive || timers.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) > 0) {
                if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX < Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX < 100 && hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) < 0) { // NEGATIVE X DIRECTION
                    hiddenXs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) + Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                    if (Configuration.widgets.get(bar.elementName.replace("Background", "")).suddenTransition)
                        hiddenXs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName, 0);
                } else if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX > Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX > 100 && hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) > 0) { // POSITIVE X DIRECTION
                    hiddenXs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenXs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) - Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                    if (Configuration.widgets.get(bar.elementName.replace("Background", "")).suddenTransition)
                        hiddenXs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName, 0);
                } else if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX > Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY < 100 && hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) < 0) { // NEGATIVE Y DIRECTION
                    hiddenYs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) + Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                    if (Configuration.widgets.get(bar.elementName.replace("Background", "")).suddenTransition)
                        hiddenYs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName, 0);
                } else if (Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosX < Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY && Configuration.widgets.get(bar.elementName.replace("Background", "")).uiPosY > 100 && hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) > 0) { // POSITIVE Y DIRECTION
                    hiddenYs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName,
                            hiddenYs.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) - Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionHidingPixels);
                    if (Configuration.widgets.get(bar.elementName.replace("Background", "")).suddenTransition)
                        hiddenYs.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName, 0);
                }
                if (timers.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) < 0)
                    timers.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName, Configuration.widgets.get(bar.elementName.replace("Background", "")).transitionTime);
            }
            if (timers.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) > -5)
                timers.replace(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName, timers.get(Configuration.widgets.get(bar.elementName.replace("Background", "")).elementName) - 1);
            lastTime = System.currentTimeMillis();
        }
        return new int[]{hiddenXs.get(bar.elementName.replace("Background", "")), hiddenYs.get(bar.elementName.replace("Background", ""))};
    }


    private void drawTexturedModalRect(int x, int y, int u, int v, int xSize, int ySize) {
        mc.ingameGUI.drawTexturedModalRect(x, y, u, v, xSize, ySize);
        /*float f = 0.00390625F;
        float f1 = 0.00390625F;

        Tessellator tessellator = Tessellator.getInstance();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + ySize, 0.0, (u + 0) * f, (v + ySize) * f1);
        tessellator.addVertexWithUV(x + xSize, y + ySize, 0.0, (u + xSize) * f, (v + ySize) * f1);
        tessellator.addVertexWithUV(x + xSize, y + 0, 0.0, (u + xSize) * f, (v + 0) * f1);
        tessellator.addVertexWithUV(x + 0, y + 0, 0.0, (u + 0) * f, (v + 0) * f1);
        tessellator.draw();*/
    }

}

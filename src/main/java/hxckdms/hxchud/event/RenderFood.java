package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static hxckdms.hxchud.Configuration.customHotbarPosOnly;
import static hxckdms.hxchud.Configuration.useDrZedsPersonalTexture;
import static hxckdms.hxchud.Configuration.widgets;

public class RenderFood {

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderFoodBars(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
            ScaledResolution scaledresolution = new ScaledResolution(Helper.mc);
            int foodVal = Helper.mc.player.getFoodStats().getFoodLevel();
            float saturationVal = Helper.mc.player.getFoodStats().getSaturationLevel();

            Configuration.hudWidget foodBar = widgets.get("FoodBar");
            Configuration.hudWidget satBar = widgets.get("SaturationBar");
            Configuration.hudWidget foodBarBackground = widgets.get("FoodBarBackground");
            Configuration.hudWidget satBarBackground = widgets.get("SaturationBarBackground");

            if (Configuration.useCustomPosition && !customHotbarPosOnly) {

                /* GET FOODBAR RESOURCE IF EXISTS */
                if (Helper.WidgetImages.keySet().contains("FoodBarBackground")) {
                    Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("FoodBarBackground"));
                } else {
                    Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                }


                /* DRAW FOODBAR BACKGROUND */
                if (Helper.previousValues.get("FoodBar") != foodVal + 20) {
                    Helper.drawBar(foodBarBackground, 1, true);
                } else {
                    Helper.drawBar(foodBarBackground, 1, false);
                }

                /* GET FOODBAR RESOURCE IF EXISTS */
                if (Helper.WidgetImages.keySet().contains("FoodBar")) {
                    Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("FoodBar"));
                } else {
                    Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                }


                /* DRAW FOODBAR */
                if (Helper.previousValues.get("FoodBar") != foodVal + 20) {
                    Helper.drawBar(foodBar, foodVal / 20f, true);
                } else {
                    Helper.drawBar(foodBar, foodVal / 20f, false);
                }

                if (satBar.alwaysShow) {
                    /* GET SATURATIONBAR BACKGROUND RESOURCE IF EXISTS */
                    if (Helper.WidgetImages.keySet().contains("SaturationBarBackground")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("SaturationBarBackground"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }


                    /* DRAW SATURATION BAR BACKGROUND */
                    if (Helper.previousValues.get(satBar.elementName) != saturationVal + 20) {
                        Helper.drawBar(satBarBackground, 1, true);
                    } else {
                        Helper.drawBar(satBarBackground, 1, false);
                    }


                    /* GET SATURATIONBAR RESOURCE IF EXISTS */
                    if (Helper.WidgetImages.keySet().contains("SaturationBar")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("SaturationBar"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    if (Helper.previousValues.get(satBar.elementName) != saturationVal + 20) {
                        Helper.drawBar(satBar, saturationVal / 20f, true);
                    } else {
                        Helper.drawBar(satBar, saturationVal / 20f, false);
                    }
                }
                Helper.previousValues.put(foodBar.elementName, foodVal + 20f);
                Helper.previousValues.put(satBar.elementName, saturationVal + 20);

                if (Configuration.showValues) {
                    GL11.glPushMatrix();
                    GL11.glScalef(foodBar.fontSize, foodBar.fontSize, foodBar.fontSize);
                    if (foodBar.showValue)
                        Helper.mc.fontRenderer.drawString(foodVal + "/" + 20, foodBar.textX - (foodBar.centered ? (Helper.mc.fontRenderer.getStringWidth(foodVal + "/" + 20) / 2) : 0), foodBar.textY, Integer.parseInt(foodBar.color.replace("0x", ""), 16), false);
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

                Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos, 0, 0, xSize, ySize); //Food Bar

                Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos, 0, 99, Math.min((Math.round(foodVal / 20f * xSize)), xSize), ySize);
                Helper.drawTexturedModalRect(xBasePos + xSize + xSpacer, yBasePos, 0, 110, Math.min((Math.round(saturationVal / 20f * xSize)), xSize), ySize);
            }
        }
    }
}

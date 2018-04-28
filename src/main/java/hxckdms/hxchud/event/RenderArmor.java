package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static hxckdms.hxchud.Configuration.*;

public class RenderArmor {

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderArmorBars(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
            ScaledResolution scaledresolution = new ScaledResolution(Helper.mc);
            Configuration.hudWidget armorBar = widgets.get("ArmorBar");
            Configuration.hudWidget armorBarBackground = widgets.get("ArmorBarBackground");


            if (Configuration.useCustomPosition && !customHotbarPosOnly) {
                /* SHOW ARMOR BAR */
                if (Helper.mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow) {
                    /* GET ARMOR BAR BACKGROUND RESOURCE IF EXISTS */
                    if (Helper.WidgetImages.keySet().contains("ArmorBarBackground")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("ArmorBarBackground"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    /* DRAW ARMOR BAR */
                    if (Helper.previousValues.get("ArmorBar") != Helper.mc.player.getTotalArmorValue() + maxArmor) {
                        Helper.drawBar(armorBarBackground, 1, true);
                    } else {
                        Helper.drawBar(armorBarBackground, 1, false);
                    }

                    /* GET ARMOR BAR RESOURCE IF EXISTS */
                    if (Helper.WidgetImages.keySet().contains("ArmorBar")) {
                        Helper.mc.getTextureManager().bindTexture(Helper.WidgetImages.get("ArmorBar"));
                    } else {
                        Helper.mc.getTextureManager().bindTexture(useDrZedsPersonalTexture ? Helper.uiZedsHealthBar : Helper.uiHealthBar);
                    }

                    /* DRAW ARMOR BAR */
                    if (Helper.previousValues.get("ArmorBar") != Helper.mc.player.getTotalArmorValue() + maxArmor) {
                        Helper.drawBar(armorBar, Helper.mc.player.getTotalArmorValue() / (float) maxArmor, true);
                    } else {
                        Helper.drawBar(armorBar, Helper.mc.player.getTotalArmorValue() / (float) maxArmor, false);
                    }
                }
                Helper.previousValues.put(armorBar.elementName, Helper.mc.player.getTotalArmorValue() + (float) maxArmor);
            } else {
                int scaledWidth = scaledresolution.getScaledWidth();
                int scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 40;
                int xSize = 88;
                int ySize = 10;
                int xSpacer = 6;

                if (Helper.mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow) {
                    Helper.drawTexturedModalRect(xBasePos, yBasePos - 12, 0, 0, xSize, ySize);
                    Helper.drawTexturedModalRect(xBasePos, yBasePos - 12, 0, 121, Math.min((Math.round((float) Helper.mc.player.getTotalArmorValue() / (float) Configuration.maxArmor * xSize)), xSize), ySize);
                }
            }


            if (Configuration.showValues) {
                GL11.glPushMatrix();
                GL11.glScalef(armorBar.fontSize, armorBar.fontSize, armorBar.fontSize);
                if (armorBar.showValue && (Helper.mc.player.getTotalArmorValue() > 0 || armorBar.alwaysShow))
                    Helper.mc.fontRenderer.drawString(Helper.mc.player.getTotalArmorValue() + "/" + Configuration.maxArmor, armorBar.textX + (armorBar.centered ? (Helper.mc.fontRenderer.getStringWidth(Helper.mc.player.getTotalArmorValue() + "/" + Configuration.maxArmor) / 2) : 0), armorBar.textY, Integer.parseInt(armorBar.color.replace("0x", ""), 16), false);
                GL11.glPopMatrix();
            }

        }
    }
}

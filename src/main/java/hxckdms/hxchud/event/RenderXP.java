package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class RenderXP {
    private static int xRes = 480, yRes = 265, timer = 0, hiddenX = 0, hiddenY = 0;
    private static long xpLast = 0;
    ResourceLocation texture = null;

    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderXPBar(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && Configuration.useCustomPosition) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int scaledWidth = scaledresolution.getScaledWidth();
            int scaledHeight = scaledresolution.getScaledHeight();
            xRes = scaledresolution.getScaledWidth();
            yRes = scaledresolution.getScaledHeight();

            Configuration.hudWidget bar = Configuration.widgets.get("ExpBar");

            if (!bar.imagePath.isEmpty() && !bar.imagePath.equals("") && texture == null) {
                System.out.println("BAR IMAGE PATH = " + bar.imagePath);
                texture = new ResourceLocation(bar.imagePath);
            }

            mc.getTextureManager().bindTexture(texture == null ? Gui.ICONS : texture);
            int i = mc.thePlayer.xpBarCap();

            if (xpLast != mc.thePlayer.experienceTotal) {
                getCalculatedHiddenPos(bar, true);
            } else {
                getCalculatedHiddenPos(bar, false);
            }

            if (bar.verticle) {
                GL11.glPushMatrix();
                GL11.glRotatef(90, 0, 0, 0);
            }

            if (i > 0) {
                int k = (int)(mc.thePlayer.experience * bar.textureSizeX);
                mc.ingameGUI.drawTexturedModalRect(bar.uiPosX + xRes, bar.uiPosY + yRes, bar.texturePosX, bar.texturePosY, bar.textureSizeX, bar.textureSizeY);

                if (k > 0) {
                    mc.ingameGUI.drawTexturedModalRect(bar.uiPosX + xRes, bar.uiPosY + yRes, bar.texturePosX, bar.texturePosY + 4, k, bar.textureSizeY);
                }
            }

            if (bar.verticle) {
                GL11.glPopMatrix();
            }

            if (mc.thePlayer.experienceLevel > 0)
            {
                String s = "" + mc.thePlayer.experienceLevel;
                int i1 = (scaledresolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(s)) / 2;
                int j1 = scaledresolution.getScaledHeight() - 31 - 4;
                mc.fontRendererObj.drawString(s, i1 + 1, j1, 0);
                mc.fontRendererObj.drawString(s, i1 - 1, j1, 0);
                mc.fontRendererObj.drawString(s, i1, j1 + 1, 0);
                mc.fontRendererObj.drawString(s, i1, j1 - 1, 0);
                mc.fontRendererObj.drawString(s, i1, j1, 8453920);
                mc.mcProfiler.endSection();
            }

            xpLast = mc.thePlayer.experienceTotal;
            event.setCanceled(true);
        }
    }

    private long lastTime = 0;
    @SuppressWarnings("all")
    private void getCalculatedHiddenPos(Configuration.hudWidget bar, boolean positive) {
        if ((System.currentTimeMillis() - lastTime > bar.msHidingSpeed) || positive) {
            if (!positive && timer < 0) {
                if (bar.uiPosX < bar.uiPosY && bar.uiPosX < 100 && hiddenX > -(bar.textureSizeX + bar.uiPosX)) { // NEGATIVE X DIRECTION
                    hiddenX = hiddenX - bar.transitionHidingPixels;
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosX > 170 && hiddenX < (bar.textureSizeX - (bar.uiPosX - xRes))) { // POSITIVE X DIRECTION
                    hiddenX = hiddenX + bar.transitionHidingPixels;
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosY < 100 && hiddenY > -(bar.textureSizeY + bar.uiPosY)) { // NEGATIVE Y DIRECTION
                    hiddenY = hiddenY - bar.transitionHidingPixels;
                } else if (bar.uiPosX < bar.uiPosY && bar.uiPosY > 170 && hiddenY < (bar.textureSizeY - (bar.uiPosY - yRes))) { // POSITIVE Y DIRECTION
                    hiddenY = hiddenY + bar.transitionHidingPixels;
                }
            } else if (positive || timer > 0) {
                if (bar.uiPosX < bar.uiPosY && bar.uiPosX < 100 && hiddenX < 0) { // NEGATIVE X DIRECTION
                    hiddenX = hiddenX + bar.transitionHidingPixels;
                    if (bar.suddenTransition)
                        hiddenX = 0;
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosX > 100 && hiddenX > 0) { // POSITIVE X DIRECTION
                    hiddenX = hiddenX - bar.transitionHidingPixels;
                    if (bar.suddenTransition)
                        hiddenX = 0;
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosY < 100 && hiddenY < 0) { // NEGATIVE Y DIRECTION
                    hiddenY = hiddenY + bar.transitionHidingPixels;
                    if (bar.suddenTransition)
                        hiddenY = 0;
                } else if (bar.uiPosX < bar.uiPosY && bar.uiPosY > 100 && hiddenY > 0) { // POSITIVE Y DIRECTION
                    hiddenY = hiddenY - bar.transitionHidingPixels;
                    if (bar.suddenTransition)
                        hiddenY = 0;
                }
                if (timer < 0)
                    timer = bar.transitionTime;
            }
            if (timer > -5)
                timer--;
            lastTime = System.currentTimeMillis();
        }
    }
}

package hxckdms.hxchud.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hxckdms.hxchud.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.HashMap;

@SuppressWarnings("all")
public class RenderHotbar {
    float zLevel = 0;
    Minecraft mc;
    static int lastItemSlot = 1;
    static Item lastItem = null, lastItemOffhand = null;
    private static HashMap<String, Integer> hiddenXs = new HashMap<>(), hiddenYs = new HashMap<>();
    private static int xRes = 480, yRes = 265;
    static {
        hiddenXs.put("Hotbar", 0);
        hiddenXs.put("Offhand", 0);
        hiddenYs.put("Hotbar", 0);
        hiddenYs.put("Offhand", 0);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHxCHotbar(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR && Configuration.useCustomPosition) {
            mc = Minecraft.getMinecraft();
            Configuration.hudWidget bar = Configuration.widgets.get("Hotbar");
            Configuration.hudWidget barSlot = Configuration.widgets.get("HotbarSelectedSlot");
            mc.getTextureManager().bindTexture(bar.imagePath.length() > 5 ? new ResourceLocation(bar.imagePath) : Configuration.useModHotbarTexture ? new ResourceLocation(Configuration.customHotbarTexture) : new ResourceLocation("textures/gui/widgets.png"));
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

            EntityPlayer player = mc.thePlayer;

            int scaledWidth = sr.getScaledWidth() / 2;
            int scaledHeight = sr.getScaledHeight();
            float f = zLevel;
            zLevel = -90.0F;
            xRes = sr.getScaledWidth();
            yRes = sr.getScaledHeight();

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            if (bar.uiPosX == -9999 && bar.uiPosY == -9999) {
                int[] shiftedPos = new int[]{0,0};
                if (bar.hideWhenUnchanged)
                    shiftedPos = getCalculatedHiddenPos(bar, (lastItem == null && player.getHeldItem() != null) || (lastItem != null && player.getHeldItem() == null) || (lastItemSlot != player.inventory.currentItem || lastItem != player.getHeldItem().getItem()));
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/widgets.png"));
                drawTexturedModalRect(scaledWidth - 91 + shiftedPos[0], scaledHeight - 22 + shiftedPos[1], 0, 0, 182, 22);
                drawTexturedModalRect(scaledWidth - 91 - 1 + player.inventory.currentItem * 20 + shiftedPos[0], scaledHeight - 22 - 1 + shiftedPos[1], 0, 22, 24, 22);

                zLevel = f;
                RenderHelper.enableGUIStandardItemLighting();

                for (int slot = 0; slot < 9; ++slot) {
                    renderHotbarItem(scaledWidth - 91 + 3 + shiftedPos[0] + (slot * 20), scaledHeight - 22 + shiftedPos[1], event.partialTicks, player, player.inventory.mainInventory[slot]);
                }
            } else {
                int[] shiftedPos = new int[]{0,0};
                if (bar.hideWhenUnchanged)
                    shiftedPos = getCalculatedHiddenPos(bar, (lastItem == null && player.getHeldItem() != null) || (lastItem != null && player.getHeldItem() == null) || (lastItemSlot != player.inventory.currentItem || lastItem != player.getHeldItem().getItem()));
                int startX = scaledWidth - 91, startY = scaledHeight - 22;
                if (bar.subElements.get("Slot1Pos").get(0) < -1) {
                    if (!bar.verticle) {
                        for (int x = 0; x < 9; x++) {
                            drawTexturedModalRect(startX + (x * bar.textureSizeX) + shiftedPos[0], startY + shiftedPos[1], bar.subElements.get("Slot" + (x + 1) + "Icon").get(0), bar.subElements.get("Slot" + (x + 1) + "Icon").get(1), bar.textureSizeX, bar.textureSizeY);
                        }
                        drawTexturedModalRect(startX - 1 + player.inventory.currentItem * bar.textureSizeX + shiftedPos[0], startY - 1 + shiftedPos[1], barSlot.texturePosX, barSlot.texturePosY, barSlot.textureSizeX, barSlot.textureSizeY);
                    } else {
                        for (int y = 0; y < 9; y++) {
                            drawTexturedModalRect(startX + shiftedPos[0], startY + (y * bar.textureSizeY) + shiftedPos[1], bar.subElements.get("Slot" + (y + 1) + "Icon").get(0), bar.subElements.get("Slot" + (y + 1) + "Icon").get(1), bar.textureSizeX, bar.textureSizeY);
                        }
                        drawTexturedModalRect(startX - 1 + shiftedPos[0], startY - 1 + player.inventory.currentItem * bar.textureSizeY + shiftedPos[1], 0, 22, 24, 22);
                    }
                } else {
                    for (int slot = 0; slot < 9; slot++) {
                        drawTexturedModalRect(bar.subElements.get("Slot" + (slot + 1) + "Pos").get(0), bar.subElements.get("Slot" + (slot + 1) + "Pos").get(1),
                                bar.subElements.get("Slot" + (slot + 1) + "Icon").get(0), bar.subElements.get("Slot" + (slot + 1) + "Icon").get(1), bar.textureSizeX, bar.textureSizeY);
                    }
                    int slot = player.inventory.currentItem;
                    drawTexturedModalRect(bar.subElements.get("Slot" + (slot + 1) + "Pos").get(0), bar.subElements.get("Slot" + (slot + 1) + "Pos").get(1),
                            bar.subElements.get("Slot" + (slot + 1) + "Icon").get(0), bar.subElements.get("Slot" + (slot + 1) + "Icon").get(1), bar.textureSizeX, bar.textureSizeY);
                }
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                zLevel = f;
                RenderHelper.enableGUIStandardItemLighting();

                if (bar.subElements.get("Slot1Pos").get(0) < -1) {
                    if (!bar.verticle) {
                        for (int x = 0; x < 9; x++) {
                            renderHotbarItem(4 + startX + (x * bar.textureSizeX - 1) + shiftedPos[0], startY + 3 + shiftedPos[1], event.partialTicks, player, player.inventory.mainInventory[x]);
                        }
                    } else {
                        for (int y = 0; y < 9; y++) {
                            renderHotbarItem(4 + startX + shiftedPos[0], startY + (y * bar.textureSizeY) + shiftedPos[1] + 3, event.partialTicks, player, player.inventory.mainInventory[y]);
                        }
                    }
                } else {
                    for (int slot = 0; slot < 9; slot++) {
                        renderHotbarItem(4 + bar.subElements.get("Slot" + (slot + 1) + "Pos").get(0), bar.subElements.get("Slot" + (slot + 1) + "Pos").get(1) + 3, event.partialTicks, player, player.inventory.mainInventory[slot]);
                    }
                }

                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            }
            event.setCanceled(true);
        }
    }

    public void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack stack) {
        RenderItem itemRenderer = new RenderItem();
        if (stack != null) {
            float f = (float)stack.animationsToGo - partialTicks;

            if (f > 0.0F) {
                GL11.glPushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GL11.glTranslatef((float)(x + 8), (float)(y + 12), 0.0F);
                GL11.glScalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);

            if (f > 0.0F) {
                GL11.glPushMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
        }
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
    public static HashMap<String, Integer> timers = new HashMap<>();
    static {
        timers.put("Hotbar", 0);
        timers.put("Offhand", 0);
    }
    private long lastTime = 0;
    private int[] getCalculatedHiddenPos(Configuration.hudWidget bar, boolean positive) {
        if ((System.currentTimeMillis() - lastTime > bar.msHidingSpeed) || positive) {
            if (!positive && timers.get(bar.elementName) < 0) {
                if (bar.uiPosX < bar.uiPosY && bar.uiPosX < 100 && hiddenXs.get(bar.elementName) > -(bar.textureSizeX + bar.uiPosX)) { // NEGATIVE X DIRECTION
                    hiddenXs.replace(bar.elementName, hiddenXs.get(bar.elementName) - bar.transitionHidingPixels);
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosX > 170 && hiddenXs.get(bar.elementName) < (bar.textureSizeX - (bar.uiPosX - xRes))) { // POSITIVE X DIRECTION
                    hiddenXs.replace(bar.elementName, hiddenXs.get(bar.elementName) + bar.transitionHidingPixels);
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosY < 100 && hiddenYs.get(bar.elementName) > -(bar.textureSizeY + bar.uiPosY)) { // NEGATIVE Y DIRECTION
                    hiddenYs.replace(bar.elementName, hiddenYs.get(bar.elementName) - bar.transitionHidingPixels);
                } else if (bar.uiPosX < bar.uiPosY && bar.uiPosY > 170 && hiddenYs.get(bar.elementName) < (bar.textureSizeY - (bar.uiPosY - yRes))) { // POSITIVE Y DIRECTION
                    hiddenYs.replace(bar.elementName, hiddenYs.get(bar.elementName) + bar.transitionHidingPixels);
                }
            } else if (positive || timers.get(bar.elementName) > 0) {
                if (bar.uiPosX < bar.uiPosY && bar.uiPosX < 100 && hiddenXs.get(bar.elementName) < 0) { // NEGATIVE X DIRECTION
                    hiddenXs.replace(bar.elementName, hiddenXs.get(bar.elementName) + bar.transitionHidingPixels);
                    if (bar.suddenTransition)
                        hiddenXs.replace(bar.elementName, 0);
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosX > 100 && hiddenXs.get(bar.elementName) > 0) { // POSITIVE X DIRECTION
                    hiddenXs.replace(bar.elementName, hiddenXs.get(bar.elementName) - bar.transitionHidingPixels);
                    if (bar.suddenTransition)
                        hiddenXs.replace(bar.elementName, 0);
                } else if (bar.uiPosX > bar.uiPosY && bar.uiPosY < 100 && hiddenYs.get(bar.elementName) < 0) { // NEGATIVE Y DIRECTION
                    hiddenYs.replace(bar.elementName, hiddenYs.get(bar.elementName) + bar.transitionHidingPixels);
                    if (bar.suddenTransition)
                        hiddenYs.replace(bar.elementName, 0);
                } else if (bar.uiPosX < bar.uiPosY && bar.uiPosY > 100 && hiddenYs.get(bar.elementName) > 0) { // POSITIVE Y DIRECTION
                    hiddenYs.replace(bar.elementName, hiddenYs.get(bar.elementName) - bar.transitionHidingPixels);
                    if (bar.suddenTransition)
                        hiddenYs.replace(bar.elementName, 0);
                }
                if (timers.get(bar.elementName) < 0)
                    timers.replace(bar.elementName,  bar.transitionTime);
            }
            if (timers.get(bar.elementName) > -5)
                timers.replace(bar.elementName, timers.get(bar.elementName) - 1);
            lastTime = System.currentTimeMillis();
        }
        return new int[]{hiddenXs.get(bar.elementName), hiddenYs.get(bar.elementName)};
    }
}

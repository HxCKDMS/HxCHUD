package hxckdms.hxchud.event;

import hxckdms.hxchud.Configuration;
import hxckdms.hxchud.libraries.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

import static hxckdms.hxchud.Configuration.widgets;

public class Helper {
    static Minecraft mc = Minecraft.getMinecraft();
    static long lastSysTime = System.currentTimeMillis();

    static int xRes = 480, yRes = 265;
    static final ResourceLocation uiHealthBar = new ResourceLocation(Configuration.customResourceLocation);
    static final ResourceLocation uiZedsHealthBar = new ResourceLocation(Constants.MOD_ID, "textures/gui/zedshealthbar.png");
    
    static HashMap<String, ResourceLocation> WidgetImages = new HashMap<>();
    static HashMap<String, Float> previousValues = new HashMap<>();

    private static HashMap<String, Integer> hiddenXs = new HashMap<>(), hiddenYs = new HashMap<>();
    private static HashMap<String, Integer> timers = new HashMap<>();
    public static void init() {
        widgets.forEach((name, widget) -> {
            if (!widget.imagePath.isEmpty() && widget.imagePath.length() > 5)
                WidgetImages.put(name, new ResourceLocation(widget.imagePath));
            hiddenXs.put(name, 0);
            hiddenYs.put(name, 0);
            previousValues.put(name, 0f);
            timers.put(name, 0);
        });
    }

    static void drawBar(Configuration.hudWidget bar, float currentPercent, boolean positive) {
        int[] dispos = getCalculatedHiddenPos(bar, positive);
        drawTexturedModalRect(bar.uiPosX + dispos[0],
                bar.verticle ? ((bar.uiPosY + dispos[1] + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) : bar.uiPosY + dispos[1], bar.texturePosX,
                bar.verticle ? ((bar.texturePosY + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) :  bar.texturePosY,
                bar.verticle ? bar.textureSizeX : Math.min(Math.round(currentPercent * bar.textureSizeX), bar.textureSizeX),
                bar.verticle ? Math.min(Math.round(currentPercent * bar.textureSizeY), bar.textureSizeY): bar.textureSizeY);
    }

    static void drawBar(Configuration.hudWidget bar, float currentPercent, boolean positive, String subElement) {
        int[] dispos = getCalculatedHiddenPos(bar, positive);
        drawTexturedModalRect(bar.uiPosX + dispos[0],
                bar.verticle ? ((bar.uiPosY + dispos[1] + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) : bar.uiPosY + dispos[1], bar.subElements.get(subElement).get(0),
                bar.verticle ? ((bar.subElements.get(subElement).get(1) + bar.textureSizeY) - (Math.round(currentPercent * bar.textureSizeY))) :  bar.subElements.get(subElement).get(1),
                bar.verticle ? bar.textureSizeX : Math.min(Math.round(currentPercent * bar.textureSizeX), bar.textureSizeX),
                bar.verticle ? Math.min(Math.round(currentPercent * bar.textureSizeY), bar.textureSizeY): bar.textureSizeY);
    }

    private static long lastTime = 0;
    @SuppressWarnings("all")
    static int[] getCalculatedHiddenPos(Configuration.hudWidget bar, boolean positive) {
        if (!getParentBar(bar).hideWhenUnchanged) return new int[]{0, 0};

        if ((System.currentTimeMillis() - lastTime > getParentBar(bar).msHidingSpeed) || positive) {
            if (!positive && timers.get(getParentBar(bar).elementName) < 0) {
                if (getParentBar(bar).uiPosX < getParentBar(bar).uiPosY && getParentBar(bar).uiPosX < 100 && hiddenXs.get(getParentBar(bar).elementName) > -(getParentBar(bar).textureSizeX + getParentBar(bar).uiPosX)) { // NEGATIVE X DIRECTION
                    hiddenXs.replace(getParentBar(bar).elementName,
                            hiddenXs.get(getParentBar(bar).elementName) - getParentBar(bar).transitionHidingPixels);
                } else if (getParentBar(bar).uiPosX > getParentBar(bar).uiPosY && getParentBar(bar).uiPosX > 170 && hiddenXs.get(getParentBar(bar).elementName) < (getParentBar(bar).textureSizeX - (getParentBar(bar).uiPosX - xRes))) { // POSITIVE X DIRECTION
                    hiddenXs.replace(getParentBar(bar).elementName,
                            hiddenXs.get(getParentBar(bar).elementName) + getParentBar(bar).transitionHidingPixels);
                } else if (getParentBar(bar).uiPosX > getParentBar(bar).uiPosY && getParentBar(bar).uiPosY < 100 && hiddenYs.get(getParentBar(bar).elementName) > -(getParentBar(bar).textureSizeY + getParentBar(bar).uiPosY)) { // NEGATIVE Y DIRECTION
                    hiddenYs.replace(getParentBar(bar).elementName,
                            hiddenYs.get(getParentBar(bar).elementName) - getParentBar(bar).transitionHidingPixels);
                } else if (getParentBar(bar).uiPosX < getParentBar(bar).uiPosY && getParentBar(bar).uiPosY > 170 && hiddenYs.get(getParentBar(bar).elementName) < (getParentBar(bar).textureSizeY - (getParentBar(bar).uiPosY - yRes))) { // POSITIVE Y DIRECTION
                    hiddenYs.replace(getParentBar(bar).elementName,
                            hiddenYs.get(getParentBar(bar).elementName) + getParentBar(bar).transitionHidingPixels);
                }
            } else if (positive || timers.get(getParentBar(bar).elementName) > 0) {
                if (getParentBar(bar).uiPosX < getParentBar(bar).uiPosY && getParentBar(bar).uiPosX < 100 && hiddenXs.get(getParentBar(bar).elementName) < 0) { // NEGATIVE X DIRECTION
                    hiddenXs.replace(getParentBar(bar).elementName,
                            hiddenXs.get(getParentBar(bar).elementName) + getParentBar(bar).transitionHidingPixels);
                    if (getParentBar(bar).suddenTransition)
                        hiddenXs.replace(getParentBar(bar).elementName, 0);
                } else if (getParentBar(bar).uiPosX > getParentBar(bar).uiPosY && getParentBar(bar).uiPosX > 100 && hiddenXs.get(getParentBar(bar).elementName) > 0) { // POSITIVE X DIRECTION
                    hiddenXs.replace(getParentBar(bar).elementName,
                            hiddenXs.get(getParentBar(bar).elementName) - getParentBar(bar).transitionHidingPixels);
                    if (getParentBar(bar).suddenTransition)
                        hiddenXs.replace(getParentBar(bar).elementName, 0);
                } else if (getParentBar(bar).uiPosX > getParentBar(bar).uiPosY && getParentBar(bar).uiPosY < 100 && hiddenYs.get(getParentBar(bar).elementName) < 0) { // NEGATIVE Y DIRECTION
                    hiddenYs.replace(getParentBar(bar).elementName,
                            hiddenYs.get(getParentBar(bar).elementName) + getParentBar(bar).transitionHidingPixels);
                    if (getParentBar(bar).suddenTransition)
                        hiddenYs.replace(getParentBar(bar).elementName, 0);
                } else if (getParentBar(bar).uiPosX < getParentBar(bar).uiPosY && getParentBar(bar).uiPosY > 100 && hiddenYs.get(getParentBar(bar).elementName) > 0) { // POSITIVE Y DIRECTION
                    hiddenYs.replace(getParentBar(bar).elementName,
                            hiddenYs.get(getParentBar(bar).elementName) - getParentBar(bar).transitionHidingPixels);
                    if (getParentBar(bar).suddenTransition)
                        hiddenYs.replace(getParentBar(bar).elementName, 0);
                }
                if (timers.get(getParentBar(bar).elementName) < 0)
                    timers.replace(getParentBar(bar).elementName, getParentBar(bar).transitionTime);
            }
            if (timers.get(getParentBar(bar).elementName) > -5)
                timers.replace(getParentBar(bar).elementName, timers.get(getParentBar(bar).elementName) - 1);
            lastTime = System.currentTimeMillis();
        }
        return new int[]{hiddenXs.get(getParentBar(bar).elementName), hiddenYs.get(hiddenXs.get(getParentBar(bar).elementName))};
    }


    static void drawTexturedModalRect(int x, int y, int u, int v, int xSize, int ySize) {
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

    static Configuration.hudWidget getParentBar(Configuration.hudWidget bar) {
        return Configuration.widgets.get(bar.elementName.replace("Background", ""));
    }
}

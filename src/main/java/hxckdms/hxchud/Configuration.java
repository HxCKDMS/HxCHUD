package hxckdms.hxchud;

import hxckdms.hxcconfig.Config;
import hxckdms.hxchud.libraries.Constants;

import java.util.HashMap;

@Config
public class Configuration {
    @Config.comment("Debug Mode Enable? Can cause lag and console spam!")
    public static boolean debugMode;

    public static boolean showValues = true, showPreviousHealth = true, highlightOnDamage = true, showOnlyLastHit, showSaturation,
                    alwaysShowArmorBar;

    public static int previousHealthDecayTimer = 500, maxArmor = 20;

    @Config.category("Custom Resources")
    public static int textureGap = 1;
    @Config.category("Custom Resources")
    public static boolean useCustomPosition, useDrZedsPersonalTexture;

    @Config.category("Custom Resources")
    public static String customResourceLocation = Constants.MOD_ID + ":textures/gui/healthBar.png";

    @Config.category("UIElements")
    public static HashMap<String, hudWidget> widgets = new HashMap<>();

    public static void init(){
        widgets.putIfAbsent("HealthBarBackground", new hudWidget("HealthBarBackground", 4, 4, 0, 0, 88, 10, 29, 5));
        widgets.get("HealthBarBackground").subElements.putIfAbsent("Highlight", new int[]{0, 11});

        widgets.putIfAbsent("HealthBar", new hudWidget("HealthBar", 4, 4, 0, 22, 88, 10, 29, 5));
        widgets.get("HealthBar").subElements.putIfAbsent("MidHealth", new int[]{0, 33});
        widgets.get("HealthBar").subElements.putIfAbsent("LowHealth", new int[]{0, 44});
        widgets.get("HealthBar").subElements.putIfAbsent("PastHealth", new int[]{0, 55});
        widgets.get("HealthBar").subElements.putIfAbsent("RegenHealth", new int[]{0, 66});
        widgets.get("HealthBar").subElements.putIfAbsent("PoisonHealth", new int[]{0, 77});
        widgets.get("HealthBar").subElements.putIfAbsent("WitherHealth", new int[]{0, 88});

        widgets.putIfAbsent("FoodBarBackground", new hudWidget("FoodBarBackground", 4, 19, 0, 0, 88, 10, 29, 20));
        widgets.putIfAbsent("FoodBar", new hudWidget("FoodBar", 4, 19, 0, 99, 88, 10, 29, 20));
        widgets.putIfAbsent("SaturationBarBackground", new hudWidget("SaturationBarBackground", 4, 19, 160, 160, 88, 10, 29, 20));
        widgets.putIfAbsent("SaturationBar", new hudWidget("SaturationBar", 4, 19, 0, 110, 88, 10, 29, 20));
        widgets.putIfAbsent("ArmorBarBackground", new hudWidget("ArmorBarBackground", 4, 34, 0, 0, 88, 10, 29, 35));
        widgets.putIfAbsent("ArmorBar", new hudWidget("ArmorBar", 4, 34, 0, 121, 88, 10, 29, 35));

        widgets.putIfAbsent("HorseHealthBarBackground", new hudWidget("HorseHealthBarBackground", 4, 49, 0, 0, 88, 10, 29, 50));
        widgets.putIfAbsent("HorseHealthBar", new hudWidget("HorseHealthBar", 4, 49, 0, 132, 88, 10, 29, 50));
        widgets.get("HorseHealthBar").subElements.putIfAbsent("MidHealth", new int[]{0, 143});
        widgets.get("HorseHealthBar").subElements.putIfAbsent("LowHealth", new int[]{0, 154});
        widgets.get("HorseHealthBar").subElements.putIfAbsent("PastHealth", new int[]{0, 165});

        widgets.putIfAbsent("HorseArmorBarBackground", new hudWidget("HorseArmorBarBackground", 4, 64, 0, 0, 88, 10, 29, 65));
        widgets.putIfAbsent("HorseArmorBar", new hudWidget("HorseArmorBar", 4, 64, 0, 176, 88, 10, 29, 65));
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class hudWidget {
        public String elementName = "health", color = "0xffffff", imagePath = "";
        public int uiPosX, uiPosY, texturePosX, texturePosY, textureSizeX, textureSizeY, textX, textY;
        public boolean centered = true, verticle = false, showValue = false;
        public HashMap<String, int[]> subElements = new HashMap<>();
        public hudWidget() {}

        public hudWidget(String name, int x, int y, int u, int v, int sizeX, int sizeY, int stringX, int stringY) {
            elementName = name;
            uiPosX = x;
            uiPosY = y;
            texturePosX = u;
            texturePosY = v;
            textureSizeX = sizeX;
            textureSizeY = sizeY;
            textX = stringX;
            textY = stringY;
        }

        public hudWidget(String name, int x, int y, int u, int v, int sizeX, int sizeY, int stringX, int stringY, HashMap<String, int[]> elements) {
            elementName = name;
            uiPosX = x;
            uiPosY = y;
            texturePosX = u;
            texturePosY = v;
            textureSizeX = sizeX;
            textureSizeY = sizeY;
            textX = stringX;
            textY = stringY;
            subElements = elements;
        }

        public hudWidget(String name, int x, int y, int u, int v, int sizeX, int sizeY, int stringX, int stringY, String colour, boolean center, boolean verti, boolean show) {
            elementName = name;
            uiPosX = x;
            uiPosY = y;
            texturePosX = u;
            texturePosY = v;
            textureSizeX = sizeX;
            textureSizeY = sizeY;
            textX = stringX;
            textY = stringY;
            color = colour;
            centered = center;
            verticle = verti;
            showValue = show;
        }
    }
}

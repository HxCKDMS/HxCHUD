package hxckdms.hxchud;

import hxckdms.hxcconfig.Config;
import hxckdms.hxchud.libraries.Constants;

import java.util.HashMap;
import java.util.LinkedList;

@Config
public class Configuration {
    @Config.comment("Debug Mode Enable? Can cause lag and console spam!")
    public static boolean debugMode;

    public static boolean showValues = true, showPreviousHealth = true, highlightOnDamage = true, showOnlyLastHit, customHotbarPosOnly, showAttackIndicator;

    public static int previousHealthDecayTimer = 500, maxArmor = 20;

    @Config.category("Custom Resources")
    public static boolean useCustomPosition, useDrZedsPersonalTexture, useModHotbarTexture = true;

    @Config.category("Custom Resources")
    public static String customResourceLocation = Constants.MOD_ID + ":textures/gui/healthbar.png",
                        customHotbarTexture = Constants.MOD_ID + ":textures/gui/hotbar.png";

    @Config.category("UIElements")
    public static HashMap<String, hudWidget> widgets = new HashMap<>();

    public static void init(){
        LinkedList<Integer> ints = new LinkedList<>();
        widgets.putIfAbsent("HealthBarBackground", new hudWidget("HealthBarBackground", 4, 4, 0, 0, 88, 10, 29, 5));
        ints.add(0);
        ints.add(11);
        widgets.get("HealthBarBackground").subElements.putIfAbsent("Highlight", ints);

        widgets.putIfAbsent("HealthBar", new hudWidget("HealthBar", 4, 4, 0, 22, 88, 10, 29, 5));
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(33);
        widgets.get("HealthBar").subElements.putIfAbsent("MidHealth", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(44);
        widgets.get("HealthBar").subElements.putIfAbsent("LowHealth", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(55);
        widgets.get("HealthBar").subElements.putIfAbsent("PastHealth", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(66);
        widgets.get("HealthBar").subElements.putIfAbsent("RegenHealth", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(77);
        widgets.get("HealthBar").subElements.putIfAbsent("PoisonHealth", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(88);
        widgets.get("HealthBar").subElements.putIfAbsent("WitherHealth", ints);

        widgets.putIfAbsent("FoodBarBackground", new hudWidget("FoodBarBackground", 4, 19, 0, 0, 88, 10, 29, 20));
        widgets.putIfAbsent("FoodBar", new hudWidget("FoodBar", 4, 19, 0, 99, 88, 10, 29, 20));
        widgets.putIfAbsent("SaturationBarBackground", new hudWidget("SaturationBarBackground", 4, 19, 160, 160, 88, 10, 29, 20));
        widgets.putIfAbsent("SaturationBar", new hudWidget("SaturationBar", 4, 19, 0, 110, 88, 10, 29, 20));
        widgets.putIfAbsent("ArmorBarBackground", new hudWidget("ArmorBarBackground", 4, 34, 0, 0, 88, 10, 29, 35, false));
        widgets.putIfAbsent("ArmorBar", new hudWidget("ArmorBar", 4, 34, 0, 121, 88, 10, 29, 35, false));

        widgets.putIfAbsent("HorseHealthBarBackground", new hudWidget("HorseHealthBarBackground", 4, 49, 0, 0, 88, 10, 29, 50));
        widgets.putIfAbsent("HorseHealthBar", new hudWidget("HorseHealthBar", 4, 49, 0, 132, 88, 10, 29, 50));
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(143);
        widgets.get("HorseHealthBar").subElements.putIfAbsent("MidHealth", ints);

        ints = new LinkedList<>();
        ints.add(0);
        ints.add(154);
        widgets.get("HorseHealthBar").subElements.putIfAbsent("LowHealth", ints);

        ints = new LinkedList<>();
        ints.add(0);
        ints.add(154);
        widgets.get("HorseHealthBar").subElements.putIfAbsent("PastHealth", ints);

        widgets.putIfAbsent("HorseArmorBarBackground", new hudWidget("HorseArmorBarBackground", 4, 64, 0, 0, 88, 10, 29, 65, false));
        widgets.putIfAbsent("HorseArmorBar", new hudWidget("HorseArmorBar", 4, 64, 0, 176, 88, 10, 29, 65, false));

        widgets.putIfAbsent("HotbarSelectedSlot", new hudWidget("HotbarSelectedSlot", 149, 245, 0, 22, 23, 23, -9999, -9999));
        widgets.putIfAbsent("Hotbar", new hudWidget("Hotbar", 149, 245, 0, 0, 20, 21, -9999, -9999));
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot1Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot2Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot3Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot4Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot5Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot6Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot7Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot8Icon", ints);
        ints = new LinkedList<>();
        ints.add(0);
        ints.add(0);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot9Icon", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot1Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot2Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot3Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot4Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot5Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot6Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot7Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot8Pos", ints);
        ints = new LinkedList<>();
        ints.add(-100);
        ints.add(-100);
        widgets.get("Hotbar").subElements.putIfAbsent("Slot9Pos", ints);
        widgets.putIfAbsent("Offhand", new hudWidget("Offhand", 345, 245, 0, 0, 20, 21, -9999, -9999));
        widgets.putIfAbsent("ExpBar", new hudWidget("ExpBar", 149, 240, 0, 64, 182, 5, 240, 230));
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class hudWidget {
        public String elementName = "health", color = "0xffffff", imagePath = "";
        public int uiPosX, uiPosY, texturePosX, texturePosY, textureSizeX, textureSizeY, textX, textY,
                    transitionHidingPixels = 1, msHidingSpeed = 50, transitionTime = 240;
        public float fontSize = 0.75f, uiScaleX = 1, uiScaleY = 1;
        public boolean centered = true, verticle = false, showValue = false, alwaysShow = true,
                hideWhenUnchanged, suddenTransition;
        public HashMap<String, LinkedList<Integer>> subElements = new HashMap<>();
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

        public hudWidget(String name, int x, int y, int u, int v, int sizeX, int sizeY, int stringX, int stringY, boolean always) {
            elementName = name;
            uiPosX = x;
            uiPosY = y;
            texturePosX = u;
            texturePosY = v;
            textureSizeX = sizeX;
            textureSizeY = sizeY;
            textX = stringX;
            textY = stringY;
            alwaysShow = always;
        }

        public hudWidget(String name, int x, int y, int u, int v, int sizeX, int sizeY, int stringX, int stringY, boolean always, HashMap<String, LinkedList<Integer>> elements) {
            elementName = name;
            uiPosX = x;
            uiPosY = y;
            texturePosX = u;
            texturePosY = v;
            textureSizeX = sizeX;
            textureSizeY = sizeY;
            textX = stringX;
            textY = stringY;
            alwaysShow = always;
            subElements = elements;
        }

        public hudWidget(String name, int x, int y, int u, int v, int sizeX, int sizeY, int stringX, int stringY, boolean always, String colour, boolean center, boolean verti, boolean show) {
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
            alwaysShow = always;
        }
    }
}

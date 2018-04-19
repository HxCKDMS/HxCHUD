package hxckdms.hxchud;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import hxckdms.hxcconfig.HxCConfig;
import hxckdms.hxcconfig.handlers.SpecialHandlers;
import hxckdms.hxchud.proxy.IProxy;

import java.io.File;

import static hxckdms.hxchud.libraries.Constants.*;
import static hxckdms.hxchud.libraries.GlobalVariables.mainConfig;
import static hxckdms.hxchud.libraries.GlobalVariables.modConfigDir;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod(modid = MOD_ID, name = MOD_NAME, version = VERSION, dependencies = DEPENDENCIES, acceptableRemoteVersions = "*")
public class HxCHP {
    @Mod.Instance(MOD_ID)
    public static HxCHP instance;

    @SidedProxy(clientSide = "hxckdms.hxchud.proxy.ClientProxy", serverSide = "hxckdms.hxchud.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInitialization(FMLPreInitializationEvent event) {
        modConfigDir = new File(event.getModConfigurationDirectory(), "HxCKDMS");
        SpecialHandlers.registerSpecialClass(Configuration.hudWidget.class);

        mainConfig = new HxCConfig(Configuration.class, "HxCHP", modConfigDir, "cfg", MOD_NAME);
        Configuration.init();
        mainConfig.initConfiguration();
        Configuration.init();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void initialization(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInitialization(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}

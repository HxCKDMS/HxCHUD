package hxckdms.hxchud.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import hxckdms.hxchud.event.RenderHPEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {
        RenderHPEvent.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
//        ClientCommandHandler.instance.registerCommand(new ClientCommandReloadConfigs());
        MinecraftForge.EVENT_BUS.register(new RenderHPEvent());
    }
}

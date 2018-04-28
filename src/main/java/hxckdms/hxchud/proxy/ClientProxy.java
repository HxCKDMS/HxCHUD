package hxckdms.hxchud.proxy;

import hxckdms.hxchud.ClientCommandReloadConfigs;
import hxckdms.hxchud.event.*;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {
        Helper.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new ClientCommandReloadConfigs());
        MinecraftForge.EVENT_BUS.register(new RenderHPEvent());
        MinecraftForge.EVENT_BUS.register(new RenderFood());
        MinecraftForge.EVENT_BUS.register(new RenderArmor());
        MinecraftForge.EVENT_BUS.register(new RenderMount());
        MinecraftForge.EVENT_BUS.register(new RenderHotbar());
        MinecraftForge.EVENT_BUS.register(new RenderXP());
    }
}

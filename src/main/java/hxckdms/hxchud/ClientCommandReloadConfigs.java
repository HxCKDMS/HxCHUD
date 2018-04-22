package hxckdms.hxchud;

import hxckdms.hxchud.libraries.GlobalVariables;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ClientCommandReloadConfigs extends CommandBase {
    @Override
    public String getName() {
        return "hxchudreloadconfigs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "reloads the client configs";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        GlobalVariables.mainConfig.initConfiguration();
    }
}

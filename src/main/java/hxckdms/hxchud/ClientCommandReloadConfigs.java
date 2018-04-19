package hxckdms.hxchud;

import hxckdms.hxchud.libraries.GlobalVariables;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class ClientCommandReloadConfigs extends CommandBase {
    @Override
    public String getCommandName() {
        return "hxchpreloadconfigs";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "reloads the client configs";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        GlobalVariables.mainConfig.initConfiguration();
    }
}

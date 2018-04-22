package hxckdms.hxchud;

import hxckdms.hxchud.libraries.GlobalVariables;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ClientCommandReloadConfigs extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "hxchudreloadconfigs";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "reloads the client configs";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        GlobalVariables.mainConfig.initConfiguration();
        sender.addChatMessage(new TextComponentString("Successfully reloaded configs."));
    }
}

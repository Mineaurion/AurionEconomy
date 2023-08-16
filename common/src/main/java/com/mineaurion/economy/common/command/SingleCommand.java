package com.mineaurion.economy.common.command;

import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.List;

public abstract class SingleCommand extends AbstractCommand<Void> {

    public SingleCommand(CommandSpec spec, String name, String permission){
        super(spec, name, permission);
    }

    public SingleCommand(CommandSpec spec, String name, String permission, String adminPermission){
        super(spec, name, permission, adminPermission);
    }

    @Override
    public void execute(EconomyPlugin plugin, Sender sender, Void ignored, List<String> args, String label) throws Exception {
        execute(plugin, sender, args, label);
    }

    public abstract void execute(EconomyPlugin plugin, Sender sender, List<String> args,  String label) throws Exception;
}

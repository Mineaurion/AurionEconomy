package com.mineaurion.economy.common.command;

import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

public abstract class SingleCommand extends AbstractCommand<Void> {

    public SingleCommand(CommandSpec spec, String name, String permission){
        super(spec, name, permission);
    }

    @Override
    public void execute(EconomyPlugin plugin, Sender sender, Void ignored, String[] args, String label) throws Exception {
        execute(plugin, sender, args, label);
    }

    public abstract void execute(EconomyPlugin plugin, Sender sender, String[] args,  String label) throws Exception;
}

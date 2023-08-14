package com.mineaurion.economy.bukkit;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import org.bukkit.command.Command;

import java.io.InputStream;

public final class EconomyBrigadier {

    private EconomyBrigadier() {}

    public static void register(Economy plugin, Command pluginCommand) throws Exception {
        Commodore commodore = CommodoreProvider.getCommodore(plugin.getLoader());
        try(InputStream is = plugin.getBootstrap().getResourceStream("economy.commodore")){
            if(is == null){
                throw new Exception("Brigadier command data missing from jar");
            }
            LiteralCommandNode<?> commandNode = CommodoreFileReader.INSTANCE.parse(is);
            //commodore.register(pluginCommand, commandNode, player -> {
            //    Sender playerAsSender = plugin.getSenderFactory().wrap(player);
            //    return plugin.getCommandManager().hasPermissionForAny(playerAsSender);
            //});
        }
    }
}

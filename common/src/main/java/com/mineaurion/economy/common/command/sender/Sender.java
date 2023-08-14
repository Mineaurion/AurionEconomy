package com.mineaurion.economy.common.command.sender;

import com.mineaurion.economy.common.plugin.EconomyPlugin;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface Sender {

    /** The uuid used by the console sender. */
    UUID CONSOLE_UUID = new UUID(0,0); // 00000000-0000-0000-0000-000000000000
    String CONSOLE_NAME = "Console";

    EconomyPlugin getPlugin();

    String getName();

    UUID getUUID();

    void sendMessage(Component message);

    boolean hasPermission(String permission);

    void performCommand(String commandLine);

    boolean isConsole();

    default boolean isValid(){
        return true;
    }
}

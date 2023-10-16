package com.mineaurion.aurioneconomy.common.command.sender;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import net.kyori.adventure.text.Component;

import java.util.Objects;
import java.util.UUID;

public abstract class SenderFactory<P extends AurionEconomyPlugin, T> implements AutoCloseable {

    private final P plugin;

    public SenderFactory(P plugin){
        this.plugin = plugin;
    }

    protected P getPlugin(){
        return this.plugin;
    }

    protected abstract UUID getUniqueId(T sender);
    protected abstract String getName(T sender);
    protected abstract void sendMessage(T sender, Component message);
    protected abstract boolean hasPermission(T sender, String permission);

    protected abstract boolean isConsole(T sender);

    public final Sender wrap(T sender){
        Objects.requireNonNull(sender, "sender");
        return new AbstractSender<>(this.plugin, this, sender);
    }

    @Override
    public void close() {}
}

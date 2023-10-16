package com.mineaurion.aurioneconomy.sponge;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;

import java.util.UUID;

public class SenderFactory extends com.mineaurion.aurioneconomy.common.command.sender.SenderFactory<AurionEconomy, Audience> {

    public SenderFactory(AurionEconomy plugin){
        super(plugin);
    }

    @Override
    protected String getName(Audience source) {
        if(source instanceof Player){
            return ((Player) source).name();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(Audience source) {
        if(source instanceof Player){
            return ((Player) source).uniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(Audience source, Component message) {
        source.sendMessage(message);
    }

    @Override
    protected boolean hasPermission(Audience source, String permission) {
        if(!(source instanceof Subject)){
            throw new IllegalStateException("Source is not a subject");
        }
        return ((Subject) source).hasPermission(permission);
    }

    @Override
    protected boolean isConsole(Audience sender) {
        return sender instanceof SystemSubject;
    }
}

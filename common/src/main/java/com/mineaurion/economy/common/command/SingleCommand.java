package com.mineaurion.economy.common.command;

import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.plugin.EconomyPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class SingleCommand extends AbstractCommand<Void> {

    public SingleCommand(CommandSpec spec, String name, String permission, Predicate<Integer> argumentCheck){
        super(spec, name, permission, argumentCheck);
    }

    public SingleCommand(CommandSpec spec, String name, String permission, String adminPermission, Predicate<Integer> argumentCheck){
        super(spec, name, permission, adminPermission, argumentCheck);
    }

    @Override
    public void execute(EconomyPlugin plugin, Sender sender, Void ignored, List<String> args, String label) throws Exception {
        execute(plugin, sender, args, label);
    }

    public abstract void execute(EconomyPlugin plugin, Sender sender, List<String> args,  String label) throws Exception;

    @Override
    public void sendUsage(Sender sender, String label) {
        TextComponent.Builder builder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_AQUA))
                .append(Component.space())
                .append(Component.text(getName().toLowerCase(Locale.ROOT), NamedTextColor.GREEN));

        if(getArgs().isPresent()){
            List<Component> argUsages = getArgs().get().stream()
                    .map(Argument::asComponent)
                    .collect(Collectors.toList());

            builder.append(Component.text(" - ", NamedTextColor.DARK_AQUA))
                    .append(Component.join(JoinConfiguration.separator(Component.space()), argUsages));
        }
        sender.sendMessage(builder.build());
    }
}

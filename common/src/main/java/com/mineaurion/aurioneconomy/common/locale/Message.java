package com.mineaurion.aurioneconomy.common.locale;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface Message {

    Component PREFIX_COMPONENT = text()
            .color(GRAY)
            .append(text('['))
            .append(text()
                    .decoration(BOLD, true)
                    .append(text("ECONOMY", AQUA))
            )
            .append(text(']'))
            .build();

    static TextComponent prefixed(ComponentLike component){
        return text()
                .append(PREFIX_COMPONENT)
                .append(space())
                .append(component)
                .build();
    }

    Args0 COMMAND_CONSOLE_CANT = () -> prefixed(text("The console can't do that, maybe you need another argument")).color(RED);
    Args0 COMMAND_NO_PERMISSION = () -> prefixed(text("You do no have permission to use this command")).color(RED);

    Args0 ALREADY_EXECUTING_COMMAND = () -> prefixed(
            text("Another command is being executed, waiting for it to finish")
            .color(GRAY)
    );

    Args2<String, Integer> BALANCE_INFO = (name, amount) -> joinNewline(
            // Player: &f{}
            // Amount: &f{}
            prefixed(text()
                    .color(AQUA)
                    .append(text("- ", WHITE))
                    .append(text("Player: "))
                    .append(text(name, WHITE))
            ),
            prefixed(text()
                    .color(AQUA)
                    .append(text("- ", WHITE))
                    .append(text("Amount: "))
                    .append(text(amount, WHITE))
            )
    );

    Args2<String, Integer> SET_AMOUNT = (name, amount) -> joinNewline(
            prefixed(text()
                    .color(AQUA)
                    .append(text(String.format("The amount %s has been set for %s", amount, name), WHITE))
            )
    );

    Args2<String, Integer> WITHDRAW_AMOUNT = (name, amount) -> joinNewline(
            prefixed(text()
                    .color(AQUA)
                    .append(text(String.format("The amount %s has been subtracted from %s's current balance", amount, name), WHITE))
            )
    );

    Args2<String, Integer> ADD_AMOUNT = (name, amount) -> joinNewline(
            prefixed(text()
                    .color(AQUA)
                    .append(text(String.format("The amount %s has been added from %s's current balance", amount, name), WHITE))
            )
    );

    Args1<String> PLAYER_NOT_FOUND = (player) -> joinNewline(
            prefixed(text()
                    .color(AQUA)
                    .append(text(String.format("The player %s was not found", player), WHITE))
            )
    );

    Args0 NOT_ENOUGH_CURRENCY = () -> prefixed(
            text("You don't have enough in your account to do this action").color(RED)
    );

    Args1<String> PAY_TARGET_NOTEXIST = (target) -> prefixed(
            text(String.format("%s doesn't have an account. You can't pay him", target)).color(RED)
    );

    Args2<Integer, String> PAY_SENDER = (amount, target) -> prefixed(
            text(String.format("The amount %s has been added to %s's account", amount, target)).color(BLUE)
    );

    Args2<Integer, String> PAY_TARGET = (amount, sender) -> prefixed(
            text(String.format("You have received %s from %s", amount, sender)).color(BLUE)
    );

    Args1<String> REQUIRED_ARGUMENT = name -> text()
            .color(DARK_GRAY)
            .append(text('<'))
            .append(text(name, GRAY))
            .append(text('>'))
            .build();

    Args1<String> OPTIONAL_ARGUMENT = name -> text()
            .color(DARK_GRAY)
            .append(text('['))
            .append(text(name, GRAY))
            .append(text(']'))
            .build();


    static Component joinNewline(final ComponentLike... components) {
        return join(JoinConfiguration.newlines(), components);
    }
    interface Args0 {
        Component build();

        default void send(Sender sender) {
            sender.sendMessage(build());
        }
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }

    interface Args2<A0, A1> {
        Component build(A0 arg0, A1 arg1);

        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }
    }
}

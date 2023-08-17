package com.mineaurion.economy.common.command;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum CommandSpec {

    BALANCE("/%s balance <user>",
            arg("user", false)),
    GIVE("/% give <user> <amount>",
            arg("user", true),
            arg("amount", true)
    ),

    WITHDRAW("/%s withdraw <user> <amount>",
            arg("user", true),
            arg("amount", true)
    ),

    ADD("/%s add <user> <amount>",
            arg("user", true),
            arg("amount", true)
    ),

    SET("/%s set <user> <amount>",
            arg("user", true),
            arg("amount", true)
    ),
    PAY("/%s pay <user> <amount>",
            arg("user", true),
            arg("amount", true)
    ),
    CHECK("/%s check <user> <amount> <commands>",
            arg("user", true),
            arg("amount", true),
            arg("commands...", true)
    ),

    TOP("/%s top"),

    ADMIN("/%s admin"),
    LOG("/%s log");

    private final String usage;
    private final List<Argument> args;

    CommandSpec(String usage, PartialArgument... args){
        this.usage = usage;
        this.args = args.length == 0 ? null : Arrays.stream(args)
                .map(builder -> {
                    String key = builder.id.replace(".", "").replace(' ', '-');
                    String description = "economy.usage." + key() + ".argument." + key;
                    return new Argument(builder.name, builder.required, description);
                })
                .collect(Collectors.toList());
    }

    CommandSpec(PartialArgument... args){
        this(null, args);
    }

    public String description(){
        return "economy.usage." + this.key() + ".description";
    }

    public String usage(){
        return this.usage;
    }

    public List<Argument> args(){
        return this.args;
    }

    public String key(){
        return name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    private static PartialArgument arg(String id, String name, boolean required){
        return new PartialArgument(id, name, required);
    }

    private static PartialArgument arg(String name, boolean required){
        return new PartialArgument(name, name, required);
    }

    private static final class PartialArgument {
        private final String id;
        private final String name;
        private final boolean required;

        private PartialArgument(String id, String name, boolean required){
            this.id = id;
            this.name = name;
            this.required = required;
        }
    }
};

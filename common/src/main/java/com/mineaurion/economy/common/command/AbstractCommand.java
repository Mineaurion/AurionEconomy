package com.mineaurion.economy.common.command;

import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.plugin.EconomyPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCommand<T> {

    private final @NonNull CommandSpec spec;
    private final @NonNull String name;

    private final @NonNull String permission;

    private final @Nullable String adminPermission;

    public AbstractCommand(@NonNull CommandSpec spec, @NonNull String name, @NonNull String permission){
        this(
                spec,
                name,
                permission,
                null
        );
    }

    public AbstractCommand(@NonNull CommandSpec spec, @NonNull String name, @NonNull String permission, @Nullable String adminPermission){
        this.spec = spec;
        this.name = name;
        this.permission = permission;
        this.adminPermission = adminPermission;
    }

    // Main execution method for the command.
    public abstract void execute(EconomyPlugin plugin, Sender sender, T target, List<String> args, String label) throws Exception;


    // Tab completion method - default implementation is provided as some commands do not provide tab completions.
    public List<String> tabComplete(EconomyPlugin plugin, String sender, String[] args) {
        return Collections.emptyList();
    }

    public @NonNull CommandSpec getSpec(){
        return this.spec;
    }

    public @NonNull String getName() {
        return this.name;
    }

    public String getDescription(){
        return getSpec().description();
    }

    public String getUsage(){
        String usage = getSpec().usage();
        return usage == null ? "" : usage;
    }

    public @NotNull String getPermission(){
        return permission;
    }

    public Optional<String> getAdminPermission(){
        return Optional.ofNullable(adminPermission);
    }

    public Optional<List<Argument>> getArgs(){
        return Optional.ofNullable(getSpec().args());
    }

    /**
     * Sends a brief command usage message to the Sender.
     * If this command has child commands, the children are listed. Otherwise, a basic usage message is sent.
     */
    public abstract void sendUsage(Sender sender, String label);
}

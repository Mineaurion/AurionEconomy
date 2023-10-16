package com.mineaurion.aurioneconomy.common.command;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.command.tabcomplete.CompletionSupplier;
import com.mineaurion.aurioneconomy.common.command.tabcomplete.TabCompleter;
import com.mineaurion.aurioneconomy.common.commands.*;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.ImmutableCollectors;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.plugin.scheduler.SchedulerAdapter;
import com.mineaurion.aurioneconomy.common.plugin.scheduler.SchedulerTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandManager {

    private final AurionEconomyPlugin plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("economy-command-executor")
            .build()
    );

    private final AtomicBoolean executingCommand = new AtomicBoolean(false);
    private final Map<String, AbstractCommand> mainCommands;

    public CommandManager(AurionEconomyPlugin plugin){
        this.plugin = plugin;
        this.mainCommands = ImmutableList.<AbstractCommand>builder()
                .add(new BalanceInfo())
                .add(new SetAmount())
                .add(new AddAmount())
                .add(new WithdrawAmount())
                .add(new Pay())
                .build()
                .stream()
                .collect(ImmutableCollectors.toMap(c -> c.getName().toLowerCase(Locale.ROOT), Function.identity()));
    }

    public AurionEconomyPlugin getPlugin(){
        return this.plugin;
    }

    @VisibleForTesting
    public Map<String, AbstractCommand> getMainCommands(){
        return this.mainCommands;
    }

    public CompletableFuture<Void> executeCommand(Sender sender, String label, List<String> args){
        UUID uuid = sender.getUUID();

        SchedulerAdapter scheduler = this.plugin.getBootstrap().getScheduler();
        List<String> argsCopy = new ArrayList<>(args);

        if(this.executingCommand.get()){
            Message.ALREADY_EXECUTING_COMMAND.send(sender);
        }

        // ref to the thread being used to exec the cmd
        AtomicReference<Thread> executorThread = new AtomicReference<>();
        // ref to the timeout tasks scheduled to catch if this command take too long to exec
        AtomicReference<SchedulerTask> timeoutTask = new AtomicReference<>();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // flags
            executorThread.set(Thread.currentThread());
            this.executingCommand.set(true);

            try {
                execute(sender, label, args);
            } catch (Throwable e){
                this.plugin.getLogger().severe("Exception whilst executing command: " + args, e);
            } finally {
                // unset flags
                this.executingCommand.set(false);
                executorThread.set(null);

                // Cancel the timeout task
                SchedulerTask timeout;
                if((timeout = timeoutTask.get()) != null){
                    timeout.cancel();
                }
            }
        }, this.executor);

        // schedule another task to catch if the command doesn't complete after 10 seconds
        timeoutTask.set(scheduler.asyncLater(() -> {
            if(!future.isDone()){
                handleCommandTimeout(executorThread, argsCopy);
            }
        }, 10, TimeUnit.SECONDS));
        return future;
    }

    private void handleCommandTimeout(AtomicReference<Thread> thread, List<String> args){
        Thread executorThread = thread.get();
        if(executorThread == null){
            this.plugin.getLogger().warn("Command execution " + args + " has not completed - another command execution is blocking it ?");
        } else {
            String stacktrace = Arrays.stream(executorThread.getStackTrace())
                    .map(el -> " " + el.toString())
                    .collect(Collectors.joining("\n"));
            this.plugin.getLogger().warn("Command execution " + args + " has not completed. Trace: \n" + stacktrace);
        }
    }

    private void execute(Sender sender, String label, List<String> arguments){
        if(arguments.isEmpty() || arguments.size() == 1 && arguments.get(0).trim().isEmpty()){
            sender.sendMessage(Message.prefixed(Component.text()
                    .color(NamedTextColor.DARK_GREEN)
                    .append(Component.text("Running Economy"))
                    .append(Component.space())
            ));
            return;
        }

        AbstractCommand main = this.mainCommands.get(arguments.get(0).toLowerCase(Locale.ROOT));

        if(main == null){
            sendCommandUsage(sender, label);
            return;
        }

        if(main.getArgumentCheck().test(arguments.size())){
            main.sendUsage(sender, label);
            return;
        }

        //Exec command
        try{
            main.execute(this.plugin, sender, arguments, label);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> tabCompleteCommand(Sender sender, List<String> arguments){
        final  List<AbstractCommand> mains = this.mainCommands.values().stream()
                .filter(m -> m.isAuthorized(sender))
                .collect(Collectors.toList());

        return TabCompleter.create()
                .at(0, CompletionSupplier.startsWith(() -> mains.stream().map(c -> c.getName().toLowerCase(Locale.ROOT))))
                .from(1, partial -> mains.stream()
                        .filter(m -> m.getName().equalsIgnoreCase(arguments.get(0)))
                        .findFirst()
                        .map(cmd -> cmd.tabComplete(this.plugin, sender, arguments.subList(1, arguments.size())))
                        .orElse(Collections.emptyList())
                )
                .complete(arguments);
    }

    private void sendCommandUsage(Sender sender, String label) {
        sender.sendMessage(Message.prefixed(Component.text()
                .color(NamedTextColor.DARK_GREEN)
                .append(Component.text("Running "))
                .append(Component.text("Economy", NamedTextColor.AQUA))
        ));

        this.mainCommands.values().stream()
                .filter(c -> c.isAuthorized(sender))
                .forEach(c -> sender.sendMessage(Component.text()
                        .append(Component.text('>', NamedTextColor.DARK_AQUA))
                        .append(Component.space())
                        .append(Component.text(String.format(c.getUsage(), label), NamedTextColor.GREEN))
                        .clickEvent(ClickEvent.suggestCommand(String.format(c.getUsage(), label)))
                        .build()
                ));
    }
}

package com.mineaurion.economy.common.command;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.commands.Balance;
import com.mineaurion.economy.common.locale.Message;
import com.mineaurion.economy.common.misc.ImmutableCollectors;
import com.mineaurion.economy.common.plugin.EconomyPlugin;
import com.mineaurion.economy.common.plugin.scheduler.SchedulerAdapter;
import com.mineaurion.economy.common.plugin.scheduler.SchedulerTask;
import net.kyori.adventure.text.Component;
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

    private final EconomyPlugin plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("economy-command-executor")
            .build()
    );

    private final AtomicBoolean executingCommand = new AtomicBoolean(false);
    private final Map<String, AbstractCommand<?>> mainCommands;

    public CommandManager(EconomyPlugin plugin){
        this.plugin = plugin;
        this.mainCommands = ImmutableList.<AbstractCommand<?>>builder()
                .add(new Balance())
                .build()
                .stream()
                .collect(ImmutableCollectors.toMap(c -> c.getName().toLowerCase(Locale.ROOT), Function.identity()));
    }

    public EconomyPlugin getPlugin(){
        return this.plugin;
    }

    @VisibleForTesting
    public Map<String, AbstractCommand<?>> getMainCommands(){
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
        }

        AbstractCommand<?> main = this.mainCommands.get(arguments.get(0).toLowerCase(Locale.ROOT));

        if(main == null){
            // TODO: change the message bellow
            sender.sendMessage(Component.text("Main is null"));
            return;
        }

        // if(!main.isAuthorized(sender)){
            // sendUsage
        // }
        System.out.println("Argument execute");
        System.out.println(arguments);

        // if(main.getArgumentCheck().test(arguments.size())){
            // sendDetail usage
        // }

        //Exec command
        try{
            main.execute(this.plugin, sender, null, arguments.toArray(new String[0]), label);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}

package com.mineaurion.aurioneconomy.common.command;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.misc.ArgumentTokenizer;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class BrigadierCommandManager<S> extends CommandManager implements Command<S>, SuggestionProvider<S> {

    protected static final String[] COMMAND_ALIASES = new String[]{"money", "economy", "aurion"};

    private final AurionEconomyPlugin plugin;

    protected BrigadierCommandManager(AurionEconomyPlugin plugin){
        super(plugin);
        this.plugin = plugin;
    }

    public abstract Sender getSender(S source);

    public abstract List<String> resolveSelector(S source, List<String> args);

    @Override
    public int run(CommandContext<S> context) throws CommandSyntaxException {
        S source = context.getSource();
        Sender sender = getSender(source);

        int start = context.getRange().getStart();
        String buffer = context.getInput().substring(start);

        List<String> arguments = ArgumentTokenizer.EXECUTE.tokenizeInput(buffer);

        String label = arguments.remove(0);
        if(label.startsWith("/")){
            label = label.substring(1);
        }

        executeCommand(sender, label, arguments);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        S source = context.getSource();
        Sender sender = getSender(source);

        int idx = builder.getStart();

        String buffer = builder.getInput().substring(idx);
        idx += buffer.length();

        List<String> arguments = ArgumentTokenizer.TAB_COMPLETE.tokenizeInput(buffer);

        if(!arguments.isEmpty() && !arguments.isEmpty()){
            idx -= arguments.get(arguments.size() - 1).length();
        }

        List<String> completions = tabCompleteCommand(sender, arguments);

        builder = builder.createOffset(idx);
        for(String completion: completions){
            builder.suggest(completion);
        }
        return builder.buildFuture();
    }
}

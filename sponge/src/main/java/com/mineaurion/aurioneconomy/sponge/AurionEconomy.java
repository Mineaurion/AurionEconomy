package com.mineaurion.aurioneconomy.sponge;

import com.mineaurion.aurioneconomy.common.plugin.AbstractAurionEconomyPlugin;
import com.mineaurion.aurioneconomy.sponge.eco.EconomyService;
import com.mineaurion.aurioneconomy.sponge.eco.TransactionResultImpl;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ProvideServiceEvent;
import org.spongepowered.api.event.lifecycle.RegisterBuilderEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.plugin.PluginContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AurionEconomy extends AbstractAurionEconomyPlugin {

    public final Bootstrap bootstrap;

    private SenderFactory senderFactory;

    private CommandExecutor commandManager;
    public AurionEconomy(Bootstrap bootstrap){
        super(bootstrap.getLogger());
        this.bootstrap = bootstrap;
    }

    @Override
    protected void registerPlatformListeners() {
    }

    @Listener
    public void onRegisterServer(final ProvideServiceEvent<EconomyService> event){
        EconomyService economyService = new EconomyService(this);
        event.suggest(() -> economyService);
    }

    @Listener
    public void onRegisterBuilder(final RegisterBuilderEvent event){
        event.register(TransactionResultImpl.Builder.class, TransactionResultImpl.Builder::new);
    }

    @Override
    protected void registerCommands() {
        this.commandManager = new CommandExecutor(this);
        this.bootstrap.registerListeners(new RegisterCommandsListener(this.bootstrap.getPluginContainer(), this.commandManager));
    }

    public static final class RegisterCommandsListener {
        private final PluginContainer pluginContainer;
        private final Command.Raw command;

        RegisterCommandsListener(PluginContainer pluginContainer, Command.Raw command){
            this.pluginContainer = pluginContainer;
            this.command = command;
        }

        @Listener
        public void onCommandRegister(RegisterCommandEvent<Command.Raw> event){
            event.register(this.pluginContainer, this.command, "economy", "aurion", "money");
        }
    }

    public SenderFactory getSenderFactory(){
        return this.senderFactory;
    }

    public Optional<Server> getServer(){
        return this.bootstrap.getServer();
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new SenderFactory(this);
    }

    @Override
    public Optional<UUID> lookupUUID(String username) {
        return getServer().flatMap(server -> server.gameProfileManager().profile(username)
                .thenApply(p -> Optional.of(p.uniqueId()))
                .exceptionally(x -> Optional.empty())
                .join()
        );
    }

    @Override
    public Optional<String> lookupUsername(UUID uuid) {
        return getServer().flatMap(server -> server.gameProfileManager().profile(uuid)
                .thenApply(GameProfile::name)
                .exceptionally(x -> Optional.empty())
                .join()
        );
    }

    @Override
    public Collection<String> getPlayersList() {
        return getServer()
                .map(server -> server.onlinePlayers().stream().map(ServerPlayer::name).collect(Collectors.toList()))
                .orElse(Collections.emptyList()
        );
    }

    @Override
    public void sendMessageToSpecificPlayer(UUID uuid, Component message) {
        Optional<ServerPlayer> player = getServer().flatMap(server -> server.player(uuid));
        player.ifPresent(p -> p.sendMessage(message));
    }

    @Override
    public Bootstrap getBootstrap() {
        return this.bootstrap;
    }

}

package com.mineaurion.aurioneconomy.common.config;

import com.google.common.base.Splitter;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ConfigurateConfigAdapter implements ConfigurationAdapter {

    private final AurionEconomyPlugin plugin;
    private final Path path;
    private ConfigurationNode root;

    public ConfigurateConfigAdapter(AurionEconomyPlugin plugin, Path path){
        this.plugin = plugin;
        this.path = path;
        reload();
    }

    protected abstract ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path);

    @Override
    public void reload() {
        ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(this.path);
        try {
            this.root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigurationNode resolvePath(String path){
        if(this.root == null){
            throw new RuntimeException("Config is not loaded");
        }
        return this.root.node(Splitter.on('.').splitToList(path).toArray());
    }

    @Override
    public String getString(String path, String def) {
        return resolvePath(path).getString(def);
    }

    @Override
    public int getInteger(String path, int def) {
        return resolvePath(path).getInt(def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.virtual()) {
            return def;
        }
        return node.childrenMap().entrySet().stream().collect(
                Collectors.toMap(k -> k.getKey().toString(), v -> v.getValue().getString())
        );
    }

    @Override
    public AurionEconomyPlugin getPlugin() {
        return this.plugin;
    }
}

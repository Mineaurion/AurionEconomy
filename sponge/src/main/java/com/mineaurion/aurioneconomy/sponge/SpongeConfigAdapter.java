package com.mineaurion.aurioneconomy.sponge;

import com.mineaurion.aurioneconomy.common.config.ConfigurateConfigAdapter;
import com.mineaurion.aurioneconomy.common.config.ConfigurationAdapter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.nio.file.Path;

public class SpongeConfigAdapter extends ConfigurateConfigAdapter implements ConfigurationAdapter {

    public SpongeConfigAdapter(AurionEconomy plugin, Path path){
        super(plugin, path);
    }

    @Override
    protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path) {
        return HoconConfigurationLoader.builder().path(path).build();
    }
}

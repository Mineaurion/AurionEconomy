package com.mineaurion.aurioneconomy.bukkit;

import com.mineaurion.aurioneconomy.common.plugin.scheduler.AbstractJavaScheduler;
import com.mineaurion.aurioneconomy.common.plugin.scheduler.SchedulerAdapter;

import java.util.concurrent.Executor;

public class BukkitSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {

    private final Executor sync;

    public BukkitSchedulerAdapter(Bootstrap bootstrap){
        super(bootstrap);
        this.sync = r -> bootstrap.getServer().getScheduler().scheduleSyncDelayedTask(bootstrap, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}

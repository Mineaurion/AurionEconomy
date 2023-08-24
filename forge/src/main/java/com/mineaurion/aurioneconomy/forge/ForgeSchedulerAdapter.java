package com.mineaurion.aurioneconomy.forge;

import com.mineaurion.aurioneconomy.common.plugin.scheduler.AbstractJavaScheduler;

import java.util.concurrent.Executor;

public class ForgeSchedulerAdapter extends AbstractJavaScheduler {

    private final Executor sync;

    public ForgeSchedulerAdapter(Bootstrap bootstrap){
        super(bootstrap);
        this.sync = r -> bootstrap.getServer().orElseThrow(() -> new IllegalStateException("Server not ready")).executeBlocking(r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}

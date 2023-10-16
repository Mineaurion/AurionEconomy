package com.mineaurion.aurioneconomy.fabric;

import com.mineaurion.aurioneconomy.common.plugin.scheduler.AbstractJavaScheduler;

import java.util.concurrent.Executor;

public class FabricSchedulerAdapter extends AbstractJavaScheduler {

    private final Executor sync;

    public FabricSchedulerAdapter(Bootstrap bootstrap){
        super(bootstrap);
        this.sync = r -> bootstrap.getServer().orElseThrow(() -> new IllegalStateException("Server not ready")).submitAndJoin(r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}

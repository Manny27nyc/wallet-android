/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package se.grunka.fortuna.accumulator;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventSchedulerImpl implements EventScheduler {
    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private final int sourceId;
    private final Map<Integer, Context> eventContexts;
    private final ScheduledExecutorService scheduler;

    public EventSchedulerImpl(int sourceId, Map<Integer, Context> eventContexts, ScheduledExecutorService scheduler) {
        this.sourceId = sourceId;
        this.eventContexts = eventContexts;
        this.scheduler = scheduler;
    }

    @Override
    public void schedule(long delay, TimeUnit timeUnit) {
        scheduled.set(true);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                Context context = eventContexts.get(sourceId);
                scheduled.set(false);
                context.source.event(context.scheduler, context.adder);
                if (!scheduled.get()) {
                    scheduler.schedule(this, 0, TimeUnit.MILLISECONDS);
                }
            }
        }, delay, timeUnit);
    }
}

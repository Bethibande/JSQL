package com.bethibande.jsql.cache;

import com.bethibande.jsql.JSQL;
import com.bethibande.jsql.SQLTable;

import java.util.concurrent.atomic.AtomicInteger;

public class CacheUpdateThread extends Thread {

    public static AtomicInteger instances = new AtomicInteger();

    private final JSQL owner;

    private final long timeout = 1000;

    public CacheUpdateThread(JSQL owner) {
        super("CacheUpdateThread-" + instances.getAndIncrement());
        super.setDaemon(true);
        this.owner = owner;
    }

    public void run() {
        while(true) {
            /*for(SQLTable<?> table : this.owner.getTables()) {
                if(table.isUseCache()) {
                    table.getCache().update();
                }
            }*/
            this.owner.updateCache();
            try {
                sleep(this.timeout);
            } catch(InterruptedException e) {}
        }
    }

}

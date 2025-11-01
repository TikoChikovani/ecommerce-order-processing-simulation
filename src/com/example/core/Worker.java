package com.example.core;

import com.example.model.Warehouse;
import com.example.model.Order;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Worker implements Runnable {
    private final BlockingQueue<Order> ordersList;
    private final Warehouse warehouse;
    private final List<Order> processedOrders;
    private volatile boolean running = true;

    public Worker(BlockingQueue<Order> ordersList, Warehouse warehouse, List<Order> processedOrders) {
        this.ordersList = ordersList;
        this.warehouse = warehouse;
        this.processedOrders = processedOrders;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Order order = ordersList.poll(100, TimeUnit.MILLISECONDS);
                if (order != null) {
                    if (warehouse.processOrder(order)) {
                        synchronized (processedOrders) {
                            processedOrders.add(order);
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;            }
        }
    }

    public void stop() {
        running = false;
    }
}

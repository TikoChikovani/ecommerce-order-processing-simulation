package com.example.core;

import com.example.model.Reservation;
import com.example.model.Warehouse;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReservationProcessor implements Runnable {
    private final BlockingQueue<Reservation> reservationsQueue;
    private final Warehouse warehouse;
    private volatile boolean running = true;
    private final Random random = new Random();
    private int processed = 0;
    private int cancelled = 0;

    public ReservationProcessor(BlockingQueue<Reservation> reservationsQueue, Warehouse warehouse) {
        this.reservationsQueue = reservationsQueue;
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Reservation reservation = reservationsQueue.poll(100, TimeUnit.MILLISECONDS);

                if (reservation != null) {
                    processed++;

                    Thread.sleep(random.nextInt(50));

                    if (random.nextDouble() < 0.3) {
                        if (warehouse.cancelReservation(reservation.getReservationId())){
                            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                            cancelled++;
                        }
                    } else {
                        System.out.println("Keeping reservation active " + reservation.getReservationId());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public int getProcessed() {
        return processed;
    }

    public int getCancelled() {
        return cancelled;
    }
}

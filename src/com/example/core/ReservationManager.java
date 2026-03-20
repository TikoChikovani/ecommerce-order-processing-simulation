package com.example.core;

import com.example.model.Product;
import com.example.model.Reservation;
import com.example.model.Warehouse;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ReservationManager implements Runnable{
    private final String customerId;
    private final Warehouse warehouse;
    private final List<Product> productList;
    private final BlockingQueue<Reservation> reservationQueue;
    private final int reservationCount;
    private final Random random = new Random();


    public ReservationManager(String customerId, Warehouse warehouse, List<Product> productList, BlockingQueue<Reservation> reservationQueue, int reservationCount) {
        this.customerId = customerId;
        this.warehouse = warehouse;
        this.productList = productList;
        this.reservationQueue = reservationQueue;
        this.reservationCount = reservationCount;
    }

    @Override
    public void run() {
        for(int i = 0; i < reservationCount; i++){
            Reservation reservation = new Reservation(customerId + "-" + i);

            int itemCount = random.nextInt(5) + 1;
            for (int j = 0; j < itemCount; j++) {
                Product product = productList.get(random.nextInt(productList.size()));
                int quantity = random.nextInt(5) + 1;
                reservation.addProduct(product, quantity);
            }

            if (warehouse.createReservation(reservation)) {
                try {
                    reservationQueue.put(reservation);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            try {
                Thread.sleep(random.nextInt(50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

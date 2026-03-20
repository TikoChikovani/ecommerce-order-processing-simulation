package com.example.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Warehouse {
    public final ConcurrentHashMap<Product, Integer>  inventory = new ConcurrentHashMap<>();

    public final ConcurrentHashMap<String, Map<Product, Integer>>  reservations = new ConcurrentHashMap<>();

    public Warehouse(List<Product> products) {
        for (Product product : products) {
            this.inventory.put(product, product.getQuantity());
        }
    }

    public synchronized boolean createReservation(Reservation reservation) {
        Map<Product, Integer>  items = reservation.getReservedItems();
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int requestedQty = entry.getValue();
            Integer availableQty = inventory.get(product);

            if (availableQty == null || availableQty < requestedQty) {
                System.out.println("Reservation for product " + product.getName() + " failed");
                return false;
            }
        }

        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.compute(product, (k, v) -> v - quantity);
            product.decreaseQuantity(quantity);
        }

        reservations.put(reservation.getReservationId(), new ConcurrentHashMap<>(items));
        System.out.println("Reservation created "  + reservation.getReservationId() + " for customer " + reservation.getCustomerId());
        return true;
    }

    public synchronized boolean cancelReservation(String reservationId) {
        Map<Product, Integer>  reservedItems = reservations.remove(reservationId);

        if (reservedItems == null) {
            System.out.println("Reservation not found for "  + reservationId);
            return false;
        }

        for (Map.Entry<Product, Integer> entry : reservedItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.compute(product, (k, v) -> v + quantity);
            product.increaseQuantity(quantity);
        }

        System.out.println("Reservation cancelled for "  + reservationId);
        return true;
    }

    public synchronized boolean convertReservationToOrder(String reservationId, Order order) {
        Map<Product, Integer>  reservedItems = reservations.get(reservationId);

        if (reservedItems == null) {
            System.out.println("Reservation not found for "  + reservationId);
            return false;
        }

        double orderPrice = 0;
        for (Map.Entry<Product, Integer> entry : reservedItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            orderPrice += product.getPrice() * quantity;
        }

        order.setOrderPrice(orderPrice);
        System.out.println("Reservation converted to order: "  + reservationId);
        return true;
    }

    public boolean processOrder(Order order) {
        for (ConcurrentHashMap.Entry<Product, Integer> entry : order.getOrderItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            if (product.getQuantity() < quantity) {
                return false;
            }
        }

        double orderPrice = 0;
        for (ConcurrentHashMap.Entry<Product, Integer> entry : order.getOrderItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.compute(product, (k, v) -> v - quantity);
            product.decreaseQuantity(quantity);
            orderPrice += product.getPrice() * quantity;
        }

        order.setOrderPrice(orderPrice);
        return true;
    }

    public int getAvailableQuantity(Product product) {
        return inventory.getOrDefault(product, 0);
    }

    public int getTotalReservedQuantity(Product product) {
        return reservations.values().stream()
                .mapToInt(reservation -> reservation.getOrDefault(product,0))
                .sum();
    }

    public Map<String, Map<Product, Integer>> getActiveReservations() {
        return new ConcurrentHashMap<>(reservations);
    }
}

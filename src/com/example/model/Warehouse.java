package com.example.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Warehouse {
    public ConcurrentHashMap<Product, Integer>  inventory = new ConcurrentHashMap<>();

    public Warehouse(List<Product> products) {
        for (Product product : products) {
            this.inventory.put(product, product.getQuantity());
        }
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
}

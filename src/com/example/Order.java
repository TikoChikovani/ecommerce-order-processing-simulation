package com.example;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private final String orderID;
    private final Map<Product, Integer> orderItems;
    private double orderPrice;

    public Order(String orderID) {
        this.orderID = orderID;
        this.orderItems = new HashMap<>();
        this.orderPrice = 0;
    }

    public void addProduct(Product product, int quantity) {
        orderItems.put(product, quantity);
    }

    public String getOrderID() {
        return orderID;
    }

    public Map<Product, Integer> getOrderItems() {
        return orderItems;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }
}

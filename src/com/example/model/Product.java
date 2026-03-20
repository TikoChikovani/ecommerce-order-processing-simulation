package com.example.model;

import java.util.Objects;

public class Product {
    private final String name;
    private final double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public synchronized int getQuantity() {
        return quantity;
    }

    public synchronized void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public synchronized void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product product)) {
            return false;
        }
        return Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}

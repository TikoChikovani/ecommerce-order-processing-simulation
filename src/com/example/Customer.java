package com.example;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Customer implements Runnable {
    private final String customerName;
    private final BlockingQueue<Order> orders;
    private final List<Product> productsList;
    private final int orderCount;
    private final Random random = new Random();


    public Customer(String customerName, BlockingQueue<Order> orders, List<Product> productsList, int orderCount) {
        this.customerName = customerName;
        this.orders = orders;
        this.productsList = productsList;
        this.orderCount = orderCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.orderCount; i++) {
            Order order = new Order(customerName + "-" + i);
            int itemCount = random.nextInt(10) + 1;
            for (int j = 0; j < itemCount; j++) {
                Product product = productsList.get(random.nextInt(productsList.size()));
                int quantity = random.nextInt(10) + 1;
                order.addProduct(product, quantity);
            }
            orders.add(order);
        }
    }
}

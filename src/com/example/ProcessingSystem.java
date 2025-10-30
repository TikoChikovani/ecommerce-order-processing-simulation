package com.example;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProcessingSystem {
    public static void main(String[] args) throws InterruptedException {
        List<Product> productList = Arrays.asList(
                new Product("Laptop", 939.99, 50),
                new Product("Mouse", 29.69, 100),
                new Product("Keyboard", 75.99, 80),
                new Product("Monitor", 239.59, 40),
                new Product("Headphones", 189.99, 60)
        );

        Warehouse warehouse = new Warehouse(productList);
        BlockingQueue<Order> ordersQueue = new LinkedBlockingQueue<>();
        List<Order> processedOrders = Collections.synchronizedList(new ArrayList<>());

        ExecutorService customerService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++) {
            customerService.submit(new Customer("com.example.Customer" + i, ordersQueue, productList, 10));
        }

        ExecutorService workerService = Executors.newFixedThreadPool(5);
        List<Worker> workers =new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Worker worker = new Worker(ordersQueue, warehouse, processedOrders);
            workers.add(worker);
            workerService.submit(worker);
        }

        customerService.shutdown();
        customerService.awaitTermination(10, TimeUnit.SECONDS);

        while (!ordersQueue.isEmpty()) {
            Thread.sleep(100);
        }

        workers.forEach(Worker::stop);
        workerService.shutdown();
        workerService.awaitTermination(5, TimeUnit.SECONDS);

        runAnalytics(processedOrders);
    }

    private static void runAnalytics(List<Order> processedOrders) {
        long totalOrders = processedOrders.parallelStream().count();

        double totalProfit = processedOrders.parallelStream().mapToDouble(Order::getOrderPrice).sum();

        Map<String, Long> productSales = processedOrders.parallelStream()
                .flatMap(order -> order.getOrderItems().entrySet().stream())
                .collect(Collectors.groupingByConcurrent(entry -> entry.getKey().getName(),
                        Collectors.summingLong(Map.Entry::getValue)));

        List<Map.Entry<String, Long>> topThreeProducts = productSales.entrySet().parallelStream()
                .sorted(Map.Entry.<String, Long> comparingByValue().reversed())
                .limit(3).toList();

        System.out.println("Total Orders Processed: " + totalOrders);
        System.out.println("Total Profit: " + totalProfit);
        System.out.println("\nTop 3 Best-Selling Products:");
        for (int i = 0; i < topThreeProducts.size(); i++) {
            Map.Entry<String, Long> entry = topThreeProducts.get(i);
            System.out.printf("%d. %s - %d units sold\n", i + 1, entry.getKey(), entry.getValue());
        }

    }
}

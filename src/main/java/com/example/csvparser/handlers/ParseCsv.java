package com.example.csvparser.handlers;

import com.example.csvparser.model.PageVisitModel;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.SneakyThrows;

public class ParseCsv {

    private DbHandler dbHandler;
    private BlockingQueue<PageVisitModel> dataQueue;
    private BlockingQueue<PageVisitModel> insertQueue;
    private ExecutorService executor;
    private PageVisitConsumer consumer;
    private static final int DATA_QUEUE_CAPACITY = 100;
    private static final int INSERT_QUEUE_CAPACITY = 1000;
    private static final int NUM_CONSUMERS = 10;

    public ParseCsv() {
        dbHandler = new DbHandler();
        dbHandler.deleteAll();
        dataQueue = new LinkedBlockingDeque<>(DATA_QUEUE_CAPACITY);
        consumer = new PageVisitConsumer(dataQueue, dbHandler);
        executor =
                Executors.newFixedThreadPool(NUM_CONSUMERS);
        /*new ThreadPoolExecutor(0, numConsumers, 0, TimeUnit.SECONDS, new SynchronousQueue<>());*/
    }

    public long getUniquePageVisitCount() {
        spawnConsumers(executor);
        produce();
        shutDownConsumers();
        return getCount();
    }

    @SneakyThrows
    private void shutDownConsumers() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }
    }

    private long getCount() {
        Optional<Long> count = dbHandler.getCount();
        return count.isPresent() ? count.get() : -1;
    }

    private void produce() {
        PageVisitProducer producer =
                new PageVisitProducer(dataQueue, getFilePath());
        producer.produce();
        producer.poisonConsumers(NUM_CONSUMERS);
    }

    private String getFilePath() {
        //TODO: Accept from user
        return "./src/test/resources/huge.csv";
    }

    private void spawnConsumers() {
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            Thread t = new Thread(consumer);
            t.start();

        }
    }

    private void spawnConsumers(ExecutorService executor) {
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            executor.submit(consumer);

        }
    }
}

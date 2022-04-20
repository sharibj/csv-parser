package com.example.csvparser.parsers;

import com.example.csvparser.handlers.DbHandler;
import com.example.csvparser.handlers.FileHandler;
import com.example.csvparser.model.PageVisitModel;
import com.example.csvparser.prosumers.PageVisitConsumer;
import com.example.csvparser.prosumers.PageVisitProducer;

import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import lombok.SneakyThrows;

public class ParseCsv {

    //region - constants
    private static final String DEFAULT_FILE_PATH = "./src/main/resources/test.csv";
    public static final String JDBC_URL = "jdbc:h2:file:./db/proddb";
    private static final int DATA_QUEUE_CAPACITY = 1000;
    private static final int NUM_CONSUMERS = 10;
    private static final Logger logger = Logger.getLogger(ParseCsv.class.getName());
    //endregion - constants

    //region - members
    private DbHandler dbHandler;
    private BlockingQueue<PageVisitModel> dataQueue;
    private ExecutorService executor;
    private PageVisitConsumer consumer;
    //endregion - members

    public ParseCsv() {
        dbHandler = new DbHandler(JDBC_URL);
        dataQueue = new LinkedBlockingDeque<>(DATA_QUEUE_CAPACITY);
        consumer = new PageVisitConsumer(dataQueue, dbHandler);
    }

    @SneakyThrows
    public void run() {
        try {
            while (true) {
                String filePath = getFilePath();
                if (filePath.equalsIgnoreCase("exit")) {
                    break;
                }
                logger.info("Using file: " + filePath);
                FileHandler.getFileIterator(filePath)
                           .ifPresent(this::printUniquePageVisitCount);
            }
        } finally {
            dbHandler.close();
        }
    }

    private void printUniquePageVisitCount(LineIterator lineIterator) {
        printOutput(getUniquePageVisitCount(lineIterator));
    }

    private void printOutput(long count) {
        System.out.println("-------------------------------------------");
        System.out.println("Unique Page Visit Count = " + count);
        System.out.println("-------------------------------------------");
    }


    private long getUniquePageVisitCount(LineIterator lineIterator) {
        initParser();
        parse(lineIterator);
        return getCount();
    }

    private void parse(LineIterator lineIterator) {
        spawnConsumers(executor);
        produce(lineIterator);
    }

    private void initParser() {
        executor = Executors.newFixedThreadPool(NUM_CONSUMERS);
        dbHandler.deleteAll();
        dataQueue.clear();
    }

    @SneakyThrows
    private void shutDownConsumers() {
        logger.info("shutting down consumers");
        poisonConsumers();
        executor.shutdown();
        executor.awaitTermination(NUM_CONSUMERS, TimeUnit.SECONDS);
    }

    private long getCount() {
        logger.info("Computing unique count");
        return dbHandler.getCount();
    }

    private void produce(LineIterator iterator) {
        PageVisitProducer producer =
                new PageVisitProducer(dataQueue, iterator);
        logger.info("Producing messages");
        try {
            producer.produce();
        } finally {
            shutDownConsumers();
        }
    }


    @SneakyThrows
    public void poisonConsumers() {
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            dataQueue.put(PageVisitModel.getPoison());
        }
    }

    @SneakyThrows
    private String getFilePath() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(
                "\nPlease enter the file path or \npress enter to use default or\ntype 'exit' to end program:\n");
        String inputFilePath = reader.readLine();
        String filePath =
                inputFilePath != null && !inputFilePath.isEmpty()
                ? inputFilePath : DEFAULT_FILE_PATH;
        return filePath;
    }

    private void spawnConsumers(ExecutorService executor) {
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            executor.submit(consumer);
        }
        logger.info(NUM_CONSUMERS + " consumers spawned");
    }
}

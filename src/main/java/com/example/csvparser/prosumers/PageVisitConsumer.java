package com.example.csvparser.prosumers;

import com.example.csvparser.handlers.DbHandler;
import com.example.csvparser.model.PageVisitModel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageVisitConsumer implements Runnable {

    private static final Logger logger = Logger.getLogger(PageVisitConsumer.class.getName());
    private final BlockingQueue<PageVisitModel> dataQueue;
    private DbHandler dbHandler;
    private static final int BATCH_SIZE = 1000;

    public PageVisitConsumer(BlockingQueue<PageVisitModel> dataQueue, DbHandler dbHandler) {
        this.dataQueue = dataQueue;
        this.dbHandler = dbHandler;
    }

    @Override
    public void run() {
        logger.info("Running consumer");
        consume();
    }

    public void consume() {
        Set<String> messages = new HashSet<>();
        while (true) {
            try {
                PageVisitModel message = dataQueue.take();
                if (message.isPoison()) {
                    if (!messages.isEmpty()) {
                        dbHandler.insert(messages);
                    }
                    logger.info("Terminating consumer.");
                    break;
                }
                useMessage(message, messages);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Consumer couldn't read from data queue. Terminated.", e);
                break;
            }
        }
    }

    private void useMessage(PageVisitModel message, Set<String> messages) {
        if (message.isValid()) {
            messages.add(message.getEmail() + message.getPhone());
            if (messages.size() > BATCH_SIZE) {
                dbHandler.insert(messages);
                messages.clear();
            }
        }
    }

}

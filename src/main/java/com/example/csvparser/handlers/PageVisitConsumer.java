package com.example.csvparser.handlers;

import com.example.csvparser.model.PageVisitModel;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageVisitConsumer implements Runnable {

    private static final Logger logger = Logger.getLogger(PageVisitConsumer.class.getName());
    private final BlockingQueue<PageVisitModel> dataQueue;
    private DbHandler dbHandler;

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
        while (true) {
            try {
                PageVisitModel message = dataQueue.take();
                if (message.isPoison()) {
                    logger.info("Terminating consumer.");
                    break;
                }
                useMessage(message);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Consumer couldn't read from data queue. Terminated.", e);
                break;
            }
        }
    }

    private void useMessage(PageVisitModel message) {
        if (message.isValid()) {
            dbHandler.insert(message);
        }
    }

}

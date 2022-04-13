package com.example.csvparser.handlers;

import com.example.csvparser.model.PageVisitModel;

import java.util.concurrent.BlockingQueue;

public class PageVisitConsumer implements Runnable {
    private final BlockingQueue<PageVisitModel> dataQueue;
    //TODO: Make thread safe?
    private DbHandler dbHandler;

    public PageVisitConsumer(BlockingQueue<PageVisitModel> dataQueue, DbHandler dbHandler) {
        this.dataQueue = dataQueue;
        this.dbHandler = dbHandler;
    }

    @Override
    public void run() {
        consume();
    }

    public void consume() {
        while (true) {
            try {
                PageVisitModel message = dataQueue.take();
                if (message.getEmail() == null) {
                    break;
                }
                useMessage(message);
            } catch (InterruptedException e) {
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

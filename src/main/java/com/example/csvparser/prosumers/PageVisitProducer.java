package com.example.csvparser.prosumers;

import com.example.csvparser.model.PageVisitModel;

import org.apache.commons.io.LineIterator;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageVisitProducer {

    private static final Logger logger = Logger.getLogger(PageVisitProducer.class.getName());
    private static final boolean SKIP_HEADER = true;
    private final BlockingQueue<PageVisitModel> pageVisitQueue;
    private LineIterator iterator;

    public PageVisitProducer(BlockingQueue<PageVisitModel> pageVisitQueue, LineIterator iterator) {
        this.pageVisitQueue = pageVisitQueue;
        this.iterator = iterator;
        if (SKIP_HEADER && this.iterator.hasNext()) {
            this.iterator.nextLine();
        }
    }

    public void produce() {
        while (iterator.hasNext()) {

            Optional<PageVisitModel> message = generateMessage();
            if (message.isPresent()) {
                try {
                    pageVisitQueue.put(message.get());
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Producer interrupted. Terminating.", e);
                    break;
                }
            }
        }
    }

    public Optional<PageVisitModel> generateMessage() {
        Optional<PageVisitModel> message = Optional.empty();
        if (iterator.hasNext()) {
            String line = iterator.nextLine();
            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                message = Optional.of(new PageVisitModel(tokens[0], tokens[1], tokens[2]));
            }
        }
        return message;
    }
}

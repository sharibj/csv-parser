package com.example.csvparser.handlers;

import com.example.csvparser.model.PageVisitModel;

import org.apache.commons.io.LineIterator;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import lombok.SneakyThrows;

public class PageVisitProducer {
    private final BlockingQueue<PageVisitModel> pageVisitQueue;
    private Optional<LineIterator> iterator;

    public PageVisitProducer(BlockingQueue pageVisitQueue, String filePath) {
        this.pageVisitQueue = pageVisitQueue;
        iterator = FileHandler.getFileIterator(filePath);
        //skip header
        if (iterator.isPresent() && iterator.get().hasNext()) {
            iterator.get().nextLine();
        }
    }

    public void produce() {
        if (iterator.isPresent()) {
            //read table
            while (iterator.get().hasNext()) {

                Optional<PageVisitModel> message = generateMessage();
                if (message.isPresent()) {
                    try {
                        pageVisitQueue.put(message.get());
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }

    public Optional<PageVisitModel> generateMessage() {
        Optional<PageVisitModel> message = Optional.empty();
        if (iterator.isPresent() && iterator.get().hasNext()) {
            String line = iterator.get().nextLine();
            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                message = Optional.of(new PageVisitModel(tokens[0], tokens[1], tokens[2]));
            }
        }
        return message;
    }

    @SneakyThrows
    public void poisonConsumers(int numConsumers) {
        for (int i = 0; i < numConsumers; i++) {
            pageVisitQueue.put(new PageVisitModel(null, null, null));
        }
    }
}

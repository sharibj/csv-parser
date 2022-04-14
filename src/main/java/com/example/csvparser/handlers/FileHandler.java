package com.example.csvparser.handlers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());

    private FileHandler() {
    }

    public static Optional<LineIterator> getFileIterator(String filePath) {
        try {
            return Optional.of(FileUtils.lineIterator(new File(filePath), "UTF-8"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't read the file", e);
            return Optional.empty();
        }
    }
}

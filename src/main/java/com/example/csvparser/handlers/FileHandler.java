package com.example.csvparser.handlers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileHandler {
    public static Optional<LineIterator> getFileIterator(String filePath) {
        try {
            return Optional.of(FileUtils.lineIterator(new File(filePath), "UTF-8"));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

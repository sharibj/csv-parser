package com.example.csvparser.handlers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;

public class FileHandler {
    public LineIterator getFileIterator(String filePath) throws IOException {
        return FileUtils.lineIterator(new File(filePath), "UTF-8");
    }
}

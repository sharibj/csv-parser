package com.example.csvparser.handlers;

import com.example.csvparser.FileWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql()
class FileHandlerTest {


    @Test
    void testGetFileIterator() {
        Assertions.assertEquals("Hello World!",
                                FileHandler.getFileIterator("./src/test/resources/test.txt").get()
                                           .nextLine());
    }

    //    @Test
    void createFile() {
        FileWriter.writeCsv("./src/test/resources/generated.csv");
    }
}
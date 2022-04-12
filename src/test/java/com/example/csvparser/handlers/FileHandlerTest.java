package com.example.csvparser.handlers;

import org.apache.commons.io.LineIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;

@Sql()
class FileHandlerTest {

    FileHandler subject = new FileHandler();

    @Test
    void testGetFileIterator() throws IOException {
        LineIterator iterator = subject.getFileIterator("./src/test/resources/test.txt");
        Assertions.assertEquals("Hello World!", iterator.nextLine());
    }
}
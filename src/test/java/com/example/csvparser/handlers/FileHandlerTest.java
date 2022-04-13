package com.example.csvparser.handlers;

import org.apache.commons.io.LineIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.Optional;

@Sql()
class FileHandlerTest {


    @Test
    void testGetFileIterator() throws IOException {
        Optional<LineIterator> iterator =
                FileHandler.getFileIterator("./src/test/resources/test.txt");
        Assertions.assertEquals("Hello World!", iterator.get().nextLine());
    }
}
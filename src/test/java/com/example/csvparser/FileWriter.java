package com.example.csvparser;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

public class FileWriter {
    private static final String HEADER = "email,phone,source";
    private static CsvSettings csvSettings = new CsvSettings();

    @SneakyThrows
    private static void writeData(String data, FileOutputStream fileOutputStream) {
        fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
        fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static void writeCsv(String filePath) {
        long startTime = System.nanoTime();
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath))) {

            if (csvSettings.isHasHeader()) {
                writeData(HEADER, fileOutputStream);
                for (int i = 0; i < csvSettings.getNumValid(); i++) {
                    Random random = new Random();
                    String randomToken = "" + random.nextLong();
                    writeData("xyz@duplicate.com_" + randomToken
                                      + ",1234567890_" + randomToken
                                      + ",google.com",
                              fileOutputStream);
                }
                for (int i = 0; i < csvSettings.getNumInvalid(); i++) {
                    writeData("7561t\t\ty2egwqdk,19782iteyqwgdkha", fileOutputStream);
                }
                for (int i = 0; i < csvSettings.getNumBlank(); i++) {
                    writeData("", fileOutputStream);
                }
                for (int i = 0; i <= csvSettings.getNumDup(); i++) {
                    writeData("xyz@duplicate.com,12345,google.com", fileOutputStream);
                }
            }
        }
        long endTime = System.nanoTime();
        float duration = (float) (endTime - startTime) / 1000000000;

        System.out.println("\n-------------------------------------------\n");
        System.out.println("Time taken to generate file in sec = " + duration);
        System.out.println("\n-------------------------------------------\n");
    }

}

@Getter
@Setter
@NoArgsConstructor
class CsvSettings {
    private long numDup = 26345678;
    private long numValid = 2444555;
    private long numInvalid = 223456;
    private long numBlank = 22345;
    private boolean hasHeader = true;
}
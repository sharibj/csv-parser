package com.example.csvparser;

import com.example.csvparser.handlers.ParseCsv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsvParserApplication {


	public static void main(String[] args) {
		SpringApplication.run(CsvParserApplication.class, args);
		ParseCsv parser = new ParseCsv();
		long startTime = System.nanoTime();
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Unique Page Visit Count = " +
						   parser.getUniquePageVisitCount());
		long endTime = System.nanoTime();
		float duration = (float) (endTime - startTime) / 1000000;
		System.out.println("Time taken to compute in msec = " + duration);
		System.out.println("\n-------------------------------------------\n");

	}
}

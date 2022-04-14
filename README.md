# csv-parser
A small project to parse large csv files and get unique count

The entire file never gets loaded in the memory at once.
1 producer reads the file line by line, generates a message and adds it to a blocking queue.
At the same time, N (configurable) consumers read from the queue, validate the message, and then try to insert a valid message into the database. 
The database has some constraints in place to reject duplicate entries. 
It's an in-memory database that stores the record on the disk.
Once the producer finishes, all the consumers get poisoned.
Once all consumers terminate, we get all the valid lines of the csv file in the database.
The unique count at this point is simply the total count of rows in the table.
The database gets purged on every run, so the count always represents the count of the current file.


## Test result
- File size = 1.3GB
- Total rows = 29 Million rows
- Time taken to compute = 55min

**Note:** Can be made faster using hashcodes. For now, this is listed in the "Future tweaks".
##Steps to execute
Go to the project directory and run the blow command

`./mvnw clean install`


`./mvnw compile exec:java
`
#### OR

Import the maven project in your IDE and run the `CsvParserApplication` class

## How to use
Once executed, you are prompted to enter the file path in the CLI.
Just enter the full (or relative) path and press enter.
The program runs in a loop so if you want to exit, simply type 'exit' instead of file path.
The output gets simply printed in the console at the end of each parsing cycle.


## Assumptions
#### CSV File
- Csv file has data in this format:
`email,phone,source`
- First line of the csv file is a header.
- Each line should have at least 3 fields. Any extra fields will be ignored.
- All fields are mandatory. If not provided, the line will be skipped.
- Uniqueness gets decided based on the first 2 fields (email and phone number).
- Any empty, invalid or duplicate lines will be skipped.

## Highlights
- Producer Consumer pattern
- Only 1 Producer since the data gets read from a file on the disk
- Many consumers that consume and persist the data in parallel
- Relying on the database for checking uniqueness
- Database (H2) specific optimisations like using truncate instead of delete and using count(\*) since H2 has optimised count(\*)
- Includes a small util to generate large csv files for testing purposes

## Future tweaks
- Using hashcodes to improve the performance and possibly get rid of the need of a database
- Same solution supporting tables of different sizes and keys
- Better exception management and fault tolerance
- Much more test coverage

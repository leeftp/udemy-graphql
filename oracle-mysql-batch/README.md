# Oracle to MySQL Batch Project

This is a simple Spring Boot project that demonstrates how to transfer delta data from an Oracle table to a MySQL table every hour.

## Building

```bash
mvn clean package
```

## Running

Set the data source properties in `src/main/resources/application.properties` and run:

```bash
java -jar target/oracle-mysql-batch-0.0.1-SNAPSHOT.jar
```

By default, the job is triggered once every hour via Spring's scheduling.

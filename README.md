## Parsing a huge JSON with Micronaut+Jackson 


PoC to read a huge file with 10 millions of Json objects 

At startup the application generates a dummy text file with a lot of jsons

When requested, a controller will read this file and parse every element "row by row" instead to reading all the file

## Test it

- grab the repo

- execute

`./gradlew run`

After a few seconds, the application will be ready. A new `test.json` file was created with millions of records

TIP: you can test with more (or less) number of records changing `MAX_ITEMS = 10_000_000;` at `Application.java`

- test

open `localhost:8080/parse/test.json` and after a few seconds you can see the sum of all `amounts` objects

INFO: every time you run the application the file is recreated so the result will be different

## Objetive

We want to parse a big file (> 1Gb) with an array of objects

These objects are represented by two classes (only to have fun)

`MyModel` and `SubModel` 

We want to sum all the `amounts` but as the size of the file we can't read it in memory so we'll parse it "on the fly"

We'll use Jackson libraries + Micronaut

## Main part

Using reactive functions we'll read the file token by token and mapping every object to our Model.

Next we'll emit the `amount`

Finally, we'll apply a reduce function to sum all amounts

```java
return Flux.<Long>generate(sink -> {
                    try {
                        var nextToken = parser.nextToken();
                        if (nextToken != JsonToken.START_OBJECT) {
                            sink.complete();
                            return;
                        }

                        var model = parser.readValueAs(MyModel.class);
                        sink.next((long)model.amount);

                    } catch (Exception e) {
                        LOG.error("Error ", e);
                        sink.error(e);
                    }

                })
                .onBackpressureBuffer()
                .reduce(0L, Long::sum);
```

## GET vs POST

In this PoC we are using a local file and a GET method to read it, it's not difficult to add a POST method to 
upload the file and run the function
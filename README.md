## Parsing a huge JSON with Micronaut+Jackson 

PoC to read a huge file with 10 millions of Json objects 

## Objetive

We want to parse a big file (> 1Gb) with an array of objects

These objects are represented by two classes (only to have a more interesting use case)

`MyModel` and `SubModel`

- We want to sum all the `amounts` but as the size of the file we can't read it in memory so we'll parse it "on the fly"
- Also, we want to stream all records in the file applying a filter for minimum amount


## Test it

- grab the repo

- execute

`./gradlew run`

## Generate a test file

Once the application is ready, you can generate a test file:

`curl localhost:8080/generate/test.json?max=10_000_000`

After a few seconds, a new `test.json` file will be created with MAX random records. 

Everyone will contain an incremental `amount` attribute (to generate predictable results).
So, for example, if you create a file with three records, the sum of all amounts will be 3+2+1=6  

TIP: you can test with more (or less) number of records changing `max` request param

## Sum all amounts

`curl localhost:8080/parse/sum/test.json`

## Stream the file

`curl localhost:8080/parse/stream/test.json`

Stream-only records with a minimum amount:

`curl localhost:8080/parse/stream/test.json?min=2_231_321`

## Main part

Using reactive functions, we'll read the file token by token and mapping every object to our Model.

```java
return Flux.<MyModel>generate(sink -> {
                    try {
                        var nextToken = parser.nextToken();
                        if (nextToken != JsonToken.START_OBJECT) {
                            sink.complete();
                            return;
                        }

                        var model = parser.readValueAs(MyModel.class);
                        sink.next(model);

                    } catch (Exception e) {
                        LOG.error("Error ", e);
                        sink.error(e);
                    }
                })
                .cache(1_000);
```

## Micronaut vs Jackson

I don't know if Micronaut is able to stream the file in this way so I'm using the Jackson parser to iterate over
the file and convert every JsonNode into a MyModel object.

Once we have the MyModel we can use, for example, the Serdeable feature of Micronaut to render everyone in the Http
response.


## GET vs POST

In this PoC we are using local files generated in local by a GET request, but it's not difficult to add a POST method to 
upload the file and perform the same logic
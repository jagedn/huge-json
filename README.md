## Parsing a huge JSON with Micronaut+Jackson 


PoC to read a huge file with 10 millions of Json objects 

At startup the application generates a dummy text file with a lot of jsons

When requested, a controller will read this file and parse every element "row by row" instead to reading all the file


# Project 1: Currency exchange calculator

Application to convert one currency to another.

## User Stories
- User can select currency to and from for conversion.
- User can enter amount to convert.
- User can get result in selected currencies.
- User can see exchanges history.

## Tools & Technologies
- Java 8+
- Maven
- JUnit
- Slfj4
- Cassandra DB
- Sprint Framework

## Getting Started

Clone repository:

    In your terminal type:
    git clone 'https://github.com/SakuraMatrix/P1-JesusEsquer.git'

Change directory

    In your terminal type:
    cd P1-JesusEsquer/

Add your API key from RapidAPI

    - h -> h.add("x-rapidapi-key", "APIKEY")

Compile

    In your terminal type:
    mvn compile

Run

    In your terminal type:
    mvn exec:java

This will start the application.

###End Points

    -GET "/exchanges" - List all exchanges history

    -GET "/exchanges/id={id}" - List exchange with the given id

    -DELETE "/exchanges/delete/id={id}" - Delete exchange with the given id

    -GET "/currencies" - Get all currencies available

    -GET "/convert/to={to}/from={from}/amount={amount}" - convert to, from and desire amount

## Future goals

- User can log in and save session.

## Acknowledgments

Thanks to our teacher Mehrab and teammates!
2021 Sakura Matrix P1

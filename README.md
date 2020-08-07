# REST API Demo

This repo is for a REST API demo, built using Kotlin, Gradle for build management/dependencies, Ktor web server, and HikariCP / Kotlin Exposed / H2 for the presistence layer.

## How To

1. Clone this repo
2. To use Gradle to run the app from the command line, open a terminal and go to the root project folder and enter
```/.gradlew run``` to run the app.
3. To use gradle to run the unit tests from the command line, open a terminal and go to the root project folder and enter ```/.gradlew test``` to run the unit tests.
4. With the app running, visit ```http://localhost:8080/contacts``` with a browser and the app will return a JSON test record built into the app init that shows API spec conformity.

### To Dos / Comments
1. Data cleaning/validation: the routing layer, or the persistence layer needs more input/data validation to protect the app and database from bad/erroneous data e.g. email address validation, empty strings, etc. (Alternatively, a service layer could implement this task).
2. Data model: the single database table could be broken up such that there are Contact, Address, and Phone tables, which would allow 0 to many inputs for e.g. phone type and number, and increased flexibility for the API.
4. Unit testing: All the routes should be tested (currently there are 3 route tests), and the persistence layer needs testing (e.g. DatabaseFactory), including the H2 database / exposed implementation e.g. methods tested with mock databases(s).
3. API Spec: Decisions about the API spec / existing data model should be made and then implemented as such, e.g.
    - Could a "Contact" have more than one phone number of the same type
    - Could a "Contact" have more than one address
    - What fields could be blank or null

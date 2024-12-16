# mini-bank-system

## Description

Provides a mini bank system that allows to create, update and search the account owners (bank customers) via API.

### Uses:

* [**Java** 17](https://www.oracle.com/java/technologies/downloads/)

* [**Spring-boot** 3+](https://spring.io/projects/spring-boot)

* [**Gradle** 8+](https://docs.gradle.org/8.0/release-notes.html)


# Application local environment

1. Open terminal

2. Download project `gh repo clone https://github.com/elisiejuskumiscius/mini-bank-system`

3. Set Project SDK to Java 17

4. Build project `gradlew build`

5. Run application `gradlew bootRun`

## Request examples

### Create
curl --request POST \
--url http://localhost:8080/customers/create/1 \
--header 'Content-Type: application/json' \
--data '{
"name": "Michael",
"lastname": "Scott",
"phoneNumber": "1234567890",
"email": "michael.scott@example.com",
"type": "PRIVATE",
"addresses": [
{
"street": "123 Gediminas Street",
"city": "Vilnius",
"postalCode": "12345"
},
{
"street": "456 Laisves Avenue",
"city": "Kaunas",
"postalCode": "67890"
}]}'

### Update

curl --request PATCH \
--url http://localhost:8080/customers/update/1 \
--header 'Content-Type: application/json' \
--data '{
"name": "Dwight",
"lastname": "Schrute",
"phoneNumber": "1234567890",
"email": "dwight.schrute@example.com",
"type": "INDIVIDUAL",
"addresses": [
{
"id": 1,
"street": "Vokieciu Street",
"city": "Vilnius",
"postalCode": "12345"
},
{
"id": 2,
"street": "456 Vilniaus Avenue",
"city": "Druskininkai",
"postalCode": "67890"
},
{
"street": "Juru st. 11",
"city": "Klaipdeda",
"postalCode": "14789"
}]}'

### Search
curl --request GET \
--url 'http://localhost:8080/customers/search?searchTerm=michael&page=0&size=10' \

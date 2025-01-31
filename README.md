# How to deploy 
## Start the broker:
    docker-compose up -d broker
## Build modules
    ./gradlew Calculator:build
    ./gradlew RestAPI:build
## Start modules
    docker-compose up -d calculator
    docker-compose up -d calculator-rest-api
## Check routes
    localhost:8080/sum?a=2&b=2
    localhost:8080/subtraction?a=2&b=2
    localhost:8080/multiplication?a=2&b=2
    localhost:8080/division?a=2&b=2

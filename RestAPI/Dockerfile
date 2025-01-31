FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY ./gradlew ./gradlew
COPY ./build.gradle ./build.gradle
COPY ./settings.gradle ./settings.gradle
COPY ./gradle ./gradle

COPY ./src ./src

RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

COPY ./build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
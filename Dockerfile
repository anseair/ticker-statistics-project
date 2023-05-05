FROM openjdk:11-jre-slim

WORKDIR /app

COPY ./target/ticker-statistic-project-0.0.1-SNAPSHOT.jar ./ticker-statistic.jar

CMD ["java", "-jar", "/app/ticker-statistic.jar"]
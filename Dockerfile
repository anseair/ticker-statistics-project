FROM openjdk:11-jre-slim

WORKDIR /app

COPY ./target/ticker-statistic-project-0.0.1-SNAPSHOT.jar ./ticker-statistic-project.jar

ENV MONGODB_URI=mongodb+srv://Anastasiya:1234@clusterprojectjava2022.sniqmob.mongodb.net/firstproject?retryWrites=true&w=majority

CMD ["java", "-jar", "/app/ticker-statistic-project.jar"]
FROM openjdk:11-jre-slim

WORKDIR /app

COPY ./target/financial-data-0.0.1-SNAPSHOT.jar ./backend-project.jar

ENV MONGODB_URI=mongodb+srv://Gena:1234@clusterjava2022.kqxogdm.mongodb.net/forum?retryWrites=true&w=0

CMD [ "java", "-jar", "/app/backend-project.jar"]
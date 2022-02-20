FROM gradle:7-jdk17-alpine as build

COPY . .

RUN gradle build

FROM openjdk:17-alpine
EXPOSE 8080

WORKDIR /app

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=build /home/gradle/build/libs/cheer-with-me.jar cheer-with-me.jar

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "cheer-with-me.jar"]

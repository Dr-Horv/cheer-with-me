FROM gradle:6.9-jdk11-alpine as build

COPY . .

RUN gradle build

FROM openjdk:8-jre-alpine
EXPOSE 8080

WORKDIR /app

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=build /home/gradle/build/libs/cheer-with-me.jar cheer-with-me.jar

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "cheer-with-me.jar"]

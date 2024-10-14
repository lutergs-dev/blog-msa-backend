FROM gradle:8.10.2-jdk21 AS builder

RUN mkdir /build
WORKDIR /build
COPY ../ /build

RUN ./gradlew User:bootJar

FROM amazoncorretto:21-alpine-jdk

COPY --from=builder /build/User/build/libs/*.jar /app.jar

ENV TIMEZONE=Asia/Seoul
ENV ACTIVE_PROFILE=server

ENTRYPOINT ["sh", "-c",
    "java \
    $JVM_OPTION \
    -jar \
    -Duser.timezone=$TIMEZONE \
    -Dspring.profiles.active=$ACTIVE_PROFILE \
    $JVM_SYSTEM_PROPERTY \
    /app.jar"
]
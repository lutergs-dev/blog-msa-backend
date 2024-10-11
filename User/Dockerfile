FROM amazoncorretto:21-alpine-jdk

ARG VERSION

COPY ./build/libs/User-$VERSION.jar /app.jar

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
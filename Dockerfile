FROM alpine:latest
WORKDIR /usr/src/jamesbot

RUN apk update && apk add openjdk8-jre && rm -rf /var/cache/apk/*

COPY target/*-with-dependencies.jar ./jamesbot.jar

CMD ["java", "-jar", "jamesbot.jar"]
FROM gradle:4.10.2-jdk8-alpine as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM openjdk:8-jre-alpine
RUN apk add --no-cache tini
ENTRYPOINT ["/sbin/tini", "--"]
COPY --from=builder /home/gradle/build/deps/external/*.jar /data/
COPY --from=builder /home/gradle/build/deps/fint/*.jar /data/
COPY --from=builder /home/gradle/build/libs/fint-provider-*.jar /data/fint-provider.jar
CMD java $JAVA_OPTS -XX:+ExitOnOutOfMemoryError -jar /data/fint-provider.jar

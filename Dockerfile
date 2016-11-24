FROM java:8
ADD build/libs/fint-provider-*.jar /data/app.jar
CMD ["java", "-jar", "/data/app.jar"]
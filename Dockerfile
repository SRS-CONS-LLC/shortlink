FROM openjdk:17
COPY build/libs/Bookstores-0.0.1-SNAPSHOT.jar bookstores-app.jar
CMD ["java", "-jar", "bookstores-app.jar"]
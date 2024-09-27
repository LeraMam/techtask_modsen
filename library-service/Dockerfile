FROM eclipse-temurin:17-jre-alpine
COPY target/*.jar /library-service/app.jar
ENTRYPOINT ["java", "-jar", "/library-service/app.jar"]
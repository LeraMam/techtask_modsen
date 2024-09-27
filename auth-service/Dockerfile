FROM eclipse-temurin:17-jre-alpine
COPY target/*.jar /auth-service/app.jar
ENTRYPOINT ["java", "-jar", "/auth-service/app.jar"]
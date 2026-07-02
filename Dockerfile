FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring
COPY build/libs/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9292

ENTRYPOINT ["java","-jar","app.jar"]
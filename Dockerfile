# -------- build stage --------
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# быстрее, если сначала скопировать только файлы сборки
COPY build.gradle* settings.gradle* gradlew gradlew.bat /app/
COPY gradle /app/gradle
RUN ./gradlew --no-daemon dependencies || true

# потом код
COPY . /app
RUN ./gradlew --no-daemon clean bootJar

# -------- runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# копируем fat-jar
COPY --from=build /app/build/libs/*.jar /app/app.jar

# важно: запуск именно java -jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

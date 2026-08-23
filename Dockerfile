# Build multietapa: Render no necesita Maven instalado, lo trae la primera capa.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Las dependencias se resuelven antes de copiar el codigo: mientras el pom no
# cambie, Docker reutiliza esta capa y el build tarda segundos en vez de minutos.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/appmanagepets-1.0.0.jar app.jar

# El plan gratuito da 512MB de RAM y 0.1 CPU. Sin estos limites la JVM calcula
# un heap demasiado grande para el contenedor y el proceso muere por OOM.
# SerialGC porque con 0.1 CPU un GC paralelo solo se pelea consigo mismo.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

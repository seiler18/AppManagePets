# Build multietapa: Render no necesita Maven instalado, lo trae la primera capa.
# Etiquetas con la distro fijada (-noble): si la base cambiara de Ubuntu a otra
# cosa por debajo, el build seguiria "funcionando" con otra imagen. No se fija
# por digest a proposito: eso congelaria tambien los parches de seguridad.
FROM maven:3.9-eclipse-temurin-17-noble AS build
WORKDIR /app

# Las dependencias se resuelven antes de copiar el codigo: mientras el pom no
# cambie, Docker reutiliza esta capa y el build tarda segundos en vez de minutos.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre-noble
WORKDIR /app

# Usuario sin privilegios: la imagen base corre como root, y cualquier fallo
# explotable en la app o en Tomcat daria root dentro del contenedor. La app no
# necesita escribir en disco ni abrir puertos bajos, asi que no pierde nada.
# El jar queda de root y solo legible: sin --chown a proposito, para que el
# proceso no pueda reescribir su propio codigo.
RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /app/target/appmanagepets-1.0.0.jar app.jar
USER app

# El plan gratuito da 512MB de RAM y 0.1 CPU. Sin estos limites la JVM calcula
# un heap demasiado grande para el contenedor y el proceso muere por OOM.
# SerialGC porque con 0.1 CPU un GC paralelo solo se pelea consigo mismo.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

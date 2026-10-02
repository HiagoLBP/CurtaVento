# Estágio 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia dependências primeiro para cache do Docker
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copia código e compila sem rodar os testes
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Estágio 2: Runtime enxuto
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria usuário sem privilégios de root por segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia apenas o JAR final
COPY --from=build /app/target/*.jar app.jar

# Otimização de memória para o limite de 512MB dos planos gratuitos
ENV JAVA_OPTS="-XX:+UseSerialGC -Xss512k -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

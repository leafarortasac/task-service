# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copia e instala os contratos compartilhados primeiro
# Isso garante que a dependência esteja disponível para o task-service
COPY shared-contracts/ ./shared-contracts/
RUN mvn -f shared-contracts/pom.xml clean install -DskipTests

# 2. Prepara o Task-Service
WORKDIR /app/task-service
COPY task-service/pom.xml .
RUN mvn dependency:go-offline

# 3. Copia o código fonte e gera o jar
COPY task-service/src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o jar gerado no estágio anterior
COPY --from=build /app/task-service/target/*.jar app.jar

# Configurações de fuso horário para Manaus
RUN apk add --no-cache tzdata
ENV TZ=America/Manaus

EXPOSE 8081

# Execução com otimização de memória
ENTRYPOINT ["java", "-Xmx512m", "-Duser.timezone=America/Manaus", "-jar", "app.jar"]
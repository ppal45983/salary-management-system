# ============================================================================
# ACME Salary Management System - Dockerfile
# Multi-stage production container build
# ============================================================================

# Stage 1: Build Angular Frontend
FROM node:18-alpine AS frontend-builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

# Stage 2: Build Spring Boot Backend
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-builder
WORKDIR /build
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 3: Final Production Runtime Container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache nginx curl

# Copy Backend JAR
COPY --from=backend-builder /build/target/*.jar app.jar

# Copy Frontend Build to Nginx
COPY --from=frontend-builder /app/dist/salary-management-system /usr/share/nginx/html

EXPOSE 80 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

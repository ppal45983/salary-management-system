# Spring Boot Configuration - Salary Management System

## Application Properties Template

Create an `application.properties` file in `backend/src/main/resources/`:

```properties
# Application Configuration
spring.application.name=salary-management-system
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/salary_management_system
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pool Configuration (HikariCP)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Logging Configuration
logging.level.root=INFO
logging.level.com.sms=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-change-this-in-production-environment
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=http://localhost:4200
cors.allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true

# Swagger/OpenAPI Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.api-docs.path=/api-docs
springdoc.api-docs.enabled=true

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## Alternative: application.yml

```yaml
spring:
  application:
    name: salary-management-system
  datasource:
    url: jdbc:mysql://localhost:3306/salary_management_system
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    root: INFO
    com.sms: DEBUG
    org.springframework.web: INFO
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

jwt:
  secret: your-super-secret-jwt-key-change-this-in-production-environment
  expiration: 86400000  # 24 hours in milliseconds

cors:
  allowed-origins: http://localhost:4200
  allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
  allowed-headers: "*"
  allow-credentials: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
  api-docs:
    path: /api-docs
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

## Development Setup

1. Create MySQL database:
```bash
mysql -u root -p -e "CREATE DATABASE salary_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

2. Copy configuration template:
```bash
cp application.properties.template backend/src/main/resources/application.properties
```

3. Update with your local database credentials

4. Run migrations:
```bash
mvn flyway:migrate
```

## Production Configuration

For production deployment:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.hikari.maximum-pool-size=50
logging.level.root=WARN
logging.level.com.sms=INFO
jwt.secret=${JWT_SECRET}
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
```

Use environment variables to inject sensitive credentials.

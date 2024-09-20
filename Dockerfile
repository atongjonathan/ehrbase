# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S ehrbase && adduser -S ehrbase -G ehrbase

USER ehrbase

WORKDIR /app

# Assuming ehrbase.jar is in the same directory as the Dockerfile
COPY ehrbase.jar /app/ehrbase.jar
COPY --chown=ehrbase:ehrbase /docker-entrypoint.sh /app/docker-entrypoint

RUN chown -R ehrbase:ehrbase /app && \
    chmod +x /app/docker-entrypoint

EXPOSE 8080

# Wrapped in entrypoint to be able to accept CLI args and use jacoco CLI env var
ENTRYPOINT ["/app/docker-entrypoint"]


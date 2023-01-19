FROM amazoncorretto:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG FRONT_JAR_FILE=front/build/libs/boot-app.jar
ARG BACK_JAR_FILE=back/build/libs/boot-app.jar
ARG CLIENT_JAR_FILE=client/build/libs/boot-app.jar
ARG CALCULATOR_JAR_FILE=calculator/build/libs/boot-app.jar
COPY ${FRONT_JAR_FILE} front-app.jar
COPY ${BACK_JAR_FILE} back-app.jar
COPY ${CLIENT_JAR_FILE} client.jar
COPY ${CALCULATOR_JAR_FILE} calculator.jar
COPY main.sh main.sh
ENTRYPOINT ["/bin/sh","main.sh"]

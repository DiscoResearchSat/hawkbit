FROM amazoncorretto:17-alpine
EXPOSE 8080

COPY hawkbit-update-server.jar /
COPY application-users.properties /

RUN set -x \
    && apk add --no-cache --virtual libressl 

VOLUME "/data"
WORKDIR "/"
ENTRYPOINT ["java","-jar","hawkbit-update-server.jar","-Xms768m -Xmx768m -XX:MaxMetaspaceSize=250m -XX:MetaspaceSize=250m -Xss300K -XX:+UseG1GC -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+HeapDumpOnOutOfMemoryError", "--spring.profiles.active=users,discosat"]

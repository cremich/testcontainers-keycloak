FROM jboss/keycloak:4.6.0.Final

ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin
ENV KEYCLOAK_IMPORT=/tmp/realm-export.json

ADD realm-export.json /tmp/realm-export.json
ADD create-keycloak-user.sh /opt/jboss/create-keycloak-user.sh

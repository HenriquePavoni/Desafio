FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM registry.redhat.io/jboss-eap-8/eap8-openjdk17-builder-openshift-rhel8:latest AS eap-builder
ENV GALLEON_PROVISION_FEATURE_PACKS=org.jboss.eap:wildfly-ee-galleon-pack,org.jboss.eap.cloud:eap-cloud-galleon-pack
ENV GALLEON_PROVISION_LAYERS=cloud-default-config
ENV GALLEON_PROVISION_CHANNELS=org.jboss.eap.channels:eap-8.0
RUN /usr/local/s2i/assemble

FROM registry.redhat.io/jboss-eap-8/eap8-openjdk17-runtime-openshift-rhel8:latest
COPY --from=eap-builder --chown=jboss:root $JBOSS_HOME $JBOSS_HOME
COPY --from=build --chown=jboss:root /app/target/autorizacao.war $JBOSS_HOME/standalone/deployments/
RUN chmod -R ug+rwX $JBOSS_HOME
EXPOSE 8080

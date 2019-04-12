FROM jboss/wildfly:latest
COPY /target/WebAppPrueba-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments

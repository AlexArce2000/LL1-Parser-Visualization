FROM tomcat:10.1-jdk17

# Limpiar apps de ejemplo de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar tu WAR como ROOT.war para que sirva en la raíz "/"
COPY target/LL1.war /usr/local/tomcat/webapps/ROOT.war

ENV PORT=8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
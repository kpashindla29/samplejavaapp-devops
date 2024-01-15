FROM ubuntu:latest

LABEL maintainer="Kishore Pashindla <kpashindla@albanybeck.com>"

# Set environment variables
ENV TOMCAT_VERSION 9.0.80
ENV CATALINA_HOME /opt/tomcat
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $CATALINA_HOME/bin:$PATH

# Install JDK & wget packages.
RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install openjdk-11-jdk wget

# Install and configure Tomcat.
RUN mkdir $CATALINA_HOME

RUN wget https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tar.gz

RUN cd /tmp && tar xvfz tomcat.tar.gz

RUN cp -Rv /tmp/apache-tomcat-${TOMCAT_VERSION}/* $CATALINA_HOME

RUN rm -rf /tmp/apache-tomcat-${TOMCAT_VERSION}

RUN rm -rf /tmp/tomcat.tar.gz

COPY target/sampleapp.war /opt/tomcat/webapps
EXPOSE 8085
#CMD /usr/local/tomcat/bin/catalina.sh run
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
#

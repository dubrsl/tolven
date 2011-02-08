#!/bin/bash

set -ue
# The java.security.egd option may be required especially on linux systems. At least in jdk1.6_10 (uncertain how far back), one of
# the providers (SunJSSE?) is defaulting to using file:///dev/random which can lead to severe if not permanent blocking when 
# retrieving random numbers. For demo purposes the following line can be uncommented to use psuedo-random numbers, but for production
# it may be necessary to invest in a hardware random number generator

export JAVA_OPTS="-Djavax.net.ssl.keyStore=/usr/local/tolven-config/credentials/dev.able.com/keystore.jks -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=/usr/local/tolven-config/credentials/dev.able.com/cacerts.jks -Djavax.net.ssl.trustStorePassword=tolven -Djava.security.egd=file:///dev/urandom -Dcom.iplanet.am.cookie.c66Encode=true -XX:MaxPermSize=256m"

./startup.sh

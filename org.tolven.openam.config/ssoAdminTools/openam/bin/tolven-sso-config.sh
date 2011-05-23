#!/bin/bash

set -ue

java -Djavax.net.ssl.keyStore=/usr/local/tolven-config/credentials/dev.able.com/keystore.jks -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=/usr/local/tolven-config/credentials/dev.able.com/cacerts.jks -Djavax.net.ssl.trustStorePassword=tolven -jar configurator.jar -f tolvenconfiguration

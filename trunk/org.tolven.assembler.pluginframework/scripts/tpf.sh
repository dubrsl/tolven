#!/bin/bash

. ./tpfenv.sh
java -Djavax.net.ssl.keyStore=$TOLVEN_CONFIG_DIR/credentials/dev.able.com/keystore.jks -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=$TOLVEN_CONFIG_DIR/credentials/dev.able.com/cacerts.jks -Djavax.net.ssl.trustStorePassword=tolven -Dsun.lang.ClassLoader.allowArraySyntax=true -jar ../pluginLib/tpf-boot.jar $*

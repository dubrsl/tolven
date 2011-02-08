#!/bin/bash

export TOLVEN_USER=admin
echo TOLVEN_USER=$TOLVEN_USER
export TOLVEN_REALM=tolven
echo TOLVEN_REALM=$TOLVEN_REALM
export TOLVEN_PASSWORD=sysadmin
echo TOLVEN_PASSWORD=xxxxxxxx
export TOLVEN_CONFIG_DIR=$TOLVEN_CONFIG
echo TOLVEN_CONFIG=$TOLVEN_CONFIG

./tpf.sh -version
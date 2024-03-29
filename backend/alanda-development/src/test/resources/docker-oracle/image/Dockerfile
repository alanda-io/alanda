# LICENSE UPL 1.0
#
# Copyright (c) 1982-2016 Oracle and/or its affiliates. All rights reserved.
#
# ORACLE DOCKERFILES PROJECT
# --------------------------
# This is the Dockerfile for Oracle Database 11g Release 2 Express Edition
# 
# REQUIRED FILES TO BUILD THIS IMAGE
# ----------------------------------
# (1) oracle-xe-11.2.0-1.0.x86_64.rpm.zip
#     Download Oracle Database 11g Release 2 Express Edition for Linux x64
#     from http://www.oracle.com/technetwork/database/database-technologies/express-edition/downloads/index.html
#
# HOW TO BUILD THIS IMAGE
# -----------------------
# Put the downloaded file in the same directory as this Dockerfile
# Run: 
#      $ docker build -t oracle/database:11.2.0.2-xe . 
#
# IMPORTANT
# ---------
# Oracle XE requires Docker 1.10.0 and above:
# Oracle XE uses shared memory for MEMORY_TARGET and needs at least 1 GB.
# Docker only supports --shm-size since Docker 1.10.0
#
# Pull base image
# ---------------
FROM oraclelinux:7-slim

# Maintainer
# ----------
MAINTAINER Gerald Venzl <gerald.venzl@oracle.com>

# Environment variables required for this build (do NOT change)
# -------------------------------------------------------------
ENV ORACLE_BASE=/u01/app/oracle \
    ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe \
    ORACLE_SID=XE \
    INSTALL_FILE_1="oracle-xe-11.2.0-1.0.x86_64.rpm.zip" \
    INSTALL_DIR="$HOME/install" \
    CONFIG_RSP="xe.rsp" \
    RUN_FILE="runOracle.sh" \
    PWD_FILE="setPassword.sh" \
    CHECK_DB_FILE="checkDBStatus.sh"

# Use second ENV so that variable get substituted
ENV PATH=$ORACLE_HOME/bin:$PATH

# Copy binaries
# -------------
COPY $INSTALL_FILE_1 $CONFIG_RSP $RUN_FILE $PWD_FILE $CHECK_DB_FILE $INSTALL_DIR/

# Install Oracle Express Edition
# ------------------------------

RUN yum -y install unzip libaio bc initscripts net-tools openssl compat-libstdc++-33 && \
    rm -rf /var/cache/yum && \
    cd $INSTALL_DIR && \
    unzip $INSTALL_FILE_1 && \
    rm $INSTALL_FILE_1 &&    \
    cat() { declare -A PROC=(["/proc/sys/kernel/shmmax"]=4294967295 ["/proc/sys/kernel/shmmni"]=4096 ["/proc/sys/kernel/shmall"]=2097152 ["/proc/sys/fs/file-max"]=6815744); [[ ${PROC[$1]} == "" ]] && /usr/bin/cat $* || echo ${PROC[$1]}; } && \
    free() { echo "Swap: 2048 0 2048"; } && \
    export -f cat free && \
    rpm -i Disk1/*.rpm &&    \
    unset -f cat free && \
    mkdir -p $ORACLE_BASE/scripts/setup && \
    mkdir $ORACLE_BASE/scripts/startup && \
    ln -s $ORACLE_BASE/scripts /docker-entrypoint-initdb.d && \
    mkdir $ORACLE_BASE/oradata && \
    chown -R oracle:dba $ORACLE_BASE && \
    mv $INSTALL_DIR/$CONFIG_RSP $ORACLE_BASE/ && \
    mv $INSTALL_DIR/$RUN_FILE $ORACLE_BASE/ && \
    mv $INSTALL_DIR/$PWD_FILE $ORACLE_BASE/ && \
    mv $INSTALL_DIR/$CHECK_DB_FILE $ORACLE_BASE/ && \
    ln -s $ORACLE_BASE/$PWD_FILE / && \
    cd $HOME && \
    rm -rf $INSTALL_DIR && \
    chmod ug+x $ORACLE_BASE/*.sh

VOLUME ["$ORACLE_BASE/oradata"]
EXPOSE 1521 8080
HEALTHCHECK --interval=1m --start-period=5m \
   CMD "$ORACLE_BASE/$CHECK_DB_FILE" >/dev/null || exit 1

CMD exec $ORACLE_BASE/$RUN_FILE


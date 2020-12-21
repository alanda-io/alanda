# Welcome to alanda !
A framework for developing processes the easy way.

[![Alanda](https://alanda.io/wp-content/uploads/2020/02/Alanda.png)](https://alanda.io/)

[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
![Node.js CI](https://github.com/alanda-io/alanda/workflows/Node.js%20CI/badge.svg?branch=master)

# Motivation
Imagine a world in which the vast majority of time spent at work is used for inspiring, motivating work which creates value for your customers.

# Table of Contents
1. [Purpose](#purpose)
1. [Requirements](#requirements)
1. [Setup](#setup)
1. [Troubleshooting](#troubleshooting)

# Purpose
What is alanda for?

# Requirements

* Docker
* docker-compose 1.*
* Maven 3.0 or higher
* Node.js, 8.10 or higher
* NPM 5.6.0
* Oracle Database 11g Express Edition Release (11.2.0) or higher

# Note:
Unless stated otherwise, the commands in the following guide assume you call them from the alanda root folder after cloning the repository.

## Docker
For installing Docker, see [Install Docker Engine - Official Documentation](https://docs.docker.com/engine/install/).
Next, prepare a network for your containers:

    docker network create --driver=bridge \
                          --subnet=10.100.0.0/16 \
                          --gateway=10.100.0.254 \
                            alanda_bpma_bridge

## Installing Oracle Database:
If you have a running Oracle database (reachable under localhost, port 1521), you can skip this step and continue with [Setup](#setup).

To create an Oracle Database Docker container, follow the instructions given in this [Oracle blog Post](https://blogs.oracle.com/oraclemagazine/deliver-oracle-database-18c-express-edition-in-containers) or read the summary below:
1. Clone [Oracle's sample Dockerfiles repository](https://github.com/oracle/docker-images)
1. Download the database binaries from [Oracle’s Database Software Downloads](https://www.oracle.com/database/technologies/oracle-database-software-downloads.html) (in case this link breaks, go to [Oracle’s technical resources web page](https://www.oracle.com/technical-resources/) and look for *Database Downloads*)
1. Copy the downloaded binaries next to the matching dockerfile under **<repo>/OracleDatabase/SingleInstance/dockerfiles/<version>**
1. Build the docker image by using ./buildDockerImage.sh

For 18.4 Express Edition:

    git clone https://github.com/oracle/docker-images
    wget -P docker-images/OracleDatabase/SingleInstance/dockerfiles/18.4.0/ \
            https://download.oracle.com/otn-pub/otn_software/db-express/oracle-database-xe-18c-1.0-1.x86_64.rpm
    cd docker-images/OracleDatabase/SingleInstance/dockerfiles
    bash dockerfiles/buildDockerImage.sh -v 18.4.0 -x

# Setup

## Start the developing environment
Use the `start-dev.sh` bash script in the root directory to start the docker containers for the Oracle Database, ElasticSearch and the Camunda Live Server. 

Camunda/Wildfly and Elasticsearch should be available on ports 8080 and 9206 respectively.

    nmap -p 8080,9206 localhost

Output: 

    8080/tcp open  http-proxy
    9206/tcp open  wap-vcard-s

## (Oracle) Create an alanda user in the database
As oracle **system** or **sys** user:

    alter session set "_ORACLE_SCRIPT"=true;
    create user alanda identified by alanda;
    grant connect, resource, create any view to alanda;
    grant unlimited tablespace to alanda;
    
    -- optional;  This silences the (liquibase) warning below when creating tables:
    -- `Cannot read from v$parameter: ORA-00942: table or view does not exist`
    grant select on v_$parameter to alanda;

Test that you can login with sqplplus (or e.g. Oracle's SQLDeveloper):

    sqlplus alanda/alanda@XE
    SQL> -- hello oracle

## (Oracle) Create the default schema (overwrites previous schema)
From the alanda repository, copy the default database migration configuration and edit to match your oracle and filesystem setup:

    cp backend/alanda-development/src/main/resources/db-migration/db-migration-default.properties backend/alanda-development/src/main/resources/db-migration/db-migration.properties
    editor ./backend/alanda-development/src/main/resources/db-migration/db-migration.properties

Now either run the main Method of this java class from your IDE, and enter the 'init' command at the prompt:

    ./backend/alanda-development/src/main/java/io/alanda/development/dbtools/AlandaDatabaseMigration.java

or execute it via Maven the command line:

    mvn --file ./backend/alanda-development clean install

    mvn --file ./backend/alanda-development  exec:java -Dexec.classpathScope="test" \
    -Dexec.mainClass="io.alanda.development.dbtools.AlandaDatabaseMigration" \
    -Dexec.cleanupDaemonThreads=false \
    -Dexec.args="init"

## (Elastic Search) Setup Indices
Once the elastic docker container is up, create the indices for Alanda:

    curl -XPUT -H "Content-Type: application/json" \
    --data @backend/alanda-camunda-es-history-plugin/src/main/resources/mapping/index-schema.json \
    http://localhost:9206/alanda-process    
    
    curl -XPUT -H "Content-Type: application/json" \
    --data @backend/alanda-camunda-es-history-plugin/src/main/resources/mapping/index-task.json \
    http://localhost:9206/alanda-task

Then create the aliases for the indices:

    curl -XPOST -H "Content-Type: application/json" 'http://localhost:9206/_aliases' -d '
    {
        "actions" : [
            { "add" : { "index" : "alanda-task", "alias" : "pmc-task" } }
        ]
    }'

    curl -XPOST -H "Content-Type: application/json" 'http://localhost:9206/_aliases' -d '
    {
        "actions" : [
            { "add" : { "index" : "alanda-process", "alias" : "pmc" } }
        ]
    }'

## Build the backend
Use the top level pom.xml to build the backend part:

    mvn -DskipTests -Dcobertura.skip=true --file backend/pom.xml clean install

## Deploy the backend

Copy the two wars in the wildfly deployment directory

    cp backend/alanda-rest/target/alanda-rest.war backend/alanda-development/src/test/resources/docker-camunda/mount_deployment
    cp backend/alanda-background/target/alanda-background.war backend/alanda-development/src/test/resources/docker-camunda/mount_deployment

## Login as admin user

The admin user **alanda** is created automatically without a password.
Use it to login at your local [alanda frontend](http://localhost:9090).

Congratulations, you now have a running alanda.io!

# Resources

- [Angular style Guide](https://angular.io/guide/styleguide)
- [Prime NG](https://www.primefaces.org/primeng/showcase/#/setup)
- [Prime Icons](https://primefaces.org/primeng/showcase/#/icons)
- [Nx Workspace - Angular](https://nx.dev/latest/angular/getting-started/getting-started)

# Troubleshooting

## Increasing the amount of inotify watchers

If you are running Debian, RedHat, or another similar Linux distribution, run the following in a terminal:
```
echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf && sudo sysctl -p
```
If you are running ArchLinux, run the following command instead:
```
echo fs.inotify.max_user_watches=524288 | sudo tee /etc/sysctl.d/40-max-user-watches.conf && sudo sysctl --system
```

## WebStorm always chooses the wrong auto import path in a workspace
Go to Settings/Preferences dialog (⌘,) -> Code Style -> Editor -> choose JavaScript or TypeScript -> Imports tab -> select **Only in files outside specified paths**

[![Webstorm_settings](https://user-images.githubusercontent.com/2495032/57608810-f5f6fb80-756d-11e9-8403-e33f17c04212.png)](https://github.com/nrwl/nx/issues/83)

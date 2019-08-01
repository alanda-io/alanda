#!/bin/bash
START_ELASTIC=0
START_ELASTIC5=0
START_ELASTIC6=1
START_ELASTIC_KIBANA5=0
START_ELASTIC_KIBANA6=0
START_ELASTIC_HQ=0
START_PSQL=0
START_ORACLE=0
START_MONGODB=0
START_SOLR=0
START_OAK=0
START_NEO4J=0
START_OPENKM=0
START_CAMUNDA=1

/home/developer/developers-happy-place/docker/docker-alanda/scripts/01_init-docker-env-alanda.sh

if [ $START_ELASTIC = "1" ]
then
	/home/developer/developers-happy-place/docker/docker-alanda/scripts/start-elastic-alanda.sh
fi

if [ $START_ELASTIC5 = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-elastic5-alanda.sh
fi

if [ $START_ELASTIC6 = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-elastic6-alanda.sh
fi

if [ $START_ELASTIC_KIBANA5 = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-elastic-kibana5-alanda.sh
fi

if [ $START_ELASTIC_KIBANA6 = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-elastic-kibana6-alanda.sh
fi

if [ $START_ELASTIC_HQ = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-elastic-hq-alanda.sh
fi

if [ $START_PSQL = "1" ]
then
	/home/developer/developers-happy-place/docker/docker-alanda/scripts/start-psql-alanda.sh
fi

if [ $START_ORACLE = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-oracle-alanda.sh
fi

if [ $START_MONGODB = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-mongodb-alanda.sh
fi

if [ $START_SOLR = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-solr-alanda.sh
fi

if [ $START_OAK = "1" ]
then
        /home/developer/developers-happy-place/docker/docker-alanda/scripts/start-oak-alanda.sh
fi

if [ $START_NEO4J = "1" ]
then
	/home/developer/developers-happy-place/docker/docker-alanda/scripts/start-neo4j-alanda.sh
fi

if [ $START_OPENKM = "1" ]
then
	/home/developer/developers-happy-place/docker/docker-alanda/scripts/start-openkm-alanda.sh
fi

if [ $START_CAMUNDA = "1" ]
then
	/home/developer/developers-happy-place/docker/docker-alanda/scripts/start-camunda-alanda.sh
fi


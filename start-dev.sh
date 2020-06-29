#!/bin/bash

echo -e '\e[1m > Stating docker images \n'
cd ./backend/alanda-development/src/test/resources/
docker-compose up -d
cd ./docker-oracle/
docker-compose up -d
cd ../../../../../../

echo -e '\n---------------------------------------------\n'

echo -e '\e[1m > Stating frontend for development'
cd ./frontend/alanda-libraries
npm run start

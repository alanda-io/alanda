#!/bin/bash

echo -e '\e[1m > Stating docker images \n'
cd ./backend/alanda-development/src/test/resources/ || { echo -e '\e[31mPath does not exist!'; exit 1; }
docker-compose up -d
cd ./docker-oracle/ || { echo -e '\e[31mPath does not exist!'; exit 1; }
docker-compose up -d
cd ../../../../../../

echo -e '\n---------------------------------------------\n'

echo -e '\e[1m > Stating frontend for development'
cd ./frontend/alanda-libraries || { echo -e '\e[31mPath does not exist!'; exit 1; }
npm run start

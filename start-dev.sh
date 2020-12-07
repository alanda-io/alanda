#!/bin/bash

echo -e '\e[1m>> Stating docker images\e[0m \n'
cd ./backend/alanda-development/src/test/resources/docker-oracle/ || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }
docker-compose down
docker-compose up -d
sleep 10
cd ../ || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }
docker-compose down
docker-compose up -d
cd ../../../../../

echo -e '\n---------------------------------------------\n'

echo -e '\e[1m>> Stating frontend for development\e[0m'
cd ./frontend/alanda || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }
npm ci
npm run start

#!/bin/bash
printf "Reindex 5.x task index to 6.x...\n"
export GREP_COLOR='0;0;42'
curl -XPUT http://localhost:9206/es6-task -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/workspace/pmc-base/backend/pmc-camunda-es-history-plugin/src/main/resources/mapping/index-task.json

curl -XPOST -H "Content-Type: application/json"  --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/reindex_pmc_task_index-alanda.json http://localhost:9206/_reindex | grep --color=auto -E "\"failures\":\[\]|$"

printf "\n...Done\n\n"

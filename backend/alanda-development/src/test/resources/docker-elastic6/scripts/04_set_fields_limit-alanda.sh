#!/bin/bash
printf "Set fields limit on Elastic6 indices...\n"

printf "\n# pmc(-process) index:\n"
export GREP_COLOR='0;0;42'
curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/set_fields_limit.json http://localhost:9206/alanda-process/_settings  | grep --color=auto -E "true|$"

printf "\n# pmc-task index:\n"
curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/set_fields_limit.json http://localhost:9206/alanda-task/_settings  | grep --color=auto -E "true|$"

printf "\n...Done (Elastic6 set fields limit)\n\n"


#!/bin/bash
printf "Import Elastic6 indices...\n"

printf "\n# Importing pmc(-process) indices:\n"
export GREP_COLOR='0;0;42'
curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/workspace/pmc-base/backend/pmc-camunda-es-history-plugin/src/main/resources/mapping/index-schema.json http://localhost:9206/alanda-process | grep --color=auto -E "true|$"

printf "\n# Importing pmc-task indices:\n"
curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/workspace/pmc-base/backend/pmc-camunda-es-history-plugin/src/main/resources/mapping/index-task.json http://localhost:9206/alanda-task | grep --color=auto -E "true|$"

printf "\n...Done (Elastic6 import indices)\n\n"

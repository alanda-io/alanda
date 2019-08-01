#!/bin/bash
printf "Reindex 5.x process index to 6.x...\n"
export GREP_COLOR='0;0;42'
#printf "Delete index for reindex (if exists):\n"
#curl -XDELETE  http://localhost:9206/alanda-process-reindex-to-6 | grep --color=auto -E "true|$"

#printf "Create index for reindex\n"
#curl -XPUT  http://localhost:9206/alanda-process-reindex-to-6 | grep --color=auto -E "true|$"

#printf "Modify fields limit.\n"
#curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/set_fields_limit.json http://localhost:9206/alanda-process-reindex-to-6/_settings | grep --color=auto -E "true|$"

curl -XPUT http://localhost:9206/es6-process -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/workspace/pmc-base/backend/pmc-camunda-es-history-plugin/src/main/resources/mapping/index-schema.json

curl -XPOST -H "Content-Type: application/json"  --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/reindex_pmc_process_index-alanda.json http://localhost:9206/_reindex | grep --color=auto -E "\"failures\":\[\]|$"
printf "\n...Done\n\n"

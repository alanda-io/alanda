#!/bin/bash
printf "Disable shard allocation on Elastic 5.x nodes...\n"
export GREP_COLOR='0;0;42'
curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic5/scripts/disable_shard_allocation.json http://elastic:changeme@localhost:9205/_cluster/settings | grep --color=auto -E "true|$"

printf "\n...Done (Disable Elastic 5.x shard allocation)\n\n"

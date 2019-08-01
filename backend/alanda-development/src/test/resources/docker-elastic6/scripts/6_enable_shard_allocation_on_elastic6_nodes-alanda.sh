#!/bin/bash
printf "Enable shard allocation on Elastic 6.x nodes...\n"
export GREP_COLOR='0;0;42'
curl -XPUT -H "Content-Type: application/json" --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/enable_shard_allocation.json http://localhost:9206/_cluster/settings | grep --color=auto -E "true|$"

printf "\n...Done (Enable Elastic 6.x shard allocation)\n\n"

#!/bin/bash
printf "Add actinst_type filed to 6.x task index...\n"
export GREP_COLOR='0;0;42'
curl -XPUT -H "Content-Type: application/json"  --data @/home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/put_new_field_to_6-alanda.json http://localhost:9206/alanda-task/_mapping/task | grep --color=auto -E "\"failures\":\[\]|$"
printf "\n...Done\n\n"

#!/bin/bash
printf "Delete Elastic 6 indices...\n"

export GREP_COLOR='0;0;42'
curl -XDELETE 'http://localhost:9206/alanda-process/' | grep --color=auto -E "true|$"
curl -XDELETE 'http://localhost:9206/alanda-task/' | grep --color=auto -E "true|$"

printf "\n...Done (Elastic 6: delete indices)\n\n"

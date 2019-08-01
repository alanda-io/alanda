#!/bin/bash
printf "A synced flush request is a “best effort” operation. It will fail if there are any pending indexing operations, but it is safe to reissue the request multiple times if necessary.\n"
printf "Perform synced flush on Elastic 5.x nodes...\n"
export GREP_COLOR='0;0;42'
curl -XPOST  http://elastic:changeme@localhost:9205/_flush/synced | grep --color=auto -E "\"failed\":0|$"

printf "\n...Done (Perform synced flush on Elastic 5.x nodes)\n\n"

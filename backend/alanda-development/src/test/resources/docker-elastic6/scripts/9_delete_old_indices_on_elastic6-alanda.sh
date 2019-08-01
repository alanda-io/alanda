#!/bin/bash
export GREP_COLOR='0;0;42'
curl -XDELETE  http://localhost:9206/alanda-process-reindex-to-5 | grep --color=auto -E "true|$"
curl -XDELETE  http://localhost:9206/alanda-task-reindex-to-5 | grep --color=auto -E "true|$"


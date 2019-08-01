#!/bin/bash
printf "Stop machine learning on Elastic 5.x nodes...\n"
export GREP_COLOR='0;0;42'

# Stopping Datafeeds
# curl -XPOST  http://elastic:changeme@localhost:9205/_xpack/ml/datafeeds/datafeed-total-requests/_stop | grep --color=auto -E "\"failed\":0|$"

# Stopping All Datafeeds
curl -XPOST  http://elastic:changeme@localhost:9205/_xpack/ml/datafeeds/_all/_stop | grep --color=auto -E "\"failed\":0|$"

# Closing Jobs
# curl -XPOST  http://elastic:changeme@localhost:9205/_xpack/ml/anomaly_detectors/total-requests/_close | grep --color=auto -E "\"failed\":0|$"

# Closing All Jobs
curl -XPOST  http://elastic:changeme@localhost:9205/_xpack/ml/anomaly_detectors/_all/_close | grep --color=auto -E "\"failed\":0|$"

printf "\n...Done\n\n"

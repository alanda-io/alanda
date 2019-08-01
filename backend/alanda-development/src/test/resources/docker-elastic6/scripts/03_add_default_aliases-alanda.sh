#!/bin/bash
printf "\n# Creating default aliases for the project specific indicies (pmc, pmc-task) on Elastic 6.x nodes\n"
export GREP_COLOR='0;0;42'

curl -XPOST -H "Content-Type: application/json" 'http://localhost:9206/_aliases' -d '
{
	"actions" : [
		{ "add" : { "index" : "alanda-task", "alias" : "pmc-task" } }
	]
}'  | grep --color=auto -E "true|$"

curl -XPOST -H "Content-Type: application/json" 'http://localhost:9206/_aliases' -d '
{
	"actions" : [
		{ "add" : { "index" : "alanda-process", "alias" : "pmc" } }
	]
}' | grep --color=auto -E "true|$"

printf "...Done\n\n"

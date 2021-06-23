#!/bin/bash
printf "Import Elastic6 indices...\n"

printf "\n# Importing pmc(-process) indices:\n"
export GREP_COLOR='0;0;42'
curl -XPUT -H "Content-Type: application/json" --data @index-schema.json http://localhost:9200/alanda-process | grep --color=auto -E "true|$"

printf "\n# Importing pmc-task indices:\n"
curl -XPUT -H "Content-Type: application/json" --data @index-task.json http://localhost:9200/alanda-task | grep --color=auto -E "true|$"

printf "\n...Done (Elastic6 import indices)\n\n"

printf "\n# Creating default aliases for the project specific indicies (pmc, pmc-task) on Elastic 6.x nodes\n"
export GREP_COLOR='0;0;42'

curl -XPOST -H "Content-Type: application/json" 'http://localhost:9200/_aliases' -d '
{
	"actions" : [
		{ "add" : { "index" : "alanda-task", "alias" : "pmc-task" } }
	]
}'  | grep --color=auto -E "true|$"

curl -XPOST -H "Content-Type: application/json" 'http://localhost:9200/_aliases' -d '
{
	"actions" : [
		{ "add" : { "index" : "alanda-process", "alias" : "pmc" } }
	]
}' | grep --color=auto -E "true|$"

printf "...Done\n\n"

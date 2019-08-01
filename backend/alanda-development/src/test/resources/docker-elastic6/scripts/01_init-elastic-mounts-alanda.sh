#!/bin/bash
printf "# Set elascticsearch 6 docker specific user and group permission on mounted folders...\n"
sudo chown -R 1000:1000 /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_*
printf "...Done\n\n"


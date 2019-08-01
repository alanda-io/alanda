#!/bin/bash
printf "# Reseting user/group permissions and delete Elasticsearch 6 logs, configs, ...\n"
sudo chown -R developer:developer /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_*

read -p "?? Do you want to delete the Elasticsearch 6 indices also? ALL DATA WILL BE LOST! (Default->[Enter: No]) [yes/No*]" yn
case $yn in
	[yY][eE][sS])
		sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_data_node1/*
		sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_data_node2/*
                sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/scripts/elastic6.init.lock
                ;;
        * )
                printf "# Elasticsearch 6 indices were not deleted.\n"
                ;;
esac

sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_logs_node1/*
sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_logs_node2/*
sudo rm /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_config_node1/elasticsearch.yml
sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_config_node1/scripts/
sudo rm /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_config_node2/elasticsearch.yml
sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/mount_config_node2/scripts/
sudo rm /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/docker-compose.yml
sudo rm -rf /home/developer/developers-happy-place/docker/docker-alanda/docker-elastic6/scripts/*
printf "...Done (Reset Elastic 6)\n\n"


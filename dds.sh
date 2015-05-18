#!/bin/bash

# get the address of the dockerized drpc server
# note: nimbus and drpc servers are typically combined
# so we lookup id of nimbus node

# get the container id
nimbus_id=$(docker ps | grep nimbus | awk '{print $1}')

if [ -z "$nimbus_id" ]; then
    echo "Failed to find nimbus node. Is storm-docker running?"
    exit -1;
fi

# inspect the container for its ip address
docker inspect $nimbus_id | grep -i ipaddress | awk '{print $2}' | sed -e 's/[",]//g'

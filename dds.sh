#!/bin/bash

# get the address of the dockerized drpc server

# get the container id
nimbus_id=$(docker ps | grep nimbus | awk '{print $1}')

# inspect the container for its ip address
docker inspect $nimbus_id | grep -i ipaddress | awk '{print $2}' | sed -e 's/[",]//g'

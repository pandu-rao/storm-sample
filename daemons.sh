#!/bin/bash

echo "Launching storm daemons in their own screens"

echo "Launching zookeeper screen ..."
screen -S 'zookeeper'  -d -m && screen -r 'zookeeper'  -X stuff $'zkServer.sh start\n'

echo "Launching nimbus screen ..."
screen -S 'nimbus'     -d -m && screen -r 'nimbus'     -X stuff $'storm nimbus\n'

echo "Launching supervisor screen ..."
screen -S 'supervisor' -d -m && screen -r 'supervisor' -X stuff $'storm supervisor\n'

echo "Launching drpc screen ..."
screen -S 'drpc'       -d -m && screen -r 'drpc'       -X stuff $'storm drpc\n'

echo "Launching ui screen ..."
screen -S 'ui'         -d -m && screen -r 'ui'         -X stuff $'storm ui\n'

screen -ls

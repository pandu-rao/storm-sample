Sample Storm DRPC Shell Bolt and Client

Sample demonstrates Storm DRPC in local and clustered modes plus ability to offload processing to other languages (Python, say).

Client submits the string "hello" to DRPC spount which uses a Shell Bolt to spawn a Python process to convert the input string to uppercase. The uppercased string is then returned to the calling function.

If new to Storm, read up about it and ensure that your development environment is setup correctly.

It is helpful to be able to run the topologies in Storm Starter:
https://github.com/apache/storm/tree/master/examples/storm-starter

This Sample can be executed in three modes: local, cluster and docker

local:
Simulates a Storm cluster in process and is useful to developing/testing

Running the topology:
```shell
./topology local
```

cluster:
Runs topologies on a Storm cluster which can reside on dev workstation/remote server.

Update storm-sample.yml to set mode: local

Running the topology:
```shell
./daemons.sh
./topology server
./topology client
```

The daemons.sh script automates launching various Storm daemons in a single byobu-tmux window (note: zookeeper/bin, storm/bin must be in PATH). Or you can use that or lauch the daemons individually, if you prefer.

The logs.sh script tails logs from various daemons. You will need multitail for this (note: log_dir variable in script), or you can tail the files individually.

docker:
Runs topologies on Dockerized Storm nodes running on dev workstation.

Clone Wurstmeister's storm-docker repository to some directory:
https://github.com/wurstmeister/storm-docker.git

Install docker-compose (say /usr/local/bin):
https://docs.docker.com/compose/install/

Run docker-compose inside the storm-docker directory:
```shell
docker-compose up
```

In storm-sample directory, get the IP address of the Docker drpc server:
```shell
./dds.sh
```

Update storm-sample.yml to set mode: docker and IP address of drpc server obtained from dds.sh.

Run the topology on Dockerized Storm:
```shell
./topology server
./topology client
```

In all cases, the output will look like this:
```shell
****************************************
****************************************
HELLO
****************************************
****************************************

Processing time: x seconds
```

Storm is fairly verbose in providing processing-related information. These have been filtered by the topology script. If you want to see the complete output, change the filter to cat instead of grep.

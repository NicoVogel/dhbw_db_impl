#!/bin/bash

MOUNT_PATH=`pwd`
USER_HOME=`eval echo ~$USER`
#https://hub.docker.com/_/maven#reusing-the-maven-local-repository
VOLUME_M2_PATH="$USER_HOME/.m2:/root/.m2"

# z -> build zuul
# m -> build manager
# i -> build Instance
# e -> build eureka
# b -> build all

function build {
  if [[ $1 == *"b"* ]] || [[ $1 == *$2* ]] || [[ -z $(docker images -q dhbw-db-$3) ]]; then
    echo "------------------------------------------------------------------------"
    echo "BUILD $3"
    echo "------------------------------------------------------------------------"
    docker run --rm -it \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v $VOLUME_M2_PATH \
      -v $MOUNT_PATH/db.impl.$3:/usr/src/mymaven \
      -e DOCKER_HOST=unix:///var/run/docker.sock \
      -w /usr/src/mymaven \
      maven:alpine \
      mvn install dockerfile:build
  fi
}

function removeIfRunning {
  docker rm -vf $(docker ps -aq --filter ancestor=dhbw-db-$1)
}

build $1 z zuul
build $1 m manager
build $1 i instance
build $1 e eureka

removeIfRunning zuul
removeIfRunning manager
removeIfRunning instance
removeIfRunning eureka

docker image prune -f

echo "------------------------------------------------------------------------"
echo "START MS DB"
echo "------------------------------------------------------------------------"

docker-compose up -d
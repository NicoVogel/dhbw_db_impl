#!/bin/bash

MOUNT_PATH=`pwd`
USER_HOME=`eval echo ~$USER`
#https://hub.docker.com/_/maven#reusing-the-maven-local-repository
VOLUME_M2_PATH="$USER_HOME/.m2:/root/.m2"

# z -> build zuul
# i -> build Instance
# e -> build eureka
# b -> build all
# r -> remove existing container to rebuild them

function remove {
  if [[ -z $(docker ps -aq --filter ancestor=dhbw-db-$1) ]]; then
      docker rm -vf $(docker ps -aq --filter ancestor=dhbw-db-$3)
    fi
}

function build {
  if [[ $1 == *"b"* ]] || [[ $1 == *$2* ]] || [[ -z $(docker images -q dhbw-db-$3) ]]; then
    echo "------------------------------------------------------------------------"
    echo "BUILD $3"
    echo $MOUNT_PATH/db.impl.$3
    echo "------------------------------------------------------------------------"
    docker run --rm -it \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v $VOLUME_M2_PATH \
      -v $MOUNT_PATH/db.impl.$3:/usr/src/mymaven \
      -e DOCKER_HOST=unix:///var/run/docker.sock \
      -w /usr/src/mymaven \
      maven:alpine \
      mvn install dockerfile:build

    remove $3
  fi
}


build $1 z zuul
build $1 i instance
build $1 e eureka


docker-compose stop
docker image prune -f


if [[ $1 == *"r"* ]]; then
  remove zuul
  remove instance
  remove eureka
  docker network prune -f
fi


echo "------------------------------------------------------------------------"
echo "START MS DB"
echo "------------------------------------------------------------------------"

docker-compose up -d

function Path-For-Docker-Toolbox($PATH){
    $PATH=(($PATH -replace "\\","/") -replace ":","").Trim("/")

    [regex]$regex='^[a-zA-Z]/'
    $PATH=$regex.Replace($PATH, {$args[0].Value.ToLower()})

    $PATH="//$PATH"
    return $PATH
}
function Path-For-Docker($PATH){
    $PATH=(($PATH -replace "\\","/") -replace ":","").Trim("/")

    [regex]$regex='^[a-zA-Z]/'
    $PATH=$regex.Replace($PATH, {$args[0].Value.ToLower()})

    $PATH="/host_mnt/$PATH"
    return $PATH
}

$MOUNT_PATH=""
$USER_PATH=""
if($args[0] -Match "t"){
    $MOUNT_PATH = Path-For-Docker-Toolbox -Path ${pwd}
    $USER_PATH = Path-For-Docker-Toolbox -Path $env:USERPROFILE
}else{
    $MOUNT_PATH = Path-For-Docker -Path ${pwd}
    $USER_PATH = Path-For-Docker -Path $env:USERPROFILE
}

#https://hub.docker.com/_/maven#reusing-the-maven-local-repository
$VOLUME_M2_PATH="${USER_PATH}/.m2:/root/.m2"

# z -> build zuul
# i -> build Instance
# e -> build eureka
# b -> build all
# r -> remove existing container to rebuild them

function remove($NAME){
    if( $(docker ps -aq --filter ancestor=dhbw-db-$NAME) ){
        docker rm -vf $(docker ps -aq --filter ancestor=dhbw-db-${NAME})
    }
}

function build($ARG, $CHAR, $NAME) {
    if((${ARG} -Match "b") -Or (${ARG} -Match "${CHAR}") -Or ( -Not $(docker images -q dhbw-db-$NAME))){
        echo "------------------------------------------------------------------------"
        echo "BUILD ${NAME}"
        echo $MOUNT_PATH/db.impl.${NAME}
        echo "------------------------------------------------------------------------"
        docker run --rm -it `
            -v /var/run/docker.sock:/var/run/docker.sock `
            -v $VOLUME_M2_PATH `
            -v $MOUNT_PATH/db.impl.${NAME}:/usr/src/mymaven `
            -e DOCKER_HOST=unix:///var/run/docker.sock `
            -w /usr/src/mymaven `
            maven:alpine `
            mvn install dockerfile:build
        
        remove -NAME $NAME
    }
}

build -ARG $args[0] -CHAR z -NAME zuul
build -ARG $args[0] -CHAR e -NAME eureka
build -ARG $args[0] -CHAR i -NAME instance

docker-compose stop

if(${ARG} -Match "r"){
    docker network prune -f
    remove -NAME zuul
    remove -NAME eureka
    remove -NAME instance
}

docker image prune -f

echo "------------------------------------------------------------------------"
echo "START MS DB"
echo "------------------------------------------------------------------------"

docker-compose up -d
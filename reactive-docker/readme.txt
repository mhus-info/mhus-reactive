
cd reactive-playground
./create.sh


docker tag reactive-playground "mhus/reactive-playground:1.0.0"
docker tag reactive-playground "mhus/reactive-playground:latest"

docker push "mhus/reactive-playground:1.0.0"
docker push "mhus/reactive-playground:latest"

First Start:
docker rm reactive-playground
docker run -it --name reactive-playground -p 18181:8181 reactive-playground

Go to http://localhost:18181/sop

Again:
docker start -i reactive-playground

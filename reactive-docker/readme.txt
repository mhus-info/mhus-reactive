
cd reactive-playground
./create.sh


docker tag reactive-playground "mhus/reactive-playground:1.0.0"
docker tag reactive-playground "mhus/reactive-playground:latest"

docker push "mhus/reactive-playground:1.0.0"
docker push "mhus/reactive-playground:latest"

docker run -it --name reactive-playground reactive-playground

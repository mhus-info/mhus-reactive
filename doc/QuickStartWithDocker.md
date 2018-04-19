

== Create Playground

If SNAPSHOT releases are required pull and install the binaries in the local maven repository.

goto directory cherry-reactive/reactive-docker/reactive-playground
and start the create script
./create.sh

or to recreate
./create.sh clean

If the docker image was created, start the image

== Start the image

docker run -it --name reactive-playground -p 18181:8181 reactive-playground 

after stopping you can restart it with

docker start reactive-playground

or

docker start -ai reactive-playground

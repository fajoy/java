cd bin
java -Djava.rmi.server.codebase=file:/%CD%/ -Djava.security.policy=file:/%CD%/client.policy -Djava.rmi.server.hostname=127.0.0.1 ComputePi
pause
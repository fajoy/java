cd bin
java -Djava.rmi.server.codebase=file:/%CD%/ -Djava.security.policy=file:/%CD%/server.policy ComputeEngine
pause
cd bin
java -Djava.rmi.server.codebase=file:/%CD%/ -Djava.security.policy=file:/%CD%/java.policy -Djava.rmi.server.hostname=127.0.0.1 RMIMapReduce
pause
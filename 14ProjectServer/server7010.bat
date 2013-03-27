cd bin
java -Djava.rmi.server.codebase=file:/%CD%/ -Djava.security.policy=file:/%CD%/java.policy  ChatServer 7010
pause
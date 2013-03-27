Project #1: Simple Chat System

In this homework, you are asked to design chat-like system.

What we want you to learn from this project are:

* Threads
* Exceptions
* I/O
* Networking

Outline:

* Write client and server programs.  Both are Java applications. 
* Use the following command to invoke the server program. 
        % java ChatServer 7010
  Then, the server program ChatServer waits for connection requests.
* Use the following command to invoke the client program. 
        % java ChatClient myserver.nctu.edu.tw 7010
  Then, the client program ChatClient connects to host
  myserver.nctu.edu.tw at port 7010.
* After input user account, connect to the server and 
  then make the following operations. 
        % java ChatClient myserver.nctu.edu.tw 7010
        Username: Student0                  <--user input user name here, and connect to server.
        /msg Error: The user 'Student0' is already online. Please change a name.	<--Server check the name.
        /msg Username:	<--And send back to client.
        Username:                           <--user input emtpy name. 
        /msg Error: No username is input.		<--Server check the name.
        /msg Username:	<--And send back to client.
        Username: Student1                  <--input user name here.
        *******************************************
        ** <user_name>, welcome to the chat system.
        *******************************************
        
  * Chat command format: 
    * "/yell msgs": send "msgs" to all people. 
      
      Examples:     
        /yell Blah, Blah, ...     <--input chat message here.
        /msg Student1 yelled: Blah, Blah, ...		<--Server sends to all clients.
        Student1 yelled: Blah, Blah, ...<-- Clients show on console.
        
        /yell	<--input chat message here.
        /msg Student1 yelled:	<--Server sends to all clients.
        Student1 yelled:	<-- Clients show on console.
        
    * "/tell someone msgs": send "msgs" to the person "someone" only. 
      
      Examples:     
        /tell Student2 Hello, Student2.	<-- input chat message here.
        /msg Student1 told Student2: Hello, Student2.	<-- Server sends to Student2.
        Student1 told Student2: Hello, Student2	<-- Student2 shows on console.
    		
    		/tell Student2 Hello, Student2.	<-- input chat message here, but if Student2 does not exist.
        /msg Error: 'Student2' is not online.	<-- Server sends back to the client.
        Error: 'Student2' is not online.	<-- The client shows on console.
    
    		/tell	<-- input chat message here, but if empty. 
        /msg Error: No target was given.	<-- Server sends back to the client.
        Error: No target was given.	<-- The client shows on console.
        
  * Post a message: 
    * "/post String msg": Client sends this message to server. In server, assign a series id "msgid" to the message, 
      put (user, msgid, type, msg) into a global buffer,e.g.(Student1, 1, String, This is a test.), 
      and then broadcast to others the message in the format: "/post user msgid String message"
      
      Examples: 
        /post String This is a test.	<--input chat message here.
        /post Student1 101 String This is a test.	<--Server broadcast to all client.
        Student1 posted message '101' in String: This is a test.	<-- clients show on console.
    		
        /post String <--input chat message here.
        /post Student1 101 String	<--Server broadcast to every client.
        Student1 posted message '101' in String:	<-- Clients show on console.
   			
        /post Obj This is a test.	<--input chat message here.
        /msg Error: No such post type.	<--Server send back to the client, and no broadcast.
        Error: No such post type.	<-- The client shows on console.
        
        /post	<--input chat message here.
        /msg Error: No post type.	<--Server send back to the client, and no broadcast.
        Error: No post type.	<-- The client shows on console.
        
    * "/remove msgid": Remove the message with msgid from the buffer, and then broadcast to others the following message. 
    
      Examples:     
        /remove 101	<--input chat message here.
        /remove Student1 101	<--Server broadcast to every client
        Student1 remove message '101': This is a test.	<-- Clients show on console
        
        /remove 101 abababab asdfasdf	<--input chat message here.
        /remove Student1 101	<--Server broadcast to every client
        Student1 remove message '101': This is a test.	<-- Clients show on console
        
        Assume that no 101 in the buffer.
        /msg Error: No such msg id.	<--Server send back to the client, and no broadcast.
        Error: No such msg id.	<-- The client show on console.
        
        /remove 101asdf	<--input chat message here.
        /msg Error: No such msg id.	<--Server send back to the client, and no broadcast.
        Error: No such msg id.	<-- The client shows on console.

        /remove	<--input chat message here.
        /msg Error: No msg id.	<--Server send back to the client, and no broadcast.
        Error: No msg id.	<-- The client shows on console.
        
    * When a new client logins, all posted messages are sent to the client in the following format,
      "/post user msgid String message" for each message. Then, client shows: 
        Student1 posted message '101' in String: This is a test.	<-- The server sends "/post Student1 101 String This is a test".
        ...
      Note that each client needs to maintain these messages (this will be important in the following projects). 
      
  	* "/showPost": List all post messages that stored in client.
        /showPost	<--input chat message here.
        Student1 posted message '101' in String: This is a test.	<--Get these messages from client, and show on console
				(Don't write into logfile.)
      
  * Other commands
	  * "/who": Display all the on-line users in the following format. 
	  	Format:
	  			Name\tIP/port\n
	  			%s\t%s[\t <-- myself]
	  	Example:
          Name	IP/port
	        Student0:	140.113.210.62/3145 
	        Student1:	140.113.210.63/1456 <-- myself
	        Student2:	140.113.210.64/4561 
	    Assume a client connect to server, but server dosen't get the name of the client. 
	    At the moment, the other client types "/who", the client should get the following:
	    Example: (Server dosen't get the name of Student0, so shows the (Unknown).)
          Name	IP/port
          (Unknown):	140.113.210.62/3145 
	        Student1:	140.113.210.63/1456 <-- myself
	        Student2:	140.113.210.64/4561
	    Assume client types "/who lkajsdlfkaj lasdkjfals", and server should work as "/who".
	    
	    You should use /msg to send every row. For example:
	    Server send to client:
	    /msg Name	IP/port
	    /msg Student0:	140.113.210.62/3145 
	    /msg Student1: 140.113.210.63/1456 <-- myself
	    /msg Student2: 140.113.210.64/4561 
	    
	  * "/kick": Allow to kick "someone" by typing "/kick someone".
	    Assume that 'Student1' types "/kick Student2". 
	    Actually, the server broadcasts "/kick Student2" first, 
	    marks "kicked" in the thread of 'Student2',
	    and then the client 'Student2' leaves by sending "/leave". 
	    Note: Do not need to worry about the privilege problem.
            Before the client sends "/leave" to server, the client can still recieve the message.
	    
    * "/connect": The client can type "/connect anotherserver.nctu.edu.tw 7000" to connect to another server.

* For server, after accept a client connection, repeatedly do the following:

  * For each new created connection, broadcast to everyone
    a message of "someone is connecting to the chat server", this message shows before greeting messages.
  * For each connection, if the client wants to leave, type "/leave". 
    Then, the connection is closed. 
  * For each closed connection (could be closed by simply killing the process), 
    broadcast to everyone a message of "someone is leaving the chat server". 
  * If the user types a wrong command, echo an error message:
    "**** Your message command '......' is incorrect". 
  * If the server does not exist, echo an error message:
    "**** The server does not exist.  Please type different domain and/or port."
  * Allow to log every input/output commands through socket into files. 	
    * Server side: 
      * log connection setup into the file "connect_log.txt":
        Format:
  				IP/port\tlogID\n
  				%s\t%d
  			Example:
          IP/port	logID <--- must be an unique number	
          140.113.210.62/3145	1											
          140.113.210.63/1456	2	
          140.113.210.64/4561	3
        ...
      * log input data received from and sent to Student1:
        input_Student1.txt: data received from Student1. e.g., 
        /yell hello every one!	<--Student1 input chat message
        /yell hello every one! <--In the log file
		    		...		    
        output_Student1.txt: data sent to Student1 e.g., 
        /msg student1 yell: hello every one!	<--Server sends to Student1
        /msg student1 yell: hello every one!	<--In the log file
		    		...
        Assume a new client has the name same as previous client who had logged out, you should append the info. into same log file. 
		    
    * Client side:
      * log input data received from and sent to server:
        input_student1.txt: data received from server. e.g.,
        /msg student1 yell: hello every one!	<--In the log file
		    		...
        output_student1.txt: data sent to server. e.g., 
        /yell hello every one!	            <--In the log file
        /post String it's a good day        <--In the log file
		    		...
        Assume you connect to server with the name used before, you should append the info. into same log file.
			
Requirements:
  
* In the server, use one thread to handle one client connection. 
* Use applications in both client and server.(Not Applet)
* Use JAVA sockets to make connection between client and server.
* Use EOFException to detect the connection termination 
  (by either using the command "/leave" gracefully or killing the thread directly).
  (In other words, you should handle if client shutdown unexpectedly).  
* Do not use Thread.stop().
* The megID and the loginID start from 1.
* MsgId can be reuse, upto your design, but msgid should be all different at a time.
* When a client leave, the client can't see the broadcast message.
* When a client connect, the client should see the connection message e.g. "Student1 is connecting to the chat server"
* Fool-proof must be designed in server.
* Output logfile of server start from connection message.
* Input logfile of server start after server get the correct name of the client.
* Output logfile of client is same as input logfile of server.
* Input logfile of client is same as output logfile of server.
* Every msg from server to client should start with /msg, except /post, /remove and /kick.
* No Prompt.
* /kick & /remove do not need to worry about the privilege problem.


About testdata:

* Only one space after all commands.
* No space, escape characters and upper case in user name.


Note:

* In the next project, you are required to improve your chat
  system, with GUI. 

* For any questions, please post.  

Due day : 04/10





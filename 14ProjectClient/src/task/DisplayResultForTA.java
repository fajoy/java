package task;

import chatclient.ChatClient;

public class DisplayResultForTA {
	public static String getUsername(){
		return ChatClient.server.userName;
	}
	public static void displayUsingWidget(String widget ,int x, int y,String args){
		ChatClient.server.writeLine(String.format("/post %s %d %d %s",widget,x,y,args));
		ChatClient.server.flush();
	}
	
}

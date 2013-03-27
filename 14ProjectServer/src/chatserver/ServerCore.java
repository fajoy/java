package chatserver;
import java.io.IOException;
import java.net.*;
import java.util.*;
public class ServerCore {
	public List<StreamHandler> clients=new ArrayList<StreamHandler>();
	private ServerSocket serverSocket=null;
	public ServerCore (int port) throws IOException {
	      serverSocket =new ServerSocket(port);
	}
	public Socket accept() throws IOException{
		return serverSocket.accept();
	}
	protected void addClient(StreamHandler streamHandler){
		clients.add(streamHandler);
	}
}

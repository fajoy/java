package chatserver;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;


import task.ServerCompute;


public class ChatServer {
	public static ChatRoom server=null;
	public static void main(String[] args) {
		/*
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		*/
		int port =7010;
		if (args.length>0)
			port=Integer.parseInt(args[0]);
		try {
			
			server=new ChatRoom(port);
			ServerCompute engine=new ServerCompute() {
				@Override
				public String[] getClientNames() {
	
					String[] names=new String[server.users.size()];
					server.users.keySet().toArray(names);
					return names;
				}
			};
			RMIHelper.createStub("@SERVER", engine);
			System.out.println(String.format("Server is open on %s port.",port));
			server.beginAccept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}

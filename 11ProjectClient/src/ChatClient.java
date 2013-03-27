import java.io.IOException;


public class ChatClient {
	static ChatRoomClient server=null;
	public static void main(String[] args) {
		int port =7010;
		String host="localhost";
		if(args.length>1){
			host=args[0];
			port=Integer.parseInt(args[1]);
		}
		connect(host, port);
		if(server==null)
			return;
		while(!server.isLeave){
			server.beginLogin();
		}
	 	//System.out.println("Connect Over.");
	}
	public static  boolean connect(String host,int port){
		try {
			server=ChatRoomClient.connect(host, port);
			return true;
			
		} catch (IOException e) {
			System.out.println("Server can't Connect.");
			return false;
		}
	}

}

import java.io.IOException;


public class ChatServer {
	public static void main(String[] args) {
		int port =7010;
		if (args.length>0)
			port=Integer.parseInt(args[0]);
		try {
			ChatRoom server=new ChatRoom(port);
			System.out.println(String.format("Server is open on %s port.",port));
			server.beginAccept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}

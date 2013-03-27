import java.io.IOException;


public class ChatClient {
	static ChatRoomClient server=null;
	static MainFrame mainFrame=null;
	public static void main(String[] args) throws InterruptedException {
		mainFrame=new MainFrame();
		mainFrame.setVisible(true);

		int port =7010;
		String host="localhost";
		if(args.length>1){
			host=args[0];
			port=Integer.parseInt(args[1]);
		}
		connect(host, port);
		
		while(true){
			Thread.sleep(100);
			if(server==null)
				continue;
			ChatRoomClient now=server;
			now.beginLogin();
			if(now==server){
			mainFrame.writeLine("Connect Over.");
			server=null;
			}
			
		}
	 	//System.out.println("Connect Over.");
	}
	public static  boolean connect(String host,int port){
		try {
			server=ChatRoomClient.connect(host, port);
			return true;
			
		} catch (IOException e) {
			mainFrame.writeLine("Server can't Connect.");
			return false;
		}
	}

}

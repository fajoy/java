package chatserver;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class ChatRoom extends ServerCore{
	public Map<String,ChatUser>users=new LinkedHashMap<String, ChatUser>();
	public Map<String,ChatPost>posts=new LinkedHashMap<String, ChatPost>();
	public StreamHandler sys=null;
	public StreamHandler con_log=null;
	public int flowMsgID=0;
	public int flowConID=0;
	public ChatRoom(int port) throws IOException {
		super(port);
		sys=new StreamHandler(System.in, System.out);
	}

	public void beginAccept(){
		FileOutputStream os;
		try {
			os = new FileOutputStream(getClass().getResource("./").getPath()+"connect_log.txt",false);
			try {
				con_log=new StreamHandler(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while(true){
			try {
				Socket socket=accept();
				ChatUser user=new ChatUser(this,socket);
				user.conID=++this.flowConID;
				addClient(user);
				user.beginLogin();
				String msg=String.format("%s/%d\t%d",socket.getInetAddress().getHostAddress(),socket.getPort(),user.conID);
				con_log.writeLine(msg);
				con_log.flush();
				sys.writeLine(String.format("%s is Connect.",msg));
				sys.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ChatUser getUser(String userName){
		return this.users.get(userName);
	}
	public synchronized  void castWrite(ChatUser[] users,String arg0){
		for (ChatUser user : users) {
			if(user.isLogin){
				user.write(arg0);
				user.flush();
			}
		}
	}
	public synchronized  void castWriteLine(ChatUser[] users,String arg0){
		this.castWrite(users,String.format("%s\n", arg0));
	}
	public synchronized  void castWriteLine(String arg0){
		ChatUser[] set=new ChatUser[this.clients.size()];
		this.clients.toArray(set);
		this.castWriteLine(set,arg0);
	}


}

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

import widgets.*;

public class ChatRoomClient extends StreamHandler {
	public Map<String, ChatPost> posts = new LinkedHashMap<String, ChatPost>();
	Socket sock=null;
	public MainFrame sys = null;
	public boolean isLeave = false;
	public boolean isLogin = false;
	public String userName = null;
	public StreamHandler input_log = null;
	public StreamHandler output_log = null;
	private Pattern pat5 = Pattern
			.compile("^([\\S]+) ([\\S]+) ([\\S]+) ([\\S]+) (.*)");
	private Pattern pat4 = Pattern.compile("^([\\S]+) ([\\S]+) ([\\S]+) (.*)");
	private Pattern pat3 = Pattern.compile("^([\\S]+) ([\\S]+) (.*)");
	private Pattern pat2 = Pattern.compile("^([\\S]+) (.*)");
	private Pattern pat1 = Pattern.compile("^([\\S]+)");
	private ChatCmdHandler chatCmdHandler=new ChatCmdHandler();
	public ChatRoomClient(Socket sock) throws IOException {
		super(sock);
		//this.sys = new StreamHandler(System.in, System.out);
		this.sock=sock;
		sys=ChatClient.mainFrame;
	}

	public static ChatRoomClient connect(String serverHost, int port)
			throws IOException {
		Socket sock = new Socket(serverHost, port);
		ChatRoomClient s = new ChatRoomClient(sock);
		return s;
	}
	public boolean isConnect(){
		return sock.isConnected();
		
	}
	public void beginLogin() throws InterruptedException {
		ChatClient.mainFrame.resetWhiteBorad();
		sys.writeLine("Username:");
		sys.flush();
		// this.setReadLineHander(entDebug);
		this.setReadLineHander(entGetUserName);
		this.setReadLineHander(entRespMsg);
		this.beginAsyncReadline();
		while (_isReading) {
			
			if (isLeave) {
				break;
			}
			Thread.sleep(100);
		}

	}

	public void invokeReadline(String line){
		
		if (userName != null) {
			String cmd = "/connect ";
			if (cmd.length() < line.length()) {
				if (line.substring(0, cmd.length()).equals(cmd)) {
					try {
						String msgline = line.substring(cmd.length());
						int msgi = msgline.indexOf(" ");
						String strHost = msgline.substring(0, msgi);
						String strPort = msgline.substring(msgi + 1);
						int port = Integer.parseInt(strPort);

						if (ChatClient.connect(strHost, port)) {
							this.writeLine("/leave");
							this.flush();
							isLeave = true;
						}
					} catch (Exception e) {
						sys.writeLine("Connect string format error.");
						sys.flush();
						return;
					}
					return;
				}
			}

			if (line.equals("/showpost")) {
				showPost();
				return;
			}
		}
		this.writeLine(line);
		this.flush();

		if (line.equals("/leave")) {
			isLeave = true;
			System.exit(0);
		}
		
	}
	public void showPost() {
		for (Object obj2 : posts.values().toArray()) {
			ChatPost post = (ChatPost) obj2;
			sys.writeLine(String.format("%s posted message '%s' in %s: %s",
					post.userName, post.msgid, post.type, post.toString()));
		}
		sys.flush();

	}

	public void setUserPost(String userName, String msgid, String type,
			String value) {
			Object obj=value;

		// /post username 101 ClassName x y data1 data2 ...
		// /post username 101 RectangleWidget 10 10 #000000 100 100
		try {
			String[] args = RegexHelper.getSubString(pat3, value);
			Widget w = null;
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			String cmd = args[3];

			Class widgetClass = Class.forName("widgets." + type);
			Object newobj = widgetClass.newInstance();
			if (newobj instanceof Widget) {
				w = (Widget) newobj;
				w.setLocation(x, y);
				w.parseCommand(cmd);
				obj = w;
			}
		} catch (Exception e) {

		}

		ChatPost post = new ChatPost(userName, msgid, type, obj);
		posts.put(msgid, post);
		sys.writeLine(String.format("%s posted message '%s' in %s: %s",
				post.userName, post.msgid, post.type, post.toString()));
		sys.flush();
		if (post.value instanceof Widget) {
			ChatClient.mainFrame.addWidget(post);
		}
		
	}
	public void invokeMove(ChatPost post ,int x, int y){
		this.writeLine(String.format("/move %s %d %d",post.msgid,x,y));
		this.flush();
	}
	public void invokePost(Widget w){
		String type="String";
		if(w instanceof RectangleWidget)
			type="RectangleWidget";
		if(w instanceof CircleWidget)
			type="CircleWidget";
		if(w instanceof JugglerWidget)
			type="JugglerWidget";
		if(w instanceof TimerWidget)
			type="TimerWidget";
		this.writeLine(String.format("/post %s %d %d %s",type,w.getX(),w.getY(),w.toCommand()));
		this.flush();
	}
	@SuppressWarnings("unused")
	private ReadLineHandler<StreamHandler> entDebug = new ReadLineHandler<StreamHandler>() {
		@Override
		public void action(StreamHandler sender, String line) {
			showMsg(line);
		}
	};
	private ReadLineHandler<StreamHandler> entGetUserName = new ReadLineHandler<StreamHandler>() {
		@Override
		public void action(StreamHandler sender, String line) {
			ChatRoomClient user = (ChatRoomClient) sender;
			String s1 = "/msg ** <";
			String s2 = ">, welcome to the chat system.";
			int i1 = line.indexOf(s1);
			int i2 = line.indexOf(s2);
			if (i1 < 0 && i2 < 0)
				return;
			userName = line.substring(s1.length(), i2);
			user.isLogin = true;
			try {
				output_log = new StreamHandler(new FileOutputStream(
						String.format(getClass().getResource("./").getPath()+"output_%s.txt", user.userName), true));
			} catch (Exception e) {
			}
			try {
				input_log = new StreamHandler(new FileOutputStream(
						String.format(getClass().getResource("./").getPath()+"input_%s.txt", user.userName), true));
			} catch (Exception e) {
			}
			user.clearReadLineHander();
			user.setReadLineHander(entRespMsg);
			user.setReadLineHander(chatCmdHandler);
		}
	};
	private ReadLineHandler<StreamHandler> entRespMsg = new ReadLineHandler<StreamHandler>() {
		@Override
		public void action(StreamHandler sender, String line) {
			String[] args = RegexHelper.getSubString(pat2, line);
			if(args==null)return;
			if(!args[1].equals("/msg"))
				return;
			String msg=args[2];
			showMsg(msg);
		}
	};


	private void showMsg(String msg) {
		sys.writeLine(msg);
		sys.flush();
	}

	@Override
	protected void readLineError(Exception e) {
		isLeave = true;
	}

	@Override
	protected void writeError(Exception e) {
		isLeave = true;
	}

	@Override
	public void write(String arg0) {
		super.write(arg0);
		if (isLogin) {
			output_log.write(arg0);
			output_log.flush();
		}
	}

	@Override
	public String readLine() {
		String str = super.readLine();
		if (isLogin) {
			input_log.writeLine(str);
			input_log.flush();
		}
		return str;
	}

	public class ChatCmdHandler implements ReadLineHandler<StreamHandler> {
		Map<String, ReadLineHandler<StreamHandler>> cmdHandler = new HashMap<String, ReadLineHandler<StreamHandler>>();
		public ChatCmdHandler() {
			cmdHandler.put("/post", entRespPost);
			cmdHandler.put("/remove", entRespRemove);
			cmdHandler.put("/move", entRespMove);
			//cmdHandler.put("/chp", entRespChp);
			cmdHandler.put("/kick", entRespKick);
		}

		@Override
		public void action(StreamHandler sender, String line) {
			String[] args = RegexHelper.getSubString(pat1, line);
			if (args == null)
				return;
			ReadLineHandler<StreamHandler> cmd = cmdHandler.get(args[1]);
			if (cmd == null)
				return;
			cmd.action(sender, line);
		}
		
		private ReadLineHandler<StreamHandler> entRespPost = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				String[] args = RegexHelper.getSubString(pat5, line);
				setUserPost(args[2], args[3], args[4], args[5]);
			}
		};

		private ReadLineHandler<StreamHandler> entRespRemove = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				String[] args = RegexHelper.getSubString(pat3, line);
				String userName = args[2];
				String msgid = args[3];
				ChatPost post = posts.get(msgid);
				posts.remove(msgid);
				sys.writeLine(String.format("%s remove message '%s' in %s: %s", userName,
						post.msgid, post.type,post.toString()));
				sys.flush();
				if(post.value instanceof Widget)
					ChatClient.mainFrame.removeWidget(post);
			}
		};
		
		private ReadLineHandler<StreamHandler> entRespMove = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				String[] args = RegexHelper.getSubString(pat4, line);
				String msgid = args[2];
				int x= Integer.parseInt(args[3]);
				int y= Integer.parseInt(args[4]);
				ChatPost post = posts.get(msgid);
				ChatClient.mainFrame.moveWidget(post,x,y);
			}
		};
		private ReadLineHandler<StreamHandler> entRespChp = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				String[] args = RegexHelper.getSubString(pat2, line);
				args = RegexHelper.getSubString(pat2,args[2]);
				String postid = args[1];
				ChatPost post = posts.get(postid);
				try{
					Widget w=(Widget)post.value;
					args = RegexHelper.getSubString(pat2, args[2]);
					int x = Integer.parseInt(args[1]);
					String args2[] = RegexHelper.getSubString(pat2, args[2]);
					int y = 0;
					String cmd="";
					if(args2==null){
						y=Integer.parseInt(args[2]);
					}else{
						y=Integer.parseInt(args2[1]);
						cmd=args2[2];
					}
					w.setLocation(x,y);
					w.parseCommand(cmd);
					ChatClient.mainFrame.moveWidget(post,x,y);
				}catch (Exception e) {
					
				}
				
				
				
			}
		};
		private ReadLineHandler<StreamHandler> entRespKick = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				String[] args = RegexHelper.getSubString(pat2, line);
				String userName = args[2];
				if (userName.equals(ChatRoomClient.this.userName)) {
					isLeave = true;
					sender.writeLine("/leave");
					sender.flush();
					System.exit(0);
				}
			}
		};

	}
}

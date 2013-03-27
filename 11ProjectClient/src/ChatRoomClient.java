import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

public class ChatRoomClient extends StreamHandler {
	public Map<String, ChatPost> posts = new HashMap<String, ChatPost>();
	public StreamHandler sys = null;
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
		this.sys = new StreamHandler(System.in, System.out);
	}

	public static ChatRoomClient connect(String serverHost, int port)
			throws IOException {
		Socket sock = new Socket(serverHost, port);
		ChatRoomClient s = new ChatRoomClient(sock);
		return s;
	}

	public void beginLogin() {
		sys.writeLine("Username:");
		sys.flush();
		// this.setReadLineHander(entDebug);
		this.setReadLineHander(entGetUserName);
		this.setReadLineHander(entRespMsg);
		this.beginAsyncReadline();
		while (_isReading) {
			String line = sys.readLine();
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
								break;
							}
						} catch (Exception e) {
							sys.writeLine("Connect string format error.");
							sys.flush();
							continue;
						}
					}
				}

				if (line.equals("/showpost")) {
					showPost();
					continue;
				}
			}
			this.writeLine(line);
			this.flush();

			if (line.equals("/leave")) {
				isLeave = true;
			}
			if (isLeave) {
				break;
			}
		}

	}

	public void showPost() {
		for (Object obj2 : posts.values().toArray()) {
			ChatPost post = (ChatPost) obj2;
			sys.writeLine(String.format("%s posted message '%s' in %s: %s",
					post.userName, post.msgid, post.type, post.value));
		}
		sys.flush();

	}

	public void setUserPost(String userName, String msgid, String type,
			String value) {
		ChatPost post = new ChatPost(userName, msgid, type, value);
		posts.put(msgid, post);
		sys.writeLine(String.format("%s posted message '%s' in %s: %s",
				post.userName, post.msgid, post.type, post.value));
		sys.flush();
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
				sys.writeLine(String.format("%s remove message '%s': %s", userName,
						post.msgid, post.value));
				sys.flush();
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

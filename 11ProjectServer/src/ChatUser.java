import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatUser extends StreamHandler {
	public ChatRoom room = null;
	public Socket sock = null;
	public String userName = null;
	public boolean isKick = false;
	public boolean isLogin = false;
	public StreamHandler input_log = null;
	public StreamHandler output_log = null;
	public int conID=0;
	private Pattern pat4 =Pattern.compile( "^([\\S]+) ([\\S]+) ([\\S]+) (.*)");
	private Pattern pat3 =Pattern.compile( "^([\\S]+) ([\\S]+) (.*)");
	private Pattern pat2 =Pattern.compile( "^([\\S]+) (.*)");
	private Pattern pat1 =Pattern.compile( "^([\\S]+)");
	private ChatCmdHandler chatCmdHandler=new ChatCmdHandler();
	public ChatUser(ChatRoom room, Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
		this.room = room;
		this.sock = socket;
	}

	public void beginLogin() {
		setReadLineHander(entReqUserName);
		beginAsyncReadline();
	}

	public void dumpPost() {
		for (Object obj2 : room.posts.values().toArray()) {
			ChatPost post = (ChatPost) obj2;
			this.writeLine(String.format("/post %s %s %s %s", post.userName,
					post.msgid,post.type, post.value));
		}
		this.flush();
	}

	private ReadLineHandler<StreamHandler> entReqUserName = new ReadLineHandler<StreamHandler>() {
		@Override
		public void action(StreamHandler sender, String line) {
			ChatUser user = (ChatUser) sender;
			line = line.replace(" ", "");
			line = line.replace("/", "");
			if (line.isEmpty()) {
				user.writeLine("/msg Error: No username is input.");
				user.writeLine("/msg Username:");
				user.flush();
				return;
			}

			if (room.getUser(line) != null) {
				String err = String
						.format("/msg Error: The user '%s' is already online. Please change a name.",
								line);
				user.writeLine(err);
				user.writeLine("/msg Username:");
				user.flush();
			} else {
				user.userName = line;
				room.castWriteLine(String.format(
						"/msg %s is connecting to the chat server.",
						user.userName));
				user.isLogin = true;
				try {
					output_log = new StreamHandler(new FileOutputStream(
							String.format(getClass().getResource("./").getPath()+"output_%s.txt", user.userName),
							true));
				} catch (Exception e) {
				}
				try {
					input_log = new StreamHandler(new FileOutputStream(
							String.format(getClass().getResource("./").getPath()+"input_%s.txt", user.userName),
							true));
				} catch (Exception e) {
				}

				room.users.put(line, user);
				user.writeLine("/msg *******************************************");
				user.writeLine(String.format(
						"/msg ** <%s>, welcome to the chat system.",
						user.userName));
				user.writeLine("/msg *******************************************");
				user.flush();
				dumpPost();
				user.clearReadLineHander();
				user.setReadLineHander(chatCmdHandler);
				String msg = String.format("%s:%d <%s> is Login.", user.sock.getInetAddress().getHostAddress(), user.sock.getPort(),user.userName);
				user.showMsg(msg);
			}
		}
	};

	private void showMsg(String msg) {
		System.out.println(msg);
	}

	@Override
	protected void writeError(Exception e) {

	}

	@Override
	protected void readLineError(Exception e) {
		// super.readLineError(e);
		this.isLogin=false;
		this.isKick=true;
		String msg;
		if(this.userName==null){
			msg = String.format("%s:%d %d is Close", this.sock.getInetAddress().getHostAddress(), this.sock.getPort(),this.conID);
		}
		else{
			msg = String.format("%s:%d <%s> is Exit", this.sock.getInetAddress().getHostAddress(), this.sock.getPort(),this.userName);
		}
		room.castWriteLine(String.format("/msg %s",msg));
		room.sys.writeLine(msg);
		room.sys.flush();
		room.clients.remove(this);
		room.users.remove(this.userName);
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
	public class ChatCmdHandler implements ReadLineHandler<StreamHandler>{
		Map<String,ReadLineHandler<StreamHandler>> cmdHandler=new HashMap<String, ReadLineHandler<StreamHandler>>();
		public ChatCmdHandler () {
			cmdHandler.put("/yell", entReqYell);
			cmdHandler.put("/tell", entReqTell);
			cmdHandler.put("/who",entReqWho);
			cmdHandler.put("/post",entReqPost);
			cmdHandler.put("/remove",entReqRemove);
			cmdHandler.put("/kick",entReqKick);
			cmdHandler.put("/leave",entReqLeave);
		}
		@Override
		public void action(StreamHandler sender, String line) {
			String[] args=RegexHelper.getSubString(pat1, line);
			if(args==null)
				return;
			ReadLineHandler<StreamHandler> cmd=cmdHandler.get(args[1]);
			if(cmd==null)
				return;
			cmd.action(sender, line);
		}
		
		private ReadLineHandler<StreamHandler> entReqYell = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args=RegexHelper.getSubString(pat2, line);
				if(args==null)
					return;
				String msg = args[2];
				room.castWriteLine(String.format("/msg %s yelled: %s",
						user.userName, msg));
			}
		};

		private ReadLineHandler<StreamHandler> entReqTell = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args=RegexHelper.getSubString(pat2, line);
				if(args==null)
					return;
				args=RegexHelper.getSubString(pat2,args[2]);
				if (args==null) {
					String err = String.format("/msg Error: No target was given.");
					user.writeLine(err);
					user.flush();
					return;
				}
				String username = args[1];
				ChatUser u = room.getUser(username);
				if (u == null) {
					String err = String.format(
							"/msg Error: The user '%s' is not online.", username);
					user.writeLine(err);
					user.flush();
					return;
				}
				String msg = args[2];
				u.writeLine(String.format("/msg %s told %s: %s", user.userName,
						u.userName, msg));
				u.flush();
			}
		};
		private ReadLineHandler<StreamHandler> entReqWho = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;				
				user.writeLine("/msg Name\tIP/port");
				for (Object obj : room.clients.toArray()) {
					ChatUser u = (ChatUser) obj;
					if (u.isKick)
						continue;
					if (u.userName == null) {
						user.writeLine(String.format("/msg %s:\t%s/%d",
								"(Unknown)", u.sock.getInetAddress()
										.getHostAddress(), u.sock.getPort()));
						continue;
					}
					if (u == user)
						user.writeLine(String.format("/msg %s:\t%s/%d\t<-- myself",
								u.userName, u.sock.getInetAddress()
										.getHostAddress(), u.sock.getPort()));
					else
						user.writeLine(String.format("/msg %s:\t%s/%d", u.userName,
								u.sock.getInetAddress().getHostAddress(),
								u.sock.getPort()));
				}
				user.flush();
			}
		};

		private ReadLineHandler<StreamHandler> entReqRemove = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args=RegexHelper.getSubString(pat2, line);
				if(args==null)
					return;
				String postid = args[2];
				ChatPost post = room.posts.get(postid);
				if (post == null) {
					user.writeLine("/msg Error: No such msg id.");
					user.flush();
					return;
				}
				room.posts.remove(postid);
				room.castWriteLine(String.format("/remove %s %s", user.userName,
						post.msgid));
			}
		};

		private ReadLineHandler<StreamHandler> entReqPost = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args=RegexHelper.getSubString(pat2, line);
				if(args==null)
					return;
				args=RegexHelper.getSubString(pat2, args[2]);
				if(args==null){
					user.writeLine("/msg Error: No post type.");
					user.flush();
					return;
				}
				String type = args[1];
				if(!type.equals("String")){
					user.writeLine("/msg Error: No such post type.");
					user.flush();
					return;
				}
				String value = args[2];
				ChatPost post = new ChatPost(user.userName, String.valueOf(++room.flowMsgID), type,value);
				room.posts.put(post.msgid, post);
				room.castWriteLine(String.format("/post %s %s %s %s", user.userName,post.msgid, post.type,post.value));
			}
		};
		private ReadLineHandler<StreamHandler> entReqKick = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args=RegexHelper.getSubString(pat2, line);
				if(args==null)
					return;
				String username = args[2];
				ChatUser u = room.getUser(username);
				if (u == null) {
					String err = String.format(
							"/msg Error: The user '%s' is not online.", username);
					user.writeLine(err);
					user.flush();
					return;
				}
				String cast = String.format("/kick %s", u.userName);
				room.castWriteLine(cast);
				u.clearReadLineHander();
				u.isKick = true;
				room.users.remove(u.userName);
				u.setReadLineHander(entReqLeave);
			}
		};

		private ReadLineHandler<StreamHandler> entReqLeave = new ReadLineHandler<StreamHandler>() {
			@Override
			public void action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				user.close();
			}
		};

	}
}

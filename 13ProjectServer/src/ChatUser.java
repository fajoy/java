import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import widgets.*;

public class ChatUser extends StreamHandler {
	public ChatRoom room = null;
	public Socket sock = null;
	public String userName = null;
	public boolean isKick = false;
	public boolean isLogin = false;
	public StreamHandler input_log = null;
	public StreamHandler output_log = null;
	public int conID = 0;
	private Pattern pat4 = Pattern.compile("^([\\S]+) ([\\S]+) ([\\S]+) (.*)");
	private Pattern pat3 = Pattern.compile("^([\\S]+) ([\\S]+) (.*)");
	private Pattern pat2 = Pattern.compile("^([\\S]+) (.*)");
	private Pattern pat1 = Pattern.compile("^([\\S]+)");
	private ChatCmdHandler chatCmdHandler = new ChatCmdHandler();

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
					post.msgid, post.type, post.toString()));
		}
		this.flush();
	}

	private ReadLineHandler<StreamHandler> entReqUserName = new ReadLineHandler<StreamHandler>() {
		@Override
		public boolean action(StreamHandler sender, String line) {
			ChatUser user = (ChatUser) sender;
			line = line.replace(" ", "");
			line = line.replace("/", "");
			if (line.isEmpty()) {
				user.writeLine("/msg Error: No username is input.");
				user.writeLine("/msg Username:");
				user.flush();
				return true;
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
				String msg = String.format("%s:%d <%s> is Login.", user.sock
						.getInetAddress().getHostAddress(),
						user.sock.getPort(), user.userName);
				user.showMsg(msg);
			}
			return true;
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
		this.isLogin = false;
		this.isKick = true;
		String msg;
		if (this.userName == null) {
			msg = String.format("%s:%d %d is Close", this.sock.getInetAddress()
					.getHostAddress(), this.sock.getPort(), this.conID);
		} else {
			msg = String.format("%s:%d <%s> is Exit", this.sock
					.getInetAddress().getHostAddress(), this.sock.getPort(),
					this.userName);
		}
		room.castWriteLine(String.format("/msg %s", msg));
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

	public class ChatCmdHandler implements ReadLineHandler<StreamHandler> {
		Map<String, ReadLineHandler<StreamHandler>> cmdHandler = new HashMap<String, ReadLineHandler<StreamHandler>>();

		public ChatCmdHandler() {
			cmdHandler.put("/yell", entReqYell);
			cmdHandler.put("/tell", entReqTell);
			cmdHandler.put("/who", entReqWho);
			cmdHandler.put("/post", entReqPost);
			cmdHandler.put("/remove", entReqRemove);
			cmdHandler.put("/move", entReqMove);
			cmdHandler.put("/change", entReqChange);
			cmdHandler.put("/kick", entReqKick);
			cmdHandler.put("/leave", entReqLeave);
		}

		@Override
		public boolean action(StreamHandler sender, String line) {
			String[] args = RegexHelper.getSubString(pat1, line);
			if (args == null)
				return false;
			ReadLineHandler<StreamHandler> cmd = cmdHandler.get(args[1]);
			if (cmd == null){
				sender.writeLine(String.format("/msg (Unknow CMD): %s",line));
				showMsg(String.format("%s(Unknow CMD): %s", ChatUser.this.userName,line));
				sender.flush();
				return false;
			}
			if (!cmd.action(sender, line)){
				sender.writeLine(String.format("/msg (Error format CMD): %s",line));				
				showMsg(String.format("%s(Error format CMD): %s", ChatUser.this.userName,line));
				sender.flush();
				return false;
			}
			sender.flush();
			return true;
			
		}

		private ReadLineHandler<StreamHandler> entReqYell = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				String msg = args[2];
				room.castWriteLine(String.format("/msg %s yelled: %s",
						user.userName, msg));
				return true;
			}
		};

		private ReadLineHandler<StreamHandler> entReqTell = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				args = RegexHelper.getSubString(pat2, args[2]);
				if (args == null) {
					String err = String
							.format("/msg Error: No target was given.");
					user.writeLine(err);
					user.flush();
					return true;
				}
				String username = args[1];
				ChatUser u = room.getUser(username);
				if (u == null) {
					String err = String.format(
							"/msg Error: The user '%s' is not online.",
							username);
					user.writeLine(err);
					user.flush();
					return true;
				}
				String msg = args[2];
				u.writeLine(String.format("/msg %s told %s: %s", user.userName,
						u.userName, msg));
				u.flush();
				return true;
			}
		};
		private ReadLineHandler<StreamHandler> entReqWho = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
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
						user.writeLine(String.format(
								"/msg %s:\t%s/%d\t<-- myself", u.userName,
								u.sock.getInetAddress().getHostAddress(),
								u.sock.getPort()));
					else
						user.writeLine(String.format("/msg %s:\t%s/%d",
								u.userName, u.sock.getInetAddress()
										.getHostAddress(), u.sock.getPort()));
				}
				user.flush();
				return true;
			}
		};

		private ReadLineHandler<StreamHandler> entReqRemove = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				String postid = args[2];
				ChatPost post = room.posts.get(postid);
				if (post == null) {
					user.writeLine("/msg Error: No such msg id.");
					user.flush();
					return true;
				}
				if(!post.userName.equals(user.userName)){
					user.writeLine("/msg Error: No Permissions.");
					user.flush();
					return true;
				}
				room.posts.remove(postid);
				room.castWriteLine(String.format("/remove %s %s",
						user.userName, post.msgid));
				return true;
			}
		};

		private ReadLineHandler<StreamHandler> entReqPost = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				args = RegexHelper.getSubString(pat2, args[2]);
				if (args == null) {
					user.writeLine("/msg Error: No post type.");
					user.flush();
					return true;
				}
				String type = args[1];
				Object value = null;
				if (type.equals("String")) {
					// /post String msg
					value = args[2];
				}

				
				if (value==null) {
						
					// /post ClassName x y data1 data2 ...
					// /post RectangleWidget 10 10 #000000 100 100
					// /post CircleWidget 10 10 #000000 #0000FF 3
					// /post JugglerWidget 10 10 #000000 100 100
					
					Widget w = null;
					try {
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
						x=(x<0)?0:x;
						y=(y<0)?0:y;
						x=(x>32767)?32767:x;
						y=(y>32767)?32767:y;
						Class widgetClass=null;
						try{
						widgetClass=Class.forName("widgets."+type);
						} catch (Exception e) {
							user.writeLine(String.format(
									"/msg Error: No such post type.", type));
							user.flush();
							return true;
						}
						Object obj=widgetClass.newInstance();
						if(obj instanceof Widget)
						{
							w=(Widget)obj;
							w.setLocation(x, y);
							if(!cmd.equals("")){
								w.parseCommand(cmd);
							}
							value = w;
						}else{
							w=null;
						}
					} catch (Exception e) {
						user.writeLine(String.format(
								"/msg Error: Post %s format error.", type));
						user.flush();
						return true;
					}
					
				}

				if (value == null) {
					user.writeLine("/msg Error: No such post type.");
					user.flush();
					return true;
				}
				ChatPost post = new ChatPost(user.userName,String.valueOf(++room.flowMsgID), type, value);
				room.posts.put(post.msgid, post);
				room.castWriteLine(String.format("/post %s %s %s %s",	user.userName, post.msgid, post.type,post.toString()));
				return true;
			}
		};
		private ReadLineHandler<StreamHandler> entReqChange = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				args = RegexHelper.getSubString(pat2,args[2]);
				String postid = args[1];
				ChatPost post = room.posts.get(postid);
				if (post == null) {
					user.writeLine("/msg Error: No such msg id.");
					user.flush();
					return true;
				}
				if(!(post.value instanceof Widget)){
					user.writeLine("/msg Error: No post isn't Widget.");
					user.flush();
					return true;
				}
				if(!post.userName.equals(user.userName)){
					user.writeLine("/msg Error: No Permissions.");
					user.flush();
					return true;
				}
				Widget w=(Widget)post.value;
				try{
					String cmd=args[2];
					w.parseCommand(cmd);
				}catch (Exception e) {
					user.writeLine("/msg Error: Change format error.");
					user.flush();
					return true;
				}
				room.castWriteLine(String.format("/change %s %s", post.msgid,w.toCommand()));
				return true;
			}
		};
		
		private ReadLineHandler<StreamHandler> entReqMove = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				args = RegexHelper.getSubString(pat2,args[2]);
				String postid = args[1];
				ChatPost post = room.posts.get(postid);
				if (post == null) {
					user.writeLine("/msg Error: No such msg id.");
					user.flush();
					return true;
				}
				if(!(post.value instanceof Widget)){
					user.writeLine("/msg Error: No post isn't Widget.");
					user.flush();
					return true;
				}
				if(!post.userName.equals(user.userName)){
					user.writeLine("/msg Error: No Permissions.");
					user.flush();
					return true;
				}
				Widget w=(Widget)post.value;
				try{
					args = RegexHelper.getSubString(pat2, args[2]);
					int x=Integer.parseInt(args[1]);
					int y=Integer.parseInt(args[2]);
					x=(x<0)?0:x;
					y=(y<0)?0:y;
					
					w.setLocation(x,y);
				}catch (Exception e) {
					user.writeLine("/msg Error: Move format error.");
					user.flush();
					return true;
				}
				room.castWriteLine(String.format("/move %s %s %s", post.msgid,w.getX(),w.getY()));
				return true;
			}
		};
		private ReadLineHandler<StreamHandler> entReqKick = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				String[] args = RegexHelper.getSubString(pat2, line);
				if (args == null)
					return false;
				String username = args[2];
				ChatUser u = room.getUser(username);
				if (u == null) {
					String err = String.format(
							"/msg Error: The user '%s' is not online.",
							username);
					user.writeLine(err);
					user.flush();
					return true;
				}
				String cast = String.format("/kick %s", u.userName);
				room.castWriteLine(cast);
				u.clearReadLineHander();
				u.isKick = true;
				room.users.remove(u.userName);
				u.setReadLineHander(entReqLeave);
				return true;
			}
		};

		private ReadLineHandler<StreamHandler> entReqLeave = new ReadLineHandler<StreamHandler>() {
			@Override
			public boolean action(StreamHandler sender, String line) {
				ChatUser user = (ChatUser) sender;
				user.close();
				return true;
			}
		};
		

	}
}

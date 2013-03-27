public class ChatPost {
	public String userName=null;
	public String value=null;
	public String type=null;
	public String msgid=null;
	public ChatPost(String userName,String msgid,String type,String value) {
		this.type=type;
		this.userName=userName;
		this.msgid=msgid;
		this.value=value;
	}	
}

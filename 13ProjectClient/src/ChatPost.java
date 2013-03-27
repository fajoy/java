import widgets.Widget;

public class ChatPost {
	public String userName=null;
	public Object value=null;
	public String type=null;
	public String msgid=null;
	public ChatPost(String userName,String msgid,String type,Object value) {
		this.type=type;
		this.userName=userName;
		this.msgid=msgid;
		this.value=value;
	}	
	
	
	@Override
	public String toString() {
		if( value instanceof Widget ){
			Widget w=(Widget)value;
			return String.format("%d %d %s", w.getX(),w.getY(),w.toCommand()); 
		}
		return value.toString();
		//return super.toString();
	}
}

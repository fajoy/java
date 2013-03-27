package fpTree;

import java.util.*;


class FPNode{
	FPNode parent;
	int frequency;
	HeadItem head;
	Map<String, FPNode> childs=new LinkedHashMap<String,FPNode>();
	FPNode(HeadItem head) {
		this.head=head;
		this.head.nodes.add(this);
	}
	FPNode(FPNode node,HeadItem head) {
		this.head=head;
		this.head.nodes.add(this);
		node.childs.put(head.Item, this);
		this.parent=node;
	}
	FPNode getChildNode(String item){
		return childs.get(item);
	}
	public String getItem(){
		return head.Item;
	}	

	public List<String> getNodePath(){
		List<String> path=new ArrayList<String>();
		FPNode n=this.parent;
		while(n!=null){
			path.add(n.getItem());
			n=n.parent;
		}
		return path;
	}
	public void showNode() {
		String prev="null";
		if(parent!=null)
			prev=this.parent.getItem();
		System.out.println(String.format("%s->%s:%d",prev,getItem(),this.frequency));
		for (FPNode node : this.childs.values()) {
			node.showNode();
		}
	}
	
}

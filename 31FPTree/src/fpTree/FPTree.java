package fpTree;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
public class FPTree {
	public String fileName;
	public int minSupport;
	
	public LinkedHashMap<String,HeadItem> table=new LinkedHashMap<String, HeadItem>();
    public LinkedHashMap<String,FPNode> roots=new LinkedHashMap<String,FPNode>();
	
	public FPTree(HeadTable headtable,int minSupport) throws IOException{
		this.fileName=headtable.fileName;
		this.minSupport=minSupport;
		for(HeadItem h : headtable.table.values()){
			if(h.frequency>=minSupport){
				HeadItem head=new HeadItem(h.Item);
				head.order=h.order;
				table.put(head.Item,head);
			}
			else
				break;
		}
		createTree();
		createCondtionlFPTree();
	}
	
	private void createTree() throws IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader( new FileInputStream(fileName)));
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter( new FileOutputStream("test.dat",false)));
		String tid_old=null;
		List<String> items=new ArrayList<String>();
		while(reader.ready()){
			String line=reader.readLine();
			String[] args=line.split("::", 3);
			String tid=args[0];
			String item=args[1];	
			
			if(!tid.equals(tid_old)){
				createNode(items);
				items=new ArrayList<String>();
				writer.append(String.format("\n%s\t", tid));
				tid_old=tid;
			}
			if(table.containsKey(item)){
				if(items.size()==0){
				writer.append(String.format("%s", item));
				}else{
					writer.append(String.format(",%s", item));	
				}
				items.add(item);
			}
		}		
		createNode(items);

		writer.flush();
		writer.close();
	}
	private void createCondtionlFPTree(){
		for(HeadItem head:table.values()){
			ConditionalFPTree cond=new ConditionalFPTree(null, head, this.minSupport);
		}
	}
	
	public void createNode(List<String> items){
		Collections.sort(items,sortItemFunc	);
		FPNode node=null;
		for(String item:items){
			node=createNode(node,item);
			node.frequency+=1;
			node.head.frequency+=1;
		}
	}
	
	public Comparator<String> sortItemFunc=	new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			//遞增排序
			return (table.get(o1).order>table.get(o2).order)?1:-1;
		}
	};
	
	public FPNode createNode(FPNode node,String item){
		if(node==null){
			FPNode root=this.roots.get(item);
			if(root==null){
				root=new FPNode(table.get(item));
				this.roots.put(item,root);
			}
			return root;
		}
		
		FPNode childNode= node.getChildNode(item);
		if(childNode==null)
			childNode=new FPNode(node,table.get(item));
		return childNode;
	}
	public void showTree() {
		for (FPNode root : this.roots.values()) {
			root.showNode();
		}
	}
	public void showTable() {
		for (HeadItem head : this.table.values()) {
			System.out.println(head.toString());
		}
	}

}
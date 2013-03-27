package fpTree;

import java.util.*;
import java.util.Map.Entry;

public class ConditionalFPTree {
	public List<String> patternBase = new ArrayList<String>();
	public LinkedHashMap<String, HeadItem> table = new LinkedHashMap<String, HeadItem>();
	public LinkedHashMap<String, FPNode> roots = new LinkedHashMap<String, FPNode>();

	public ConditionalFPTree(List<String> base, HeadItem head, int minSupport) {
		if (base != null)
			patternBase.addAll(base);
		patternBase.add(head.Item);
		LinkedHashMap<List<String>, Integer> itemPaths = new LinkedHashMap<List<String>, Integer>();

		// count head table
		for (FPNode terminal_node : head.nodes) {
			List<String> path = terminal_node.getNodePath();
			itemPaths.put(path, terminal_node.frequency);
			for (String item : path) {
				this.addHeadFrequeny(item, terminal_node.frequency);
			}
		}
		this.sortTable();
		// create node
		for (Map.Entry<List<String>, Integer> entry : itemPaths.entrySet()) {
			List<String> path = entry.getKey();
			createNode(path, entry.getValue());
		}

		//create New ConditionalFPTree
		this.outputResult(minSupport);
		
		//this.createCondtionlFPTree(minSupport);
		
		
	}
	public void outputResult(int minSupport){
		
		if(table.size()>0){
			Iterator<HeadItem> i=table.values().iterator();
			HeadItem newHead=i.next();
			if (newHead.frequency < minSupport) return;
			String baseKey=Join(patternBase,",");
			System.out.print(String.format("%s\t",baseKey));
			System.out.print(String.format("%s:%d",newHead.Item,newHead.frequency));
			while(i.hasNext()){
				newHead=i.next();
				if (newHead.frequency >= minSupport) {
					System.out.print(String.format(",%s:%d",newHead.Item,newHead.frequency));
				} else
					break;
			}
			System.out.print("\n");
		}
	}
	private void createCondtionlFPTree(int minSupport){
		for (HeadItem newHead : table.values()) {
			if (newHead.frequency >= minSupport) {
				ConditionalFPTree condTree = new ConditionalFPTree(patternBase,	newHead, minSupport);
			} else
				break;
		}
	}
	public String Join(List<String> strings,String p){
		StringBuffer sb=new StringBuffer();
		Iterator<String> i=strings.iterator();
		if(i.hasNext())
			sb.append(i.next());
		while(i.hasNext()){
			sb.append(p);
			sb.append(i.next());
		}
		return sb.toString();
	}
	public void createNode(List<String> items, int frequency) {
		Collections.sort(items, sortItemFunc);
		FPNode node = null;
		for (String item : items) {
			node = createNode(node, item);
			node.frequency += frequency;
		}
	}

	public FPNode createNode(FPNode node, String item) {
		if (node == null) {
			FPNode root = this.roots.get(item);
			if (root == null) {
				root = new FPNode(table.get(item));
				this.roots.put(item, root);
			}
			return root;
		}
		FPNode childNode = node.getChildNode(item);
		if (childNode == null)
			childNode = new FPNode(node, table.get(item));
		return childNode;
	}

	public void sortTable() {
		List<Map.Entry<String, HeadItem>> mapEntry = new ArrayList<Map.Entry<String, HeadItem>>(
				table.entrySet());
		Collections.sort(mapEntry, sortHeadTableFunc);
		table = new LinkedHashMap<String, HeadItem>();
		int i = 0;
		for (Map.Entry<String, HeadItem> m : mapEntry) {
			table.put(m.getKey(), m.getValue());
			m.getValue().order = i++;
		}
	}

	public void addHeadFrequeny(String item, int frequeny) {
		HeadItem head = this.table.get(item);
		if (head == null) {
			head = new HeadItem(item);
			this.table.put(item, head);
		}
		head.frequency += frequeny;
	}

	public static Comparator<Map.Entry<String, HeadItem>> sortHeadTableFunc = new Comparator<Map.Entry<String, HeadItem>>() {
		@Override
		public int compare(Entry<String, HeadItem> o1,
				Entry<String, HeadItem> o2) {
			// 遞減排序
			return (o1.getValue().frequency > o2.getValue().frequency) ? -1 : 1;
		}
	};
	public Comparator<String> sortItemFunc = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			// 遞增排序
			return (table.get(o1).order > table.get(o2).order) ? 1 : -1;
		}
	};

}

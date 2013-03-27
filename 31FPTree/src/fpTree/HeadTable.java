package fpTree;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class HeadTable {
	public Map<String,HeadItem> table=new LinkedHashMap<String, HeadItem>();
	public String fileName;
	public HeadTable(String fileName) throws IOException{
		this.fileName=fileName;
		
		BufferedReader reader=new BufferedReader(new InputStreamReader( new FileInputStream(fileName)));
		
		//Scan到headTable
		while(reader.ready()){
			String line=reader.readLine();
			String[] args=line.split("::", 3);
			if(args==null||args.length<2)
				continue;
			//String tid=args[0];
			String item=args[1];
			this.addHeadFrequeny(item, 1);
		}		
		//this.headTable.showTable();
		this.sortTable();
	}
	//對headtable增加item 
	public void addHeadFrequeny(String item, int frequeny)
    {
            HeadItem head=this.table.get(item);
            if (head == null)
            {
                head = new HeadItem(item);
                this.table.put(item, head);
            }
            head.frequency += frequeny;
    }
	//排序
	public static Comparator<Map.Entry<String, HeadItem>> sortHeadTableFunc=	new Comparator<Map.Entry<String, HeadItem>>() {
		@Override
		public int compare(Entry<String, HeadItem> o1, Entry<String, HeadItem> o2) {
			//遞減排序
			return (o1.getValue().frequency>o2.getValue().frequency)?-1:1;
		}
	};
	public void sortTable(){
		List<Map.Entry<String, HeadItem>> mapEntry = new ArrayList<Map.Entry<String, HeadItem>>(table.entrySet());
		Collections.sort(mapEntry,sortHeadTableFunc	);
		table=new LinkedHashMap<String, HeadItem>();
		int i=0;
		for(Map.Entry<String, HeadItem> m : mapEntry){
			table.put(m.getKey(), m.getValue());
			m.getValue().order=i++;
		}
	}
	public void showTable() {
		for (HeadItem head : table.values()) {
			System.out.println(head.toString());
		}
	}
	public int getOrder(String item){
		return table.get(item).order;
	}
	public FPTree createFPTree(int minSupport) throws IOException{
		FPTree fptree=new FPTree(this,minSupport);
		return fptree;
	}
}
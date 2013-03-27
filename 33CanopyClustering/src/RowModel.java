
import java.util.*;
import java.util.Map.Entry;
public class RowModel {
	public String rowId="";
	LinkedHashMap<String, Integer> items= new LinkedHashMap<String, Integer>();
	public static void main(String args[]){	
		RowModel row=RowModel.parse("0\t1:0,2,3");
		System.out.format("%s\n", row.toRowString());
		RowModel row2=RowModel.parse(row.toRowString());

	}
	public static RowModel parse(String line){
		String[] args=line.split("\t", 2);
		RowModel data=new RowModel();
		data.rowId=args[0];
		String[] moveids=args[1].split(",");
		for(int i=0;i<moveids.length;i++)
		{
			if(!moveids[i].isEmpty())
				data.items.put(moveids[i], 1);
		}
		return data;
	}
	public String toRowString() {
		StringBuffer sb=new StringBuffer();
		sb.append(String.format("%s\t",rowId));
		Iterator<String> i= items.keySet().iterator();
		if(i.hasNext()){
			String entry=i.next();
			sb.append(String.format("%s",entry));
		}
		while(i.hasNext()){
			String entry=i.next();
			sb.append(String.format(",%s",entry));
		}
		return sb.toString();
	};
	public double getJaccardSimilarity(RowModel obj){
		if(this.items.size()==0||obj.items.size()==0){
			if(this.items.size()==0&&obj.items.size()==0)
				return 1.0d;
			else
				return 0.0d;
		}
		int same=0;
		for(String moveid:obj.items.keySet()){
			if(this.items.containsKey(moveid))
				same++;
		}
		int total=this.items.size()+obj.items.size()-same;
		double d=1f;
		d=(same+0d)/total;
		return d;
	}
	/*
	public double getCosineSimilarity(RowModel obj){
		if(this.items.size()==0){
			if(obj.items.size()>0)
				return 0.0d;
			else
				return 1.0d;
		}
		int same=0;
		for(String moveid:obj.items.keySet()){
			if(this.items.containsKey(moveid))
				same++;
		}
		
		double abs=Math.pow(this.items.size(), 0.5)*Math.pow(obj.items.size(), 0.5);
		double d=1f;
		d=same/abs;
		return d;
	}*/
}

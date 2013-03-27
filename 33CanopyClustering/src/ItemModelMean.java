

import java.util.*;
import java.util.Map.Entry;



public class ItemModelMean {
	public static void main(String args[]){
		
		//ItemModelMean mean=ItemModelMean.parse("0\t1:0.1,2:1,3:0.5");
		ItemModelMean mean=ItemModelMean.parse("0\t");
		System.out.format("%s\n", mean.toMeanString(false));
		ItemModelMean mean2=ItemModelMean.parse(mean.toMeanString(true));
		mean2.showData();
	}
	public static ItemModelMean parse(String line){
		ItemModelMean mean=new ItemModelMean();
		String[] args=line.split("\t",2);
		mean.meanId=args[0];
		String[] value=args[1].split(",");
		Double sum=0d;
		for (String string : value) {
			String[] v=string.split(":");
			if(v.length==1)
				continue;
			double m= Double.parseDouble(v[1]);
			sum+=m*m;
			mean.itemMean.put(v[0], m);
		}
		mean.distanceCache=Math.pow(sum, 0.5);
		return mean;
	}
	
	public String meanId="";
	public HashMap<String, Double> itemMean= new HashMap<String, Double>();
	private double distanceCache=0;
	private ItemModelMean () {
		
	}
	public ItemModelMean(ItemModelMean old,Collection<RowModel> items){
		this.meanId=old.meanId;
		this.itemMean= new HashMap<String, Double>(old.itemMean);
		this.setMean(items);
	}
	public ItemModelMean(String meanId,Collection<RowModel> items){
		this.meanId=meanId;
		this.setMean(items);
	}
	private void setMean(Collection<RowModel> items) {
		if (items.size()==0)return;
			
		itemMean=new HashMap<String, Double>();
		for (RowModel item : items) {
			for (Entry<String, Integer> entry:item.items.entrySet()){
				String key=entry.getKey();
				double sum=0;
				if(itemMean.containsKey(key))
					sum= itemMean.get(key);
				sum+=entry.getValue();
				this.itemMean.put(key, sum);
			}	
		}
		int size=items.size();
		double sum=0;
		for (Entry<String, Double> entry:itemMean.entrySet()){
			String key=entry.getKey();
			double mean= entry.getValue()/size;
			entry.setValue(mean);
			sum+=mean*mean;
		}
		this.distanceCache=Math.pow(sum, 0.5);
	}
	
	public double getCosineSimilarity(RowModel row){
		if(row.items.size()==0||this.itemMean.size()==0){
			if(row.items.size()==0&&this.itemMean.size()==0)
				return 1.0d;
			else
				return 0.0d;
		}
		double same=0;
		for(Entry<String, Integer> entry:row.items.entrySet()){
			String key=entry.getKey();
			if(this.itemMean.containsKey(key)){
				same+=this.itemMean.get(key)*entry.getValue();
			}
		}
		
		double abs=this.distanceCache*Math.pow(row.items.size(), 0.5);
		double d=1f;
		d=same/abs;
		return d;
	}
	public String toMeanString(boolean hasId) {
		StringBuffer sb=new StringBuffer();
		if(hasId)
			sb.append(String.format("%s\t",meanId));
		Iterator<Entry<String, Double>> i= itemMean.entrySet().iterator();
		if(i.hasNext()){
			Entry<String, Double> entry=i.next();
			sb.append(String.format("%s:%f",entry.getKey(),entry.getValue()));
		}
		while(i.hasNext()){
			Entry<String, Double> entry=i.next();
			sb.append(String.format(",%s:%f",entry.getKey(),entry.getValue()));
		}
		return sb.toString();
	};
	
	public void showData(){
		System.out.format("meanid=%s\t",meanId);
		Iterator<Entry<String, Double>> i= itemMean.entrySet().iterator();
		if(i.hasNext()){
			Entry<String, Double> entry=i.next();
			System.out.format("%s:%f",entry.getKey(),entry.getValue());
		}
		while(i.hasNext()){
			Entry<String, Double> entry=i.next();
			System.out.format(",%s:%f",entry.getKey(),entry.getValue());
		}
		System.out.println();
		//System.out.format("\nmeanid=%s moveidsize=%d \n",meanId,this.MoveIDs.size());
	}
}

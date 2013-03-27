import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;






public class KMeanClustering {
	public static void main(String[] args) throws IOException  {
		URL url = CanopyClustering.class.getResource("moveid.dat");
		CanopyClustering c= new CanopyClustering();
		//c.T1=0.32;
		//c.T2=0.32;
		c.parseData(url.getFile());
		//c.setFastCanopySet();
		//c.showCanopy();
		KMeanClustering means=new KMeanClustering(c);
		//c.showData();
		//c.showDistance();
		//means.showMean();
		//means.showGroup();
		//means.getMeanGroups();
		
		batchTest(c);
	}

	public KMeanClustering(CanopyClustering c){
		List<ItemModelMean> ms=c.toMeans(true);
		for(ItemModelMean m:ms){
			MeanGroup mg=new MeanGroup(m);
			groups.put(m.meanId, mg);
		}
		items=new HashMap<String, RowModel>(c.rows);
	}
	
	public KMeanClustering(){
		
	}
	public void addMean(String line){
		ItemModelMean m=ItemModelMean.parse(line);
		groups.put(m.meanId, new MeanGroup(m));
	}
	private static  void batchTest(CanopyClustering c){
		for(double t=0.0;t<=1.0;t+=0.01){
			c.T2=t;
			c.T1=t;
			c.canopys.clear();
			long time1=System.currentTimeMillis();
			c.setFastCanopySet();
			time1=System.currentTimeMillis()-time1;
			//c.showCanopy();
			//System.out.flush();
			KMeanClustering means=new KMeanClustering(c);
			long time2=System.currentTimeMillis();
			means.getMeanGroups();
			time2=System.currentTimeMillis()-time2;
			
			means.showGroup();
			//System.out.format("T\tk\ttime\tkmeanrun\ttime\n");
			System.out.format("@%f\t%d\t%d\t%d\t%d\n",t,c.canopys.size(),time1,means.round,time2);
			if(c.canopys.size()==c.rows.size())
				break;
		}
	}	
	public ItemModelMean getCloseMean(Collection<MeanGroup> collection,RowModel row){
		Iterator<MeanGroup> i=collection.iterator();
		ItemModelMean mean=i.next().mean;
		double max=mean.getCosineSimilarity(row);
		while(i.hasNext()){
			ItemModelMean comp=i.next().mean;
			double  d=comp.getCosineSimilarity(row);
			if(d>max){
				mean=comp;
				max=d;
			}
		}
		return mean;
	}
	public HashMap<String, RowModel> items=null;
	public HashMap<String,MeanGroup> groups=new HashMap<String, MeanGroup>();
	public int round=0;
	public void getMeanGroups(){
		round=1;
		
		//System.out.format("run %d\n",run++);
		HashMap<String,MeanGroup> g=this.getNewGroup(this.groups);
		while(isDiff(groups, g)){
			groups=g;	
			g=this.getNewGroup(this.groups);
			//this.showGroup();
			//this.showMean();
			round++;

		}
		
	}
	private HashMap<String,MeanGroup> getNewGroup(HashMap<String,MeanGroup>  old){
		HashMap<String,MeanGroup> groups=new HashMap<String,MeanGroup>();
		//dup mean
		for(MeanGroup g:old.values()){
			ItemModelMean m=new ItemModelMean(g.mean,g.items.values());
			groups.put(m.meanId, new MeanGroup(m));
		}
		//group item
		for(RowModel item:items.values()){
			ItemModelMean mean=getCloseMean(old.values(), item);
			groups.get(mean.meanId).items.put(item.rowId,item);
		}
		//create mean vector
		for(MeanGroup mg:groups.values()){
			mg.mean=new ItemModelMean(mg.meanId, mg.items.values());
			//???
			//mean group is null~ 
			/*
			if(mg.mean.itemMean.size()==0){
				HashMap<String, Double> oldmeanItem=old.get(mg.meanId).mean.itemMean;
				mg.mean.itemMean=new HashMap<String, Double>(oldmeanItem);
			}*/
		}
		return groups;
	}
	public static boolean isDiff(HashMap<String,MeanGroup>  gs1,HashMap<String,MeanGroup>  gs2){
		for(String id :gs1.keySet()){
			MeanGroup mg1=gs1.get(id);
			MeanGroup mg2=gs2.get(id);
			ItemModelMean m1=mg1.mean;
			ItemModelMean m2=mg2.mean;
			if(m1.itemMean.size()!=m2.itemMean.size())
				return true;
			for(String mi: m1.itemMean.keySet()){
				if(!m2.itemMean.containsKey(mi))
					return true;
				double d1=m1.itemMean.get(mi);
				double d2=m2.itemMean.get(mi);
				if(m1.itemMean.size()!=m2.itemMean.size())
					return true;	
			}
		}
		
		return false;
	}
	
	public void showGroup(){
		System.out.format("gid:count\tid:distance\n");
		for(MeanGroup g:groups.values()){
			System.out.format("%s:%d\t",g.mean.meanId,g.items.size());
			if(g.items.size()==0){
				System.out.format("\n");
				continue;
			}
			Iterator<RowModel> i= g.items.values().iterator();
			RowModel row=i.next();
			//System.out.format("%s",obj.UserId);
			
			System.out.format("%s:%f",row.rowId,g.mean.getCosineSimilarity(row));
			int show_C=0;
			while(i.hasNext()){
				row=i.next();
				//System.out.format(",%s",obj.UserId);
				if(show_C<10){
					show_C++;
					System.out.format(",%s:%f",row.rowId,g.mean.getCosineSimilarity(row));
				}
			}
			System.out.format("\n");
			//g.mean.showData();
		}
	}
	public void showMean(){
		System.out.format("mid\titemid:vector\n");
		for(MeanGroup g:groups.values()){
			System.out.format("%s:%d\t",g.mean.meanId,g.items.size());
			ItemModelMean m=g.mean;
			
			//System.out.format("%s",obj.UserId);
			if(m.itemMean.size()>0){
			Iterator<Entry<String, Double>> ei=m.itemMean.entrySet().iterator();
			Entry<String, Double> en=ei.next();
			System.out.format("%s:%f",en.getKey(),en.getValue());
			while(ei.hasNext())
				en=ei.next();
				System.out.format(",%s:%f",en.getKey(),en.getValue());
			}
			System.out.format("\n");
			//g.mean.showData();
		}
	}
	public class MeanGroup{
		ItemModelMean mean;
		String meanId;
		public HashMap<String,RowModel> items=new HashMap<String,RowModel>();
		public MeanGroup(ItemModelMean obj){
			this.mean=obj;
			this.meanId=obj.meanId;
		}
	}
}

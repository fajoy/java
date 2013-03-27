

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CanopyModel{
	public RowModel center;
	public Map<String,RowModel> items=new LinkedHashMap<String,RowModel>();
	public CanopyModel(RowModel center){
		this.center=center;
		items.put(center.rowId,center);
	}
	public ItemModelMean toMean(String meanId,boolean isPoint){
		if(isPoint){
			List<RowModel> centerP=new ArrayList<RowModel>();
			centerP.add(center);
			return new ItemModelMean(meanId,centerP);
		}
		else{
			return new ItemModelMean(meanId,items.values());
		}
	
	}
}
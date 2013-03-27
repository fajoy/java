import java.util.*;
import java.util.Map.Entry;
public class SortSample {

	public LinkedHashMap<Integer,Integer> map=new LinkedHashMap<Integer, Integer>();
	public static void main(String[] args) {
		SortSample s=new SortSample();
		s.show();
		System.out.println("sort");
		s.sort();
		s.show();
	}
	public SortSample(){
		
		map.put(0, 9);
		map.put(1, 8);
		map.put(2, 7);
		map.put(3, 6);
		map.put(4, 5);
		map.put(6, 3);
		map.put(7, 0);
		map.put(8, 1);
		map.put(9, 2);
		map.put(5, 4);
		
	}
	public void sort(){		
		List<Map.Entry<Integer, Integer>> mapEntry = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(mapEntry,
		new Comparator<Map.Entry<Integer,Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return (o1.getValue()>o2.getValue())?1:-1;
			}
		}
		);
		map=new LinkedHashMap<Integer, Integer>();
		for(Map.Entry<Integer, Integer> m : mapEntry){
			map.put(m.getKey(), m.getValue());
		}
		
	}
	 
		
		
	public void show(){
		for (Integer i : map.keySet()) {
			System.out.println(String.format("%d:%d",i,map.get(i) ));
		}
	}
}

/* 執行結果
0:9
1:8
2:7
3:6
4:5
6:3
7:0
8:1
9:2
5:4
sort
7:0
8:1
9:2
6:3
5:4
4:5
3:6
2:7
1:8
0:9
*/

import java.util.*;
import java.io.*;
public class SerializableSample {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
			new SerializableSample();
	}
	public SerializableSample() throws FileNotFoundException, IOException, ClassNotFoundException{
		String path=getClass().getResource("./").getPath()+"test.txt";
		LinkedHashMap<String, Integer> map =new LinkedHashMap<String, Integer>();
		map.put("test", 1);
		map.put("test2", 2);
		map.put("test3", 3);
		ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(path));
		out.writeObject(map);
		out.close();
		ObjectInputStream in =new ObjectInputStream(new FileInputStream(path));
		map =(LinkedHashMap<String, Integer>)in.readObject();
		in.close();		
		for(String k:map.keySet()){
			System.out.format("%s=%d\n",k,map.get(k));
		}
		
	}

}

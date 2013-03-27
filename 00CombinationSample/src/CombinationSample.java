import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

public class CombinationSample {
	public static void main(String[] args) throws IOException {
		new CombinationSample();
	}
	public BufferedWriter sys=new BufferedWriter(new OutputStreamWriter(System.out));
	public CombinationSample() throws IOException {
		String[] items = new String[] { "a", "b", "c", "d","e","f","g"};
		CombinationHelper h=new CombinationHelper(items.length,4,5);
		int [] indexs=h.next();
		while(indexs!=null){
			int index=indexs[0];
			System.out.print(String.format("%s", items[index]));
			for (int i = 1; i < indexs.length; i++) {
				index=indexs[i];
				System.out.print(String.format(",%s", items[index]));
			}
			System.out.print("\n");
			indexs=h.next();
		}
	}
}

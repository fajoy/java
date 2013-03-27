import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import fpTree.FPTree;
import fpTree.HeadTable;
public class FPTreeSample {
	public static void main(String[] args) throws IOException {
		test(0);
		/*
		for(int i=1010;i>50;i-=10){
			test(i);
		}*/
	}
	
	public static void test(int minSuport) throws IOException{
		long time =System.currentTimeMillis();
		HeadTable table=new HeadTable(getFileName("test.dat"));
		//table.showTable();
		FPTree fptree=table.createFPTree(minSuport);
		//fptree.showTable();
		//fptree.showTree();
		time=System.currentTimeMillis()-time;
		System.out.print("min:\t"+minSuport+"\thead count:\t"+fptree.table.size()+"\tspend time:\t"+time+"\n");
	}

	public static String getFileName(String fileName){
		URL url = FPTreeSample.class.getResource(fileName);
		
		return url.getFile();
	}



}

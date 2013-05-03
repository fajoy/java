package fajoy;

import java.io.*;
import java.util.Date;


import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class HDFSGetSample {
	
	public static final String tmpPath=System.getProperty("java.io.tmpdir");
	public static void main(String[] args) throws IOException {
		
		

		String src = "sample20130502201641";
			
		
		final FileSystem fs = FileSystem.get(new Configuration());
		Path path=new Path(src);
		String fullPath=fs.getFileStatus(path).getPath().toUri().getPath();
		HDFSAction get=new HDFSAction() {
			@Override
			public void actionDir(FileSystem fs, Path path,
					FileStatus fileStatus) {
				String createDir=tmpPath+path.toUri().getPath();
				File dir=new File(createDir);
				System.out.format("create dir %s \n", createDir);
				dir.mkdirs();
			}
			@Override
			public void actionFile(FileSystem fs, Path path,
					FileStatus fileStatus) {
				
				try {
					InputStream is=fs.open(path);
					
					String dst=tmpPath+path.toUri().getPath();
					File f=new File(dst);
					OutputStream os = new FileOutputStream(f);
					System.out.format("copy %s -> %s\n", path.toUri().getPath(),dst);
					
					IOUtils.copy(is, os);
					is.close();
					os.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		get.run(fs,new Path(src),true);
		
	}

}

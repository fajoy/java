package fajoy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSListSample {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH:mm" +
			"");
	
	public static void main(String[] args) throws Exception {
		FileSystem fs = FileSystem.get(new Configuration());
		Path path=new Path("./");
		System.out.format("list %s \n",path.toUri().getPath());		
		HDFSAction ls =new HDFSAction() {
			@Override
			public void actionDir(FileSystem fs, Path path, FileStatus fileStatus) {
				System.out.format("%s %10s\t%s\n"
						,dateForm.format(new Date( fileStatus.getModificationTime()))
						,fileStatus.getLen()
						,path.toUri().getPath());
			};
			
			
			@Override
			public void actionFile(FileSystem fs, Path path,FileStatus fileStatus) {
				System.out.format("%s %10s\t%s\n"
						,dateForm.format(new Date( fileStatus.getModificationTime()))
						,fileStatus.getLen()
						,path.toUri().getPath());
			}
		};
		ls.run(fs, path, true);
		System.exit(0);
	}

}

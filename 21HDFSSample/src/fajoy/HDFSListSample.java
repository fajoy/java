package fajoy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSListSample {
	public static void main(String[] args) throws Exception {
		FileSystem fs = FileSystem.get(new Configuration());
		Path path=new Path("./");
		HDFSAction ls =new HDFSAction() {
			@Override
			public void actionDir(FileSystem fs, Path path, FileStatus fileStatus) {
				System.out.format("%s %10s\t%s\n",fileStatus.getAccessTime(),fileStatus.getLen(),path);
			};
			
			
			@Override
			
			public void actionFile(FileSystem fs, Path path,FileStatus fileStatus) {

				System.out.format("%s %10s\t%s\n",fileStatus.getAccessTime(),fileStatus.getLen(),path);
			}
		};
		ls.run(fs, path, true);
		System.exit(0);
	}

}

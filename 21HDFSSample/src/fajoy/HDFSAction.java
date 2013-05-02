package fajoy;

import java.io.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public abstract class HDFSAction {
	public void run(FileSystem fs,Path path, boolean recursive) {
		begin();
		sub_run(fs, path, recursive);
		end();
	}

	void sub_run(FileSystem fs,Path path, boolean recursive) {
		try {
			FileStatus[] list;
			list = fs.listStatus(path);
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDir()) {
					actionDir(fs,list[i].getPath(),list[i]);
					if (recursive)
						sub_run(fs,list[i].getPath(), recursive);
				}else{
					actionFile(fs,list[i].getPath(),list[i]);;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionFile(FileSystem fs,Path path,FileStatus fileStatus) {
	}

	public void actionDir(FileSystem fs,Path path,FileStatus fileStatus) {
	}

	public void begin() {
	}

	public void end() {
	}

}
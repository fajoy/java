package fajoy;

import java.io.*;

public abstract class FileAction {
	public void run(File dir, boolean recursive) {
		begin();
		sub_run(dir, recursive);
		end();
	}

	void sub_run(File dir, boolean recursive) {
		File[] list = dir.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].isFile()) {
				actionFile(list[i]);
			}

			if (list[i].isDirectory()) {
				actionDir(list[i]);
				if (recursive)
					sub_run(list[i], recursive);
			}
		}
	}

	public void actionFile(File file) {
	}

	public void actionDir(File dir) {
	}

	public void begin() {
	}

	public void end() {
	}

}
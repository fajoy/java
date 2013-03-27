package task;

import java.io.Serializable;

public class Sleep implements Task {
	long t=0;
	@Override
	public Object execute() {
		try {
			Thread.sleep(this.t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null; 
	}

	@Override
	public void init(String init_str) {
		this.t=Long.parseLong(init_str);
	}
}

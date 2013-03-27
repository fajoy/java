package task;

public class Sleep implements Task {
	private static final long serialVersionUID = 1L;
	long t = 0;

	@Override
	public Object execute() {
		try {
			Thread.sleep(this.t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.format("sleep %d is end.\n", t);
		return null;
	}

	@Override
	public void init(String init_str) {
		this.t = Long.parseLong(init_str);
	}
}

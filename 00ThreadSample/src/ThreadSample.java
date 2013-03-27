public class ThreadSample {
	public static void main(String[] args) {
		new ThreadSample();
	}

	private Thread mainThread = Thread.currentThread();
	private int i = 0;
	private ThreadAdd t1 = new ThreadAdd(100);
	private ThreadAdd t2 = new ThreadAdd(200);

	public ThreadSample() {
		while (t1.isAlive() || t2.isAlive()) {
			try {
				Thread.currentThread().sleep(500);
				System.out.println("MainThread is wake up.");
			} catch (InterruptedException e) {
				System.out.println("MainThread is interrupt");
			}
		}
		System.out.println("Main is Over.");

	}

	private synchronized void addI(int j) {
		threadSync();
		this.i += 1;
		String msg = String.format("Thread Id=%d,i=%d,j=%d", Thread
				.currentThread().getId(), this.i, j);
		System.out.println(msg);
		System.out.flush();
		notifyAll();
	}
	private synchronized void threadSync(){
		if (t1 != null && t2 != null) {
			while (Thread.currentThread() == t1 && t1.j > t2.j) {
				try {
					wait();
					System.out.println("Child t1 is waki up.");
				} catch (InterruptedException e) {
					System.out.println("Child t1 is Interrupted.");
				}
			}

			if (Thread.currentThread() == t2 && t2.j == 5) {
				try {
					t1.interrupt();
					System.out.println("Child t2 is sleep.");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	private class ThreadAdd extends Thread {
		int sleepTime;
		int j = 0;

		public ThreadAdd(int sleepTime) {
			this.sleepTime = sleepTime;
			this.start();
		}

		@Override
		public void run() {
			for (j = 0; j < 10; j++) {
				ThreadSample.this.addI(j);
				try {
					Thread.sleep(this.sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			ThreadSample.this.mainThread.interrupt();
			// super.run();
		}
	}

}
/* 執行結果
Thread Id=8,i=1,j=0
Thread Id=9,i=2,j=0
Thread Id=9,i=3,j=1
Child t1 is waki up.
Thread Id=8,i=4,j=1
Thread Id=9,i=5,j=2
Child t1 is waki up.
Thread Id=8,i=6,j=2
MainThread is wake up.
Thread Id=9,i=7,j=3
Child t1 is waki up.
Thread Id=8,i=8,j=3
Thread Id=9,i=9,j=4
Child t1 is waki up.
Thread Id=8,i=10,j=4
MainThread is wake up.
Child t2 is sleep.
MainThread is wake up.
MainThread is wake up.
MainThread is wake up.
MainThread is wake up.
Thread Id=9,i=11,j=5
Child t1 is Interrupted.
Thread Id=8,i=12,j=5
Thread Id=9,i=13,j=6
Child t1 is waki up.
Thread Id=8,i=14,j=6
Thread Id=9,i=15,j=7
Child t1 is waki up.
Thread Id=8,i=16,j=7
MainThread is wake up.
Thread Id=9,i=17,j=8
Child t1 is waki up.
Thread Id=8,i=18,j=8
Thread Id=9,i=19,j=9
Child t1 is waki up.
Thread Id=8,i=20,j=9
MainThread is interrupt
MainThread is interrupt
Main is Over.
*/
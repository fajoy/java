package task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Vector;

public abstract class ServerCompute implements Compute {
	public abstract String[] getClientNames();

	public ServerCompute() {
		super();
	}

	public Object executeTask(Task t, String target) {
		if (target.equals("@SERVER") || target.isEmpty()) {
			try {
				Method method = t.getClass().getMethod("execute",
						new Class[] {});
				Gridify gridify = method.getAnnotation(Gridify.class);
				System.out.format("%s mapper is %s\n", t.getClass().getSimpleName(), gridify.mapper());
				System.out.format("%s reducer is %s\n", t.getClass().getSimpleName(), gridify.reducer());
				Method mapper = t.getClass().getMethod(gridify.mapper(),
						new Class[] { int.class });
				Method reducer = t.getClass().getMethod(gridify.reducer(),
						new Class[] { Vector.class });
				
				return mapReduce(t, mapper, reducer);
			} catch (NullPointerException e) {

			} catch (Exception e) {
				e.printStackTrace();
			}
			return t.execute();
		}

		// Client run
		try {
			System.out.format("task %s target %s\n",t.getClass().getSimpleName(), target);
			Compute comp = RMIHelper.getSkeleton(target);
			return comp.executeTask(t, target);
		} catch (Exception e) {
			System.out.format("target client %s not exist\n", target);
			e.printStackTrace();
		}
		return t.execute();

	}

	private Object mapReduce(Task t, Method mapper, Method reducer)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		String[] clientNames = getClientNames();
		int clientNum = clientNames.length;
		@SuppressWarnings("unchecked")
		Vector<Task> map = (Vector<Task>) mapper.invoke(t,new Object[] { clientNum });
		final Vector<Object> result = new Vector<Object>();
		for (int i = 0; i < map.size(); i++) {
			Task clientTask = map.get(i);
			final String clientName = clientNames[i];
			TaskCallBack.execute(clientTask, clientName, new Callback() {
				@Override
				public void callback(task.ServerCompute.TaskCallBack sender,
						Object returnValue) {
					System.out.format("%s %s mapper is over ret=%s\n",clientName, sender.task.getClass().getSimpleName(),returnValue.toString());
					result.add(returnValue);
				}
			});
		}
		while (result.size() != clientNum) {
			Thread.sleep(100);
		}
		Vector<Object> ret=result;
		Object  merge=reducer.invoke(t, new Object[] { ret});
		return merge;

	}

	public interface Callback {
		public void callback(TaskCallBack sender, Object returnValue);
	}

	public static class TaskCallBack extends Thread {
		Callback callback = null;
		Task task = null;
		String target = null;

		public TaskCallBack(String registryName) {
			this.target = registryName;
		}

		@Override
		public void run() {
			Object ret = null;
			try {
				Compute comp = RMIHelper.getSkeleton("@SERVER");
				ret = comp.executeTask(task, target);
			} catch (RemoteException e) {
				// e.printStackTrace();
				ret = task.execute();
			}
			callback.callback(this, ret);
		}

		public static void execute(Task task, String target, Callback callback) {
			TaskCallBack t = new TaskCallBack(target);
			t.task = task;
			t.callback = callback;
			t.start();
			Thread.yield();
		}
	}
}
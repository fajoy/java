import java.rmi.RemoteException;
import java.util.Map.Entry;
import java.util.Vector;

import task.*;




public class RMIMapReduce {

	public static void main(String[] args) {
		/*
	  	try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		 */
		new RMIMapReduce();
	}
	public String[] clientNames=new String[]{"a","b","c"};
	public RMIMapReduce(){
		
		//Client rebind
		for(int i=0;i<clientNames.length;i++){ 
			Compute engine =new ClientCompute();	
			RMIHelper.createStub(clientNames[i], engine);
			System.out.format("RMIServr %s start ok\n",clientNames[i]);
		}
		
		// Server rebind
		String ServerName = "@SERVER";
		Compute engine = new ServerCompute() {	
			@Override
			public String[] getClientNames() {
				return clientNames;
			}
		};
		RMIHelper.createStub(ServerName, engine);
		System.out.format("RMIServr %s start ok\n",ServerName);
		
		
		//TaskCallBack debug
		
		//Client "a" create Task Server from Client Get
		try {
			String clientName="a";
			String taskId="sleepTask";
			GetTask getTask=new GetTask("Sleep", "1000");
			Compute skeleton1 = RMIHelper.getSkeleton(clientName);
			//to Client
			Task task = (Task)skeleton1.executeTask(getTask,clientName);
			if(task==null)
				System.out.format("no such task.\n");
			else
				TaskStore.saveTask(taskId, task);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		// Client "b" rexe Task
		do {
			final String taskId = "sleepTask";
			final String clientName="b";
			final Task task=TaskStore.getTask(taskId);
			if(task==null){
				System.out.format("no such task.\n");
				break;
			}
			
			TaskCallBack.execute(task, clientName, new Callback() {
				@Override
				public void callback(TaskCallBack sender, Object returnValue) {
					System.out.println(String.format("/msg task %s(%s) execute return:%s",task.getClass().getSimpleName(),taskId,returnValue));
				}
			});
		} while (false);
		
		
		//showTask
		for ( Entry<String, Task> taskItem : TaskStore.tasks.entrySet()) {
			System.out.format("Task ID: %s,Task Type: %s\n",taskItem.getKey(),taskItem.getValue().getClass().getSimpleName());
		}


	
	
		
		//MapReduce Debug
		try{
		Class<?> taskClass=Class.forName(String.format("task.%s", "GridifyPrime"));
			Task prime =(Task)taskClass.newInstance();
			final int value=202;
			String args=String.format("%d 1 %d",value,value);
			prime.init(args);
			TaskCallBack.execute(prime,"@SERVER",new Callback() {				
				@Override
				public void callback(RMIMapReduce.TaskCallBack sender, Object returnValue) {
					Long ret=(Long)returnValue;
					System.out.format("%d %s prime.\n",value, (ret==1)?"is":"not is");
				}
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		
		
		
		// Server end
		System.out.format("main is end.\n");
		System.exit(0);
	
	}
	
	  public interface Callback {
			public void callback(TaskCallBack sender,Object returnValue);
		}

		public static class TaskCallBack extends Thread {
			Callback callback = null;
			Task task = null;
			String target=null;
			public TaskCallBack(String registryName) {
				this.target=registryName;
			}

			@Override
			public void run() {
				Object ret=null;
				try {
					Compute comp=RMIHelper.getSkeleton("@SERVER");
					ret = comp.executeTask(task, target);
				} catch (RemoteException e) {
					e.printStackTrace();
					//ret=task.execute();
				}
				callback.callback(this,ret);
			}
			
			public static void execute(Task task,String target, Callback callback){
				TaskCallBack t=new TaskCallBack(target);
				t.task = task;
				t.callback = callback;
				t.start();
				Thread.yield();
			}
		}
	
}

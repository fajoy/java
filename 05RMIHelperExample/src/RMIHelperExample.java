import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import task.*;


public class RMIHelperExample {

	public static void main(String[] args) {
		// Client rebind 
		String ClientName = "@Client";
		Compute clientEngine = new Compute() {
			@Override
			public Object executeTask(Task t, String target) throws RemoteException {
				//System.out.format("%s run %s task on Client\n",target,t.toString());
				return t.execute();
			}
		};
		RMIHelper.createStub(ClientName, clientEngine);
		System.out.format("RMIClient start ok\n");
		
		// Server rebind
		String ServerName = "@SERVER";
		Compute serverEngine = new Compute() {
			@Override
			public Object executeTask(Task t, String target) throws RemoteException {
				//System.out.format("%s run %s task on Server\n",target,t.toString());
				if(target.equals("@SERVER")||target.isEmpty()){
					System.out.format("Server save task\n");
					return t.execute();
				}
				//Client run
				try {
					Compute comp= RMIHelper.getSkeleton(target);
					if(comp!=null)
						return comp.executeTask(t, target);
				} catch (Exception e) {

				}
				return t.execute();
			}
		};
		 RMIHelper.createStub(ServerName, serverEngine);
		System.out.format("RMIServr start ok\n");
		
		// Client1 create Task
		try {
			Class taskClass=Class.forName(String.format("task.%s", "Pi"));
			Task pi =(Task)taskClass.newInstance();
			String piLen="10";
			String taskId="taskPi";
			TaskStore taskStore=new TaskStore(taskId,pi, piLen);
			Compute skeleton1 = RMIHelper.getSkeleton(ServerName);
			skeleton1.executeTask(taskStore,ServerName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		// Client2 rexe Task
		try {
			String taskId="taskPi";
			String target="@Client";
			TaskRexe rexe=new TaskRexe(taskId,target);
			Compute serv= RMIHelper.getSkeleton(ServerName);
			Object ret= serv.executeTask(rexe, target);
			System.out.format("ret= %s \n", ret.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// Server end
		try {
			UnicastRemoteObject.unexportObject(serverEngine, false);
			UnicastRemoteObject.unexportObject(clientEngine, false);
			System.out.format("RMIServer Close\n");
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
	}

}

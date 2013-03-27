package task;

import java.rmi.RemoteException;

public class ClientCompute implements Compute{
	public Object executeTask(Task t, String target) throws RemoteException {
		//System.out.format("rexe task %s target %s\n",t.getClass().getSimpleName(), target);
		return t.execute();
	}
}

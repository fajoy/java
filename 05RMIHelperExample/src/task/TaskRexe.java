package task;
import java.rmi.RemoteException;

public class TaskRexe implements Task {
	private static final long serialVersionUID = 1L;
	final String Server_Name="@SERVER";
	String taskId="";
	String target="";
	@Override
	public Object execute() {
		try {
			TaskTaker taskTaker=new TaskTaker(taskId);				
			Compute comp = RMIHelper.getSkeleton(Server_Name);
			if(target=="")target=Server_Name;
			TaskStore taskStore=(TaskStore) comp.executeTask(taskTaker, target);
			Task task=taskStore.task;
			task.init(taskStore.args);
			Compute comp_target = RMIHelper.getSkeleton(target);
			Object ret=comp_target.executeTask(taskStore.task, target);
			return ret;
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {

		}
		return null;
	}
	public TaskRexe(String taskId,String target){
		this.taskId=taskId;
		this.target=target;
		
	}
	
	@Override
	public void init(String init_str) {

	}

}

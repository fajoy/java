package task;

public class TaskKiller implements Task {
	private static final long serialVersionUID = 1L;
	String taskId;
	public TaskKiller(String taskId){
		this.taskId=taskId;	
	}
	@Override
	public Object execute() {
		TaskStore.remove(taskId);
		return null; 
	}

	@Override
	public void init(String init_str) {
	
	}
}

package task;


public class TaskTaker implements Task {
	private static final long serialVersionUID = 1L;
	String taskId;
	public TaskTaker(String taskId){
		this.taskId=taskId;
	}
	@Override
	public Object execute() {
		return TaskStore.getTaskStore(taskId);
	}

	@Override
	public void init(String init_str) {
		
	}

}

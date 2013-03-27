package task;
import java.util.LinkedHashMap;

public class TaskStore implements Task {
	private static final long serialVersionUID = 1L;
	private static LinkedHashMap<String, TaskStore> tasks=new LinkedHashMap<String, TaskStore>();
	public static void saveTask(TaskStore taskStore){
		tasks.put(taskStore.taskId, taskStore);
	}
	public static TaskStore getTaskStore(String id){
		return tasks.get(id);
	}
	public static void remove(String id){
		tasks.remove(id);
	}
	public String taskId=null;	
	public String args=null;
	public Task task=null;
	public TaskStore(String taskId,Task t,String args){
		this.taskId=taskId;
		this.task=t;
		this.args=args;
	}
	
	@Override
	public Object execute() {
		TaskStore.saveTask(this);
		return null;
	}
	@Override
	public void init(String init_str) {
	}
}

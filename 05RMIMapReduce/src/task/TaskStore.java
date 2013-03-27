package task;
import java.util.LinkedHashMap;

public class TaskStore {
	private static final long serialVersionUID = 1L;
	public static LinkedHashMap<String, Task> tasks=new LinkedHashMap<String, Task>();
	public static void saveTask(String taskId,Task task){
		tasks.put(taskId, task);
	}
	public static Task getTask(String id){
		return tasks.get(id);
	}
	public static void remove(String id){
		tasks.remove(id);
	}
	public String taskId=null;	
	public String args=null;
	public Task task=null;
	private TaskStore(){

	}

}

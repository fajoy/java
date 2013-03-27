package task;

public class GetTask implements Task {

	public String taskType="";
	public String inti_str="";
	@Override
	public Task execute() {
		Task task=null;
		try {
			Class<?> taskClass=null;
			taskClass = Class.forName(String.format("task.%s", taskType));
			task=(Task)taskClass.newInstance();
		} catch (Exception e) {
			System.out.format("no %s such type task.\n", taskType);
			//e.printStackTrace();
		}
		try {
			task.init(inti_str);
		} catch (Exception e) {
			System.out.format("task init args error.\n");
			return null;
		}
		return task;
	}
	public  GetTask(String taskType,String init_str){
		this.taskType=taskType;
		this.inti_str=init_str;
	}
	@Override
	public void init(String init_str) {

		
	}

}

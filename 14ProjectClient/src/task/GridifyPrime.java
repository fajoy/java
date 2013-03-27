package task;

import java.util.Vector;

import chatclient.ChatRoomClient;

public class GridifyPrime implements Task{
	private static final long serialVersionUID = 1L;
	
	
	/*
	 * /task t1 GridifyPrime 100 1 100
	 * /rexe t1
	 * */
	public long prime=2l;
	public long min=2l;
	public long max=21l;
	public GridifyPrime(String init_str){
		this.init(init_str);
	}
	public GridifyPrime(){}
	@Override
	@Gridify(mapper="primeMapper",reducer="primeReducer")
	public Object execute() {
		long ret=1l;
		for(long divisor=(min<2)?2:min;divisor<max;divisor++)
			if(prime%divisor==0) {
				ret=divisor;
				break;
		}
		try{
		String name= DisplayResultForTA.getUsername();
		DisplayResultForTA.displayUsingWidget("StringWidget", 0, 0, String.format("#000000 #ffffff %s GridifyPrime %d %d %d \n",name, prime,min,max));
		}catch (Exception e) {

		}
		return ret;
	}
	public Vector<GridifyPrime> primeMapper(int num){
		Vector<GridifyPrime> tasks=new Vector<GridifyPrime>();
		long diff=prime/num;	
		for(int i=0;i<num;i++){
			long min=1+diff*i;
			long max=1+diff*(i+1);
			if(max>=prime)
				max=prime;
			GridifyPrime gp= new GridifyPrime(String.format("%d %d %d",prime,min,max));
			tasks.add(gp);
		}
		return tasks;
	}
	
	public Long primeReducer(Vector<Long> results){
		Long ret=1l;
		for (Long result : results) {
			if(result!=1) {
				ret=result;
				break;
			}
		}
		return ret;	
	}
	
	@Override
	public void init(String init_str) {
		
		String[] args=init_str.split("( )+",3);
		prime=Long.valueOf(args[0]);
		min=Long.valueOf(args[1]);
		max=Long.valueOf(args[2]);
		if(prime<0||min>max||min>prime)
			Long.valueOf(args[100]);
	}

}

package chatserver;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import task.Compute;

public class RMIHelper{
		public static Compute createStub(String serverName,Compute engine){
			   if (System.getSecurityManager() == null) {
		            System.setSecurityManager(new SecurityManager());
		        }
		        try {
		            Compute stub =(Compute) UnicastRemoteObject.exportObject(engine, 0);
		            Registry registry = LocateRegistry.getRegistry();
		            registry.rebind(serverName, stub);
		            return stub;
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
	            return null;
		}
		public static Compute getSkeleton(String serverName){
			if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new SecurityManager());
	        }
	        try {
	            Registry registry = LocateRegistry.getRegistry();
	            Compute comp = (Compute) registry.lookup(serverName);
	            return comp;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
		}
		
	}
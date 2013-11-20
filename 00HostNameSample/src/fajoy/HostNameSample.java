package fajoy;
import java.net.*;

public class HostNameSample {
    public static void outHostName(InetAddress address, String s)
    {
        System.out.println("InetAddress : " + s );
        System.out.println("CanonicalHostName :" + address.getCanonicalHostName());
        System.out.println("HostName :" + address.getHostName());
        System.out.println("");
    }
	public static void main(String[] args)throws Exception {
        if (args.length == 0){
            outHostName(InetAddress.getLocalHost(), "LocalHost");
            return ;
        }
        outHostName(InetAddress.getByName(args[0]),args[0]);
	}
}

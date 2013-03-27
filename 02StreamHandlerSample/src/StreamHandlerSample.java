import java.io.*;
import java.net.*;




public class StreamHandlerSample {

	private static StreamHandler server=null;
	private static StreamHandler log=null;
	public static void main(String[] args) throws UnknownHostException, IOException {      
          // 設定port
          int serverPort=5050;
          FileOutputStream fo=new FileOutputStream("./clietn.log", false);
          log=new StreamHandler(fo);
          
          // 初始socket連接
          try {
          Socket clientSocket=new Socket("localhost",serverPort);
          server =new StreamHandler(clientSocket.getInputStream(),clientSocket.getOutputStream());
          server.setReadLineHander(ent_ReadLine);
          server.beginAsyncReadline();
          //System.out.println(sh.readLine());
          }
          catch (ConnectException e) {
        	  System.out.println("server can't connet.");
  		  }
         
	}
	
	
	private static ReadLineHandler<StreamHandler> ent_ReadLine=new ReadLineHandler<StreamHandler>() {
		@Override
		public void action(StreamHandler sender, String line) {
			System.out.println(line);
			System.out.flush();
			log.write(String.format("%s%s", line,"\n"));
			log.flush();
		}
	};

}

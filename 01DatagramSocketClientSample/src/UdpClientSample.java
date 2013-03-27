import java.io.*;    // 引用輸入串流讀取功能 
import java.net.*;    

public class UdpClientSample {

	/**
	 * @param args
	 * @throws SocketException 
	 */
	public static void main(String[] args) throws SocketException {
        // 用來存放伺服器IP位址的變數
        InetAddress serverIp;
        
        // 嘗試連接Server
        try {
            // 設定IP
            serverIp = InetAddress.getByName("localhost");
 
            // 設定port
            int serverPort=5050;
            
            DatagramSocket clientSocket = new DatagramSocket();
            String msg="hello world.";
            byte[] sendData = null;
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIp, serverPort);
            clientSocket.send(sendPacket);
 
            // 關閉連線
            clientSocket.close(); 
        } catch (IOException e) {
            // 出錯後顯示錯誤訊息
        	 System.out.println("Connect error.");
        }
	}

}

import java.io.BufferedWriter; // 引用串流功能
import java.io.IOException; // 引用例外功能
import java.io.OutputStreamWriter; // 引用輸出串流功能
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket; // 引用伺服器socket
import java.net.*; // 引用Socket網路功能

public class UdpServerSample {
	// 宣告一個靜態的server socket
	private static ServerSocket serverSocket;

	// 程式進入點
	public static void main(String[] args) {

		// 設定port
		int port = 5050;

		// 嘗試Listen一個連線
		try {

			DatagramSocket serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[4096];

			// 輸出"伺服器已啟動"
			System.out.println("Server is start.");
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			InetAddress clientIp = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
			String msg = new String(receivePacket.getData(),0,receivePacket.getLength());
			System.out.println(String.format("%s:%d(%d)\t%s", clientIp.getHostAddress(),clientPort,msg.length(),
					msg));

		} catch (IOException e) {
			// 如果失敗則顯示"Socket Error"
			System.out.println("Socket ERROR");
		}

		// 顯示結束連線
		System.out.println("Socket is End");
	}

}

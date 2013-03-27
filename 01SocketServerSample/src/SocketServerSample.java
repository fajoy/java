
import java.io.BufferedReader;
import java.io.BufferedWriter;        // 引用串流功能
import java.io.IOException;            // 引用例外功能
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;    // 引用輸出串流功能
import java.net.ServerSocket;         // 引用伺服器socket
import java.net.Socket;                // 引用Socket網路功能

public class SocketServerSample {
    // 宣告一個靜態的server socket
    private static ServerSocket serverSocket;
 
    // 程式進入點
    public static void main(String[] args) {
 
        // 設定port
        int port=5050;
 
        // 嘗試Listen一個連線
        try {
 
            // 初始化Server Socket 
            serverSocket =new ServerSocket(port);
 
            // 輸出"伺服器已啟動"
            System.out.println("Server is start.");
 
            // 接受來自客戶端的連線
            Socket socket=serverSocket.accept();
 
            // 接收來自Server的訊息 
            BufferedReader  br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
            // 顯示收到的訊息
            System.out.println(br.readLine());
            
 
        
        } catch (IOException e) {
            // 如果失敗則顯示"Socket Error"
            System.out.println("Socket ERROR");
        }
 
        // 顯示結束連線
        System.out.println("Socket is End");
    }

}

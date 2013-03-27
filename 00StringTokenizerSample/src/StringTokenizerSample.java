import java.util.*;
public class StringTokenizerSample {
	public static void main(String[] args) {
		String str="+-*/0123456798!@#$%^&*(){}[]<>\"\'? \t\r\n\r\n\n\n\n hello world.";
		StringTokenizer st=new StringTokenizer(str);
		while(st.hasMoreTokens()){
			System.out.println(st.nextToken());
		}

	}

}

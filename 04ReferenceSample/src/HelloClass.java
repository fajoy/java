import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.jar.Attributes.Name;

public class HelloClass{
		public int var_int=Integer.MIN_VALUE;
		public Integer var_Integer=Integer.MAX_VALUE;
		public double var_float=Double.MIN_VALUE;
		public double var_Double=Double.MAX_VALUE;
		public char var_char='a';
		public String var_String="a";
		
		@HelloName(name="JAVA")
		//@HelloName()
		public static void main(String[] args){
			Method method;
			try {
				method = HelloClass.class.getMethod("main", new Class[]{String[].class});
				HelloName name=method.getAnnotation(HelloName.class);
				System.out.format("%s : Hello World.\n",name.name());
			} catch (Exception e) {
				System.out.format("%s : Hello World.\n","null");
			}
			
		}
}

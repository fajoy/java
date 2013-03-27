public class HelloClass{
		public int var_int=Integer.MIN_VALUE;
		public Integer var_Integer=Integer.MAX_VALUE;
		public float var_float=Float.MIN_VALUE;
		public double var_Double=Double.MAX_VALUE;
		public char var_char='a';
		public String var_String="a";
		public static void main(String[] args) {
			System.out.println("Hello World.");
		}
		
		public int getVar_int(){
			return var_int;
		}
		public void setVar_int(int i){
			this.var_int=i;
		}
		public float getVar_float(){
			return var_float;
		}
		public void setVar_Double(double i){
			this.var_Double=i;
		}
}

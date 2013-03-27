import java.beans.BeanInfo;
import java.lang.reflect.*;
public class ReferenceSample {
	public static String[] args=new String[]{};
	public static void main(String[] args) throws Exception {
		ReferenceSample.args=args;
		ReferenceSample obj=new ReferenceSample();
	}
	public ReferenceSample() throws  Exception{
		//剖析本身物件
		show(this);
/*output
class name:ReferenceSample
 method name:showProperties
  parameter class:java.lang.Class
  parameter class:java.lang.Object
 return type:void
 method name:main
  parameter class:[Ljava.lang.String;
 return type:void
 method name:run
  parameter class:java.lang.Class
  parameter class:[Ljava.lang.String;
 return type:void
 method name:show
  parameter class:java.lang.Object
 return type:void
 field type:[Ljava.lang.String;
       name:args
       value:[Ljava.lang.String;@66848c
 */
		
		//取得類別
		Class newClass=Class.forName("HelloClass");
		//產生物件
		Object obj=newClass.newInstance();
		
		//剖析物件內容
		show(obj);
/*output
class name:HelloClass
 method name:main
  parameter class:[Ljava.lang.String;
 return type:void
 field type:int
       name:var_int
       value:-2147483648
 field type:java.lang.Integer
       name:var_Integer
       value:2147483647
 field type:double
       name:var_float
       value:4.9E-324
 field type:double
       name:var_Double
       value:1.7976931348623157E308
 field type:char
       name:var_char
       value:a
 field type:java.lang.String
       name:var_String
       value:a
*/
		
		//顯示obj中符合Number類別的屬性
		showProperties(Number.class,obj);
/*output
show HelloClass properties which type is java.lang.Number
 int var_int = -2147483648
 java.lang.Integer var_Integer = 2147483647
 double var_float = 4.9E-324
 double var_Double = 1.7976931348623157E308
*/
		
		//執行newClass物件中static main的方法
		run(newClass,ReferenceSample.args);
/*output
Hello World.
*/
	}
	//剖析 傳入物件的類別 擁有那些方法 與 屬性
	public void show(Object obj)throws  Exception{
		Class objType=obj.getClass();
		System.out.println(String.format("class name:%s",objType.getName()));
		for(Method method: objType.getDeclaredMethods()){
			//取得方法名稱
			String methodName=method.getName();
			System.out.println(String.format(" method name:%s",methodName));
			for(Class parameterType: method.getParameterTypes()){
				//取得參數類別
				String parameterTypeName=parameterType.getName();
				System.out.println(String.format("  parameter class:%s",parameterTypeName));	
			}
			//取得回傳類別
			String returnClassName=method.getReturnType().getName();
			System.out.println(String.format(" return type:%s",returnClassName));	
		}
		
		for(Field field: objType.getFields()){
			//取得 屬性類別
			String className=field.getType().getName();
			//取得 屬性名稱
			String name=field.getName();
			//取得 屬性值
			Object value=field.get(obj);
			System.out.println(String.format(" field type:%s",className));
			System.out.println(String.format("       name:%s",name));
			System.out.println(String.format("       value:%s",value));
		}
	}
	
	//顯示傳入物件中 符合 propertiesType類別的屬性
	public void showProperties(Class propertiesType,Object obj) throws Exception{
		Class objType=obj.getClass();
		String propertiesTypeName=propertiesType.getName();
		System.out.println(String.format("show %s properties which type is %s",objType.getName(),propertiesTypeName));
		for(Field field: objType.getFields()){
			String className=field.getType().getName();
			String name=field.getName();
			Object value=field.get(obj);
			//判斷屬性類別 是否符合propertiesType類別
			if(propertiesType.isInstance(value)){
				System.out.println(String.format(" %s %s = %s",className,name,value));
			}
		}
	}
	//執行傳入物件中 所定義static main的方法 
	public void run(Class mainClass,String[] args){
		try{
			Method method=mainClass.getMethod("main", new Class[]{String[].class});
			method.invoke(null,new Object[]{ReferenceSample.args});
		}catch (Exception e) {e.printStackTrace();}
	}	
	
}

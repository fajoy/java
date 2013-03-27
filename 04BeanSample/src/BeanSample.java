import java.beans.*;

import java.lang.reflect.Method;
import java.util.*;

public class BeanSample {
	public static void main(String[] args) throws Exception {
		BeanSample obj = new BeanSample();
	}

	public BeanSample() throws Exception {
		Class newClass = Class.forName("HelloClass");
		Object obj = newClass.newInstance();
		
		BeanHelper helper = new BeanHelper(obj);
		for(String name:helper.mapProperty.keySet()){
			Object value=helper.getProperty(name);
			Class cls=helper.getPropertyType(name);
			if(value instanceof Number ){
				System.out.println(String.format("%s %s = %s",cls,name,value));	
				helper.setProperty(name, 10);
			}
			value=helper.getProperty(name);
			System.out.println(String.format("%s %s = %s",cls,name,value));
			
		}
/*output
class java.lang.Class class = class HelloClass
int var_int = -2147483648
int var_int = 10
*/
		
	}

	public class BeanHelper {
		Object obj = null;
		public LinkedHashMap<String,PropertyDescriptor> mapProperty=new LinkedHashMap<String, PropertyDescriptor>();
		public BeanHelper(Object obj) {
			this.obj = obj;
			BeanInfo info = null;
			try {
				info = Introspector.getBeanInfo(obj.getClass());
			} catch (java.beans.IntrospectionException ex) {
				ex.printStackTrace();
			}
			//找出set或get開頭的方法 
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				//取得的field_name第一個字元一定是小寫
				String field_name = pd.getName(); // get property
				
				if(pd.getPropertyType()!=null)
					if(pd.getReadMethod()!=null&&pd.getWriteMethod()!=null)
					mapProperty.put(field_name, pd);
			}
		}
		
		//得到屬性類別
		public Class getPropertyType(String attributeName){
			PropertyDescriptor pd= mapProperty.get(attributeName);
			if(pd==null)
				return null;
			return pd.getPropertyType();
		}
		
		//取得屬性值
		public Object getProperty(String attributeName){
			PropertyDescriptor pd= mapProperty.get(attributeName);
			if(pd==null)
				return null;
			Method readMethod=pd.getReadMethod();
			if(readMethod==null)
				return null;
			try {
				return readMethod.invoke(this.obj,new Object[]{});
			} catch (Exception e) {
				return null;
			}
		}
		//設定屬性值
		public void setProperty(String attributeName,Object value){
			PropertyDescriptor pd= mapProperty.get(attributeName);
			if(pd==null)
				return;
			Method writeMethod=pd.getWriteMethod();
			if(writeMethod==null)
				return ;
			try {
				writeMethod.invoke(this.obj,value);	
			} catch (Exception e) {
				return ;
			}
		}
	}
}

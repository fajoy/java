package fajoy;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import java.lang.reflect.Field;
public class BotoProperties {
	
	public static BotoProperties Constants=new BotoProperties();
	public  String aws_access_key_id="";
	public  String aws_secret_access_key="";
	public  String is_secure="True";
	public  String host="";

	public BotoProperties(){	
		String configFile = System.getProperties().getProperty("user.home")+"/.boto";
        Properties properties = new Properties();
        try {
        	properties.load(new FileInputStream(configFile));
        } catch (Exception e) {
                    
            return;
        }
        
        Class<BotoProperties> c= BotoProperties.class;
        @SuppressWarnings("unchecked")
		Enumeration<String> keys =(Enumeration<String>) properties.propertyNames();
        while (keys.hasMoreElements()) {
			String string = (String) keys.nextElement();
			try {
				Field f=c.getField(string);
				if(f!=null){
					f.set(this, properties.get(string));
				}
			} catch (Exception e) {
					
			}
		}	
        
	}

	
	public static void main(String[] args) throws Exception {	
		Class<BotoProperties> c= BotoProperties.class;
        Field[] fs=  c.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
        	Field f=fs[i];
        	if (f.getType().equals(String.class)){
        		System.out.format("%s = %s \n",f.getName(),f.get(BotoProperties.Constants));
        	}
		}
	}
 
}

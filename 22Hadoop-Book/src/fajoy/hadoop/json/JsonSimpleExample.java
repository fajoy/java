package fajoy.hadoop.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class JsonSimpleExample {
	public static void main(String[] args) throws ParseException {
		String json_str = "{\n" +
				"\"sub_json\":{\"id\":10},\n" + 
				"\"items\":[0,1,null,2,3],\n"+
				"\"text\" : \"hello\"\n" +
				"}";
		JSONParser parser = new JSONParser();
		Object jsonObj = parser.parse(json_str);
		dumpJSONObject(jsonObj);
	}
	static void dumpJSONObject(Object dumpObj){
		if (dumpObj instanceof JSONArray){
			System.out.format("[]\n",dumpObj.toString());
			JSONArray arr=(JSONArray)dumpObj;
			for (Object obj:arr){
				dumpJSONObject(obj);
			}
			return;
		}
		if (dumpObj instanceof JSONObject){
			JSONObject jsonObj=(JSONObject)dumpObj;
			System.out.format("{}\n");
			for (Object key : jsonObj.keySet()) {
				Object value=jsonObj.get(key);
				System.out.format("%s %s = ",value.getClass() ,key);
				dumpJSONObject(value);
			}
			return;
		}		
		System.out.format("%s\n",dumpObj );
	}
	/*
	 * ====result==== 
	 * {}
	 * class java.lang.String text = hello
	 * class org.json.simple.JSONArray items = []
	 * 0
	 * 1
	 * null
	 * 2
	 * 3
	 * class org.json.simple.JSONObject sub_json = {}
	 * class java.lang.Long id = 10
	 * }
	 */
}

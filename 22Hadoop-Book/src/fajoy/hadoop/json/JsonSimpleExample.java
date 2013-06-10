package fajoy.hadoop.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class JsonSimpleExample {
	public static void main(String[] args) throws ParseException {
		String json_str = "{" +
				"\"text\" : \"hello\"," +
				"\"items\":[0,1,2,3],"+
				"\"sub_json\":{\"id\":\"10\"}" + 
				"}";
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject)parser.parse(json_str);
		dumpJSONObject(jsonObj);
	}
	static void dumpJSONObject(JSONObject jsonObj){
		for (Object key : jsonObj.keySet()) {
			Object value=jsonObj.get(key);
			if (value != null) {
				if (value instanceof JSONObject){
					System.out.format("%s %s\n",value.getClass() ,key);
					dumpJSONObject((JSONObject)value);
					continue;
				}
				System.out.format("%s  %s : %s\n",value.getClass() ,key,value );
			}
		}		
	}
	/*
	 * ====result==== 
	 * class java.lang.String  text : hello
	 * class org.json.simple.JSONArray  items : [0,1,2,3]
	 * class org.json.simple.JSONObject sub_json
	 * class java.lang.String  id : 10
	 */
}

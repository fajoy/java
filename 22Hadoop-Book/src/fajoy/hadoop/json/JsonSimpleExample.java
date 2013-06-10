package fajoy.hadoop.json;


import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class JsonSimpleExample {

	public static void main(String[] args) throws ParseException {

		String json_str = "{\"created_at\" : \"Thu, 29 Dec 2011 21:46:01 +0000\",\"from_user\" : \"grep_alex\",\"text\" : \"RT @kevinweil: After a lot of hard work by ...\"}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(json_str);

		for (Object key : jsonObj.keySet()) {
			if (jsonObj.get(key) != null) {
				System.out.format("%s : %s\n", key,jsonObj.get(key));
			}
		}

	}

}

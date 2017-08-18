package org.adobe.assist.VirtualAssistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestTemplate;

import com.adobe.sfdc.pojo.TestData;
import com.adobe.sfdc.pojo.UserData;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MyController {

	public TestData getResponseData(TestData testdata) {
		if(testdata.getLuisCallRequired()){
			testdata = getLUISData(testdata);
			testdata = getDbData(testdata);
		}else{
			boolean isFinalData = validateData(testdata);
			if(isFinalData){
				String stepType = null;
				String entityName = null;
				System.out.println("Process Request");
				testdata.setFinalResponse(true);
				Map<String,String> newMap = new HashMap<String,String>();
				for(UserData userData: testdata.getUserData()){
					if(userData.getUserDataType().startsWith("RESPONSE")){
						stepType = userData.getUserDataType();
						entityName = userData.getEntityName();
					}else{
						newMap.put(userData.getEntityName(), userData.getUserText().toUpperCase());
					}
					
				}
				System.out.println(newMap);
				
				if("RESPONSE_RANDOM".equals(stepType)){
					// Create a case for Response Random with entity data
				}else if("RESPONSE_DECISION".equals(stepType)){
					// Get all the entities and get actual value to display to user for database
					testdata = getDbDataResponse(testdata, stepType,newMap,entityName);
				}
			}else{
				System.out.println("Need more data from user");
			}
		}
		return testdata;
	}



	private TestData getDbDataResponse(TestData testdata, String stepType, Map<String, String> newMap, String entityName) {
		try{
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
			@SuppressWarnings("deprecation")
			DB database = mongoClient.getDB("steps_temp");

			String resp = getFinalResponseData(database,testdata, stepType, newMap, entityName);
			
			updateTestDataWithFinalResp(resp, testdata);
			
		}catch(Exception e){  
			e.printStackTrace();
		}
		return testdata;
	}


	private void updateTestDataWithFinalResp(String resp, TestData testdata) {
		for(UserData userData : testdata.getUserData()){
			if(userData.getUserDataType().startsWith("RESPONSE")){
				userData.setResponseText(resp);
			}
		}
	}



	private String getFinalResponseData(DB database, TestData testdata, String stepType, Map<String, String> newMap, String entityName) {
		String respText = null;
		DBCursor results = null;
		try{
			DBCollection collection = database.getCollection("userdata");
			
			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject("STEPTYPE",stepType));
			obj.add(new BasicDBObject("FIELDDATA",entityName));
			andQuery.put("$and", obj);
			
			results = collection.find(andQuery);
			System.out.println(results.size());
			while(results.hasNext()) {
				DBObject resultData = results.next();
				BasicDBList obj1 = (BasicDBList) resultData.get("DISCRIMINATOR");
				Map<String,String> testMap = new HashMap<String,String>();
				
				List<String> res = new ArrayList<String>();
				for(Object el: obj1) {
					testMap.putAll((Map<String,String>)el);
				}
				
				if(testMap.equals(newMap)){
					respText = (String)resultData.get("USERTEXT");
				}
			}
		}catch(Exception e){  
			e.printStackTrace();
		}finally{
			results.close();
		}
		return respText;
	}


	private boolean validateData(TestData testdata) {
		for(UserData userData : testdata.getUserData()){
			if(userData.getUserText() == null && "ENTITY".equals(userData.getUserDataType())){
				return false;
			}
		}
		return true;
	}


	private TestData getDbData(TestData testdata) {
		try{
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
			@SuppressWarnings("deprecation")
			DB database = mongoClient.getDB("steps_temp");

			getRData(database, testdata);
			getStepsData(database,testdata);
			
		}catch(Exception e){  
			e.printStackTrace();
		}
		return testdata;
	}


	private void getRData(DB database, TestData testdata) {
		List<UserData> tempUserData = new ArrayList<UserData>();
		DBCursor results = null;
		try{
			DBCollection collection = database.getCollection("rdata");
			results = collection.find(new BasicDBObject("INPUT_DATA",testdata.getIntentName()));

			while(results.hasNext()) {
				String resultData = (String) results.next().get("OUTPUT_DATA");
				UserData remainingUserData = new UserData();
				remainingUserData.setEntityName(resultData);
				for(UserData userData : testdata.getUserData()){
					if(userData.getEntityName().equals(resultData)){
						remainingUserData.setUserText(userData.getUserText());
					}
				}
				tempUserData.add(remainingUserData);
			}
		}catch(Exception e){  
			e.printStackTrace();
		}finally{
			results.close();
		}
		testdata.setUserData(tempUserData);
	}


	private void getStepsData(DB database, TestData testdata) {
		DBCursor results = null;
		try{
			DBCollection collection = database.getCollection("userdata");
			//results = collection.find(new BasicDBObject("STEPTYPE","ENTITY"));
			results = collection.find(new BasicDBObject());
			
			while(results.hasNext()) {
				DBObject resultData = results.next();
				for(UserData userData : testdata.getUserData()){
					if(userData.getEntityName().equals(resultData.get("FIELDDATA"))){
						if(!((String)resultData.get("STEPTYPE")).equals("RESPONSE_DECISION")){
							userData.setResponseText((String) resultData.get("USERTEXT"));
						}
						userData.setUserDataType((String)resultData.get("STEPTYPE"));
					}
				}
			}
		}catch(Exception e){  
			e.printStackTrace();
		}finally{
			results.close();
		}
	}


	private TestData getLUISData(TestData testdata) {
		String query = testdata.getUserQuery();
		final String uri = "https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/1dc5771c-1761-4d86-ab36-b8730079ed09?subscription-key=77d788a631524be487b54f39a9362f13&verbose=true&timezoneOffset=0&q="+query  ;

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		System.out.println(result);

		Map<String,String> inputData = new HashMap<String,String>();

		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(result);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if(json != null){
			JSONObject topScoringIntent = (JSONObject)json.get("topScoringIntent");
			System.out.println(topScoringIntent);
			testdata.setIntentName((String)topScoringIntent.get("intent"));
			testdata.setLuisCallRequired(false);
			testdata.setFinalResponse(false);

			JSONArray entities = (JSONArray) json.get("entities");
			System.out.println(entities);
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = entities.iterator();
			while (iterator.hasNext()) {
				JSONObject entity = iterator.next();
				inputData.put((String)entity.get("type"),(String) entity.get("entity"));
				UserData userData = new UserData();
				userData.setUserText((String) entity.get("entity"));
				userData.setEntityName((String)entity.get("type"));
				testdata.getUserData().add(userData);
			}
		}

		System.out.println(inputData);
		return testdata;
	}



}

package org.spg.refactoring.utilities;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JSONGenerator {
	private static Gson gson;
	
	static{
		gson = new GsonBuilder()
				.disableHtmlEscaping()
//				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
				.setFieldNamingStrategy(new MyFieldNamingStrategy())
				.setPrettyPrinting()
				.serializeNulls()
				.create();
	}
	
	
	
	
	public static synchronized String toJSON (Object obj){
		return gson.toJson(obj);
	}
	
	
	/**
	 * Print response given as a JSON element
	 * @param JSONelement
	 */
	private static void printJSON(JsonObject JSONobject){
		//for each property
		for (Entry<String, JsonElement> entry :  JSONobject.entrySet()){
			System.out.println(entry.getKey() +"\t"+ entry.getValue());
			JsonArray propertiesJSON = (JsonArray)entry.getValue();
			//for each subregion of a property
			Iterator propertiesSubregion = propertiesJSON.iterator();
			while (propertiesSubregion.hasNext()){
				JsonObject JSONsubregion = (JsonObject) propertiesSubregion.next();
				System.out.println(JSONsubregion.get("min") +"\t");
				System.out.println(JSONsubregion.get("max") +"\t");				
			}
		}
	}	
	
	
}



/**
 * A custom field naming strategy
 * @author sgerasimou
 *
 */
class MyFieldNamingStrategy implements FieldNamingStrategy
{
    public String translateName(Field field)
    {
    	return field.getName().toLowerCase();	    	
    }
}

package createParameterSets;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class NestedJsonCombinations {
	public static void generateCombinations(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
        if (index == parameters.size() || parameters.isEmpty()) {
            if (!current.entrySet().isEmpty()) {
                result.add(current.deepCopy());
            }
            return;
        }
        
        String type   = null;
        String policy = null;
        Boolean isRequired = false;
        
        try {

        	Parameter parameter = parameters.get(index);
        	

        	type = parameter.getType();
            isRequired = parameter.getRequired() == null ? false : parameter.getRequired();
            policy = parameter.getPolicy();

        }
        catch (NullPointerException e) {
        	System.out.println(String.format("Incorrect parameter:"));
        	System.out.println(parameters.get(index));
        	throw new IllegalArgumentException("The parameter is missing a part (key, type, possibleValues, etc.)");
        	
        }
        if (type.equalsIgnoreCase("nestedjson")) {
           NestedJsonCombinations.handleNestedJsonType(parameters, current, index, result);
           return;
        	
        }
        policy = policy.toLowerCase();
        switch (policy) {
//            case "date":
//                handleDateType(parameters, current, index, result);
//                break;
            case "multivalue":
                handleMultiValueType(parameters, current, index, result);
                break;
//            case "nestedjson":
//                NestedJsonCombinations.handleNestedJsonType(parameters, current, index, result, policy);
//                break;
            case "singlevalue":
                handleSingleValueType(parameters, current, index, result);
                break;
        }
        
        
        if (!isRequired) {
            generateCombinations(parameters, current, index + 1, result);
        }
    }

	private static void handleSingleValueType(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
    	Parameter parameter = parameters.get(index);
    	List<Object> possibleValues = parameter.getPossibleValues();
    	String key = (String) parameter.getKey();

        String type = parameter.getType().toLowerCase();
    	
    	for (Object value : possibleValues) {
    		JsonObject valueObject = new JsonObject();
    		String resultType = "";
    		if (Arrays.asList("numeric", "string", "boolean").contains(type)) {	
	    		if (type.equalsIgnoreCase("numeric")) {

	        		Double valueNumeric = (Double) value;
	        		resultType = "number";
	        		if (Utilities.isDouble(value.toString())) {
	
	                  valueObject.addProperty("value", valueNumeric);
   				 
	       			}
	 				 else {

	         			valueObject.addProperty("value", valueNumeric.intValue());
	 				 }
	        	}
	        	else if (Arrays.asList("string", "boolean").contains(type)) {
	        		valueObject.addProperty("value", (String) value);
					resultType = type.equalsIgnoreCase("string") ? "string" : "boolean";
	        	}
	    		
	    		valueObject.addProperty("type", resultType);
                current.add(key, valueObject);
                generateCombinations(parameters, current, index + 1, result);
                current.remove(key);

    		}
        	
        	else if (type.equalsIgnoreCase("date")) {

        		Set<String> singleValueOperations = new HashSet<>(Arrays.asList("eq", "lt", "gt", "lte", "gte"));
            	String dateFrom = (String) value;
        		String operation = parameter.getOperation();
        		resultType = "date";
                if ("range".equalsIgnoreCase(operation)) {
                	
                	for (Object dateToObj : possibleValues) {

                		String dateTo = (String) dateToObj;
                		
                        Date startDate = Configurations.DATE_PARSER.parse(dateFrom);
                        Date endDate = Configurations.DATE_PARSER.parse(dateTo);

                        if (startDate.before(endDate)) {
                            JsonArray dateValues = new JsonArray();
                            dateValues.add(dateFrom);
                            dateValues.add(dateTo);

                            valueObject.add("value", dateValues);
                            valueObject.addProperty("type", resultType);
                            valueObject.addProperty("operation", operation);

                            current.add(key, valueObject);
                            generateCombinations(parameters, current, index + 1, result);
                            current.remove(key);
                        }
                    }
                } else if (singleValueOperations.contains(operation)) {
                	

                    valueObject.addProperty("value", dateFrom);
                    valueObject.addProperty("type", type);
                    valueObject.addProperty("operation", operation);

                    if (current.has(key)) {	
                    	short counter = 0;
                    	String newkey = key;
                    	while (current.has(newkey)) {
                    		counter++;
                    		newkey = key + Configurations.KEY_SUFFIX + counter;
                    	}

                    	String existingOperation = current.getAsJsonObject(key).get("operation").getAsString();

                    	boolean hasCorrectRangeType = (existingOperation.equals("gt") || existingOperation.equals("gte")) 
                    										&& (operation.equals("lt") || operation.equals("lte"));
                    	if (hasCorrectRangeType) {
                            current.add(newkey, valueObject);
                            generateCombinations(parameters, current, index + 1, result);
                            current.remove(newkey);                		
                    	}
                    
                    }
                    else {
                        current.add(key, valueObject);
                        generateCombinations(parameters, current, index + 1, result);
                        current.remove(key);                	
                    }

                }
            } 
    	}
    }
    
	public static void handleNestedJsonType(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
        
    	Parameter parameter = parameters.get(index);
		String key = (String) parameter.getKey();
		
		List<Parameter> possibleValues = Parameter.LinkedTreeMapToParameterType(parameter.getPossibleValues());
        List<JsonObject> nestedJsonCombinations = new ArrayList<>();
        
        // Generate combinations among the NestedJson "possibleValues"
        generateCombinations(possibleValues, new JsonObject(), 0, nestedJsonCombinations);
        

        for (JsonObject nestedJsonCombination : nestedJsonCombinations) {
            current.addProperty(key, nestedJsonCombination.toString());
            Combinations.generateCombinations(parameters, current, index + 1, result); 
            current.remove(key);
        }
    }
	

    
//    private static void handleDateType(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
//        Set<String> singleValueOperations = new HashSet<>(Arrays.asList("eq", "lt", "gt", "lte", "gte"));
//    	
//        Parameter parameter = parameters.get(index);
//    	List<String> possibleValues = objectToStringType(parameter.getPossibleValues());
//    	String key = (String) parameter.getKey();
//        String operation = parameter.getOperation();
//        String type = parameter.getType();
//
//        
//        for (String dateFrom : possibleValues) {
//            JsonObject valueObject = new JsonObject();
//            if ("range".equalsIgnoreCase(operation)) {
//                for (String dateTo : possibleValues) {
//                    Date startDate = Configurations.DATE_PARSER.parse(dateFrom);
//                    Date endDate = Configurations.DATE_PARSER.parse(dateTo);
//
//                    if (startDate.before(endDate)) {
//                        JsonArray dateValues = new JsonArray();
//                        dateValues.add(dateFrom);
//                        dateValues.add(dateTo);
//
//                        valueObject.add("value", dateValues);
//                        valueObject.addProperty("type", "date");
//                        valueObject.addProperty("operation", operation);
//                        
//                        
//           
//                        current.add(key, valueObject);
//                        generateCombinations(parameters, current, index + 1, result);
//                        current.remove(key);
//                    }
//                }
//            } else if (singleValueOperations.contains(operation)) {
//            	
//
//                valueObject.addProperty("value", dateFrom);
//                valueObject.addProperty("type", type);
//                valueObject.addProperty("operation", operation);
//
//                if (current.has(key)) {	
//                	short counter = 0;
//                	String newkey = key;
//                	while (current.has(newkey)) {
//                		counter++;
//                		newkey = key + Configurations.KEY_SUFFIX + counter;
//                	}
//
//                	String existingOperation = current.getAsJsonObject(key).get("operation").getAsString();
//
//                	boolean hasCorrectRangeType = (existingOperation.equals("gt") || existingOperation.equals("gte")) 
//                										&& (operation.equals("lt") || operation.equals("lte"));
//                	if (hasCorrectRangeType) {
//                        current.add(newkey, valueObject);
//                        generateCombinations(parameters, current, index + 1, result);
//                        current.remove(newkey);                		
//                	}
//                
//                }
//                else {
//                    current.add(key, valueObject);
//                    generateCombinations(parameters, current, index + 1, result);
//                    current.remove(key);                	
//                }
//
//            }
//        }
//    }
    
    private static void handleMultiValueType(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
        // Generates combinations for multi-valued parameters
    	Parameter parameter = parameters.get(index);
    	List<Object> possibleValues = (List<Object>) parameter.getPossibleValues();
    	String key  = (String) parameter.getKey();
        String type = parameter.getType();
        String operation = parameter.getOperation();
        String resultType = "";
        for (int i = 0; i < (1 << possibleValues.size()); i++) {
            JsonArray combination = new JsonArray();
            JsonObject valueObject = new JsonObject();
            for (int j = 0; j < possibleValues.size(); j++) {
                if ((i & (1 << j)) > 0) {
                	Object value = possibleValues.get(j);
                	

//                	if (value instanceof Double) {
//                		type = "number";
//                		combination.add(((Double) value).intValue());
//                	}
//                	else {
//                		type = "string";
//                		combination.add((String) value);
//                	}
//                	
//                	valueObject.add("value", combination);
//                	valueObject.addProperty("type", type);
//                	
//                	if (operation!=null) {
//                    	valueObject.addProperty("operation", operation);
//                	}
                	
                	
                	
                	
                	
                	
    	    		if (type.equalsIgnoreCase("numeric")) {

    	        		Double valueNumeric = (Double) value;
    	        		resultType = "number";
    	        		if (Utilities.isDouble(value.toString())) {
    	
    	        			combination.add(valueNumeric);
       				 
    	       			}
    	 				 else {

    	 					combination.add(valueNumeric.intValue());
    	 				 }
    	        	}
    	        	else if (type.equalsIgnoreCase("string")) {
    	        		combination.add((String) value);  
    					resultType = "string";
    	        	}
    	    		
    	    		
//    	    		if (operation!=null) {
//                    	valueObject.addProperty("operation", operation);
//                	}
    	    		
    	    		
                	valueObject.add("value", combination);
    	    		valueObject.addProperty("type", resultType);

//                    current.add(key, valueObject);
//                    generateCombinations(parameters, current, index + 1, result);
//                    current.remove(key);

            		
                	
                	
                	

                }
            	
            }
            if (combination.size() > 0) {
        		current.add(key, valueObject);
                generateCombinations(parameters, current, index + 1, result);                
        		current.remove(key);
            }
        }
    }
    
//
//    private static List<String> objectToStringType (List<Object> list) {
//    	// Casts List<Object> -> List<Parameter>
//    	
//    	List<String> paramList = new ArrayList<String>();
//    	
//    	for (Object obj : list) {
//            if (obj instanceof String) {
//            	String param = (String) obj;
//                paramList.add(param);
//                
//            } else {
//                throw new IllegalArgumentException("Possible values list contains non-Parameter objects.");
//            }
//        }
//    	
//    	return paramList;
//    }

}
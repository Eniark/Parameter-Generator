package createParameterSets;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class NestedJsonCombinations {
	public static void generateCombinations(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {

        if (index == parameters.size() || parameters.isEmpty()) {
            if (!current.entrySet().isEmpty()) {
                result.add(current.deepCopy());
            }
            return;
        }

        Parameter parameter = parameters.get(index);
    	
        if (parameter.isEmpty()) {
            result.add(current.deepCopy());
            return;        	
        }
        
    	String type = parameter.getType();
    	Boolean isRequired = parameter.getRequired() == null ? false : parameter.getRequired();
        
    	String policy = parameter.getPolicy()
        						 .toLowerCase();

        switch (policy) {
            case "multivalue":
                handleMultiValueType(parameters, current, index, result);
                break;
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

        		List<String> singleValueOperations = Arrays.asList("eq", "lt", "gt", "lte", "gte");
            	String dateFrom = (String) value;
        		String operation = parameter.getOperation().toLowerCase();
        		resultType = "date";
                if ("range".equalsIgnoreCase(operation)) { // this branch handles ranges of dates
                	
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
                } else if (singleValueOperations.contains(operation)) { // this branch handles cases where operation != 'range'

                    valueObject.addProperty("value", dateFrom);
                    valueObject.addProperty("type", type);
                    valueObject.addProperty("operation", operation);

                    if (current.has(key)) { // since we pass date as 2 separate objects (date from and date to) we have to check if a part of date was already processed
                    	short counter = 0;
                    	String newkey = key; // this is to handle duplicate keys
                    	while (current.has(newkey)) {
                    		counter++;
                    		newkey = key + Configurations.__KEY_SUFFIX + counter;
                    	}
                    	
                    	JsonObject otherPartOfDate = current.getAsJsonObject(key);
                    	String existingOperation = otherPartOfDate.get("operation").getAsString().toLowerCase();
                		String dateTo = otherPartOfDate.get("value").getAsString();

                    	boolean hasCorrectRangeType = (Arrays.asList("gt", "gte").contains(existingOperation)
                    										&& Arrays.asList("lt", "lte").contains(operation))
                    									|| 
	                    							  (Arrays.asList("lt", "lte").contains(existingOperation)
	                										&& Arrays.asList("gt", "gte").contains(operation));
                    	
                        Date startDate = Configurations.DATE_PARSER.parse(dateFrom);
                        Date endDate = Configurations.DATE_PARSER.parse(dateTo);

                        boolean hasCorrectRange = startDate.before(endDate);
                            	
                    	if (hasCorrectRangeType && hasCorrectRange) {
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
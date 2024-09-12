package createParameterSets;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Combinations {


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

//    private static void handleDateType(List<Map<String, Object>> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
//    	Map<String, Object> parameter = parameters.get(index);
//    	List<String> possibleValues = parameter.getPossibleValues();
//        
//        String startDateKey = null;
//        String endDateKey = null;
//        
//        if (parameter.get("key") instanceof List) {
//        	List<String> keys = (List<String>) parameter.getKey();
//        	startDateKey = keys.get(0);
//            endDateKey = keys.get(1);
//            
//            for (String dateFrom : possibleValues) {
//                for (String dateTo : possibleValues) {
//                    Date startDate = Configurations.DATE_PARSER.parse(dateFrom);
//                    Date endDate = Configurations.DATE_PARSER.parse(dateTo);
//
//                    if (startDate.before(endDate)) {
//
//    		            	current.addProperty(startDateKey, dateFrom);
//    		                current.addProperty(endDateKey, dateTo);
//    		                generateCombinations(parameters, current, index + 1, result);
//    		                current.remove(startDateKey);
//    		                current.remove(endDateKey);
//                        }
//                    }
//                }
//        }
//        else {
//        	handleSingleValueType(parameters, current, index, result);
//        }
//        
//    }
//    
//
    private static void handleMultiValueType(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
        // Generates combinations for multi-valued parameters
    	Parameter parameter = parameters.get(index);
    	List<Object> possibleValues = (List<Object>) parameter.getPossibleValues();
        String key = (String) parameter.getKey();
        for (int i = 0; i < (1 << possibleValues.size()); i++) {
            JsonArray combination = new JsonArray();
            for (int j = 0; j < possibleValues.size(); j++) {
                if ((i & (1 << j)) > 0) {
                	Object value = possibleValues.get(j);
                	if (value instanceof Double) {
                        combination.add(((Double) value).intValue());
                	}
                	else {
                        combination.add((String) value);

                	}
                }
            }
            if (combination.size() > 0) {
            	if (combination.size() <= Configurations.NUMBER_OF_COMBINATIONS_FOR_ARRAYS) {
            		current.add(key, combination);
            	}
                generateCombinations(parameters, current, index + 1, result);
                
            	if (combination.size() <= Configurations.NUMBER_OF_COMBINATIONS_FOR_ARRAYS) {
            		current.remove(key);
            	}
            }
        }
    }


    private static void handleSingleValueType(List<Parameter> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
        // Generates combinations for single-valued parameters
    	Parameter parameter = parameters.get(index);
    	List<Object> possibleValues = parameter.getPossibleValues();
    	
        String type = parameter.getType();
		
        for (Object value : possibleValues) {
            if ((parameter.getKey() instanceof String)) {
    	        String key = (String) parameter.getKey();
            	if (type.equalsIgnoreCase("numeric")) {
            		Double valueNumeric = (Double) value;
            		if (Utilities.isDouble(value.toString())) {
	 					current.addProperty(key, valueNumeric);
	 				 }
	 				 else {
	 					current.addProperty(key, valueNumeric.intValue());
	 				 }
            	}
            	else if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("date")) {
 					current.addProperty(key, (String) value);            		
            	}
            	
                generateCombinations(parameters, current, index + 1, result);
                current.remove(key);
            } else {
        		List<String> keys = (List<String>) parameter.getKey();
            	String startDateKey = keys.get(0);
            	String endDateKey = keys.get(1);
                
            	for (Object dateToObj : possibleValues) {
                	String dateFrom = (String) value;
            		String dateTo = (String) dateToObj;
                    Date startDate = Configurations.DATE_PARSER.parse(dateFrom);
                    Date endDate = Configurations.DATE_PARSER.parse(dateTo);

                    if (startDate.before(endDate)) {

		            	current.addProperty(startDateKey, dateFrom);
		                current.addProperty(endDateKey, dateTo);
		                generateCombinations(parameters, current, index + 1, result);
		                current.remove(startDateKey);
		                current.remove(endDateKey);
                    }
                }
            }
    	}
    }

        
        
////        ======================
////        Map<String, Object> parameter = parameters.get(index);
////    	List<String> possibleValues = parameter.getPossibleValues();
////        
////        String startDateKey = null;
////        String endDateKey = null;
////        
////        if (parameter.get("key") instanceof List) {
////        	List<String> keys = (List<String>) parameter.getKey();
////        	startDateKey = keys.get(0);
////            endDateKey = keys.get(1);
////            
////            for (String dateFrom : possibleValues) {
////                for (String dateTo : possibleValues) {
////                    Date startDate = Configurations.DATE_PARSER.parse(dateFrom);
////                    Date endDate = Configurations.DATE_PARSER.parse(dateTo);
////
////                    if (startDate.before(endDate)) {
////
////    		            	current.addProperty(startDateKey, dateFrom);
////    		                current.addProperty(endDateKey, dateTo);
////    		                generateCombinations(parameters, current, index + 1, result);
////    		                current.remove(startDateKey);
////    		                current.remove(endDateKey);
////                        }
////                    }
////                }
////        }
////        else {
////        	handleSingleValueType(parameters, current, index, result);
////        }
////        
////    }
//
//    

}
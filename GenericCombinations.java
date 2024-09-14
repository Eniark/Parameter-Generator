//package createParameterSets;
//
//import java.text.ParseException;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//
//public class GenericCombinations extends Combinations {
//	
//	protected static void handleDateType(List<Map<String, Object>> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
//    	Map<String, Object> parameter = parameters.get(index);
//    	List<String> possibleValues = (List<String>) parameter.get("possibleValues");
//        
//        String startDateKey = null;
//        String endDateKey = null;
//        
//        if (parameter.get("key") instanceof List) {
//        	List<String> keys = (List<String>) parameter.get("key");
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
//	protected static void handleArrayType(List<Map<String, Object>> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
//        // Generates combinations for multi-valued parameters
//    	Map<String, Object> parameter = parameters.get(index);
//    	List<Object> possibleValues = (List<Object>) parameter.get("possibleValues");
//        String key = (String) parameter.get("key");
//        for (int i = 0; i < (1 << possibleValues.size()); i++) {
//            JsonArray combination = new JsonArray();
//            for (int j = 0; j < possibleValues.size(); j++) {
//                if ((i & (1 << j)) > 0) {
//                	Object value = possibleValues.get(j);
//                	if (value instanceof Double) {
//                        combination.add(((Double) value).intValue());
//                	}
//                	else {
//                        combination.add((String) value);
//
//                	}
//                }
//            }
//            if (combination.size() > 0) {
//            	if (combination.size() <= Configurations.NUMBER_OF_COMBINATIONS_FOR_ARRAYS) {
//            		current.add(key, combination);
//            	}
//                generateCombinations(parameters, current, index + 1, result);
//                
//            	if (combination.size() <= Configurations.NUMBER_OF_COMBINATIONS_FOR_ARRAYS) {
//            		current.remove(key);
//            	}
//            }
//        }
//    }
//    
//	protected static void handleSingleValueType(List<Map<String, Object>> parameters, JsonObject current, int index, List<JsonObject> result) throws ParseException {
//        // Generates combinations for single-valued parameters
//    	Map<String, Object> parameter = parameters.get(index);
//    	List<Object> possibleValues = (List<Object>) parameter.get("possibleValues");
//        String key = (String) parameter.get("key");
//
//        for (Object value : possibleValues) {
//            if (Utilities.isDouble(value.toString())) {
//                current.addProperty(key, ((Double) value).intValue());
//            } else {
//                current.addProperty(key, value.toString());
//            }
//            generateCombinations(parameters, current, index + 1, result);
//            current.remove(key);
//        }
//    }
//}

package createParameterSets;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Parameter {

    private String policy;

    private Object key; 

    private String type;

    // Only for Nestedjson type
    private String operation;

    private List<Object> possibleValues; 

    private Boolean required = false; 

    public String getPolicy() {
        return policy;
    }

    public Parameter(Object key, String policy, String type, 
    		String operation, Boolean required, List<Object> possibleValues) {
    	
    	this.key = key;
    	this.policy = policy;
    	this.type = type;
    	this.operation = operation;
    	this.required = required;
    	this.possibleValues = possibleValues;
    }



    public Object getKey() {
        return this.key;
    }


    public String getType() {
        return this.type;
    }
    
    public String getOperation() {
        return this.operation;
    }


    public List<Object> getPossibleValues() {
        return this.possibleValues;
    }


    public Boolean getRequired() {
        return this.required;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                  "key='"   +  this.key    + '\'' +
                ", type='"  +  this.type   + '\'' +
                ", policy=" +  this.policy + '\'' +
                ", possibleValues='"  +  this.possibleValues   + '\'' +
                ", required='"        +  this.required + '\'' +
                '}';
    }
    
    
    public boolean isEmpty () {
    	return this.key==null 
				&& this.policy==null 
					&& this.type==null;

    }
    
    public static List<Parameter> LinkedTreeMapToParameterType (List<Object> list) {

    	// Make this a pure function
    	
    	List<Parameter> paramList = new ArrayList<Parameter>();
    	
    	for (Object obj : list) {
    		LinkedTreeMap jsonNode = (LinkedTreeMap) obj;
    		
    		String policy = (String) jsonNode.get("policy");
    		Object key = (Object) jsonNode.get("key");
    		String type = (String) jsonNode.get("type");
    		Boolean required = (Boolean) jsonNode.get("required");
    		List<Object> possibleValues = (List<Object>) jsonNode.get("possibleValues");
    		String operation = (String) jsonNode.get("operation");
    		
    		
    		Parameter parameterObject = new Parameter(key, policy, type, operation, required, possibleValues); 

			paramList.add(parameterObject);
        }
    	
    	return paramList;
    }
    
}

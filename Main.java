package createParameterSets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;

import com.opencsv.exceptions.CsvValidationException;


public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException, CsvValidationException, ParseException { 
//		try {
		
			String INPUT_FILE_NAME = null;
			String OUTPUT_FILE_NAME = null;
			char OUTPUT_FILE_DELIMITER = ',';
			
		
			if (Configurations.DEBUG != 1) {
				if (args.length < 3) {
					
					System.out.println("Please supply all the parameters");
					System.exit(0);
				}
				else {
					INPUT_FILE_NAME  = args[0];
					OUTPUT_FILE_NAME =  args[1];
					OUTPUT_FILE_DELIMITER = args[2].charAt(0);
				}			
			}
			else {
				INPUT_FILE_NAME  = Configurations.INPUT_FILE_NAME;
				OUTPUT_FILE_NAME =  Configurations.OUTPUT_FILE_NAME;
				OUTPUT_FILE_DELIMITER = Configurations.OUTPUT_FILE_DELIMITER;			
			}
			
			
			try {
				if (INPUT_FILE_NAME == null) {
		            throw new IllegalArgumentException("You must specify the input file names.");
		        }
				
			}
			catch (IllegalArgumentException e) {
	            Main.returnResultToBatch("", 1);
			}
			
				
	        CSVReader reader = Utilities.readCSV(INPUT_FILE_NAME, OUTPUT_FILE_DELIMITER);
	        String[] header  = reader.readNext();
	        
	        try {
		        if (header == null) {
		            throw new IllegalArgumentException("The CSV file is empty or cannot be read.");
		        }
	        }
			catch (IllegalArgumentException e) {
	            Main.returnResultToBatch("", 1);
			}
				
	        GsonBuilder builder = new GsonBuilder();
	        Gson gson = builder.setPrettyPrinting().create();
	        
//	        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
	        
	        Type type = new TypeToken<List<Parameter>>() {}.getType();
	
	        String[] line;
	        List<List<String>> resultList = new ArrayList<>();
	
	        while ((line = reader.readNext()) != null) {
	            String functionName = line[0];
	            String parameters = line[1];
	            System.out.println(functionName);
        		
	            List<Parameter> parsedParameters = gson.fromJson(parameters, type);
        		

	            List<JsonObject> combinations = new ArrayList<>();
//	            try {
	            	Combinations.generateCombinations(parsedParameters, new JsonObject(), 0, combinations);
//	            }
//	            catch (IllegalArgumentException e) {
//		            Main.returnResultToBatch("", 1);
//	            }
	            
	
	            for (JsonObject jsonObject : combinations) {
	                List<String> newList = new ArrayList<>();
	                newList.add(functionName);
	                newList.add(jsonObject.toString());
	                resultList.add(newList);
	
	            }
	
	        }
	        reader.close();
	        
	        String filePath = Utilities.writeCSV(header, resultList, OUTPUT_FILE_NAME, OUTPUT_FILE_DELIMITER);
	
	        Main.returnResultToBatch(filePath, 0);

//			}
//		
//		catch (Exception e) {
//			if (Configurations.DEBUG==1) {
//				System.out.println(e);
//			}
//            Main.returnResultToBatch("And Unhandled error occured in the Java app. Exiting the program.", -1);
//		}
    }

	private static void returnResultToBatch(String message, int code) {
		if (message.length() != 0) {
			System.out.println(message);	    	
	    }
	    System.exit(code);	
	}
	
}



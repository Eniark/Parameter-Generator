package createParameterSets;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;

public class Utilities {
    public static CSVReader readCSV(String fileName, char delimiter) throws IOException {
    	FileReader fileReader;
        
        if (Configurations.DEBUG==1) {
    	    
        	String RESOURCES_ROOT_PATH = "src/main/resources/";
            fileReader = new FileReader(RESOURCES_ROOT_PATH + fileName + "." + Configurations.FILE_EXTENSION);
//            File inputFile = new File(fileReader);
    	    
        }
        
        else {
        	File inputsDir = new File("../Parameter Generator/" + Configurations.INPUTS_DIRECTORY);
    	    if (!inputsDir.exists()) {
    	    	throw new IOException(String.format("Directory for CSV files \"%s\" not found. Please put the input files in there.", Configurations.INPUTS_DIRECTORY));
    	    }

    	    File inputFile = new File(inputsDir, fileName + "." + Configurations.FILE_EXTENSION);
          
          
    	    
    	    if (!inputFile.exists()) {
    	    	throw new IOException(String.format("File \"%s\" not found. Please put the input file in \"%s\" folder.", fileName, Configurations.INPUTS_DIRECTORY));
    	    }
    	    
            fileReader = new FileReader(inputFile);
        }
        
        CSVParser parser = new CSVParserBuilder()
            .withSeparator(delimiter)
            .build();


		CSVReader reader = new CSVReaderBuilder(fileReader)
            .withCSVParser(parser)
            .build();

        return reader;
        
        }
    
 
        

    public static String writeCSV(String[] header, List<List<String>> combinations, String fileName, char delimiter) throws IOException {
    	System.out.print("Writing amount of rows: ");
    	System.out.println(combinations.size());
//    	if (combinations == null || combinations.isEmpty()) {
//            throw new IllegalArgumentException("The list of data is empty or null.");
//        }
    	
	    File resultsDir = new File("../Parameter Generator/" + Configurations.RESULTS_DIRECTORY);
	    if (!resultsDir.exists()) {
	        resultsDir.mkdirs(); 
	    }

	    File outputFile = new File(resultsDir, fileName + "." + Configurations.FILE_EXTENSION);
    	
        try (CSVWriter writer = (CSVWriter) new CSVWriterBuilder(new FileWriter(outputFile, false)) // false = overwrites resulting file
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(CSVWriter.NO_ESCAPE_CHARACTER)
                .withSeparator(delimiter)
                .build()) 
        {


        	
        	writer.writeNext(header);
            for (List<String> row : combinations) {
            	String[] arr = row.toArray(new String[0]);
            	arr[1] = removeSuffixFromKeys(arr[1]);
                writer.writeNext(arr);
            }
            
        
        }

        return outputFile.getAbsolutePath();
        
    }
    

    public static String removeSuffixFromKeys(String str) {
    	String regex = Configurations.KEY_SUFFIX + "[0-9]+";
    	return str.replaceAll(regex, "");

    }
    
    public static boolean isDouble(String string) {
    	return string.charAt(string.length() - 1) != '0';
    
    }

}

package createParameterSets;

import java.text.SimpleDateFormat;

public class Configurations {
	public static final String INPUT_FILE_NAME = "Development Tests.csv";
	public static final String OUTPUT_FILE_NAME = INPUT_FILE_NAME + "_Combinations.csv";
	public static final String RESULTS_DIRECTORY = "Outputs";
	public static final String INPUTS_DIRECTORY = "Inputs";
	
	public static final char OUTPUT_FILE_DELIMITER = ';';
	public static final Byte DEBUG = 1;
	
	public static final short NUMBER_OF_COMBINATIONS_FOR_ARRAYS = 10000; // -1 for unlimited
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat(DATE_FORMAT);
	public static final String KEY_SUFFIX = "_TEMP_INFORMATION_";


}
	
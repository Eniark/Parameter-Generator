package createParameterSets;


	
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;




public class BaseCSVParameters {
	static Scanner scanner = new Scanner(System.in);
	static String CONFIGURATIONS_FOLDER = "ResultConfigs";
	static String FILE_NAME_PREFIX = "parameterConfigurations_";
	static String FILE_NAME_DATE_FORMAT = "yyyyMMdd_HHmmss";
	static String FILE_EXTENSION = "csv";
	
	
	private static Path promptForFile() throws IOException {
		Path filePath = null;
		System.out.println("Do you want to create a new file for parameters?\n1 - yes\n0 - no");
        boolean createNewFile =  scanner.nextInt() != 0;
        
        if (createNewFile) {
        	System.out.println("Use the default directory?\n1 - yes\n0 - no");
            boolean useDefaultDirectory = scanner.nextInt() != 0;
            scanner.nextLine();
            
            String directoryPath = BaseCSVParameters.CONFIGURATIONS_FOLDER;
            System.out.println(Paths.get("/" + BaseCSVParameters.CONFIGURATIONS_FOLDER));
            
            
            if (!useDefaultDirectory) {
            	System.out.println("Specify the directory path using \"/\" as separator.");
                directoryPath = scanner.nextLine();
            }
            
            File directory = new File(directoryPath);
            if (!directory.exists()){
            	directory.mkdir();
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat(BaseCSVParameters.FILE_NAME_DATE_FORMAT); 
            String datetime = dateFormat.format(new Date());
            String fileName = BaseCSVParameters.FILE_NAME_PREFIX + datetime + '.' + BaseCSVParameters.FILE_EXTENSION;
            
            // Create the file 
            filePath = Paths.get(directoryPath, fileName);
            filePath.toFile().createNewFile(); 
            System.out.println("INFO: File successfully created."); 

        	
        }
        
        else {
        	System.out.println("Specify the full file path using \"/\" as separator.");
        	filePath = Paths.get(scanner.nextLine());
        }
		
        
//        return new Object[] { filePath, value2 };
        return filePath;
        
	}
	
	private static void promptForRecords() {
		
		String LOOP_EXIT_CHAR = ".";
//		String multipleChoiceType = "Array";
//		String singleChoiceType = "Array";
//		
		HashMap<String, Object> Results = new HashMap<String, Object>();
		System.out.println("===ADDING NEW RECORDS===");
		System.out.println("Please specify the next infromation:");
		System.out.println("API name of the function:");
		
		String functionName = scanner.nextLine();
		
		System.out.println("Parameter name:");
		String parameterName = scanner.nextLine();
		

		HashMap<String, String> typeId2String = new HashMap<String, String>();
		typeId2String.put("1", "Array");
		typeId2String.put("2", "String");
		typeId2String.put("3", "NestedJsonObject");
		
		System.out.println("Is this a multiple-choice field?:\n1 - Yes\n0 - No");
		boolean isMultipleChoice = scanner.nextInt() != 0;
		

		System.out.println("Parameter data type:\n1 - Number\n2 - String\n3 - NestedJsonObject\n4 - Date");
		int typeChoice = scanner.nextInt();
		scanner.nextLine();
		
		
		if (isMultipleChoice) {
			List<Object> possibleValuesStr = new ArrayList<>();
			short counter = 1;
			boolean going = true;
			System.out.println("Please input the possible values this parameter can obtain "
					+ "(type \"%s\" to finish)".formatted(LOOP_EXIT_CHAR));
			while (going) {
				System.out.println("Value %d:".formatted(counter));
				Object value = null;
				if (typeChoice == 1) {
					value = scanner.nextInt();
					scanner.nextLine();
				}
				else if (typeChoice == 2) {					
					value = scanner.nextLine();
				}
				
				if (value.equals(LOOP_EXIT_CHAR)) {
					going = false;
				}
				else {
					possibleValuesStr.add(value);					
				}
				
				counter++;
			}
			
			System.out.println(possibleValuesStr.toString());
			

		}
//			else if (typeChoice == 2) {
//				System.out.println("Please input the value for this parameter");
//				String value = scanner.nextLine();
//				
//			}
//		}	
		
	}
	
	private static void writeRecords(Path filePath) {
		
		
	}
	public static void promptUser() throws IOException {
		
		Path filePath = BaseCSVParameters.promptForFile();
		BaseCSVParameters.promptForRecords();
		
		
        String name = scanner.nextLine();

        // Prompt user for their age
        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
	}
}

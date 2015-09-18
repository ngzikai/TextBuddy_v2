import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*
 * AUTHOR: NG ZI KAI
 * MATRIC NO: A0125962B
 * 
 * 
 * USAGE: java TextBuddy myTextFile.txt
 * */

public class TextBuddy {
	BufferedWriter bw;
	BufferedReader br;
	String fileName;

	boolean exitControl = true;

	// Magic strings for error messages
	private static final String INVALID_COMMAND_ERROR = "Invalid Command: Valid commands are \"add\", \"delete\", \"display\", \"clear\", \"search\", \"sort\" and \"exit\"";
	private static final String FILE_CREATION_ERROR = "Error encountered when trying to create file";
	private static final String INSERTION_ERROR = "Error encountered when trying to insert line into file";
	private static final String DELETION_ERROR = "Error encountered when trying to delete line from file";
	private static final String DISPLAY_ERROR = "Error encountered when trying to display file";
	private static final String CLEAR_ERROR = "Error encountered when trying to clear contents from file";
	private static final String SORT_ERROR = "Error encountered when trying to sort the file";
	private static final String SEARCH_ERROR = "Error encountered when trying to search contents from file";

	TextBuddy(String fileName) {
		this.fileName = fileName;
	}

	void run() {
		File file = createFile(fileName);
		displayMessage("Welcome to TextBuddy. " + file.getName() + " is ready to use");
		Scanner sc = new Scanner(System.in);
		while (exitControl) {
			displayMessage(processCommand(sc));
		}
	}

	File createFile(String fileName) {
		File file = new File(fileName);
		// create new file if it doesn't exist
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				displayMessage(FILE_CREATION_ERROR);
				System.exit(0);
			}
		}
		return file;
	}

	String[] extractCommand(String command) {
		String trimmedCommand = trimLine(command);
		String[] splittedCommand = trimmedCommand.split(" ");
		String[] returnArray = new String[2];

		String s = concatenateLine(splittedCommand);

		returnArray[0] = splittedCommand[0];
		returnArray[1] = trimLine(s);

		return returnArray;
	}

	String concatenateLine(String[] splittedCommand) {
		// Concatenate the rest of the command from index 1 to
		// splittedCommand.length to a string
		String s = "";
		for (int i = 1; i < splittedCommand.length; i++) {
			s += splittedCommand[i] + " ";
		}
		return s;
	}

	// Helper method to trim the string
	private String trimLine(String lineToTrim) {
		return lineToTrim.trim();
	}

	public String processCommand(Scanner sc) {
		System.out.print("command: ");
		String nextCommand = sc.nextLine();
		String[] extractedCommand = extractCommand(nextCommand);

		String commandType = extractedCommand[0];

		String returnMessage = "";

		switch (commandType.toLowerCase()) {
			case "add" :
				returnMessage = writeToFile(fileName, extractedCommand[1]);
				break;

			case "delete" :
				int lineToRemove = convertToInt(extractedCommand[1]);
				if (lineToRemove > 0) {
					returnMessage = removeFromFile(fileName, lineToRemove);
				} else {
					returnMessage = DELETION_ERROR;
				}
				break;

			case "display" :
				returnMessage = displayContents(fileName);
				break;

			case "clear" :
				returnMessage = clearContents(fileName);
				break;

			case "sort" :
				returnMessage = sort(fileName);
				break;
			
			case "search" :
				returnMessage = search(fileName, extractedCommand[1]);
				break;

			case "exit" :
				this.exitControl = false;
				break;

			default :
				returnMessage = INVALID_COMMAND_ERROR;
				break;
		}

		return returnMessage;
	}

	void displayMessage(String message) {
		System.out.println(message);
	}

	private int convertToInt(String stringToConvert) {
		try {
			return Integer.parseInt(stringToConvert);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public String writeToFile(String fileName, String text) {
		try {
			String trimmedText = trimLine(text);
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write(trimmedText + System.getProperty("line.separator"));
			bw.flush();
			bw.close();

			return "Added to " + fileName + ": \"" + trimmedText + "\"";
		} catch (IOException e) {
			return INSERTION_ERROR;
		}
	}

	String removeFromFile(String fileName, int lineToRemove) {
		try {
			File inputFile = new File(fileName);
			File tempFile = new File("temp.txt");
			br = new BufferedReader(new FileReader(fileName));

			String currentLine, removedLine = null, trimmedLine = null;
			int lineCounter = 1;

			while ((currentLine = br.readLine()) != null) {
				// if line is not to be removed, print to new file
				if (lineCounter != lineToRemove) {
					trimmedLine = trimLine(currentLine);
					writeToFile("temp.txt", trimmedLine);
				} else {
					removedLine = trimLine(currentLine);
				}
				lineCounter++;
			}
			br.close();

			// delete the old inputFile and rename tempFile as the inputFile
			inputFile.delete();
			tempFile.renameTo(inputFile);

			if (removedLine != null) {
				return "Deleted from " + fileName + ": \"" + removedLine + "\"";
			} else {
				return "Not a valid line to remove!";
			}

		} catch (IOException e) {
			return DELETION_ERROR;
		}
	}

	String displayContents(String fileName) {
		try {
			br = new BufferedReader(new FileReader(fileName));

			String currentLine;
			int lineCounter = 1;
			boolean isEmpty = true;

			String returnString = "";

			// Loop through the file and print all the lines
			while ((currentLine = br.readLine()) != null) {
				String trimmedLine = trimLine(currentLine);
				returnString += lineCounter + ": " + trimmedLine + "\n";
				isEmpty = false;
				lineCounter++;
			}

			// remove the last "\n"
			if(!returnString.equals("")){
				returnString = returnString.substring(0, returnString.length() - 1);
			}
			
			br.close();

			if (isEmpty) {
				return fileName + " is empty.";
			} else {
				return returnString;
			}
		} catch (IOException e) {
			return DISPLAY_ERROR;
		}
	}

	String clearContents(String fileName) {
		// creates a new empty file and renames it to the original file, then
		// deletes the original file
		try {
			File inputFile = new File(fileName);
			File tempFile = new File("temp.txt");

			tempFile.createNewFile();

			inputFile.delete();
			tempFile.renameTo(inputFile);

			return "All lines deleted from " + fileName;
		} catch (IOException e) {
			return CLEAR_ERROR;
		}
	}

	String sort(String fileName) {
		try {
			File inputFile = new File(fileName);
			File tempFile = new File("temp.txt");
			ArrayList<String> fileContents = new ArrayList<String>();

			br = new BufferedReader(new FileReader(fileName));

			String currentLine, trimmedLine = null;

			String returnString = "";

			while ((currentLine = br.readLine()) != null) {
				// if line is not to be removed, print to new file
				trimmedLine = trimLine(currentLine);
				fileContents.add(trimmedLine);
			}
			br.close();

			Collections.sort(fileContents);

			for (int i = 0; i < fileContents.size(); i++) {
				String s = writeToFile("temp.txt", fileContents.get(i));
				// System.out.println(s);
			}

			// delete the old inputFile and rename tempFile as the inputFile
			inputFile.delete();
			tempFile.renameTo(inputFile);

			returnString += "Sorted File:\n";
			returnString += displayContents(fileName);

			return returnString;
		} catch (IOException e) {
			return SORT_ERROR;
		}
	}

	String search(String fileName, String query) {
		try {
			br = new BufferedReader(new FileReader(fileName));

			ArrayList<Integer> lineNumbers = new ArrayList<Integer>();

			String currentLine;
			String returnString = "";
			int lineCounter = 1;

			// Changing both query and line to be read to lower case to provide
			// case-insenstive matching
			// Using of regular expressions to
			String regex = ".*" + query.toLowerCase() + ".*";

			while ((currentLine = br.readLine()) != null) {
				// if the current line matches the regex, add the current line
				// number to the lineNumbers array
				if (trimLine(currentLine).toLowerCase().matches(regex)) {
					lineNumbers.add(lineCounter);
				}
				lineCounter++;
			}

			if (lineNumbers.size() > 0) {
				returnString += "Line number(s) containing the word \"" + query + "\": ";
				// print the first index
				returnString += lineNumbers.get(0);
				if (lineNumbers.size() >= 2) {
					for (int i = 1; i < lineNumbers.size(); i++) {
						returnString += ", " + lineNumbers.get(i);
					}
				}
			} else {
				returnString = "There are no lines containing the word \"" + query + "\"";
			}

			br.close();

			return returnString;
		} catch (IOException e) {
			return SEARCH_ERROR;
		}
	}

	public static void main(String args[]) {
		if (args.length == 1) {
			String fileName = args[0];
			TextBuddy tb = new TextBuddy(fileName);
			tb.run();
		} else {
			System.out.println("USAGE: java TextBuddy <FILENAME>");
		}

	}
}

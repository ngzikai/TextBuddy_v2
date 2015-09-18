import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

/*
 * AUTHOR: NG ZI KAI
 * MATRIC NO: A0125962B
 * 
 * 
 * USAGE: java TextBuddy myTextFile.txt
 * */

public class TextBuddy {
	static BufferedWriter bw;
	static BufferedReader br;
	private String _fileName;

	TextBuddy(String fileName){
		_fileName = fileName;
	}

	void run(){
		File file = createFile(_fileName);
		displayMessage("Welcome to TextBuddy. " + file.getName() + " is ready to use");
		Scanner sc = new Scanner(System.in);
		while(true){
			processCommand(sc);
		}
	}

	File createFile(String fileName){
		File file = new File(fileName);
		//create new file if it doesn't exist
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				displayMessage("Error encountered when trying to create file!");
				System.exit(0);
			}
		}
		return file;
	}

	String[] extractCommand(String command){
		String trimmedCommand = trimLine(command);
		String[] splittedCommand = trimmedCommand.split(" ");
		String[] returnArray = new String[2]; 

		//join the resulting string back (excluding the first word)
		String s = "";
		for(int i = 1; i < splittedCommand.length; i++){
			s += splittedCommand[i] + " ";
		}

		returnArray[0] = splittedCommand[0];
		returnArray[1] = trimLine(s);

		return returnArray;
	}

	void processCommand(Scanner sc){
		System.out.print("command: ");
		String nextCommand = sc.nextLine();
		String[] extractedCommand = extractCommand(nextCommand);

		String commandType = extractedCommand[0];

		switch(commandType.toLowerCase()){
		case "add":
			writeToFile(_fileName, extractedCommand[1]);
			break;
		case "delete":
			int lineToRemove = convertToInt(extractedCommand[1]);
			if(lineToRemove > 0){
				removeFromFile(_fileName, lineToRemove);
			}else{
				displayMessage("Error in processing delete command");
			}
			break;
		case "display":
			displayContents(_fileName);
			break;
		case "clear":
			clearContents(_fileName);
			break;
		case "exit":
			sc.close();
			System.exit(0);
			break;
		default:
			displayMessage("Invalid Command: Valid commands are \"add\", \"delete\", \"display\", \"clear\" and \"exit\"");
			break;
		}
	}

	void displayMessage(String message){
		System.out.println(message);
	}

	int convertToInt(String stringToConvert){
		try{
			return Integer.parseInt(stringToConvert);
		}catch(NumberFormatException e){
			//displayMessage("\""+ stringToConvert +"\" is not a valid number");
			return -1;
		}
	}

	void writeToFile(String fileName, String text){
		try{
			String trimmedText = trimLine(text);
			bw = new BufferedWriter(new FileWriter(fileName,true));
			bw.write(trimmedText + System.getProperty("line.separator"));
			bw.flush();
			bw.close();
			System.out.println("Added to " + fileName + ": \"" + trimmedText +"\"");
		}catch(IOException e){
			System.out.println("Error when writing to file!");
		}
	}

	void removeFromFile(String fileName, int lineToRemove){
		try{
			File inputFile = new File(fileName);
			File tempFile = new File("temp.txt");

			br = new BufferedReader(new FileReader(fileName));
			bw = new BufferedWriter(new FileWriter(tempFile));

			String currentLine, removedLine = null, trimmedLine = null;
			int lineCounter = 1;

			while((currentLine = br.readLine()) != null){
				//if line is not to be removed, print to new file
				if(lineCounter != lineToRemove){
					trimmedLine = trimLine(currentLine);
					bw.write(trimmedLine + System.getProperty("line.separator"));
				}else{
					removedLine = trimLine(currentLine);
				}
				lineCounter++;
			}
			bw.flush();
			bw.close(); 
			br.close(); 

			if(removedLine != null){
				System.out.println("Deleted from " + fileName + ": \"" + removedLine + "\"");
			}else{
				System.out.println("Not a valid line to remove!");
			}
			//delete the old inputFile and rename tempFile as the inputFile
			inputFile.delete();
			tempFile.renameTo(inputFile);
		}catch(IOException e){
			System.out.println("Error when trying to delete line from file!");
		}
	}

	void displayContents(String fileName){
		try{
			br =  new BufferedReader(new FileReader(fileName));

			String currentLine;
			int lineCounter = 1;
			boolean isEmpty = true;

			//Loop through the file and print all the lines
			while((currentLine = br.readLine()) != null){
				String trimmedLine = trimLine(currentLine);
				System.out.println(lineCounter + ": " + trimmedLine);
				isEmpty = false;
				lineCounter++;
			}

			if(isEmpty){
				System.out.println(fileName + " is empty.");
			}
			br.close();
		}catch(IOException e){
			System.out.println("Error with trying to display file!");
		}
	}

	void clearContents(String fileName){
		//creates a new empty file and renames it to the original file, then deletes the original file
		try{
			File inputFile = new File(fileName);
			File tempFile = new File("temp.txt");

			tempFile.createNewFile();

			inputFile.delete();
			tempFile.renameTo(inputFile);
			System.out.println("All lines removed from: " + fileName);

		}catch(IOException e){
			System.out.println("Error when trying to clear contents of file!");
		}
	}

	String trimLine(String lineToTrim){
		return lineToTrim.trim(); 
	}
	
	public static void main(String args[]){
		if(args.length == 1){
			String fileName = args[0];

			TextBuddy tb = new TextBuddy(fileName);
			tb.run();
		}else{
			System.out.println("USAGE: java TextBuddy <FILENAME>");
		}
		
	}
}

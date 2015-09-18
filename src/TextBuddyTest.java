import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {
	
	TextBuddy testTB;
	String fileName;
	
	@Before
	public void setUp() throws Exception {
		fileName = "hello.txt";
		testTB = new TextBuddy(fileName);
	}

	@Test
	public void testCreateFile() {
		File expectedFile = new File(fileName);
		File actualFile = testTB.createFile(fileName);

		assertEquals(expectedFile.getName(), actualFile.getName());
		assertEquals(expectedFile.getPath(), actualFile.getPath());
	}

	@Test
	public void testExtractCommand() {
		String testCommand =  "add Hello World!";
		String[] expectedArr = new String[2];
		
		expectedArr[0] = "add";
		expectedArr[1] = "Hello World!";
		
		assertArrayEquals(expectedArr, testTB.extractCommand(testCommand));
	}

	@Test
	public void testWriteToFile() {
		String write = "The big brown fox jumped over the lazy dog";
		String expectedOutput = "Added to " + fileName + ": \"" + write + "\"";

		assertEquals(expectedOutput, testTB.writeToFile(fileName, write));

		File actualFile = new File(fileName);
		
		//test if file exists
		assertTrue(actualFile.exists());
		try{
			BufferedReader br = new BufferedReader(new FileReader(actualFile.getName()));	

			String currLine;
			while((currLine = br.readLine()) != null){
				//Test if line has been written into actual file.
					assertEquals(write, currLine);
			}
			
			br.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testRemoveFromFile() {
		//clear contents from file before testing
		testTB.clearContents(fileName);
		
		//insert line into file
		String lineToRemove = "Hello World!";
		
		testTB.writeToFile(fileName, lineToRemove);
		
		String expectedMessage = "Deleted from " + fileName + ": \"" + lineToRemove + "\"";
		
		assertEquals(expectedMessage, testTB.removeFromFile(fileName, 1));
	}

	@Test
	public void testDisplayContents() {
		//clear file first before displaying
		testTB.clearContents(fileName);
		
		testTB.writeToFile(fileName, "Hello World!");
		testTB.writeToFile(fileName, "Lalalalala");
		testTB.writeToFile(fileName, "This is a test!");
		
		String expectedOutput = "1: Hello World!\n" + 
								"2: Lalalalala\n" +
								"3: This is a test!";
		
		assertEquals(expectedOutput, testTB.displayContents(fileName));
	}

	@Test
	public void testClearContents() {
		testTB.clearContents(fileName);
		
		testTB.writeToFile(fileName, "Hello World!");
		testTB.writeToFile(fileName, "Lalalalala");
		testTB.writeToFile(fileName, "This is a test!");
		
		String expectedOutput = "All lines deleted from " + fileName;
		
		assertEquals(expectedOutput, testTB.clearContents(fileName));
		
		//check if file is indeed empty
		boolean isEmpty = true;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			while(br.readLine() != null){
				isEmpty = false;
			}
			
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		assertTrue(isEmpty);
	}
	
	@Test
	public void testSort(){
		//clear file contents before testing
		testTB.clearContents(fileName);
		
		testTB.writeToFile(fileName, "a");
		testTB.writeToFile(fileName, "c");
		testTB.writeToFile(fileName, "b");
		
		String expectedOutput = "Sorted File:\n" +
								"1: a\n" + 
								"2: b\n" +
								"3: c";
		
		assertEquals(expectedOutput, testTB.sort(fileName));
	}

}

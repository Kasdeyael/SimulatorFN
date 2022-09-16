package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.junit.BeforeClass;
import org.junit.Test;

import parser.ParseLabel;
import parser.Parser;

public class WriteFileTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new ParseLabel().parse("testing.csv");
		new  Parser().parse("testingEd.jsonl",1440);
	} 


	/**
	 * Test that it writes the file, with two measurements, correctly.
	 */
	@Test
	public void testDouble() {
		//check if it made the file
		File file = new File("testingparsed.txt");
		assertTrue(file.exists());
		assertFalse(file.isDirectory());
		
		//check contents are what expected
		String[] inf = {"0","16.66666666", "20.83333333","25", "45.83333333", "66.66666666"};
		String[] vac = {"0","8.33333333", "16.66666666", "33.33333333", "33.33333333", "33.33333333"};

		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			br.readLine();
			int pos = 0;
			while ((line = br.readLine()) != null) { 
				String[] tempArr = line.split(","); //separate each line
				assertEquals(inf[pos],tempArr[0]);
				assertEquals(vac[pos], tempArr[1]);
				pos++;
			}
			assertEquals(6, pos);
			br.close();
		}catch(Exception e) {
			fail("Error writing the file");
		}


	}
	
	/**
	 * Test that it writes the file, with one measurements, correctly.
	 */
	@Test
	public void testSingle() {
		//check if it made the file
		File file = new File("testingEd.jsonlparsed.txt");
		assertTrue(file.exists());
		assertFalse(file.isDirectory());
		
		//check contents are what expected
		String[] inf = {"0.0","37.5", "37.5","31.25", "31.25", "25.0"};

		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			br.readLine();
			int pos = 0;
			while ((line = br.readLine()) != null) { 
				assertEquals(inf[pos],line);
				pos++;
			}
			assertEquals(6, pos);
			br.close();
		}catch(Exception e) {
			fail("Error writing the file");
		}


	}

}

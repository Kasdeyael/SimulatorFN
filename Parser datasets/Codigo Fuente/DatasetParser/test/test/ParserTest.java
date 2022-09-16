package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;

import parser.Parser;

public class ParserTest {

	private static Parser fl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fl = new  Parser();
		fl.parse("testingEd.jsonl",1440);

	}

	/**
	 * Test that the number of users is correct
	 */
	@Test
	public void testNumberUsers() {
		//test number of users
		int detected = fl.getNumberUsers();
		assertEquals(16, detected);
	}

	/**
	 * Test that the number of users spreading news per day is correct
	 */
	@Test
	public void testUsuariosDia() {
		ArrayList<Integer> usuarios = new ArrayList<Integer>();
		usuarios.add(6);
		usuarios.add(6);
		usuarios.add(5);
		usuarios.add(5);
		usuarios.add(4);
		ArrayList<Integer> inf = fl.getListaUsuariosDia();
		assertEquals(usuarios.size(), inf.size());
		for(int i=0; i< inf.size(); i++) {

			assertEquals(usuarios.get(i), inf.get(i));
		}

	}

	/**
	 * Test that percentage of spread is correct
	 */
	@Test
	public void testPercentages() {
		ArrayList<Double> infExpected = new ArrayList<Double>();
		infExpected.add(0.0);
		infExpected.add((double)6/(double)16 * 100.0);
		infExpected.add((double)6/(double)16 * 100.0);
		infExpected.add((double)5/(double)16 * 100.0);
		infExpected.add((double)5/(double)16 * 100.0);
		infExpected.add((double)4/(double)16 * 100.0);

		ArrayList<Double> inf = fl.getPercentages();
		assertEquals(infExpected.size(), inf.size());
		for(int i=0; i< inf.size(); i++) {

			assertEquals(infExpected.get(i), inf.get(i));
		}
	}
	
	public void testWriter() {
		
	}


}

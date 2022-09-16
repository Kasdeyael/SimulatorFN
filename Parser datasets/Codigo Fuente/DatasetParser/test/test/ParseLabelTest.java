package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;

import parser.ParseLabel;

public class ParseLabelTest {

	private static ParseLabel fl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fl = new ParseLabel();
		fl.parse("testing.csv");
	} 

	/**
	 * Test that it parsed the number of users in the file correctly
	 */
	@Test
	public void testNumberUsers() {
		//test number of users
		int detected = fl.getNumberUsers();
		assertEquals(24, detected);
	}

	/**
	 * Test that it parsed the infected per day correctly.
	 */
	@Test
	public void testInfectedPerDay() {
		ArrayList<Integer> infExpected = new ArrayList<Integer>();
		infExpected.add(4);
		infExpected.add(5);
		infExpected.add(6);
		infExpected.add(11);
		infExpected.add(16);
		ArrayList<Integer> inf = fl.getInfected();
		assertEquals(infExpected.size(), inf.size());


		assertEquals(infExpected, inf);

	}
	
	/**
	 * Test that it parsed the vaccinated per day correctly.
	 */
	@Test
	public void testVaccinatedPerDay() {
		ArrayList<Integer> vacExpected = new ArrayList<Integer>();
		vacExpected.add(2);
		vacExpected.add(4);
		vacExpected.add(8);
		vacExpected.add(8);
		vacExpected.add(8);
		ArrayList<Integer> vac = fl.getVaccinated();
		assertEquals(vacExpected.size(), vac.size());


		assertEquals(vacExpected, vac);

	}

	/**
	 * Test that percentage of infected is correct
	 */
	@Test
	public void testInfPercentages() {
		ArrayList<Double> infExpected = new ArrayList<Double>();
		infExpected.add(0.0);
		infExpected.add((double)4/(double)24 * 100.0);
		infExpected.add((double)5/(double)24 * 100.0);
		infExpected.add((double)6/(double)24 * 100.0);
		infExpected.add((double)11/(double)24 * 100.0);
		infExpected.add((double)16/(double)24 * 100.0);

		ArrayList<Double> inf = fl.getInfectedPercentage();
		assertEquals(infExpected.size(), inf.size());
		for(int i=0; i< inf.size(); i++) {

			assertEquals(infExpected.get(i), inf.get(i));
		}
	}
	/**
	 * Test that percentage of vaccinated is correct
	 */
	@Test
	public void testVacPercentages() {

		ArrayList<Double> vacExpected = new ArrayList<Double>();
		vacExpected.add(0.0);
		vacExpected.add((double)2/(double)24 * 100.0);
		vacExpected.add((double)4/(double)24 * 100.0);
		vacExpected.add((double)8/(double)24 * 100.0);
		vacExpected.add((double)8/(double)24 * 100.0);
		vacExpected.add((double)8/(double)24 * 100.0);

		ArrayList<Double> vac = fl.getVaccinatedPercentage();
		assertEquals(vacExpected.size(), vac.size());

		for(int i=0; i< vac.size(); i++) {
			assertEquals(vacExpected.get(i), vac.get(i));
			
		}
	}

}

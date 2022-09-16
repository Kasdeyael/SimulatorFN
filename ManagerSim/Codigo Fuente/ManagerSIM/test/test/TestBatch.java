package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import batch.BatchRuns;
import batch.ParamValue;

import databaseCon.ConnectionMgr;
import databaseCon.DatabaseConnector;
import exception.IncompatibleParameterException;
import inputFiles.Parameters;
import metricCalc.MetricCalculator;

public class TestBatch {

	private static HashMap<String,ParamValue> parameters;
	static BatchRuns batch;
	static DatabaseConnector db_ins;
	static ConnectionMgr con;
	static MetricCalculator metrics;
	static String path = "."+File.separator+"test"+File.separator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		parameters = new HashMap<String, ParamValue> ();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document params = db.parse(path+"testBatchParameters.xml");

			NodeList list = params.getElementsByTagName("parameter");


			for (int temp = 0; temp < list.getLength(); temp++) {

				Node n = list.item(temp);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) n;
					if(element.getAttribute("name").equals("probInfect")) {

						ParamValue pInf = new ParamValue("probInfect");
						pInf.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						pInf.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						pInf.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("probInfect",pInf);
					} else if(element.getAttribute("name").equals("probMakeDenier")) {

						ParamValue pMD = new ParamValue("probMakeDenier");
						pMD.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						pMD.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						pMD.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("probMakeDenier",pMD);
					} else if(element.getAttribute("name").equals("probAcceptDeny")) {

						ParamValue pAD = new ParamValue("probAcceptDeny");
						pAD.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						pAD.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						pAD.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("probAcceptDeny",pAD);
					} else if(element.getAttribute("name").equals("probInfl")) {

						ParamValue pInfl = new ParamValue("probInfl");
						pInfl.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						pInfl.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						pInfl.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("probInfl",pInfl);
					} else if(element.getAttribute("name").equals("engagement")) {

						ParamValue eng = new ParamValue("engagement");
						eng.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						eng.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						eng.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("engagement",eng);
					}
				}

			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			fail("Couldn't read the batch file");
		}
	}


	/**
	 * Test that it creates the files expected and they execute the right parameters.
	 */
	@Test
	public void testBatch() {

		try {
			Parameters params = new Parameters(path+"dbTest.properties");
			BatchRuns run = new BatchRuns(params.getParameter("batch_file"),params.getParameter("batch_params"));
			
			run.startRuns(params.getParameter("batch_runner"),params.getParameter("batch_config"),true);
			File fl = new File(params.getParameter("output_folder"));
			String pref = params.getParameter("prefix");
			String suf = params.getParameter("suffix");
			File[] files = fl.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {

					if(pathname.getName().startsWith(pref) && pathname.getName().endsWith(suf) && pathname.isFile()) {
						return true;
					}
					return false;
				}
			});
			Arrays.parallelSort(files);
			int reps = 1;
			for(ParamValue p : parameters.values()) {
				reps = reps * (Integer.valueOf(p.getValues().toString())+1);
			}

			assertEquals(reps, files.length);

			BufferedReader lineReader = null;
			BigDecimal pInf = parameters.get("probInfect").getInit();
			BigDecimal pMD = parameters.get("probMakeDenier").getInit();
			BigDecimal pAD = parameters.get("probAcceptDeny").getInit();
			BigDecimal pInfl = parameters.get("probInfl").getInit();
			BigDecimal eng = parameters.get("engagement").getInit();

			for(File f : files) {
				//for each file, open and check params
				lineReader = new BufferedReader(new FileReader(f));
				System.out.println(f.getName());
				//now gather modeltype
				try {
					String[] data = lineReader.readLine().split(","); //header
					String[] firstLine = lineReader.readLine().split(","); //the first line of file

					for(int i = 0; i < data.length; i++) {

						if(data[i].replace('"', ' ').trim().equals("probInfect")) 
							assertEquals(pInf.stripTrailingZeros().toString(),firstLine[i]);


						if(data[i].replace('"', ' ').trim().equals("probMakeDenier")) 
							assertEquals(pMD.stripTrailingZeros().toString(),firstLine[i]);

						if(data[i].replace('"', ' ').trim().equals("probAcceptDeny")) 
							assertEquals(pAD.stripTrailingZeros().toString(),firstLine[i]);

						if(data[i].replace('"', ' ').trim().equals("probInfl")) 
							assertEquals(pInfl.stripTrailingZeros().toString(),firstLine[i]);

						if(data[i].replace('"', ' ').trim().equals("engagement")) 
							assertEquals(eng.stripTrailingZeros().toString(),firstLine[i]);

					}

					lineReader.close();

					if(pInf.compareTo(parameters.get("probInfect").getEnd()) == -1) {
						pInf = pInf.add(parameters.get("probInfect").getStep());
					} else {

						pInf = parameters.get("probInfect").getInit();
						if(pAD.compareTo(parameters.get("probAcceptDeny").getEnd()) == -1) {
							pAD = pAD.add(parameters.get("probAcceptDeny").getStep());
						} else {
							pAD = parameters.get("probAcceptDeny").getInit();
							if(pMD.compareTo(parameters.get("probMakeDenier").getEnd()) == -1) {
								pMD = pMD.add(parameters.get("probMakeDenier").getStep());
							} else {
								pMD = parameters.get("probMakeDenier").getInit();
								if(pInfl.compareTo(parameters.get("probInfl").getEnd()) == -1) {
									pInfl = pInfl.add(parameters.get("probInfl").getStep());

								} else {
									pInfl = parameters.get("probInfl").getInit();
									if(eng.compareTo(parameters.get("engagement").getEnd()) == -1) {
										eng = eng.add(parameters.get("engagement").getStep());
									}
								}

							}
						}
					}

				} catch (IOException e) {
					fail("Error opening files to compare");
				}

			}

			for(File f: fl.listFiles()) {

				if(f.getName().startsWith(pref) && f.isFile()) {
					f.delete();
				}
			}

		} catch (FileNotFoundException | IncompatibleParameterException e1) {
			fail("Could not open file. "+e1.getMessage());
		}
	}

}


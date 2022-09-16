package batch;


import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.math.BigDecimal;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import exception.IncompatibleParameterException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class BatchRuns {

	private File fileBatch;
	private File fileParameters;
	private int model;
	private HashMap<String,ParamValue> parameters;

	/**
	 * Constructor for BatchRuns
	 * @param file file for the batch runner
	 * @param fileParam file with the parameters to sweep
	 * @throws IncompatibleParameterException 
	 */
	public BatchRuns(String file, String fileParam) throws IncompatibleParameterException {
		fileBatch = new File(file);
		fileParameters = new File(fileParam);
		parameters = new HashMap<String,ParamValue>();
		model = -1;
		readModel();
	}

	/**
	 * Read the model parameters to run as stated by XML file
	 * @return true if processed, false if not
	 * @throws IncompatibleParameterException 
	 */
	private void readModel() throws IncompatibleParameterException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document params = db.parse(fileParameters);

			NodeList list = params.getElementsByTagName("parameter");
			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					if(!element.getAttribute("name").equals("model")) continue;
					model = Integer.parseInt(element.getElementsByTagName("value").item(0).getTextContent()); //the model
					break;
				}
			}
			if(model>4 || model <1) throw new IncompatibleParameterException("The model value in the batch parameter file is wrong.");
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
					} else if(model>1 && element.getAttribute("name").equals("probInfl")) {

						ParamValue pInfl = new ParamValue("probInfl");
						pInfl.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						pInfl.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						pInfl.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("probInfl",pInfl);
					} else if(model>2 && element.getAttribute("name").equals("engagement")) {

						ParamValue eng = new ParamValue("engagement");
						eng.setInit(new BigDecimal(element.getElementsByTagName("start").item(0).getTextContent()));
						eng.setEnd(new BigDecimal(element.getElementsByTagName("end").item(0).getTextContent()));
						eng.setStep(new BigDecimal(element.getElementsByTagName("step").item(0).getTextContent()));
						parameters.put("engagement",eng);
					}
				}

			}
			if(model == 2 && parameters.size() != 4) throw new IncompatibleParameterException("Not all parameters could be found for model 2");
			if(model >= 3 && parameters.size() != 5) throw new IncompatibleParameterException("Not all parameters could be found for model 3");
			if(model == 1 && parameters.size() != 3)throw new IncompatibleParameterException("Not all parameters could be found for model 1");

		} catch (ParserConfigurationException | SAXException | NumberFormatException | IOException e) {
			throw new IncompatibleParameterException("Could not parse batch configuration file to set initial parameters");
		}

	}

	/**
	 * Processes all the runs to execute, until the end of the parameter file is reached.
	 * @param batchRun the file for the batch runs
	 * @param batchConfig the config file for the runner
	 * @param startOver if it's necessary to reset the parameters
	 * @throws IncompatibleParameterException
	 */
	public void startRuns(String batchRun, String batchConfig, boolean startOver) throws IncompatibleParameterException {

		if(model == -1) return;
		
		if(!new File(batchRun).exists() || !new File(batchConfig).exists()) throw new IncompatibleParameterException("The batch parameters are not set correctly");
		
		if(startOver) regenBatchFile(); //we start over if specified
		do { 

			try {

				ProcessBuilder p = new ProcessBuilder("java", "-jar", batchRun, "-hl", "-r", "-c", batchConfig);
				
				p.redirectErrorStream(false);
				File log = new File("log.txt");
				p.redirectOutput(Redirect.to(log));

				Process p1 = p.start();
				p1.waitFor();

			} catch (IOException | InterruptedException e) {

				throw new IncompatibleParameterException("Error running the model");
			}

		} while(updateModels());
	}

	/**
	 * Updates the parameters based on the model we're running.
	 * @return true if there's more runs to do or false if we've reached the end.
	 */
	private boolean updateModels() throws IncompatibleParameterException{

		if(model == 3) return updateModel3();
		else if (model == 2) return updateModel2();
		else return updateModel1();

	}

	/**
	 * Updates the parameters for model 3.
	 * @return true if there's more runs to do or false if we've reached the end.
	 * @throws IncompatibleParameterException 
	 */
	private boolean updateModel3() throws IncompatibleParameterException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// parse XML file
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileBatch);

			NodeList list = doc.getElementsByTagName("parameter");
			BigDecimal probInf = null, engagement = null, probMakeDenier = null, probAcceptDeny = null, probInfl = null;

			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");
				if(n.getNodeValue().equals("probInfect")) {
					Node nod = nm.getNamedItem("value");

					probInf=new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probMakeDenier")) {
					Node nod = nm.getNamedItem("value");

					probMakeDenier=new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probAcceptDeny")) {
					Node nod = nm.getNamedItem("value");

					probAcceptDeny=new BigDecimal(nod.getNodeValue());

				}  else if(n.getNodeValue().equals("engagement")) {
					Node nod = nm.getNamedItem("value");
					engagement =new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probInfl")) {
					Node nod = nm.getNamedItem("value");
					probInfl =new BigDecimal(nod.getNodeValue());
				}
			}
			
			if(probInf == null || probMakeDenier ==null || probAcceptDeny == null || probInfl ==null || engagement == null) 
				throw new IncompatibleParameterException("The batch file for the simulator doesn't have proper format");
			
			if(probInf.compareTo(parameters.get("probInfect").getEnd()) == -1) {
				probInf = probInf.add(parameters.get("probInfect").getStep());
			}else {

				probInf = parameters.get("probInfect").getInit();
				if(probAcceptDeny.compareTo(parameters.get("probAcceptDeny").getEnd()) == -1) {
					probAcceptDeny = probAcceptDeny.add(parameters.get("probAcceptDeny").getStep()); //add step
				} else {
					probAcceptDeny = parameters.get("probAcceptDeny").getInit(); //set to beginning
					if(probMakeDenier.compareTo(parameters.get("probMakeDenier").getEnd()) == -1) {
						probMakeDenier = probMakeDenier.add(parameters.get("probMakeDenier").getStep());
					} else {
						probMakeDenier = parameters.get("probMakeDenier").getInit();
						if(probInfl.compareTo(parameters.get("probInfl").getEnd()) == -1) {
							probInfl = probInfl.add(parameters.get("probInfl").getStep());

						} else {
							probInfl = parameters.get("probInfl").getInit();
							if(engagement.compareTo(parameters.get("engagement").getEnd()) == -1) {
								engagement = engagement.add(parameters.get("engagement").getStep());
							} else {
								return false;
							}
						}

					}
				}
			}
			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");
				if(n.getNodeValue().equals("probInfect")) {
					nm.getNamedItem("value").setTextContent(probInf.toString());
				} else if(n.getNodeValue().equals("probMakeDenier")) {
					nm.getNamedItem("value").setTextContent(probMakeDenier.toString());
				} else if(n.getNodeValue().equals("probAcceptDeny")) {
					nm.getNamedItem("value").setTextContent(probAcceptDeny.toString());
				} else if(n.getNodeValue().equals("engagement")) {
					nm.getNamedItem("value").setTextContent(engagement.toString());
				} else if(n.getNodeValue().equals("probInfl")) {
					nm.getNamedItem("value").setTextContent(probInfl.toString());
				}

			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);

			StreamResult streamResult = new StreamResult(fileBatch);
			transformer.transform(domSource, streamResult);
			System.out.println("engagement: "+engagement.toString()+", PINFL: "+probInfl.toString()+", PMD: "+probMakeDenier.toString()+", PAD: "+probAcceptDeny.toString()+", PINF: "+probInf.toString());
			return true;
		} catch (ParserConfigurationException | SAXException | NumberFormatException | IOException e) {
			throw new IncompatibleParameterException("error parsing the file to edit its values");
		} catch (TransformerException e) {

			throw new IncompatibleParameterException("Error saving the XML file for the new parameters");
		} 


	}

	/**
	 * Regenerates the batch file according to the batch parameters we are currently running.
	 * @throws IncompatibleParameterException 
	 */
	private void regenBatchFile() throws IncompatibleParameterException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {


			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileBatch);

			NodeList list = doc.getElementsByTagName("parameter");

			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");


				if(n.getNodeValue().equals("probMakeDenier")) {
					
					nm.getNamedItem("value").setTextContent(parameters.get("probMakeDenier").getInit().toString());

				} else if(n.getNodeValue().equals("probAcceptDeny")) {

					nm.getNamedItem("value").setTextContent(parameters.get("probAcceptDeny").getInit().toString());

				} else if(n.getNodeValue().equals("probInfect")) {
					
					nm.getNamedItem("value").setTextContent(parameters.get("probInfect").getInit().toString());

				} else if(model>1 && n.getNodeValue().equals("probInfl")) {
					
					nm.getNamedItem("value").setTextContent(parameters.get("probInfl").getInit().toString());

				} else if(model>2 && n.getNodeValue().equals("engagement")) {

					nm.getNamedItem("value").setTextContent(parameters.get("engagement").getInit().toString());

				} else if(n.getNodeValue().equals("modelType")) {

					nm.getNamedItem("value").setTextContent(String.valueOf(model));

				} 


			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);

			StreamResult streamResult = new StreamResult(fileBatch);
			transformer.transform(domSource, streamResult);


		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IncompatibleParameterException("error parsing the file to edit its values");
		} catch (TransformerException e) {

			throw new IncompatibleParameterException("Error saving the XML file for the new parameters");
		} 
	}

	/**
	 * Updates the parameters for model 1.
	 * @return true if there's more runs to do or false if we've reached the end.
	 * @throws IncompatibleParameterException 
	 */
	private boolean updateModel1() throws IncompatibleParameterException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileBatch);

			NodeList list = doc.getElementsByTagName("parameter");
			BigDecimal probInfect = null, probMakeDenier = null, probAcceptDeny = null;

			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");
				if(n.getNodeValue().equals("probInfect")) {
					Node nod = nm.getNamedItem("value");

					probInfect=new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probMakeDenier")) {
					Node nod = nm.getNamedItem("value");

					probMakeDenier=new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probAcceptDeny")) {
					Node nod = nm.getNamedItem("value");

					probAcceptDeny=new BigDecimal(nod.getNodeValue());

				} 
			}
			if(probInfect == null || probMakeDenier ==null || probAcceptDeny == null) throw new IncompatibleParameterException("The batch file for the simulator doesn't have proper format");
			if(probInfect.compareTo(parameters.get("probInfect").getEnd()) == -1) {
				probInfect = probInfect.add(parameters.get("probInfect").getStep());
			}else {

				probInfect = parameters.get("probInfect").getInit();
				if(probAcceptDeny.compareTo(parameters.get("probAcceptDeny").getEnd()) == -1) {
					probAcceptDeny = probAcceptDeny.add(parameters.get("probAcceptDeny").getStep());
				} else {
					probAcceptDeny = parameters.get("probAcceptDeny").getInit();
					if(probMakeDenier.compareTo(parameters.get("probMakeDenier").getEnd()) == -1) {
						probMakeDenier = probMakeDenier.add(parameters.get("probMakeDenier").getStep());
					} else {
						return false;

					}
				}
			}


			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");
				if(n.getNodeValue().equals("probInfect")) {
					nm.getNamedItem("value").setTextContent(probInfect.toString());
				} else if(n.getNodeValue().equals("probMakeDenier")) {
					nm.getNamedItem("value").setTextContent(probMakeDenier.toString());

				} else if(n.getNodeValue().equals("probAcceptDeny")) {
					nm.getNamedItem("value").setTextContent(probAcceptDeny.toString());

				}

			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);

			StreamResult streamResult = new StreamResult(fileBatch);
			transformer.transform(domSource, streamResult);
			System.out.println("PMD: "+probMakeDenier.toString()+", PAD: "+probAcceptDeny.toString()+", PINF: "+probInfect.toString());

			return true;
		} catch (ParserConfigurationException | SAXException | NumberFormatException | IOException e) {
			throw new IncompatibleParameterException("error parsing the file to edit its values");
		} catch (TransformerException e) {

			throw new IncompatibleParameterException("Error saving the XML file for the new parameters");
		} 

	}

	/**
	 * Updates the parameters for model 2.
	 * @return true if there's more runs to do or false if we've reached the end.
	 * @throws IncompatibleParameterException 
	 */
	private boolean updateModel2() throws IncompatibleParameterException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileBatch);


			NodeList list = doc.getElementsByTagName("parameter");
			BigDecimal probInfect = null, probMakeDenier = null, probAcceptDeny = null, probInfl = null;

			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");
				if(n.getNodeValue().equals("probInfect")) {
					Node nod = nm.getNamedItem("value");

					probInfect=new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probMakeDenier")) {
					Node nod = nm.getNamedItem("value");

					probMakeDenier=new BigDecimal(nod.getNodeValue());

				} else if(n.getNodeValue().equals("probAcceptDeny")) {
					Node nod = nm.getNamedItem("value");

					probAcceptDeny=new BigDecimal(nod.getNodeValue());

				}  else if(n.getNodeValue().equals("probInfl")) {
					Node nod = nm.getNamedItem("value");
					probInfl =new BigDecimal(nod.getNodeValue());
				}
			}
			if(probInfect == null || probMakeDenier ==null || probAcceptDeny == null || probInfl ==null) 
				throw new IncompatibleParameterException("The batch file for the simulator doesn't have proper format");
			if(probInfect.compareTo(parameters.get("probInfect").getEnd()) == -1) {
				probInfect = probInfect.add(parameters.get("probInfect").getStep());
			}else {
				//probInfect is last position
				probInfect = parameters.get("probInfect").getInit();
				if(probAcceptDeny.compareTo(parameters.get("probAcceptDeny").getEnd()) == -1) {
					probAcceptDeny = probAcceptDeny.add(parameters.get("probAcceptDeny").getStep());
				} else {
					probAcceptDeny = parameters.get("probAcceptDeny").getInit();
					if(probMakeDenier.compareTo(parameters.get("probMakeDenier").getEnd()) == -1) {
						probMakeDenier = probMakeDenier.add(parameters.get("probMakeDenier").getStep());
					} else {
						probMakeDenier = parameters.get("probMakeDenier").getInit();
						if(probInfl.compareTo(parameters.get("probInfl").getEnd()) == -1) {
							probInfl = probInfl.add(parameters.get("probInfl").getStep());

						} else {
							return false;
						}

					}
				}
			}


			for (int temp = 0; temp < list.getLength(); temp++) {

				Node node = list.item(temp);
				NamedNodeMap nm = node.getAttributes();
				Node n = nm.getNamedItem("name");
				if(n.getNodeValue().equals("probInfect")) {
					nm.getNamedItem("value").setTextContent(probInfect.toString());
				} else if(n.getNodeValue().equals("probMakeDenier")) {
					nm.getNamedItem("value").setTextContent(probMakeDenier.toString());

				} else if(n.getNodeValue().equals("probAcceptDeny")) {
					nm.getNamedItem("value").setTextContent(probAcceptDeny.toString());

				} else if(n.getNodeValue().equals("probInfl")) {
					nm.getNamedItem("value").setTextContent(probInfl.toString());

				}

			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);

			StreamResult streamResult = new StreamResult(fileBatch);
			transformer.transform(domSource, streamResult);
			System.out.println("PINFL: "+probInfl.toString()+", PMD: "+probMakeDenier.toString()+", PAD: "+probAcceptDeny.toString()+", PINF: "+probInfect.toString());
			return true;
		} catch (ParserConfigurationException | SAXException | NumberFormatException | IOException e) {
			throw new IncompatibleParameterException("error parsing the file to edit its values");
		} catch (TransformerException e) {

			throw new IncompatibleParameterException("Error saving the XML file for the new parameters");
		} 


	}


}

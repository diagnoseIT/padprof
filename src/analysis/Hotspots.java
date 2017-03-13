package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import hotspots.*;;

/**
 * reads the hotspots XML file from YourKit
 * 
 * @author Alexander Bran
 *
 */
public class Hotspots {
	private String xmlFilePath;
	private View view;

	public Hotspots(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
		readXMLFile();
	}

	public void readXMLFile() {

		JAXBContext jaxb = null;
		try {
			jaxb = JAXBContext.newInstance("hotspots");
			javax.xml.bind.Unmarshaller un = jaxb.createUnmarshaller();
			// File xml = new File("Call-tree--by-thread.xml");
			File xml = new File(xmlFilePath);
			view = (View) un.unmarshal(xml);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * find the most important methods of this particular hotspot
	 * 
	 * @return
	 */
	public ArrayList<String> findImportantMethods() {
		ArrayList<String> importantMethods = new ArrayList<>();
		List<Node> nodes = view.getNode();
		int totalTime = 0;
		int counter = 0;
		// first just methods with a time above the average count is taken
		for (Node node : nodes) {
			totalTime += node.getTimeMs();
			counter++;
		}
		int averageTime = totalTime / counter;

		for (Node node : nodes) {
			if (node.getTimeMs() >= averageTime) {
				importantMethods.add(node.getMethod());
			}
		}

		return importantMethods;
	}

	/**
	 * returns all the hotspot methods
	 * @return
	 */
	public ArrayList<String> getAllHotspots() {
		ArrayList<String> importantMethods = new ArrayList<>();
		List<Node> nodes = view.getNode();

		for (Node node : nodes) {

			importantMethods.add(node.getMethod());

		}

		return importantMethods;
	}
}

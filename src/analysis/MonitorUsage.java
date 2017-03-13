package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import monitor_usage.*;

/**
 * This class can read the xml files from the "MonitorUsage" view of your kit.
 * @author Alexander Bran
 *
 */
public class MonitorUsage {
	private String xmlFilePath;
	View view;

	String name;

	public MonitorUsage(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
		readFile();
		setName();
	}

	/**
	 * reads the xml file
	 */
	private void readFile() {
		JAXBContext jaxb = null;
		try {
			jaxb = JAXBContext.newInstance("monitor_usage");
			javax.xml.bind.Unmarshaller un = jaxb.createUnmarshaller();
			// File xml = new File("Call-tree--by-thread.xml");
			File xml = new File(xmlFilePath);
			view = (View) un.unmarshal(xml);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getBlockedThreadCount() {
		List<Node> nodeList = view.getNode();
	
		return nodeList.size();
	}

	public int getBlockedThreadsMaxCount() {
		List<Node> nodeList = view.getNode();
		int maxCount = 0;
		int count = 0;
		for (Node node : nodeList) {
			count++;
			List<Node> insideList = node.getNode();
			
			count = insideList.size();
			if (count > maxCount)
				maxCount = count;
		}
	
		return maxCount;

	}

	public int getBlockedThreadsAverageCount() {
		List<Node> nodeList = view.getNode();
		int overallValue = 0;
		int count = 0;
		for (Node node : nodeList) {
			count++;
			List<Node> insideList = node.getNode();
			overallValue += insideList.size();
		}

		return overallValue / count;

	}
	
	public int getBlockedThreadsCompleteCount() {
		List<Node> nodeList = view.getNode();
		int overallValue = 0;
		
		for (Node node : nodeList) {
			
			List<Node> insideList = node.getNode();
			overallValue += insideList.size();
		}

		return overallValue;

	}

	/**
	 * sets the name from the xml file
	 */
	private void setName() {
		int start = xmlFilePath.lastIndexOf("/");
		name = xmlFilePath.substring(start + 1, xmlFilePath.length());
	}

	/**
	 * @return name from the xml file
	 */
	public String getName() {
		return name;
	}

	/**
	 * gets all subnodes from one specific node
	 * 
	 * @param mainNode
	 *            Node from which the subNodes should be found
	 * @return list of all the subnodes from a certain node
	 */
	private List<Node> returnAllSubnodes(Node mainNode) {
		List<Node> output = new ArrayList<Node>();

		Stack<Node> stackNodes = new Stack<Node>();
		stackNodes.add(mainNode);

		while (!stackNodes.isEmpty()) {
			Node currentNode = stackNodes.pop();
			List<Node> allChildren = currentNode.getNode();
			stackNodes.addAll(allChildren);
			output.addAll(allChildren);
		}

		return output;
	}
}

package analysis;

import java.io.File;

import java.util.ArrayList;

import java.util.List;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

//import method_list.*;
import call_tree_new.Node;
import call_tree_new.View;

/**
 * This class can read the "All threads" and "By Thread" call tree xml data from YourKit.
 * @author Alexander Bran
 *
 */
public class CallTree {
	private String xmlFilePath;
	View view;
	static List<MethodCall> methodCalls = new ArrayList<MethodCall>();
	String name;

	public CallTree(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
		readFile();
		setName();
	}

	/**
	 * reads the xml file
	 */
	public void readFile() {
		JAXBContext jaxb = null;
		try {
			jaxb = JAXBContext.newInstance("call_tree_new");
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
	 * returns the average method count of a file
	 * @return
	 */
	public int getAverageMethodCalls() {
		List<Node> nodes = view.getNode();

		int nodeSize = 0;

		// gets all methods from one thread (because in the xml are all threads
		// integrated)
		for (int i = 0; i < nodes.size(); i++) {
			// every step is one thread, all threads have first the thread.run()
			// method
			List<Node> allNodes = returnAllSubnodes(nodes.get(i));
			nodeSize += allNodes.size();

			// get deeper to get the function name, not the thread name

			List<Node> deepOne = nodes.get(i).getNode();

			if (deepOne.size() != 0) {
				String name = deepOne.get(0).getCallTree();
				if (checkForSameMethod(methodCalls, name)) {
					setNewMethodValue(methodCalls, name, allNodes.size() - 1);
					// just add, when it is bigger than one method call. (maybe
					// in some cases not useful.
				} else if ((allNodes.size() - 1) > 1) {
					MethodCall methodCall = new MethodCall(name, allNodes.size() - 1, nodes.get(i));
					methodCalls.add(methodCall);
				}

			}

		}
		int averageMethodCount = 0;
		for (MethodCall methodCall : methodCalls) {
			averageMethodCount += methodCall.getAllMethodCalls();
		}

		averageMethodCount = 0;

		// divide the main calls into subCalls to get a finer granularity
		List<MethodCall> methodCallSave = new ArrayList<MethodCall>();

		for (int i = 0; i < 5; i++) {
			int listSize = methodCalls.size();

			for (int j = 0; j < listSize; j++) {
				if (divideIntoSubMethods(methodCalls.get(j).getNode())) {
					methodCallSave.add(methodCalls.get(j));
				}
			}

		}

		for (MethodCall methodCall : methodCalls) {

			averageMethodCount += methodCall.getAllMethodCalls();

		}

		averageMethodCount = (averageMethodCount / methodCalls.size());

		return averageMethodCount;

	}

	/**
	 * get the percentage of the method (max) time in relation to the whole
	 * execution time
	 * 
	 * -> just reasonable for all thread data
	 * 
	 * @param name
	 * @return
	 */
	public double getPercentageOfTime(String name) {
		double percentage = -1;
		double maxMethodTime = 0;

		List<Node> startNodes = view.getNode();

		double overallTime = startNodes.get(0).getTimeMs();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			//each nodePackage is a somehow 'main method'
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {
					
					int currentTime = node.getTimeMs();
					if (currentTime > maxMethodTime)
						maxMethodTime = currentTime;

				}
			}
		}
		if (maxMethodTime != 0) {

			double onePercent = overallTime / 100;
			percentage = maxMethodTime / onePercent;

		}

		return (Math.floor(percentage * 100) / 100);
	}

	/**
	 * get the average method calls of one method. (it is good for checking if a
	 * method has the CTH.)
	 * 
	 * @param name
	 * @param methodCall
	 * @return
	 */
	public int getAverageMethodCalls(String name) {
		int averageMethodCalls = 0;
		int count = 0;

		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {
					List<Node> subNodesCountList = returnAllSubnodes(node);
					averageMethodCalls += subNodesCountList.size();

					count++;
				}
			}
		}

		if (count == 0) {
			averageMethodCalls = -1;
		} else {
			averageMethodCalls = averageMethodCalls / count;
		}
		return averageMethodCalls;
	}

	/**
	 * get the min method calls of one method. (it is good for checking if a
	 * method has the CTH.)
	 * 
	 * @param name
	 * @param methodCall
	 * @return
	 */
	public int getMinMethodCalls(String name) {
		int minMethodCalls = Integer.MAX_VALUE;
		int count = 0;

		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {
					List<Node> subNodesCountList = returnAllSubnodes(node);
					int actualCount = subNodesCountList.size();
					if (minMethodCalls > actualCount)
						minMethodCalls = actualCount;

					count++;
				}
			}
		}
		if (count == 0)
			minMethodCalls = -1;

		return minMethodCalls;
	}

	/**
	 * get the max method calls of one method. (it is good for checking if a
	 * method has the CTH.)
	 * 
	 * @param name
	 * @param methodCall
	 * @return
	 */
	public int getMaxMethodCalls(String name) {
		int maxMethodCalls = 0;
		int count = 0;

		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {
					List<Node> subNodesCountList = returnAllSubnodes(node);
					int actualCount = subNodesCountList.size();
					if (maxMethodCalls < actualCount)
						maxMethodCalls = actualCount;

					count++;
				}
			}
		}

		if (count == 0)
			maxMethodCalls = -1;

		return maxMethodCalls;
	}

	/**
	 * get the average/max/min method time of one method. (it is good for
	 * checking if a method has the CTH.)
	 * 
	 * @param name
	 * @param methodCall
	 * @return
	 */
	public int getMinMethodTime(String name) {
		int minMethodTime = Integer.MAX_VALUE;
		int count = 0;

		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {
					int currentTime = node.getTimeMs();
					if (currentTime < minMethodTime)
						minMethodTime = currentTime;
					count++;
				}
			}
		}
		if (count == 0)
			minMethodTime = -1;

		return minMethodTime;
	}

	/**
	 * get the average/max/min method time of one method. (it is good for
	 * checking if a method has the CTH.)
	 * 
	 * @param name
	 * @param methodCall
	 * @return
	 */
	public int getMaxMethodTime(String name) {
		int maxMethodTime = 0;
		int count = 0;

		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {
					int currentTime = node.getTimeMs();
					if (currentTime > maxMethodTime)
						maxMethodTime = currentTime;
					count++;
				}
			}
		}
		if (count == 0)
			maxMethodTime = -1;

		return maxMethodTime;
	}

	/**
	 * get the average/max/min method time of one method. (it is good for
	 * checking if a method has the CTH.)
	 * 
	 * @param name
	 * @param methodCall
	 * @return
	 */
	public int getAverageMethodTime(String name) {
		int averageMethodTime = 0;
		int count = 0;

		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {
				if (node.getCallTree().contains(name)) {

					averageMethodTime += node.getTimeMs();
					count++;
				}
			}
		}

		if (count == 0) {
			averageMethodTime = -1;
		} else {
			averageMethodTime = averageMethodTime / count;

		}
		return averageMethodTime;
	}
	//new: Analysis of the count of methods with the word "cache" in it. 
	public int countMethodOccurences(String name){
		int counter = 0;
		List<Node> startNodes = view.getNode();

		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {

				
				String callTree = node.getCallTree();
				int found = callTree.indexOf("org");
				if (found == -1)
					found = callTree.indexOf("com");
				if(found == -1)
					found = callTree.indexOf("java");
				String methodName = "";
				if (found!=-1){
					methodName = callTree.substring(found, callTree.length());
				
	
				if (methodName.toLowerCase().contains(name)) {
					counter++;
				}
				}
			}
		}

		
		
		return counter;
	}
	
	
	//new: Analysis of the count of methods with the word "cache" in it. 
	/**
	 * returns the count for methods with a specific String in their name
	 * @param name String which needs to be contained in the method name
	 * @return
	 */
	public double getPercentagesWithPattern(String name){
		int counter = 0;
		ArrayList<Node> seenNodes = new ArrayList<>();
		List<Node> startNodes = view.getNode();
		double overallTime = startNodes.get(0).getTimeMs();
		double time = 0.0;
		for (int i = 0; i < startNodes.size(); i++) {
			List<Node> nodePackage = returnAllSubnodes(startNodes.get(i));
			for (Node node : nodePackage) {

				
				String callTree = node.getCallTree();
				int found = callTree.indexOf("org");
				if (found == -1)
					found = callTree.indexOf("com");
				if(found == -1)
					found = callTree.indexOf("java");
				String methodName = "";
				if (found!=-1){
					methodName = callTree.substring(found, callTree.length());
				
	
				if (methodName.toLowerCase().contains(name)) {
					if(!seenNodes.contains(node)){
//						seenNodes.clear();
						counter++;
						time += node.getTimeMs();
						seenNodes.addAll(returnAllSubnodes(node));
					}
					
				}
			}
			}
		}

		System.out.println("Overall Value: " + overallTime + " found time: " + time);
		double onePercent = overallTime / 100;
		double percentage = time / onePercent;

		
		
		System.out.println("Counter: " + counter);
		return (Math.floor(percentage * 100) / 100);
	}
	

	

	/**
	 * like the other method, but divides the sub methods into more submethods
	 * 
	 * @param startNode
	 */
	private static boolean divideIntoSubMethods(Node startNode) {
		boolean found = false;
		List<Node> duplicateCheck = new ArrayList<Node>();
		Stack<Node> stackNodes = new Stack<Node>();

		stackNodes.add(startNode);

		// same as the retunrAllSubnodes method but with instrumentation in it
		// to look for the children count
		while (!stackNodes.isEmpty()) {
			Node currentNode = stackNodes.pop();
			List<Node> allChildren = currentNode.getNode();
			stackNodes.addAll(allChildren);

			int methodCountOfChild = returnAllSubnodes(currentNode).size();
			// it should have at least two children, and a reasonable method
			// count afterwards
			if (allChildren.size() > 1 && methodCountOfChild >= 50 && !duplicateCheck.contains(currentNode)) {
				if (checkForSameMethod(methodCalls, currentNode.getCallTree())) {
					setNewMethodValue(methodCalls, currentNode.getCallTree(), methodCountOfChild);
					// System.out.println("AA: " + currentNode.getCallTree());
				} else {
					found = true;
					// System.out.println("BB: " + currentNode.getCallTree());
					MethodCall newMethodCall = new MethodCall(currentNode.getCallTree(), methodCountOfChild,
							currentNode);
					methodCalls.add(newMethodCall);
					duplicateCheck = returnAllSubnodes(currentNode);
				}
			}
		}
		return found;
	}

	/**
	 * checks if the method call list already holds a certain method.
	 * 
	 * @param methodCallList
	 * @param name
	 * @return
	 */
	private static boolean checkForSameMethod(List<MethodCall> methodCallList, String name) {
		boolean isOld = false;
		for (MethodCall methodCall : methodCallList) {
			if (methodCall.getName().equals(name)) {
				isOld = true;

			}
		}
		return isOld;
	}

	/**
	 * sets a new value of the method call count
	 * 
	 * @param methodCallList
	 * @param name
	 * @param count
	 */
	private static void setNewMethodValue(List<MethodCall> methodCallList, String name, int count) {

		for (MethodCall methodCall : methodCallList) {
			if (methodCall.getName().equals(name)) {
				methodCall.addSameMethod(count);

			}
		}

	}

	/**
	 * gets all subnodes from one specific node
	 * 
	 * @param mainNode
	 *            Node from which the subNodes should be found
	 * @return list of all the subnodes from a certain node
	 */
	private static List<Node> returnAllSubnodes(Node mainNode) {
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

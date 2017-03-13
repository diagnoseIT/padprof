package analysis;

import call_tree_new.Node;

public class MethodCall {

	private String name;
	private int count = 1;
	private int methodCallsMax;
	Node node;
	
	public MethodCall(String name, int methodCallsMax, Node node){
		this.name = name;
		this.methodCallsMax = methodCallsMax;
		this.node = node;
	}
	
	public void addSameMethod(int methodCalls){
		count++;
		if(methodCalls>methodCallsMax)
			methodCallsMax = methodCalls;
	}

	public String getName() {
		return name;
	}

	public Node getNode() {
		return node;
	}

	public int getCount() {
		return count;
	}

	public int getAllMethodCalls() {
		return methodCallsMax;
	}
	
	
}

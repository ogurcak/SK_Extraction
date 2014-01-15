
package ogurcak.fiit.graph;


import java.util.ArrayList;
import java.util.List;






public class Node implements Comparable<Node>
{


	private List<Edge> parents;

	private List<Edge> childrens;

	private String ruleName;

	private Double probability = 0.0;

	private List<Object> customValueList;






	public Node(String ruleName)
	{

		this.ruleName = ruleName;
		parents = new ArrayList<Edge>();
		childrens = new ArrayList<Edge>();
		customValueList = new ArrayList<Object>();
	}






	public void createChildrens(List<Node> nodes) {

		for (Node node : nodes) {
			Edge edge = new Edge(this, node);
			this.childrens.add(edge);
			node.parents.add(edge);
		}
	}






	public void calculateBayesProbability() {

		this.probability = 0.0;
		for (Edge edge : parents) {
			this.probability += edge.getBayesProbability();
		}
		if (parents.isEmpty())
			this.probability = 1.0;
	}






	public Double getProbability() {

		return this.probability;
	}






	public String getRuleName() {

		return this.ruleName;
	}






	public void setProbability(Double probability) {

		this.probability = probability;
	}






	public void putCustomValue(Object value) {

		this.customValueList.add(value);
	}






	public List<Object> getCustomValueList() {

		List<Object> copy = new ArrayList<Object>(this.customValueList);
		this.customValueList.clear();
		return copy;
	}






	public String toString() {

		return ruleName + " probability: " + getProbability() + " parents: " + parents.size() + " childrens: " + childrens.size();
	}






	public int compareTo(Node arg) {

		return this.getProbability() > arg.getProbability() ? 1 : (this.getProbability() < arg.getProbability() ? -1 : 0);
	}

}

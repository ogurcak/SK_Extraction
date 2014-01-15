
package ogurcak.fiit.graph;


import org.apache.log4j.Logger;






public class Edge
{

	private static Logger logger = Logger.getLogger(Edge.class.getName());

	private Node parent;

	private Node children;

	private int goodResults = 0;

	private int allResults = 0;






	public Edge(Node parent, Node children)
	{

		this.parent = parent;
		this.children = children;
	}






	public void addResult() {

		this.allResults++;
	}






	public void addGoodResult() {

		this.goodResults++;
	}






	public Double getProbability() {

		logger.info(this.goodResults + " " + this.allResults);

		if (this.allResults == 0)
			return 0.0;

		return (double) this.goodResults / this.allResults;
	}






	public double getBayesProbability() {

		return getProbability() * parent.getProbability();
	}






	public Node getChildren() {

		return this.children;
	}

}

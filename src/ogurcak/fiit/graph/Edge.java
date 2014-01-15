
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
		this.goodResults = 1;
		this.allResults = 10;
	}






	public Double getProbability() {

		if (this.allResults == 0)
			return 0.0;

		return (double) this.goodResults / this.allResults;
	}






	public double getBayesProbability() {

		return getProbability() * parent.getProbability();
	}

}

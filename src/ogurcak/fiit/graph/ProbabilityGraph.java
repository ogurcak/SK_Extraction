
package ogurcak.fiit.graph;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Server.Database;
import org.apache.log4j.Logger;






public class ProbabilityGraph
{

	private static Logger logger = Logger.getLogger(ProbabilityGraph.class.getName());

	// one list for every Level
	private Node root;

	private List<Node> dayNodes;

	private List<Node> nameNodes;

	private List<Node> placeNodes;

	private HashMap<String, Node> nodes;






	public void init() throws SQLException {

		logger.info("INIT Probability graph");

		// create all nodes
		dayNodes = new ArrayList<Node>();
		nameNodes = new ArrayList<Node>();
		placeNodes = new ArrayList<Node>();
		root = new Node("Event");
		nodes = new HashMap<String, Node>();


		Statement st = Database.getConnection().createStatement();
		ResultSet rs = st.executeQuery("SELECT GATE_DateTime.rule FROM GATE_DateTime GROUP BY GATE_DateTime.rule");

		while (rs.next()) {
			String name = rs.getString("rule");
			Node node = new Node(name);
			dayNodes.add(node);
			nodes.put(name, node);
		}


		rs = st.executeQuery("SELECT GATE_Name.rule FROM GATE_Name GROUP BY GATE_Name.rule");

		while (rs.next()) {
			String name = rs.getString("rule");
			Node node = new Node(name);
			nameNodes.add(node);
			nodes.put(name, node);
		}


		rs = st.executeQuery("SELECT GATE_Location.rule FROM GATE_Location GROUP BY GATE_Location.rule");

		while (rs.next()) {
			String name = rs.getString("rule");
			Node node = new Node(name);
			placeNodes.add(node);
			nodes.put(name, node);
		}

		st.close();

		// create edges
		root.createChildrens(dayNodes);
		root.createChildrens(nameNodes);
		root.createChildrens(placeNodes);

		for (Node node : dayNodes)
			node.createChildrens(nameNodes);

		for (Node node : nameNodes)
			node.createChildrens(placeNodes);



		// train graph

		// TODO


		// TODO delete this test data
		logger.info("ROOT NODE");
		logger.info(root.toString());

		logger.info("DAYS NODES");
		for (Node node : dayNodes)
			logger.info(node.toString());

		logger.info("NAMES NODES");
		for (Node node : nameNodes)
			logger.info(node.toString());

		logger.info("PLACE NODES");
		for (Node node : placeNodes)
			logger.info(node.toString());

	}






	public void clean() {

		for (Node node : nodes.values())
			node.setProbability(0.0);
	}






	public List<Calendar> sortDates(List<Map.Entry<String, Calendar>> customMap) {

		List<Calendar> list = new ArrayList<Calendar>();
		for (Map.Entry<String, Calendar> entry : customMap) {
			Node node = nodes.get(entry.getKey());
			node.putCustomValue(entry.getValue());
			node.calculateBayesProbability();
		}
		Collections.sort(dayNodes);

		for (Node node : dayNodes) {
			for (Object value : node.getCustomValueList())
				list.add((GregorianCalendar) value);
		}

		return list;
	}






	public List<String> sortNames(List<Map.Entry<String, String>> customMap) {

		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> entry : customMap) {
			Node node = nodes.get(entry.getKey());
			node.putCustomValue(entry.getValue());
			node.calculateBayesProbability();
		}
		Collections.sort(nameNodes);

		for (Node node : nameNodes) {
			for (Object value : node.getCustomValueList())
				list.add((String) value);
		}

		return list;
	}






	public List<String> sortPlaces(List<Map.Entry<String, String>> customMap) {

		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> entry : customMap) {
			Node node = nodes.get(entry.getKey());
			node.putCustomValue(entry.getValue());
			node.calculateBayesProbability();
		}

		Collections.sort(placeNodes);

		for (Node node : placeNodes) {
			for (Object value : node.getCustomValueList())
				list.add((String) value);
		}

		return list;
	}

}


package ogurcak.fiit.graph;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import Server.Database;






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






	public void trainChildrenEdges(Long id) {

		try {
			PreparedStatement st = Database.getConnection().prepareStatement("SELECT * FROM userEdited WHERE userEdited.id = ?");
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Long message = rs.getLong("fk_message");

				String name = rs.getString("name");
				String place = rs.getString("place");
				Date date = rs.getDate("dateFrom");

				for (Edge edge : childrens) {

					// get message send date
					
					Date messageSendDate = null;
					PreparedStatement prs = Database.getConnection().prepareStatement("SELECT receivedMessage.messageDate FROM receivedMessage WHERE receivedMessage.id = ?");
					prs.setLong(1, message);
					ResultSet anot = prs.executeQuery();
					
					while (anot.next()) {
						messageSendDate = anot.getDate("messageDate");
					}
					
					if(messageSendDate == null)
						continue;
					
					// train all date nodes
					prs = Database.getConnection().prepareStatement("SELECT * FROM GATE_DateTime WHERE GATE_DateTime.fk_message = ? AND GATE_DateTime.rule = ? ");
					prs.setLong(1, message);
					prs.setString(2, edge.getChildren().ruleName);
					anot = prs.executeQuery();

					while (anot.next()) {
						edge.addResult();

						// transfer annotation to date
						Calendar calendarSendDate = new GregorianCalendar();
						calendarSendDate.setTime(messageSendDate);

						if (anot.getInt("AddDAY") != 0) {
							calendarSendDate.add(Calendar.DATE, anot.getInt("AddDAY"));
							if (anot.getInt("DAY_OF_WEEK") != 0)
								calendarSendDate.set(Calendar.DAY_OF_WEEK, anot.getInt("DAY_OF_WEEK"));
						} else if (anot.getInt("DAY_OF_WEEK") != 0) {
							Calendar backup = (Calendar) calendarSendDate.clone();
							calendarSendDate.set(Calendar.DAY_OF_WEEK, anot.getInt("DAY_OF_WEEK"));
							if (backup.get(Calendar.DAY_OF_YEAR) >= calendarSendDate.get(Calendar.DAY_OF_YEAR))
								calendarSendDate.add(Calendar.DATE, 7);
						}

						if (anot.getInt("AddMONTH") != 0)
							calendarSendDate.add(Calendar.MONTH, anot.getInt("AddMONTH"));

						if (anot.getInt("AddYEAR") != 0)
							calendarSendDate.add(Calendar.YEAR, anot.getInt("AddYEAR"));

						if (anot.getInt("AddHOUR") != 0)
							calendarSendDate.add(Calendar.HOUR_OF_DAY, anot.getInt("AddHOUR"));

						if (anot.getInt("AddMINUTE") != 0)
							calendarSendDate.add(Calendar.MINUTE, anot.getInt("AddMINUTE"));

						if (anot.getInt("DAY") != 0)
							calendarSendDate.set(Calendar.DATE, anot.getInt("DAY"));

						if (anot.getInt("MONTH") != 0)
							calendarSendDate.set(Calendar.MONTH, anot.getInt("MONTH") - 1);

						if (anot.getInt("YEAR") != 0)
							calendarSendDate.set(Calendar.YEAR, anot.getInt("YEAR"));

						if (anot.getInt("HOUR") != 0)
							calendarSendDate.set(Calendar.HOUR_OF_DAY, anot.getInt("HOUR"));

						if (anot.getInt("MINUTE") != 0)
							calendarSendDate.set(Calendar.MINUTE, anot.getInt("MINUTE"));

						// compare
						if (calendarSendDate.getTime().equals(date)) {
							edge.addGoodResult();
							edge.getChildren().trainChildrenEdges(id);
						}
					}


					// train all location nodes
					prs = Database.getConnection().prepareStatement("SELECT LOCATION FROM GATE_Location WHERE GATE_Location.fk_message = ? AND GATE_Location.rule = ? ");
					prs.setLong(1, message);
					prs.setString(2, edge.getChildren().ruleName);
					anot = prs.executeQuery();

					while (anot.next()) {
						edge.addResult();

						String resultLocation = anot.getString("LOCATION");
						if (editDistance(place, resultLocation)) {
							edge.addGoodResult();
							edge.getChildren().trainChildrenEdges(id);
						}
					}



					// train all name nodes
					prs = Database.getConnection().prepareStatement("SELECT NAME FROM GATE_Name WHERE GATE_Name.fk_message = ? AND GATE_Name.rule = ? ");
					prs.setLong(1, message);
					prs.setString(2, edge.getChildren().ruleName);
					anot = prs.executeQuery();

					while (anot.next()) {
						edge.addResult();

						String resultName = anot.getString("NAME");
						if (editDistance(name, resultName)) {
							edge.addGoodResult();
							edge.getChildren().trainChildrenEdges(id);
						}
					}

					prs.close();

				}
			}
			st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}






	private Boolean editDistance(String s, String t) {

		int m = s.length();
		int n = t.length();
		int[][] d = new int[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= n; j++) {
			d[0][j] = j;
		}
		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				if (s.charAt(i - 1) == t.charAt(j - 1)) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					d[i][j] = min((d[i - 1][j] + 1), (d[i][j - 1] + 1), (d[i - 1][j - 1] + 1));
				}
			}
		}
		return (d[m][n]) <= (s.length() * 0.2);
	}






	private int min(int a, int b, int c) {

		return (Math.min(Math.min(a, b), c));
	}

}

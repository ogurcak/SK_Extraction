
package ogurcak.fiit;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import Server.Database;

import org.apache.log4j.Logger;

import gate.Annotation;
import gate.AnnotationSet;
import gate.CorpusController;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;






class GATE
{

	private static Logger logger = Logger.getLogger(GATE.class.getName());

	private static final String GATE_HOME = "$HOME/GATE/";

	private AnnotationSet annotations = null;

	private String stringDocument = null;






	protected void init() throws GateException {

		logger.debug("GATE initialization started");
		System.setProperty("gate.home", GATE_HOME);
		
		
		try {
			Statement stmt = Database.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT place FROM  userEdited WHERE place <> '' GROUP BY place");
			PrintWriter out = new PrintWriter("app/plugins/ANNIE/resources/gazetteer/sk_userEntered.lst");
			while (rs.next()) {
			        String val = rs.getString(1);
			        out.println(val);
			}
			out.close();
		} catch (SQLException e) {
			logger.error("Can't load user edited places to Gazetteer.");
		} catch (FileNotFoundException e) {
			logger.error("Can't load file with user edited places.");
		}
		

		Gate.init();
		// gate.gui.MainFrame.getInstance().setVisible(true);

		logger.debug("Gate initialized");
	}






	protected void startAnnotation(String s) throws ResourceInstantiationException, ExecutionException, PersistenceException, IOException {

		this.stringDocument = s;
		gate.Document document;
		document = gate.Factory.newDocument(stringDocument);

		gate.Corpus corpus = gate.Factory.newCorpus("My Corpus");
		corpus.add(document);

		gate.CorpusController annie = (CorpusController) gate.util.persistence.PersistenceManager.loadObjectFromFile(new File("app/gate.xgapp"));
		annie.setCorpus(corpus);
		logger.debug("Corpus maked. Starting ANNIE");
		annie.execute();
		logger.debug("ANNIE finished");
		annie.cleanup();

		annotations = document.getAnnotations().get();
	}






	protected List<Calendar> getDates(Calendar sentTime) throws GateException {

		List<Calendar> dates = new ArrayList<Calendar>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");

		for (Annotation anota : annotations.get("SK_DateTime")) {
			Calendar currentCalendar = (Calendar) sentTime.clone();

			if (anota.getFeatures().get("AddDAY") != null) {
				currentCalendar.add(Calendar.DATE, Integer.parseInt((String) anota.getFeatures().get("AddDAY")));
				if (anota.getFeatures().get("DAY_OF_WEEK") != null)
					currentCalendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt((String) anota.getFeatures().get("DAY_OF_WEEK")));
			} else if (anota.getFeatures().get("DAY_OF_WEEK") != null) {
				Calendar backup = (Calendar) currentCalendar.clone();
				currentCalendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt((String) anota.getFeatures().get("DAY_OF_WEEK")));
				if (backup.get(Calendar.DAY_OF_YEAR) >= currentCalendar.get(Calendar.DAY_OF_YEAR))
					currentCalendar.add(Calendar.DATE, 7);
			}

			if (anota.getFeatures().get("AddMONTH") != null)
				currentCalendar.add(Calendar.MONTH, Integer.parseInt((String) anota.getFeatures().get("AddMONTH")));

			if (anota.getFeatures().get("AddYEAR") != null)
				currentCalendar.add(Calendar.YEAR, Integer.parseInt((String) anota.getFeatures().get("AddYEAR")));

			if (anota.getFeatures().get("AddHOUR") != null)
				currentCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt((String) anota.getFeatures().get("AddHOUR")));

			if (anota.getFeatures().get("AddMINUTE") != null)
				currentCalendar.add(Calendar.MINUTE, Integer.parseInt((String) anota.getFeatures().get("AddMINUTE")));

			if (anota.getFeatures().get("DAY") != null)
				currentCalendar.set(Calendar.DATE, Integer.parseInt((String) anota.getFeatures().get("DAY")));

			if (anota.getFeatures().get("MONTH") != null)
				currentCalendar.set(Calendar.MONTH, Integer.parseInt((String) anota.getFeatures().get("MONTH")) - 1);

			if (anota.getFeatures().get("YEAR") != null)
				currentCalendar.set(Calendar.YEAR, Integer.parseInt((String) anota.getFeatures().get("YEAR")));

			if (anota.getFeatures().get("HOUR") != null)
				currentCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String) anota.getFeatures().get("HOUR")));

			if (anota.getFeatures().get("MINUTE") != null)
				currentCalendar.set(Calendar.MINUTE, Integer.parseInt((String) anota.getFeatures().get("MINUTE")));

			//TODO: this makes problems
			if (currentCalendar.after((Calendar) sentTime))
				dates.add(currentCalendar);
		}
		return dates;
	}
	
	
	
	
	protected List<Map.Entry<String, Calendar>> getDatesMap(Calendar sentTime) throws GateException {

		List<Map.Entry<String, Calendar>> dates = new ArrayList<Map.Entry<String, Calendar>>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");

		for (Annotation anota : annotations.get("SK_DateTime")) {
			Calendar currentCalendar = (Calendar) sentTime.clone();

			if (anota.getFeatures().get("AddDAY") != null) {
				currentCalendar.add(Calendar.DATE, Integer.parseInt((String) anota.getFeatures().get("AddDAY")));
				if (anota.getFeatures().get("DAY_OF_WEEK") != null)
					currentCalendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt((String) anota.getFeatures().get("DAY_OF_WEEK")));
			} else if (anota.getFeatures().get("DAY_OF_WEEK") != null) {
				Calendar backup = (Calendar) currentCalendar.clone();
				currentCalendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt((String) anota.getFeatures().get("DAY_OF_WEEK")));
				if (backup.get(Calendar.DAY_OF_YEAR) >= currentCalendar.get(Calendar.DAY_OF_YEAR))
					currentCalendar.add(Calendar.DATE, 7);
			}

			if (anota.getFeatures().get("AddMONTH") != null)
				currentCalendar.add(Calendar.MONTH, Integer.parseInt((String) anota.getFeatures().get("AddMONTH")));

			if (anota.getFeatures().get("AddYEAR") != null)
				currentCalendar.add(Calendar.YEAR, Integer.parseInt((String) anota.getFeatures().get("AddYEAR")));

			if (anota.getFeatures().get("AddHOUR") != null)
				currentCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt((String) anota.getFeatures().get("AddHOUR")));

			if (anota.getFeatures().get("AddMINUTE") != null)
				currentCalendar.add(Calendar.MINUTE, Integer.parseInt((String) anota.getFeatures().get("AddMINUTE")));

			if (anota.getFeatures().get("DAY") != null)
				currentCalendar.set(Calendar.DATE, Integer.parseInt((String) anota.getFeatures().get("DAY")));

			if (anota.getFeatures().get("MONTH") != null)
				currentCalendar.set(Calendar.MONTH, Integer.parseInt((String) anota.getFeatures().get("MONTH")) - 1);

			if (anota.getFeatures().get("YEAR") != null)
				currentCalendar.set(Calendar.YEAR, Integer.parseInt((String) anota.getFeatures().get("YEAR")));

			if (anota.getFeatures().get("HOUR") != null)
				currentCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String) anota.getFeatures().get("HOUR")));

			if (anota.getFeatures().get("MINUTE") != null)
				currentCalendar.set(Calendar.MINUTE, Integer.parseInt((String) anota.getFeatures().get("MINUTE")));

			//TODO: this makes problems
			if (currentCalendar.after((Calendar) sentTime))
				dates.add(new MyEntry<String, Calendar>((String)anota.getFeatures().get("rule"), currentCalendar));
		}
		return dates;
	}


	protected List<String> getLocations() throws GateException {
		List<String> places = new ArrayList<String>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");

		for (Annotation anota : annotations.get("SK_Location")) {
			places.add((String) anota.getFeatures().get("LOCATION"));
		
		}
		
		return places;
	}
	
	
	
	protected List<Map.Entry<String, String>> getLocationsMap() throws GateException {
		List<Map.Entry<String, String>> places = new ArrayList<Map.Entry<String, String>>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");

		for (Annotation anota : annotations.get("SK_Location")) {
			places.add(new MyEntry<String, String>((String)anota.getFeatures().get("rule"), (String) anota.getFeatures().get("LOCATION")));
		
		}
		
		return places;
	}
	
	
	
	
	
	protected List<String> getNames() throws GateException {
		List<String> places = new ArrayList<String>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");

		for (Annotation anota : annotations.get("SK_Name")) {
			places.add((String) anota.getFeatures().get("NAME"));
		
		}
		
		return places;
	}
	
	
	
	protected List<Map.Entry<String, String>> getNamesMap() throws GateException {
		List<Map.Entry<String, String>> names = new ArrayList<Map.Entry<String, String>>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");

		for (Annotation anota : annotations.get("SK_Name")) {
			names.add(new MyEntry<String, String>((String)anota.getFeatures().get("rule"), (String) anota.getFeatures().get("NAME")));
		
		}
		
		return names;
	}
	




	protected void saveDatesToDatabase(int message_id) {

		for (Annotation anota : annotations.get("SK_DateTime")) {

			try {
				PreparedStatement preparedStmt = Database
						.getConnection()
						.prepareStatement(
								"INSERT INTO GATE_DateTime (fk_message, type, GATE_id, start, end, DAY, MONTH, YEAR, HOUR, MINUTE, AddDAY, DAY_OF_WEEK, AddMONTH, AddYEAR, AddHOUR, AddMINUTE, kind, rule) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				preparedStmt.setInt(1, message_id);
				preparedStmt.setString(2, anota.getType());
				preparedStmt.setInt(3, anota.getId());
				preparedStmt.setLong(4, anota.getStartNode().getOffset());
				preparedStmt.setLong(5, anota.getEndNode().getOffset());
				
				if (anota.getFeatures().get("DAY") != null)
					preparedStmt.setInt(6, Integer.parseInt((String) anota.getFeatures().get("DAY")));
				else
					preparedStmt.setNull(6, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("MONTH") != null)
					preparedStmt.setInt(7, Integer.parseInt((String) anota.getFeatures().get("MONTH")));
				else
					preparedStmt.setNull(7, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("YEAR") != null)
					preparedStmt.setInt(8, Integer.parseInt((String) anota.getFeatures().get("YEAR")));
				else
					preparedStmt.setNull(8, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("HOUR") != null)
					preparedStmt.setInt(9, Integer.parseInt((String) anota.getFeatures().get("HOUR")));
				else
					preparedStmt.setNull(9, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("MINUTE") != null)
					preparedStmt.setInt(10, Integer.parseInt((String) anota.getFeatures().get("MINUTE")));
				else
					preparedStmt.setNull(10, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("AddDAY") != null)
					preparedStmt.setInt(11, Integer.parseInt((String) anota.getFeatures().get("AddDAY")));
				else
					preparedStmt.setNull(11, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("DAY_OF_WEEK") != null)
					preparedStmt.setInt(12, Integer.parseInt((String) anota.getFeatures().get("DAY_OF_WEEK")));
				else
					preparedStmt.setNull(12, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("AddMONTH") != null)
					preparedStmt.setInt(13, Integer.parseInt((String) anota.getFeatures().get("AddMONTH")));
				else
					preparedStmt.setNull(13, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("AddYEAR") != null)
					preparedStmt.setInt(14, Integer.parseInt((String) anota.getFeatures().get("AddYEAR")));
				else
					preparedStmt.setNull(14, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("AddHOUR") != null)
					preparedStmt.setInt(15, Integer.parseInt((String) anota.getFeatures().get("AddHOUR")));
				else
					preparedStmt.setNull(15, java.sql.Types.INTEGER);
				
				if (anota.getFeatures().get("AddMINUTE") != null)
					preparedStmt.setInt(16, Integer.parseInt((String) anota.getFeatures().get("AddMINUTE")));
				else
					preparedStmt.setNull(16, java.sql.Types.INTEGER);
				
				preparedStmt.setString(17, (String) anota.getFeatures().get("kind"));
				preparedStmt.setString(18, (String) anota.getFeatures().get("rule"));

				Database.insert(preparedStmt);

				preparedStmt.close();

			} catch (NumberFormatException e) {
				logger.warn("Error during integer parssing:" + e);
			} catch (SQLException e) {
				logger.warn("Error during database insertion:" + e);
			}
		}
		
		
		
		for (Annotation anota : annotations.get("SK_Name")) {

			try {
				PreparedStatement preparedStmt = Database
						.getConnection()
						.prepareStatement(
								"INSERT INTO GATE_Name (fk_message, type, GATE_id, start, end, NAME, kind, rule) VALUES (?,?,?,?,?,?,?,?)");
				preparedStmt.setInt(1, message_id);
				preparedStmt.setString(2, anota.getType());
				preparedStmt.setInt(3, anota.getId());
				preparedStmt.setLong(4, anota.getStartNode().getOffset());
				preparedStmt.setLong(5, anota.getEndNode().getOffset());
				preparedStmt.setString(6, (String) anota.getFeatures().get("NAME"));
				preparedStmt.setString(7, (String) anota.getFeatures().get("kind"));
				preparedStmt.setString(8, (String) anota.getFeatures().get("rule"));

				Database.insert(preparedStmt);

				preparedStmt.close();

			} catch (NumberFormatException e) {
				logger.warn("Error during integer parssing:" + e);
			} catch (SQLException e) {
				logger.warn("Error during database insertion:" + e);
			}
		}
		
		
		for (Annotation anota : annotations.get("SK_Location")) {

			try {
				PreparedStatement preparedStmt = Database
						.getConnection()
						.prepareStatement(
								"INSERT INTO GATE_Location (fk_message, type, GATE_id, start, end, LOCATION, kind, rule) VALUES (?,?,?,?,?,?,?,?)");
				preparedStmt.setInt(1, message_id);
				preparedStmt.setString(2, anota.getType());
				preparedStmt.setInt(3, anota.getId());
				preparedStmt.setLong(4, anota.getStartNode().getOffset());
				preparedStmt.setLong(5, anota.getEndNode().getOffset());
				preparedStmt.setString(6, (String) anota.getFeatures().get("LOCATION"));
				preparedStmt.setString(7, (String) anota.getFeatures().get("kind"));
				preparedStmt.setString(8, (String) anota.getFeatures().get("rule"));

				Database.insert(preparedStmt);

				preparedStmt.close();

			} catch (NumberFormatException e) {
				logger.warn("Error during integer parssing:" + e);
			} catch (SQLException e) {
				logger.warn("Error during database insertion:" + e);
			}
		}
		
		
	}
}

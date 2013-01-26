
package Server.Method.POSTActions;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Extractor.Event;
import Extractor.ICalendar;
import Server.Database;
import Server.JSON;
import Server.Response;






public class Analyze
{

	private DataOutputStream outgoing;

	private BufferedReader incoming;

	private static Logger logger = Logger.getLogger(Analyze.class.getName());

	private Event event = null;

	private JSONObject JSONobj = null;

	private int id = 0;

	private Socket connection = null;

	private String authorization;

	private String extractionMethod;






	public Analyze(DataOutputStream outgoing, BufferedReader incoming, Socket connection)
	{

		this.outgoing = outgoing;
		this.incoming = incoming;
		this.connection = connection;
	}






	@SuppressWarnings("unchecked")
	public void prepareExtractionMethod(String extractionMethod, String authorization) throws Exception {

		try {
			if (extractionMethod == null)
				extractionMethod = "ogurcak.fiit.SK_extractor";

			this.authorization = authorization;
			this.extractionMethod = extractionMethod;

			Class<Event> c2 = (Class<Event>) Class.forName(extractionMethod);

			if (c2.getSuperclass() != Event.class) {
				new Response(outgoing).sendResponse(404, "Error: Ilegal extraction method \"" + extractionMethod + "\". Select another one.");
				logger.error("Ilegal Extraction method \"" + extractionMethod + "\".");
				c2 = (Class<Event>) Class.forName("ogurcak.fiit.SK_extractor");
			}

			Constructor<Event> ctor = c2.getConstructor();
			event = ctor.newInstance();

		} catch (ClassNotFoundException e) {
			new Response(outgoing).sendResponse(404, "Error: Extraction method \"" + extractionMethod + "\" doesnt available. Select another one.");
			logger.error("Extraction method \"" + extractionMethod + "\" doesnt available.");
			throw (new Exception("Extraction method \"" + extractionMethod + "\" doesnt available."));
		}
	}






	public void analyzeMessage() throws Exception {

		if (event == null)
			throw (new Exception("Call prepareExtractionMethod() first !!!"));

		try {
			// read text of message
			String message = "";
			String line = "";
			while (incoming.ready()) {
				line = incoming.readLine();
				message = message.concat(line);
				message = message.concat("\n");
			}

			logger.info(message);


			// check if message has iCalendar standard
			// and if not
			// analyze message with selected extraction method
			if (ICalendar.isICalendar(message)) {
				ICalendar ical = new ICalendar();
				ical.parse(message);

				if (ical.getName() != null)
					event.addName(ical.getName());
				if (ical.getPlace() != null)
					event.addPlace(ical.getPlace());
				if (ical.getDescription() != null)
					event.setDescription(ical.getDescription());
				if (ical.getDateFrom() != null)
					event.addDateFrom(ical.getDateFrom());
				if (ical.getDateTo() != null)
					event.addDateTo(ical.getDateTo());
			} else {
				event.parseMessage(message);
				event.analyzeMessage();
			}

		} catch (IOException e) {
			logger.fatal("Error while reading from stream: " + e);
		} catch (MessagingException e) {
			logger.fatal("Error while parssing message: " + e);
		}

	}






	public void makeJsonObject() throws Exception {

		if (event == null)
			throw (new Exception("Call prepareExtractionMethod() first and then analyzeMessage() !!!"));

		if (id == 0)
			throw (new Exception("Call saveDataToDatabase() first !!!"));

		try {
			JSONobj = new JSONObject();

			JSON json = new JSON();
			JSONArray names = json.getNamesInJSON(event.getNames());
			JSONArray places = json.getPlacesInJSON(event.getPlaces());
			JSONArray datesFrom = json.getDatesFromInJSON(event.getDatesFrom());
			JSONArray datesTo = json.getDatesToInJSON(event.getDatesTo());
			JSONArray timesFrom = json.getTimesFromInJSON(event.getDatesFrom());
			JSONArray timesTo = json.getTimesToInJSON(event.getDatesTo());


			String description;
			if (event.getDescription() != null)
				description = event.getDescription();
			else
				description = "";



			JSONobj.put("MessageID", id);
			JSONobj.put("Description", description);
			JSONobj.put("Name", names);
			JSONobj.put("DateFrom", datesFrom);
			JSONobj.put("DateTo", datesTo);
			JSONobj.put("TimeFrom", timesFrom);
			JSONobj.put("TimeTo", timesTo);
			JSONobj.put("Place", places);

		} catch (JSONException e) {
			logger.fatal(e.getMessage() + " :Problem with JSON creation.");
		}
	}






	public void sendJsonObject() throws Exception {

		if (JSONobj == null)
			throw (new Exception("Call makeJsonObject() first !!!"));

		new Response(outgoing).sendResponse(200, JSONobj.toString());
	}






	public void saveDataToDatabase() throws Exception {

		if (event == null)
			throw (new Exception("Call prepareExtractionMethod() first and then analyzeMessage() !!!"));

		try {
			logger.debug("Saving to database started.");

			saveMessageToDatabase();
			saveNamesToDatabase();
			savePlacesToDatabase();
			saveDescriptionToDatabase();
			saveDatesFromToDatabase();
			saveDatesToToDatabase();
			
			event.setMessageId(id);
			event.saveToDatabase();

		} catch (MessagingException e) {
			logger.error("Error while reading mimeMessage: " + e);
		} catch (SQLException e) {
			logger.error("Error while writting to database: " + e+": "+e.getMessage());
		} catch (IOException e) {
			logger.error("Error while converting to String: " + e);
		}

	}






	private void saveDatesToToDatabase() throws SQLException {

		logger.debug("Preparing statement for to dates");

		PreparedStatement preparedStmt = null;
		for (Calendar dateTo : event.getDatesTo()) {
			dateTo.set(Calendar.SECOND, 0);
			preparedStmt = Database.getConnection().prepareStatement("INSERT INTO sendedDateTo(fk_message	, dateTo) VALUES (?,?)");
			preparedStmt.setInt(1, id);
			preparedStmt.setTimestamp(2, new java.sql.Timestamp(dateTo.getTime().getTime()));

			Database.insert(preparedStmt);
		}
		if (preparedStmt != null)
			preparedStmt.close();

		logger.debug("From dates saved");
	}






	private void saveDatesFromToDatabase() throws SQLException {

		logger.debug("Preparing statement for from dates");

		PreparedStatement preparedStmt = null;
		for (Calendar dateFrom : event.getDatesFrom()) {
			dateFrom.set(Calendar.SECOND, 0);
			preparedStmt = Database.getConnection().prepareStatement("INSERT INTO sendedDateFrom(fk_message	, dateFrom) VALUES (?,?)");
			preparedStmt.setInt(1, id);
			preparedStmt.setTimestamp(2, new java.sql.Timestamp(dateFrom.getTime().getTime()));

			Database.insert(preparedStmt);
		}
		if (preparedStmt != null)
			preparedStmt.close();

		logger.debug("From dates saved");
	}






	private void saveDescriptionToDatabase() throws SQLException {

		logger.debug("Preparing statement for descriptions");

		PreparedStatement preparedStmt = Database.getConnection().prepareStatement("INSERT INTO sendedDescription (fk_message	, description) VALUES (?,?)");
		preparedStmt.setInt(1, id);
		preparedStmt.setString(2, event.getDescription());

		Database.insert(preparedStmt);
		preparedStmt.close();

		logger.debug("Description saved");
	}






	private void savePlacesToDatabase() throws SQLException {

		logger.debug("Preparing statement for places");

		PreparedStatement preparedStmt = null;
		for (String place : event.getPlaces()) {
			preparedStmt = Database.getConnection().prepareStatement("INSERT INTO sendedPlace(fk_message	, place) VALUES (?,?)");
			preparedStmt.setInt(1, id);
			preparedStmt.setString(2, place);

			Database.insert(preparedStmt);
		}
		if (preparedStmt != null)
			preparedStmt.close();

		logger.debug("Places saved");
	}






	private void saveNamesToDatabase() throws SQLException {

		logger.debug("Preparing statement for names");

		PreparedStatement preparedStmt = null;
		for (String name : event.getNames()) {
			preparedStmt = Database.getConnection().prepareStatement("INSERT INTO sendedName (fk_message	, name) VALUES (?,?)");
			preparedStmt.setInt(1, id);
			preparedStmt.setString(2, name);

			Database.insert(preparedStmt);
		}
		if (preparedStmt != null)
			preparedStmt.close();

		logger.debug("Names saved");
	}






	private void saveMessageToDatabase() throws MessagingException, SQLException, IOException {

		MimeMessage message = this.event.getMessage();

		logger.debug("Getting recepients.");
		String receivers = "";
		for (Address address : message.getRecipients(Message.RecipientType.TO)) {
			receivers = receivers.concat(address + ";");
		}

		logger.debug("Getting senders.");
		String senders = "";
		for (Address address : message.getFrom()) {
			senders = senders.concat(address + ";");
		}

		logger.debug("Preparing statement for message");

		long beforeInsertTimestamp = System.currentTimeMillis();

		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(
				"INSERT INTO receivedMessage (client, authorization, extractionMethod, messageFrom, messageTo, messageDate, messageSubject, messageContent) VALUES (?,?,?,?,?,?,?,?)");
		preparedStmt.setString(1, connection.getInetAddress().toString());
		preparedStmt.setString(2, this.authorization);
		preparedStmt.setString(3, this.extractionMethod);
		preparedStmt.setString(4, senders);
		preparedStmt.setString(5, receivers);
		preparedStmt.setTimestamp(6, new java.sql.Timestamp(message.getSentDate().getTime()));
		preparedStmt.setString(7, message.getSubject());
		preparedStmt.setString(8, String.valueOf(message.getContent()));

		Database.insert(preparedStmt);

		long afterInsertTimestamp = System.currentTimeMillis();

		logger.debug("Message saved");
		logger.debug("Getting message id");

		PreparedStatement preparedStmt1 = Database
				.getConnection()
				.prepareStatement(
						"SELECT * FROM receivedMessage WHERE client=? AND authorization=? AND extractionMethod=? AND messageFrom=? AND messageTo=? AND messageSubject=? AND messageContent=?");
		preparedStmt1.setString(1, connection.getInetAddress().toString());
		preparedStmt1.setString(2, this.authorization);
		preparedStmt1.setString(3, this.extractionMethod);
		preparedStmt1.setString(4, senders);
		preparedStmt1.setString(5, receivers);
		preparedStmt1.setString(6, message.getSubject());
		preparedStmt1.setString(7, String.valueOf(message.getContent()));

		ResultSet rs = preparedStmt1.executeQuery();

		while (rs.next()) {
			long rsTimeStamp = rs.getTimestamp("timeStamp").getTime();
			logger.debug("1:"+beforeInsertTimestamp/1000);
			logger.debug("2:"+rsTimeStamp/1000);
			logger.debug("3:"+afterInsertTimestamp/1000);
			if (rsTimeStamp/1000 >= beforeInsertTimestamp/1000 && rsTimeStamp/1000 <= afterInsertTimestamp/1000) {
				id = rs.getInt("id");
				preparedStmt1.close();
				break;
			}
		}

		logger.debug("Message id taken. Id: " + id);

	}
}

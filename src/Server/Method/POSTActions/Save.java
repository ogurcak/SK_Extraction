
package Server.Method.POSTActions;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Server.Database;
import Server.Response;






public class Save
{

	private static Logger logger = Logger.getLogger(Save.class.getName());

	private DataOutputStream outgoing;

	private BufferedReader incoming;






	public Save(DataOutputStream outgoing, BufferedReader incoming)
	{

		this.outgoing = outgoing;
		this.incoming = incoming;
	}






	public void saveToDatabase() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdadf = new SimpleDateFormat("yyyy-MM-dd");



		try {

			String message = incoming.readLine();

			logger.info(message); // message

			JSONObject obj = (JSONObject) new JSONTokener(message).nextValue();

			int received_id = obj.getInt("id");
			String sended_name = obj.getString("Name");
			String sended_place = obj.getString("Place");
			Boolean sended_allDay = obj.getBoolean("AllDay");
			String sended_description = obj.getString("Description");

			Date sended_dateFrom;
			Date sended_dateTo;

			if (sended_allDay) {
				sended_dateFrom = sdadf.parse(obj.getString("DateFrom"));
				Calendar dateFrom = new GregorianCalendar();
				dateFrom.setTime(sended_dateFrom);
				dateFrom.set(Calendar.HOUR_OF_DAY, 0);
				dateFrom.set(Calendar.MINUTE, 0);
				dateFrom.set(Calendar.SECOND, 0);
				sended_dateFrom = dateFrom.getTime();

				sended_dateTo = sdadf.parse(obj.getString("DateTo"));
				Calendar dateTo = new GregorianCalendar();
				dateTo.setTime(sended_dateFrom);
				dateTo.set(Calendar.HOUR_OF_DAY, 0);
				dateTo.set(Calendar.MINUTE, 0);
				dateTo.set(Calendar.SECOND, 0);
				sended_dateFrom = dateTo.getTime();
			} else {
				String sended_dateFrom_String = obj.getString("DateFrom") + " " + obj.getString("TimeFrom");
				sended_dateFrom = sdf.parse(sended_dateFrom_String);

				String sended_dateTo_String = obj.getString("DateTo") + " " + obj.getString("TimeTo");
				sended_dateTo = sdf.parse(sended_dateTo_String);
			}



			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(
					"INSERT INTO userEdited (fk_message, name, place, dateFrom, dateTo, allDay, description) VALUES (?,?,?,?,?,?,?)");
			preparedStmt.setInt(1, received_id);
			preparedStmt.setString(2, sended_name);
			preparedStmt.setString(3, sended_place);
			preparedStmt.setTimestamp(4, new java.sql.Timestamp(sended_dateFrom.getTime()));
			preparedStmt.setTimestamp(5, new java.sql.Timestamp(sended_dateTo.getTime()));
			preparedStmt.setBoolean(6, sended_allDay);
			preparedStmt.setString(7, sended_description);

			Database.insert(preparedStmt);

			preparedStmt.close();

			new Response(outgoing).sendResponse(200, "Data saved into database.");
		} catch (SQLException e) {
			logger.error(e.getMessage() + " :Database insertion problem.");
			new Response(outgoing).sendResponse(404, "Database insertion problem.");
		} catch (JSONException e) {
			logger.fatal(e.getMessage() + " :Parsing JSON error.");
			new Response(outgoing).sendResponse(404, "Problem with parsing");
		} catch (IOException e) {
			logger.fatal(e.getMessage() + " :Reading JSON error.");
			new Response(outgoing).sendResponse(404, "Problem with reading");
		} catch (ParseException e) {
			logger.fatal(e.getMessage() + " :Parsing Date error.");
			new Response(outgoing).sendResponse(404, "Problem with parsing");
		}

	}
}

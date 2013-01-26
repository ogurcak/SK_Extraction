package Extractor;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;





/**
 * Class in message detect the presence of iCalendar microformat standard
 * and then extract data from it.
 * 
 * @author Filip Ogurcak
 * @version 1.0
 */
final public class ICalendar {

	  /** Name of event */
	  protected String name = null;

	  /** Place of event */
	  protected String place = null;

	  /** Description of event */
	  protected String description = null;

	  /** Start of event */
	  protected Calendar dateFrom = null;

	  /** End of event */
	  protected Calendar dateTo = null;

	  private static Logger logger = Logger.getLogger(ICalendar.class.getName());





	  /**
	   * Default constructor
	   */
	  public ICalendar() {

	  }





	  /**
	   * Detect the presence of iCalendar microformat and return true if
	   * message contains it or false if doesn't.
	   * 
	   * @param message
	   *                  String in witch method detect the presence of
	   *                  iCalendar microformat
	   * @return <code>true</code> if message contains iCalendar microformat
	   *         or <code>false</code> if doesn't.
	   */
	  public static boolean isICalendar(String message) {

		    if (message.contains("BEGIN:VCALENDAR")) if (message.contains("BEGIN:VEVENT")) if (message.contains("END:VCALENDAR")) if (message.contains("END:VEVENT")) return true;
		    return false;
	  }





	  /**
	   * Parse iCalendar microformat present in message and set fields.
	   * 
	   * @param message
	   *                  String in witch method detected the presence of
	   *                  iCalendar microformat
	   */
	  public void parse(String message) {

		    Pattern namePatern = Pattern.compile("^SUMMARY:.*?$", Pattern.MULTILINE);
		    Pattern placePatern = Pattern.compile("^LOCATION:.*?$", Pattern.MULTILINE);
		    Pattern descriptionPatern = Pattern.compile("^DESCRIPTION:.+?[A-Z]+:", Pattern.MULTILINE | Pattern.DOTALL);
		    Pattern dateFromPatern = Pattern.compile("^DTSTART:.*?$", Pattern.MULTILINE);
		    Pattern dateToPatern = Pattern.compile("^DTEND:.*?$", Pattern.MULTILINE);

		    Matcher matcher = namePatern.matcher(message.replace("\\n", " "));
		    if (matcher.find()) name = matcher.group().replace("SUMMARY:", "").replace("\\", "");

		    matcher = placePatern.matcher(message.replace("\\n", " "));
		    if (matcher.find()) place = matcher.group().replace("LOCATION:", "").replace("\\", "");

		    matcher = descriptionPatern.matcher(message.replace("\\n", " "));
		    if (matcher.find()) {
			      description = matcher.group().replace("DESCRIPTION:", "").replace("\\", "");
			      description = description.replace(description.substring(description.lastIndexOf("\n")), "").replace("\n", "");
		    }

		    matcher = dateFromPatern.matcher(message.replace("\\n", " "));
		    if (matcher.find()) {
			      DateFormat formatter;
			      Date date = null;
			      formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			      try {
					date = (Date) formatter.parse(matcher.group().replace("DTSTART:", "").replace("T", "").replace("Z", ""));
			      } catch (ParseException e) {
					logger.error(e.getMessage());
			      }
			      dateFrom = GregorianCalendar.getInstance();
			      dateFrom.setTime(date);
			      dateFrom.add(Calendar.HOUR, 1);
		    }


		    matcher = dateToPatern.matcher(message.replace("\\n", " "));
		    if (matcher.find()) {
			      DateFormat formatter;
			      Date date = null;
			      formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			      try {
					date = (Date) formatter.parse(matcher.group().replace("DTEND:", "").replace("T", "").replace("Z", ""));
			      } catch (ParseException e) {
					logger.error(e.getMessage());
			      }
			      dateTo = GregorianCalendar.getInstance();
			      dateTo.setTime(date);
			      dateTo.add(Calendar.HOUR, 1);
		    }

	  }





	  /**
	   * Get event name extracted from iCalendar.
	   * 
	   * @return name of the event
	   */
	  public String getName() {

		    return this.name;
	  }





	  /**
	   * Get start of event extracted from iCalendar.
	   * 
	   * @return start of the event
	   */
	  public Calendar getDateFrom() {

		    return this.dateFrom;
	  }





	  /**
	   * Get end of event extracted from iCalendar.
	   * 
	   * @return end of the event
	   */
	  public Calendar getDateTo() {

		    return this.dateTo;
	  }





	  /**
	   * Get event place extracted from iCalendar.
	   * 
	   * @return place of the event
	   */
	  public String getPlace() {

		    return this.place;
	  }





	  /**
	   * Get event description extracted from iCalendar.
	   * 
	   * @return description of the event
	   */
	  public String getDescription() {

		    return this.description;
	  }

}

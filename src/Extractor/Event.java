package Extractor;


import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;




/**
 * Abstract class designed for making connection with event extraction methods.
 * Extended is method <i>analyzeMessage()</i>, which call extraction methods and
 * set all necessary variables.
 * 
 * @author Filip Ogurcak
 * @version 1.0
 */
abstract public class Event {

	  /** List of events names extracted from message */
	  protected List<String> names = new ArrayList<String>();

	  /** List of events places extracted from message */
	  protected List<String> places = new ArrayList<String>();

	  /** Event description extracted from message */
	  protected String description = null;

	  /** List of starts of events extracted from message */
	  protected List<Calendar> datesFrom = new ArrayList<Calendar>();

	  /** List of ends of events extracted from message */
	  protected List<Calendar> datesTo = new ArrayList<Calendar>();

	  /** Email message in which extraction is performed */
	  protected MimeMessage message = null;

	  /** Logger used to make logs during program's runtime */
	  protected static Logger logger = Logger.getLogger(Event.class.getName());





	  /**
	   * Default constructor
	   */
	  public Event() {

	  }





	  /**
	   * Constructor with email message represented as String.</br> This
	   * message must be well formated and contains all necessary parts
	   * (sender, receiver, date, subject and content).
	   * <p>
	   * <i><b>Example of well formated message:</b> </br> From: Filip
	   * Ogurcak &lt;filip.ogurcak@gmail.com&gt; </br>To:
	   * filip.ogurcak@gmail.com </br>Date: 1334571973 </br>Subject: aaa
	   * </br>Content: The following is the information need for the arch
	   * set up. If you would like to volunteer to help fill out the
	   * attachment form* and drop by 1906</i>
	   * 
	   * @param message
	   *                  Represent the well formed headers and content of
	   *                  email message, from which could be extracted event
	   *                  data
	   * @throws MessagingException
	   *                   when the message is badly formated
	   */
	  public Event(String message) throws MessagingException {

		    parseMessage(message);

	  }





	  /**
	   * Constructor with specific format of email message.</br> This
	   * message contains all necessary parts (sender, receiver, date,
	   * subject and content), so it's easy to use it.
	   * <p>
	   * 
	   * @param message
	   *                  Represent the email message with all headers and
	   *                  content
	   */
	  public Event(MimeMessage message) {

		    this.message = message;
	  }





	  /**
	   * Set message with specific format of email message.</br> This
	   * message contains all necessary parts (sender, receiver, date,
	   * subject and content), so it's easy to use it.
	   * <p>
	   * 
	   * @param message
	   *                  Represent the email message with all headers and
	   *                  content
	   */
	  public void setMessage(MimeMessage message) {

		    this.message = message;
	  }





	  /**
	   * Method convert message saved in String to specific format of email
	   * message. This message must be well formated and contains all
	   * necessary parts (sender, receiver, date, subject and content).
	   * <p>
	   * <i><b>Example of well formated message:</b> </br> From: Filip
	   * Ogurcak &lt;filip.ogurcak@gmail.com&gt; </br>To:
	   * filip.ogurcak@gmail.com </br>Date: 1334571973 </br>Subject: aaa
	   * </br>Content: The following is the information need for the arch
	   * set up. If you would like to volunteer to help fill out the
	   * attachment form* and drop by 1906</i>
	   * 
	   * @param message
	   *                  Represent the well formed headers and content of
	   *                  email message, from which could be extracted event
	   *                  data
	   * @throws MessagingException
	   *                   when the message is badly formated
	   */
	  public void parseMessage(String message) throws MessagingException {

		    String fromAddress = null;
		    String[] toAddresses = null;
		    String subject = null;
		    String content = null;
		    Date date = null;

		    Pattern fromPattern = Pattern.compile("^From: .*?$", Pattern.MULTILINE);
		    Pattern toPattern = Pattern.compile("^To: .*?$", Pattern.MULTILINE);
		    Pattern datePattern = Pattern.compile("^Date: .*?$", Pattern.MULTILINE);
		    Pattern subjectPattern = Pattern.compile("^Subject:.*?$", Pattern.MULTILINE);
		    Pattern contentPattern = Pattern.compile("Content: (.*\\s)*", Pattern.MULTILINE);

		    Matcher matcher;
		    matcher = fromPattern.matcher(message);
		    if (matcher.find()) {
			      fromAddress = matcher.group().replace("From: ", "");
			      String[] splited = fromAddress.split(" ");
			      for (String s : splited)
					if (s.contains("<") && s.contains(">") && s.contains("@")) fromAddress = s.replace("<", "").replace(">", "");
		    }

		    matcher = toPattern.matcher(message);
		    if (matcher.find()) {
			      String[] splited = matcher.group().replace("To: ", "").split(" ");
			      List<String> list = new ArrayList<String>();
			      for (String s : splited)
					if (s.contains("<") && s.contains(">") || s.contains("@")) list.add(s.replace("<", "").replace(">", ""));
			      toAddresses = new String[list.size()];
			      list.toArray(toAddresses);
		    }


		    matcher = datePattern.matcher(message);
		    if (matcher.find()) date = new Date(Long.valueOf(matcher.group().replace("Date: ", "") + "000"));

		    matcher = subjectPattern.matcher(message);
		    if (matcher.find()) subject = matcher.group().replace("Subject: ", "");

		    matcher = contentPattern.matcher(message);
		    if (matcher.find()) content = matcher.group().replace("Content: ", "");


		    Session sn = Session.getInstance(System.getProperties());
		    this.message = new MimeMessage(sn);
		    this.message.setFrom(new InternetAddress(fromAddress));
		    InternetAddress[] toIntAdds = new InternetAddress[toAddresses.length];
		    for (int i = 0; i < toAddresses.length; i++) {
			      toIntAdds[i] = new InternetAddress(toAddresses[i]);
		    }

		    this.message.setRecipients(Message.RecipientType.TO, toIntAdds);
		    this.message.setSubject(subject);
		    this.message.setSentDate(date);
		    this.message.setText(content);
	  }





	  /**
	   * Abstract method designed for extend. After it, this method provides
	   * all extraction functionality and sets all variables.
	   */
	  abstract public void analyzeMessage();





	  /**
	   * Get list of events names extracted from message.
	   * 
	   * @return list of events names
	   */
	  public List<String> getNames() {

		    return this.names;
	  }





	  /**
	   * Get list of events starts extracted from message.
	   * 
	   * @return list of events starts
	   */
	  public List<Calendar> getDatesFrom() {

		    return this.datesFrom;
	  }





	  /**
	   * Get list of events ends extracted from message.
	   * 
	   * @return list of events ends
	   */
	  public List<Calendar> getDatesTo() {

		    return this.datesTo;
	  }





	  /**
	   * Get list of events places extracted from message.
	   * 
	   * @return list of events places
	   */
	  public List<String> getPlaces() {

		    return this.places;
	  }





	  /**
	   * Get event description extracted from message.
	   * 
	   * @return Description of event.
	   */
	  public String getDescription() {

		    return this.description;
	  }





	  /**
	   * Add name to events names list.
	   * 
	   * @param name
	   *                  name of event
	   */
	  public void addName(String name) {

		    this.names.add(name);
	  }





	  /**
	   * Add place to events places list. *
	   * 
	   * @param place
	   *                  place of event
	   */
	  public void addPlace(String place) {

		    this.places.add(place);
	  }





	  /**
	   * Set description of event.
	   * 
	   * @param description
	   *                  description of event
	   */
	  public void setDescription(String description) {

		    this.description = description;
	  }





	  /**
	   * Add start to events starts list.
	   * 
	   * @param dateFrom
	   *                  start of event
	   */
	  public void addDateFrom(Calendar dateFrom) {

		    this.datesFrom.add(dateFrom);
	  }





	  /**
	   * Add end to events ends list.
	   * 
	   * @param dateTo
	   *                  end of event
	   */
	  public void addDateTo(Calendar dateTo) {

		    this.datesTo.add(dateTo);
	  }

}


package ogurcak.fiit;


import gate.util.GateException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;







public class SK_extractor extends Extractor.Event
{

	private static Logger logger = Logger.getLogger(GATE.class.getName());

	private GATE gate;






	public void analyzeMessage() {

		// set up default name
		try {
			addName(message.getSubject().replaceAll("Fwd: ", "").replaceAll("Re: ", ""));
		} catch (MessagingException e) {
			addName("");
			logger.warn("Error during getting message subject" + e.getMessage());
		}

		// set up default place
		

		// set up default description
		description = "Automatic generated with ogurcak.fiit.SK_extraction method";

		// set up event duration
		gate = new GATE();
		try {
			gate.init();
			gate.startAnnotation((String) message.getContent());

			Calendar cal = Calendar.getInstance();
			cal.setTime(message.getSentDate());

			List<Calendar> dates = gate.getDates(cal);
			logger.debug("Dates getted");
			for (Calendar c : dates) {
				addDateFrom((Calendar) c.clone());
				c.add(Calendar.HOUR, 1);
				addDateTo(c);
			}
			
			List<String> places = gate.getLocations();
			if(places.isEmpty())
				addPlace("");
			else
				for (String place : places)
					addPlace(place);

		} catch (GateException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}

	}






	public void saveToDatabase() {

		gate.saveDatesToDatabase(getMessageId());

	}

}

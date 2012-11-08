/*
 * Slovak University of Technology in Bratislava, Faculty of Informatics and Information Technologies;
 * Course: Team project;
 * Academic year: 2012/13;
 * Project name: Simulation of demonstration in the city;
 * Project leader: Ing. Ivan Kapustík;
 * Authors: STeam (Bc. Britvík Andrej, Bc. Dupa¾ Martin, Bc. Gomola Matej, Bc. Králik Gergely, Bc. Michalec
 * 		Peter, Bc. Ogurèák Filip, Bc. Palát Peter);
 */


package ogurcak.fiit;


import gate.util.GateException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;




public class SK_extractor extends Extractor.Event
{

	private static Logger logger = Logger.getLogger(GATE.class.getName());
	
	public void analyzeMessage() {

		// set up default name
		try {
			addName(message.getSubject());
		} catch (MessagingException e) {
			addName("");
			System.err.println("Error during getting message subject" + e.getMessage());
		}

		// set up default place
		addPlace("");

		// set up default description
		description = "Automatic generated with ogurcak.fiit.SK_extraction method";

		// set up event duration
		GATE gate = new GATE();
		try {
			gate.init();
			gate.startAnnotation((String) message.getContent());

			Calendar cal = Calendar.getInstance();
			cal.setTime(message.getSentDate());

			List<Calendar> dates = gate.getDates(cal);
			for (Calendar c : dates) {
				addDateFrom((Calendar) c.clone());
				c.add(Calendar.HOUR, 1);
				addDateTo(c);
			}
		} catch (GateException e) {
			logger.log(Level.INFO, e.getMessage());
		} catch (IOException e) {
			logger.log(Level.INFO, e.getMessage());
		} catch (MessagingException e) {
			logger.log(Level.INFO, e.getMessage());
		}

	}

}

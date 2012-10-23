
package ogurcak.fiit;


import gate.util.GateException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;






public class SK_extractor extends Extractor.Event
{

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
			for(Calendar c: dates){
				addDateFrom((Calendar)c.clone());
				c.add(Calendar.HOUR, 1);
				addDateTo(c);
			}
		} catch (GateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

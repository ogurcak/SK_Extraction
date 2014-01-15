
package ogurcak.fiit;


import gate.util.GateException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

import ogurcak.fiit.graph.ProbabilityGraph;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;






public class SK_extractor extends Extractor.Event
{

	private static Logger logger = Logger.getLogger(GATE.class.getName());

	private GATE gate = null;

	private ProbabilityGraph probabilityGraph = null;






	public void init() {

		try {
			logger.info("Init gate");
			gate = new GATE();
			gate.init();

			logger.info("Init dataSorter");
			probabilityGraph = new ProbabilityGraph();
			probabilityGraph.init();

		} catch (GateException e) {
			logger.error(e.getMessage());
			gate = null;
			probabilityGraph = null;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			gate = null;
			probabilityGraph = null;
		}
	}






	public void analyzeMessage() {

		// TODO this must be call before
		init();

		// set up default name
		try {
			addName(Normalizer.normalize(message.getSubject().replaceAll("Fwd: ", "").replaceAll("Re: ", ""), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
		} catch (MessagingException e) {
			addName("");
			logger.warn("Error during getting message subject" + e.getMessage());
		}

		// set up default description
		description = "Automatic generated with ogurcak.fiit.SK_extraction method";

		// set up event duration

		try {
			gate.startAnnotation(Normalizer.normalize((String) message.getContent(), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));

			Calendar cal = Calendar.getInstance();
			cal.setTime(message.getSentDate());

			List<Calendar> dates = probabilityGraph.sortDates(gate.getDatesMap(cal));
			logger.debug("Dates getted");
			for (Calendar c : dates) {
				addDateFrom((Calendar) c.clone());
				c.add(Calendar.HOUR, 1);
				addDateTo(c);
			}

			logger.debug("Getting names");
			List<String> names = probabilityGraph.sortNames(gate.getNamesMap());
			if (!names.isEmpty())
				for (String name : names)
					addName(name);
			
			
			logger.debug("Places getted");
			List<String> places = probabilityGraph.sortPlaces(gate.getLocationsMap());
			if (places.isEmpty())
				addPlace("");
			else
				for (String place : places)
					addPlace(place);

			logger.debug("Cleanning graph");
			probabilityGraph.clean();
			

		} catch (GateException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}






	public void saveToDatabase() {

		gate.saveDatesToDatabase(getMessageId());

	}

}

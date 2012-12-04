
package ogurcak.fiit;


import java.util.Calendar;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

			if (currentCalendar.after((Calendar) sentTime))
				dates.add(currentCalendar);
		}
		return dates;
	}
}

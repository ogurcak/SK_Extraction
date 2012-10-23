
package ogurcak.fiit;


import java.util.Calendar;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Server.Main;

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

	private static Logger logger = Logger.getLogger(Main.class.getName());

	private static final String GATE_HOME = "D:/GATE_Developer_7.0";

	private AnnotationSet annotations = null;

	private String stringDocument = null;






	protected void init() throws GateException {

		logger.log(Level.INFO, "GATE initialization");
		System.setProperty("gate.home", GATE_HOME);

		Gate.init();
		gate.gui.MainFrame.getInstance().setVisible(false);

		logger.log(Level.INFO, "Gate initialized");
	}






	protected void startAnnotation(String s) throws ResourceInstantiationException, ExecutionException, PersistenceException, IOException {

		this.stringDocument = s;
		gate.Document document;
		document = gate.Factory.newDocument(stringDocument);

		gate.Corpus corpus = gate.Factory.newCorpus("My Corpus");
		corpus.add(document);

		gate.CorpusController annie = (CorpusController) gate.util.persistence.PersistenceManager.loadObjectFromFile(new File("app/gate.xgapp"));
		annie.setCorpus(corpus);
		annie.execute();
		annie.cleanup();

		annotations = document.getAnnotations().get();
	}






	@SuppressWarnings("static-access")
	protected List<Calendar> getDates(Calendar sentTime) throws GateException {

		List<Calendar> dates = new ArrayList<Calendar>();

		if (annotations.isEmpty())
			throw new GateException("Gate is not initialized. Call method \"init()\" to initialization.");


		for (Annotation anota : annotations.get("Date")) {
			Calendar currentCalendar = (Calendar) sentTime.clone();


			if (anota.getFeatures().get("DAY") != null)
				currentCalendar.set(Calendar.DATE, Integer.parseInt((String) anota.getFeatures().get("DAY")));

			if (anota.getFeatures().get("MONTH") != null)
				currentCalendar.set(Calendar.MONTH, Integer.parseInt((String) anota.getFeatures().get("MONTH"))-1);

			if (anota.getFeatures().get("YEAR") != null)
				currentCalendar.set(Calendar.YEAR, Integer.parseInt((String) anota.getFeatures().get("YEAR")));

			if (anota.getFeatures().get("HOUR") != null)
				currentCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String) anota.getFeatures().get("HOUR")));

			if (anota.getFeatures().get("MINUTE") != null)
				currentCalendar.set(Calendar.MINUTE, Integer.parseInt((String) anota.getFeatures().get("MINUTE")));

			if (anota.getFeatures().get("DAY_OF_WEEK") != null) {
				Calendar backup = (Calendar) currentCalendar.clone();
				currentCalendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt((String) anota.getFeatures().get("DAY_OF_WEEK")));
				if (backup.DAY_OF_MONTH > currentCalendar.DAY_OF_MONTH)
					currentCalendar.add(Calendar.DATE, 7);
			}



			if (anota.getFeatures().get("AddDAY") != null)
				currentCalendar.add(Calendar.DATE, Integer.parseInt((String) anota.getFeatures().get("DAY")));

			if (anota.getFeatures().get("AddMONTH") != null)
				currentCalendar.add(Calendar.MONTH, Integer.parseInt((String) anota.getFeatures().get("MONTH")));

			if (anota.getFeatures().get("AddYEAR") != null)
				currentCalendar.add(Calendar.YEAR, Integer.parseInt((String) anota.getFeatures().get("YEAR")));

			if (anota.getFeatures().get("AddHOUR") != null)
				currentCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt((String) anota.getFeatures().get("HOUR")));

			if (anota.getFeatures().get("AddMINUTE") != null)
				currentCalendar.add(Calendar.MINUTE, Integer.parseInt((String) anota.getFeatures().get("MINUTE")));


			dates.add(currentCalendar);
		}
		return dates;
	}
}

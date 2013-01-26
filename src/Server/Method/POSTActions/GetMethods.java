
package Server.Method.POSTActions;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Extractor.Event;
import Server.Response;






public class GetMethods
{

	private static Logger logger = Logger.getLogger(GetMethods.class.getName());

	private DataOutputStream outgoing;

	private List<String> foundClasses = null;

	private JSONObject JSONobj = null;

	@SuppressWarnings("unused")
	private BufferedReader incoming;






	public GetMethods(DataOutputStream outgoing, BufferedReader incoming)
	{

		this.outgoing = outgoing;
		this.incoming = incoming;
	}






	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getMethods() {

		final FileFilter filter = new FileFilter()
		{

			public boolean accept(File pathname) {

				return pathname.getName().endsWith(".jar");
			}
		};

		File file = new File("extraction_methods");
		List<File> jars = new ArrayList<File>();

		for (File f : file.listFiles(filter))
			jars.add(f);

		foundClasses = new ArrayList<String>();
		for (File f : jars) {
			JarFile jar;
			try {
				jar = new JarFile(f);
				for (Enumeration em1 = jar.entries(); em1.hasMoreElements();) {
					String s = em1.nextElement().toString();
					if (s.contains(".class")) {
						s = s.replace("/", ".").replace(".class", "");
						Class<Event> c2;
						try {
							c2 = (Class<Event>) Class.forName(s);
							if (c2.getSuperclass() == Event.class)
								foundClasses.add(s);
						} catch (ClassNotFoundException e) {
							logger.fatal(e.getMessage());
						}
					}
				}
			} catch (IOException e) {
				logger.fatal(e.getMessage());
			}
		}
	}






	public void makeJsonObject() throws Exception {

		if (foundClasses == null)
			throw (new Exception("Call getMethods() first !!!"));

		JSONobj = new JSONObject();
		JSONArray implementatios = new JSONArray();

		for (String s : foundClasses)
			implementatios.put(s);

		try {
			JSONobj.put("Implementations", implementatios);
		} catch (JSONException e) {
			logger.fatal(e.getMessage() + " :Problem with JSON creation.");
		}

	}






	public void sendJsonObject() throws Exception {

		if (JSONobj == null)
			throw (new Exception("Call makeJsonObject() first !!!"));

		new Response(outgoing).sendResponse(200, JSONobj.toString());

	}
}

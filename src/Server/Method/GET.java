
package Server.Method;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;


import Server.Response;






public class GET
{

	private static Logger logger = Logger.getLogger(GET.class.getName());






	public GET(DataOutputStream outgoing, BufferedReader incoming, Socket connection)
	{

		try {

			logger.info("Client send GET request.");

			while (incoming.ready())
				logger.info(incoming.readLine());

			String responseString = "Error: GET request doesnt available.\r\n";
			new Response(outgoing).sendResponse(404, responseString);

		} catch (IOException e) {
		}
	}
}

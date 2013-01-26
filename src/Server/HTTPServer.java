
package Server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import Server.Method.GET;
import Server.Method.POST;






/** Main part of server part of application. Provides communication with client part, analyze
 * requests, send responses and call required methods.
 * <p>
 * This class extends Java Thread, so every request is analyzed and executed in separate thread.
 * 
 * @author Filip Ogurcak
 * @version 1.0 */
public class HTTPServer extends Thread
{

	private Socket connection = null;

	private BufferedReader incoming = null;

	private DataOutputStream outgoing = null;

	private static Logger logger = Logger.getLogger(HTTPServer.class.getName());






	/** Default constructor.
	 * 
	 * @param connection
	 *              Incoming connection from client part */
	public HTTPServer(Socket connection)
	{

		this.connection = connection;
	}






	/** Function starts after incoming request from client part, and in separate thread analyze and
	 * execute this request. */
	public void run() {

		try {


			logger.info("The Client " + connection.getInetAddress() + ":" + connection.getPort() + " is connected.");

			incoming = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			outgoing = new DataOutputStream(connection.getOutputStream());

			String authorization = incoming.readLine();
			StringTokenizer tokenizer = new StringTokenizer(authorization);
			String httpMethod = tokenizer.nextToken();
			tokenizer.nextToken();

			if (httpMethod.equals("POST")) {
				new POST(outgoing, incoming, connection);
			}

			if (httpMethod.equals("GET")) {
				new GET(outgoing, incoming, connection);
			}

		} catch (IOException e) {
		}


	}



}

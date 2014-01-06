/*
 * File: HTTPServer.java
 * Author: Matt Jones
 * Date: 1/2/2014
 * Desc: This class responds to HTTP requests given a HTTPHeader object. None of the networking
 *       should be done in this class.
 */


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class HTTPServer {
	
	// default date format for all threads to use (only ever needs to be set once)
	private static SimpleDateFormat defaultSDF;
	private static ServerConfig conf;
	
	/**
	 * Initialize the static resources for the server
	 */
	public static void initServer(ServerConfig sc) {
		// set up gmt timezone
		HTTPServer.defaultSDF = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		HTTPServer.defaultSDF.setTimeZone(TimeZone.getTimeZone("GMT"));
		// set the settings for this server
		HTTPServer.conf = sc;
	}
	
	/**
	 * Respond to a request given a HTTPHeader
	 * @param hh HTTPHeader with request information
	 * @return String with response data
	 *  
	 * TODO: Return a stream instead of a string. 
	 */
	public static InputStream respond(HTTPHeader hh) {
		
		if(!hh.isValid()) {
			return new ByteArrayInputStream(get400(0).getBytes(Charset.forName("UTF-8")));
		}
		
		// special server info case
		if(hh.getRequestLocation().equals("/srv.info")) {

			HTTPHeader resp = new HTTPHeader(200,"OK");
			resp.setAttribute("Date", defaultSDF.format(new Date()) + " GMT");
			resp.setAttribute("Content-Type", "text/html; charset=UTF-8;");
			
			String message = "<div style='font-family: arial; font-size: 16px; padding: 25px; color: rgb(50,50,100);'>Hello from Matt Async Server 0.0.0.0.0.1 Alpha Beta</div>";
			String out = resp.toString() + message;
			
			return new ByteArrayInputStream(out.getBytes(Charset.forName("UTF-8")));
		}
		
		// otherwise send a 404
		return new ByteArrayInputStream(get400(4).getBytes(Charset.forName("UTF-8")));
	}
	
	/**
	 * Default 400+ error message
	 * @param code 400 + code is the error number. i.e. code = 4 equates to 404
	 * @return Error response to send
	 */
	public static String get400(int code) {
		
		String errorType = null;
		int errorNum = 400+code;
		
		switch(code) {
		
			case 0:
				errorType = "Bad Request";
				break;
			case 4:
				errorType = "Not Found";
				break;
			default:
				errorType = "Bad Request";
		
		}
		
		HTTPHeader resp = new HTTPHeader(errorNum,errorType);
		resp.setAttribute("Date", defaultSDF.format(new Date()) + " GMT");
		resp.setAttribute("Content-Type", "text/html; charset=UTF-8;");
		
		String message = "<div style='font-family: arial; font-size: 16px; padding: 25px; color: rgb(50,50,100);'>"+errorNum+" "+errorType+"</div>";
		String out = resp.toString() + message;
		
		return out;
		
	}

}

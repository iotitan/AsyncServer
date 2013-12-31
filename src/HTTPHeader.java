/*
 * File: HTTPHeader.java
 * Author: Matt Jones
 * Date: 12/31/2013
 * Desc: Generic HTTP header object, uses HashMap to store keys/values
 */

import java.util.HashMap;
import java.util.Map;

public class HTTPHeader {
	
	public static enum method{OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT};
	
	private method requestMethod;
	private String requestLocation;
	private String requestHTTPVersion;
	
	// TODO: may or may not be better to have standard fields hard-coded instead of create maps for each request
	private Map<String,String> headers = null;
	private boolean valid = true;
	
	/**
	 * Attempt to parse a HTTP header
	 * @param rawData The raw request data
	 */
	public HTTPHeader(String rawData) {
		
		if(rawData == null) {
			valid = false;
			return;
		}
		
		// attempt to break down header fields
		headers = new HashMap<String,String>();
		
		String[] lines = rawData.split("\n");
		
		// if there are no individual lines
		if(lines == null || lines.length == 0) {
			valid = false;
			return;
		}
		
		int fSpace, lSpace;
		fSpace = lines[0].indexOf(" ");
		lSpace = lines[0].lastIndexOf(" ");
		
		// if required 3 fields are not found, header is not valid
		if(fSpace < 0 || lSpace < 0) {
			valid = false;
			return;
		}
		
		// get basic info about the request
		String rMethod = lines[0].substring(0,fSpace).toUpperCase();
		if(rMethod.equals("OPTIONS")) {
			requestMethod = method.OPTIONS;
		}
		else if(rMethod.equals("GET")) {
			requestMethod = method.GET;
		} 
		else if(rMethod.equals("HEAD")) {
			requestMethod = method.HEAD;
		}
		else if(rMethod.equals("POST")) {
			requestMethod = method.POST;
		}
		else if(rMethod.equals("PUT")) {
			requestMethod = method.PUT;
		}
		else if(rMethod.equals("DELETE")) {
			requestMethod = method.DELETE;
		}
		else if(rMethod.equals("TRACE")) {
			requestMethod = method.TRACE;
		}
		else if(rMethod.equals("CONNECT")) {
			requestMethod = method.CONNECT;
		}
		
		requestLocation = lines[0].substring(fSpace+1,lSpace);
		requestHTTPVersion = lines[0].substring(lSpace+1).trim();
		
		int col = 0;
		String hName, hVal;
		// account for first line and empty last line
		for(int i = 1; i < lines.length - 1; i++) {
			try {
				col = lines[i].indexOf(':');
				hName = lines[i].substring(0,col).trim();
				hVal = lines[i].substring(col+1).trim();
				headers.put(hName, hVal);
			}
			catch(Exception e) {
				// server should send generic 400 error
				System.out.println("ERROR: INVALID HEADER: " + e.getMessage());
				valid = false;
			}
		}
	}
	
	/**
	 * Get a header by name
	 * @param name The name of the header field
	 * @return Value of the field, null if it does not exist
	 */
	public String getAttribute(String name) {		
		return headers.get(name);
	}
	
	/**
	 * Get the map of header fields
	 * @return Map of header fields
	 */
	public Map<String,String> getAll() {
		return headers;
	}
	
	/**
	 * Get the method of the request
	 * @return Enum HTTPHeader.method with the request type
	 */
	public method getRequestMethod() {
		return requestMethod;
	}
	
	/**
	 * Get the requested location (file) from the header
	 * @return Location as a String
	 */
	public String getRequestLocation() {
		return requestLocation;
	}
	
	/**
	 * Get the HTTP version
	 * @return HTTP version as a String
	 */
	public String getRequestHTTPVersion() {
		return requestHTTPVersion;
	}
	
	/**
	 * Determine if the HTTP header is valid
	 * @return True if valid
	 */
	public boolean isValid() {
		return valid;
	}
}

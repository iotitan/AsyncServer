
public class HTTPServer {
	
	public static String respond(HTTPHeader hh) {
		
		if(!hh.isValid()) {
			return get400(0);
		}
		
		// special server info case
		if(hh.getRequestLocation().equals("/srv.info")) {
			String message = "<div style='font-family: arial; font-size: 16px; padding: 25px; color: rgb(50,50,100);'>Hello from Matt Async Server 0.0.0.0.0.1 Alpha Beta</div>";
			String out = 
					"HTTP/1.0 200 OK\n"+
					"Content-Type: text/html; charset=UTF-8;\n"+
					"\n"+
					message;
			return out;
		}
		
		// otherwise send a 404
		return get400(4);
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
				errorType = "Not found";
				break;
			default:
				errorType = "Bad Request";
		
		}
		
		String message = "<div style='font-family: arial; font-size: 16px; padding: 25px; color: rgb(50,50,100);'>"+errorNum+" "+errorType+"</div>";
		String out = 
				"HTTP/1.0 "+errorNum+" "+errorType+"\n"+
				"Content-Type: text/html; charset=UTF-8;\n"+
				"\n"+
				message;
		return out;
		
	}

}

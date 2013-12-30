
public class HTTPServer {
	
	public static String respond(HTTPHeader hh) {
		
		String message = "<div style='font-family: arial; font-size: 16px; padding: 25px; color: rgb(50,50,100);'>Hello from Matt Async Server 0.0.0.0.0.1 Alpha Beta</div>";
		String out = 
				"HTTP/1.0 200 OK\n"+
				"Content-Type: text/html; charset=UTF-8;\n"+
				"\n"+
				message;
		return out;
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

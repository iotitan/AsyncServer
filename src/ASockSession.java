/*
 * File: ASockSession.java
 * Author: Matt Jones
 * Date: 09/27/2013
 * Desc: Handle asynchronous socket sessions
 */

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.LinkedList;

import com.github.iotitan.annotations.GET;

public class ASockSession implements CompletionHandler<Integer, Void>
{
	// socket channel to client
	private AsynchronousSocketChannel as; // socket we are sending and receiving on
	
	// input and output buffers
	private static final int buffSize = 2048;
	private LinkedList<String> readBuffList; // consecutive reads from the client (basically a list of buffers)
	private ByteBuffer buff;
	
	// for detecting the end of a message
	private static final String[] messageTerminators = {"\n\n","\r\n\r\n"};
	private int terminatorCount[];
	
	// mode information
	public static enum Mode{READ, WRITE, PROC, DONE, ERROR};
	private Mode mode;
	
	//Reflection Invoker
	private RouteInvoker invoker = new RouteInvoker();

	//Request String;
	public static HTTPHeader request;
	public static HTTPHeader response;
	
	// final string to send
	public static String output;
	

	/**
	 * Create a asynchronous socket session
	 * @param a The socket we will be using
	 */
	public ASockSession(AsynchronousSocketChannel a) {
		as = a;
		readBuffList = new LinkedList<String>();
		buff = ByteBuffer.allocate(buffSize);
		mode = Mode.READ; // start out in read mode
		
		terminatorCount = new int[messageTerminators.length];
		for(int i = 0; i < terminatorCount.length; i++)
			terminatorCount[i] = 0;
	}
	
	/**
	 * Add the collected data to the message queue and attempt more reads
	 */
	public void completed(Integer read, Void a) 
	{
		switch(mode)
		{
			// if a read is in progress
			case READ:
				continueRead(read, a);
				break;
			// if a write is in progress
			case WRITE:
				continueWrite(a);
				break;
			// time to process data
			case PROC:
				handleProc(a);
				break;
			// do something when the interaction with the client is done
			case DONE:
				handleDone();
				break;
			// error condition
			case ERROR:
				break;
		}
	}
	
	/**
	 * Method required by interface, if a read/write fails, do this
	 */
	public void failed(Throwable t, Void a) {
		System.out.println("LOG: An error occured while performing " + getMode().name());
		t.printStackTrace();
		try {
			as.close();
		}
		catch(IOException e) {
			System.out.println("LOG: Attempted to close previously closed connection.");
		}
	}
	
	/**
	 * Init the async read loop
	 */
	public void beginRead() {
		as.read(buff, null, this);
	}
	
	/**
	 * Continue reading if the message is long.
	 * NOTE: Message should end with two of "\r\n"
	 * @param read Number of characters read
	 * @param a
	 */
	private void continueRead(int read, Void a) {

		// did not read anything, skip processing...
		if(read < 0) {
			setMode(Mode.PROC);
			completed(0,a);
			return;
		}
		
		// add existing data to queue
		buff.position(0);
		int i, j;
		char[] cb = new char[read];
		boolean resetCount = false;
		
		// detect if the end of the message was reached
		for(i = 0; i < read; i++) {
			cb[i] = (char)buff.get(i);
			// TODO: possibly have a hashmap of all characters involved in null terminator
			if(cb[i] == '\r' || cb[i] == '\n') {
				resetCount = true;
				for(j = 0; j < messageTerminators.length; j++) {
					if(messageTerminators[j].charAt(terminatorCount[j]) == cb[i]) {
						terminatorCount[j]++;
						if(terminatorCount[j] == messageTerminators[j].length()) {
							readBuffList.add(new String(cb,0,i+1));
							
							// TODO: THIS DOES NOT ACCEPT CONTENT AFTER HEADERS (no payload)
							// TODO: Payload handling here - be sure to add the rest of the buffer above

							setMode(Mode.PROC);
							completed(0,a);
							return;
						}
					}
					else {
						terminatorCount[j] = 0;
					}
				}
			}
			else if(resetCount) {
				for(j = 0; j < terminatorCount.length; j++)
					terminatorCount[j] = 0;
			}
		}
		
		buff.position(0);
		readBuffList.add(new String(cb));
		
		// ready buffer for new data
		buff.clear();
		as.read(buff, null, this);
	}
	
	/**
	 * Continue writing to the client.
	 * @param a
	 */
	private void continueWrite(Void a) {
		System.out.println("LOG: Now writing back to client...");
		
		ByteBuffer op = ByteBuffer.allocate(output.length());
		op.put(output.getBytes(Charset.forName("UTF-8")));
		op.position(0); // reset buffer position
		
		setMode(Mode.DONE);
		as.write(op, null, this);
	}
	
	/**
	 * Perform this once a transaction to the client has completed
	 */
	private void handleDone() {	
		System.out.println("LOG: Closing connection.");
		try {
			as.close();
		}
		catch(IOException e) {
			System.out.println("LOG: Attempted to close previously closed connection.");
		}
	}
	
	/**
	 * Process the data that has been received
	 * @param a
	 */
	private void handleProc(Void a) {
		/* TODO: Chromium and possibly other browsers open a connection but send 
		 * 		 no request (usually where favicon.ico request would be)... 
		 */
		if(!readBuffList.isEmpty()) {
			System.out.println("LOG: Now processing...");
			String temp = readBuffList.get(0);
			for(int i = 1; i < readBuffList.size(); i++)
				temp += readBuffList.get(i);
			
			request = new HTTPHeader(temp);
			invoker.callMethodByAnnotation(ASock.route, request.getRequestMethod().toString().toString(), request.getRequestLocation());
			output = HTTPServer.respond(new HTTPHeader(temp));
		}
		else {
			System.out.println("WARNING: Client appears to have opened a connection but made no request!");
			output = HTTPServer.get400(0);
		}
		
		// TODO: have a Mode.ERROR for handling bad requests and headers that are too long
		setMode(Mode.WRITE);
		// trigger completion call since this particular part is synchronous
		completed(0,a);
	}
	
	/**
	 * Set the mode of this session
	 * @param m Mode to set sessions to
	 */
	public void setMode(Mode m)
	{
		mode = m;
	}
	
	/**
	 * Get the current mode of this session
	 * @return Session mode
	 */
	public Mode getMode()
	{
		return mode;
	}

}

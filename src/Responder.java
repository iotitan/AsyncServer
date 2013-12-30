/*
 * File: Responder.java
 * Author: Matt Jones
 * Date: 09/27/2013
 * Desc: Start and maintain all sessions
 */

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Responder implements CompletionHandler<AsynchronousSocketChannel, Void>
{
	private AsynchronousServerSocketChannel listener;
	private static final int THREAD_COUNT = 10;
	
	/**
	 * Initialize the responder with an async server socket channel.
	 * @param s Server socket channel
	 */
	public Responder(AsynchronousServerSocketChannel s)
	{
		listener = s;
	}
	
	/**
	 * On completion of accept event
	 */
	public void completed(AsynchronousSocketChannel c, Void att)
	{
		// make sure we start listening for another client
		listener.accept(null,this);
		
		try
		{
			System.out.println("Client has connected: " + c.getRemoteAddress().toString());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		ASockSession rm = new ASockSession(c);
		rm.beginRead(); // trigger the start of read sequence
	}
	
	public void failed(Throwable exc, Void att)
	{
	}
}

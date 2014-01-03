/*
 * File: ASock.java
 * Author: Matt Jones
 * Date: 09/27/2013
 * Desc: Set up server socket and accept incoming connections (really only the first connection)
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class ASock
{	
	/**
	 * Example, only for testing
	 * route should be a dynamically loaded class ideally and not static
	 */
	public static RouteHandler route = new RouteHandler();
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Starting up...");
		
		HTTPServer.initServer();
		
		System.out.println("Startup complete...");
		
		final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(11111));
		
		listener.accept(null, new Responder(listener));
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				
			}
		}

	}
}

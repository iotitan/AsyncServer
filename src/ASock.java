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
	public static void main(String[] args) throws IOException
	{
		System.out.println("Starting up...");
		
		final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(11111));
		
		listener.accept(null, new Responder(listener));
		while(true){}

	}
}

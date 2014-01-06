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
		
		ServerConfig sc = new ServerConfig("web.conf");
		
		System.out.println("CONFIG: port: " + sc.getSetting("server.port"));
		System.out.println("CONFIG: root directory: " + sc.getSetting("server.root"));
		
		HTTPServer.initServer(sc);
		
		System.out.println("Startup complete...");
		
		// TODO: error handling if parseInt() fails. Possibly do this in ServerConfig class
		final AsynchronousServerSocketChannel listener = 
				AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(Integer.parseInt(sc.getSetting("server.port"))));
		
		listener.accept(null, new Responder(listener));
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				
			}
		}

	}
}

/*
 * File: ServerConfig.java
 * Author: Matt Jones
 * Date: 1/3/2014
 * Desc: Class used to read and store server configurations. The general 
 *       idea is that each setting is "category.subCategory.name". This can
 *       easily be build into a tree for quick lookup of settings and is 
 *       less cumbersome to write.
 */

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


public class ServerConfig {

	private boolean valid;
	private ConfNode root;
	
	/**
	 * Node for the configuration tree
	 */
	private class ConfNode {
		private String name;
		private String value;
		private LinkedList<ConfNode> children;
	
		/**
		 * Build node with the specified name
		 * @param n node name
		 * @param v node value
		 */
		public ConfNode(String n, String v) {
			name = n;
			children = null;
			value = v;
		}
		
		/**
		 * Add a child node to config tree
		 * @param n Name of node
		 * @param v Value contained in node
		 */
		public void addChild(String n, String v) {
			if(children == null)
				children = new LinkedList<ConfNode>();
			// check for existing entries
			Iterator<ConfNode> it = children.iterator();
			ConfNode temp = null;
			while(it.hasNext()) {
				temp = it.next();
				if(temp.name.equals(n)) {
					// place warning here? a value may have been overwritten
					temp.value = v;
					return;
				}
			}
			children.add(new ConfNode(n,v));
		}
	}
	
	/**
	 * Parse a file of configuration info
	 * @param fileName Name of file to parse
	 */
	public ServerConfig(String fileName) {
		
		valid = true;
		
		try {
			Scanner in = new Scanner(new File(fileName));
		}
		catch(Exception e) {
			valid  = false;
			System.out.println("ERROR: Could not read configuration file.\n" + e.getMessage());
		}
	}
	
	/**
	 * Insert a node into the settings tree
	 * @param name Name of setting
	 * @param value Value of setting
	 */
	public void insert(String name, String value) {
		
	}
	
	/**
	 * Get a setting from the tree
	 * @param name The name of the setting
	 * @return Value of the setting
	 */
	public String getSetting(String name) {
		return null;
	}
	
	/**
	 * If the configuration file read is valid
	 * @return True if valid
	 */
	public boolean isValid() {
		return valid;
	}
	
}

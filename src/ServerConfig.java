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

	private ConfNode root;
	
	// hard-coded default settings in 2D array
	// TODO: this could probably just be a 1D array, but 2D is easier to read/understand
	public static final String[][] requiredSettings = {
														{"server.port","8080"},
														{"server.root","/web"}
													};
	
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
		 * Add a child node to config tree. Returns existing node if name exists.
		 * @param n Name of node
		 * @param v Value contained in node
		 * @return The created/existing node
		 */
		public ConfNode addChild(String n, String v) {
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
					return temp;
				}
			}
			// if no existing nodes, create and return a new one
			ConfNode newNode = new ConfNode(n,v);
			children.add(newNode);
			return newNode;
		}
		
		/**
		 * Get a child of the current node by name
		 * @param name Name of the node
		 * @return The ConfNode with the given name
		 */
		public ConfNode getChild(String name) {
			if(name == null || children == null || children.size() == 0)
				return null;
			Iterator<ConfNode> it = children.iterator();
			ConfNode temp = null;
			while(it.hasNext()) {
				temp = it.next();
				if(name.equals(temp.name)) {
					return temp;
				}
			}
			return null;
		}
		
		/**
		 * Get the value contained in this node
		 * @return Node value
		 */
		public String getValue() {
			return value;
		}
	}
	
	/**
	 * Parse a file of configuration info
	 * @param fileName Name of file to parse
	 */
	public ServerConfig(String fileName) {
		
		Scanner in = null;
		root = new ConfNode("","");
		
		try {
			in = new Scanner(new File(fileName));
		}
		catch(Exception e) {
			System.out.println("WARNING: Could not read configuration file. Using defaults...\n" + e.getMessage());
			
			// if no config was found, use default settings
			defaultConfig(null);
			
			return;
		}
		
		// get and read settings from file. Settings format is extremely simple (categoty.subCategory.name = value)
		String line = null;
		String[] split;
		while(in.hasNextLine()) {
			line = in.nextLine();
			// comment lines start with '#' and ignore empty lines
			if(line.trim().length() < 1 || line.trim().charAt(0) == '#')
				continue;
			split = line.split("=");
			// if no split on '=', not valid name/value pair
			if(split.length < 2)
				continue;
			insert(split[0].trim(),split[1].trim());
		}
		
		// check and make sure the things we need were set in the file
		for(int i = 0; i < requiredSettings.length; i++) {
			if(getSetting(requiredSettings[i][0]) == null) {
				defaultConfig(requiredSettings[i][0]);
				System.out.println("WARNING: " + requiredSettings[i][0] + " not set. Using default of " + requiredSettings[i][1]);
			}
		}
	}
	
	/**
	 * Set up the server with the default settings
	 * @param name The name of the setting to get the default of. Null if all are needed
	 */
	private void defaultConfig(String name) {
		for(int i = 0; i < requiredSettings.length; i++) {
			if(name == null || name.equals(requiredSettings[i][0]))
				insert(requiredSettings[i][0],requiredSettings[i][1]);
		}
	}
	
	/**
	 * Insert a node into the settings tree
	 * @param name Name of setting
	 * @param value Value of setting
	 */
	public void insert(String name, String value) {
		if(name == null || value == null)
			return;

		String[] nameSplit = name.split("\\.");
		ConfNode temp = root;
		
		// add nodes in the tree until the last part of the name, then add the value
		for(int i = 0; i < nameSplit.length-1; i++) {
			temp = temp.addChild(nameSplit[i], null);
		}
		temp.addChild(nameSplit[nameSplit.length-1], value);
	}
	
	/**
	 * Get a setting from the tree
	 * @param name The name of the setting
	 * @return Value of the setting
	 */
	public String getSetting(String name) {
		if(name == null)
			return null;
		
		String[] nameSplit = name.split("\\.");
		ConfNode temp = root;
		
		// traverse config tree
		for(int i = 0; i < nameSplit.length; i++) {
			temp = temp.getChild(nameSplit[i]);
			if(temp == null)
				return null;
		}

		if(temp != null)
			return temp.getValue();
		
		return null;
	}
	
}

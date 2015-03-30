package com.nuevapartida.parser;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class References {
	private static Logger logger = Logger.getLogger(References.class);
	
	private static HashMap<String, HashMap<String, String>> references = new HashMap<String, HashMap<String, String>>();
	
	public static String getString(String type, String name) {
		HashMap<String, String> obj = references.get(type);
		if (obj != null) {
			if (obj.get(name) == null) {
				logger.error("Referencia no encontrada: " + name);
				return null;
			} else {
				return obj.get(name);
			}			
		} else {
			return null;
		}
	}
	
	public static long get(String type, String name) {
		HashMap<String, String> obj = references.get(type);
		if (obj != null) {
			if (obj.get(name) == null) {
				logger.error("Referencia no encontrada: " + name);
				return -1;
			} else {
				return Long.parseLong(obj.get(name));
			}			
		} else {
			return -1;
		}
	}
	
	public static void put(String type, String name, long value) {
		putString(type, name, "" + value);
	}
	
	public static void putString(String type, String name, String value) {
		HashMap<String, String> obj = references.get(type);
		if (obj == null) {
			obj = new HashMap<String, String>();
		} else if (obj.get(name) != null) {
			logger.error("Referencia duplicada\n" + name + "::" + value);
		}
		obj.put(name, value);
		references.put(type, obj);
	}
	
	public static void empty() {
		references = new HashMap<String, HashMap<String, String>>();
	}
}

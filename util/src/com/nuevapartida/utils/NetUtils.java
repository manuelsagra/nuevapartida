package com.nuevapartida.utils;

import java.awt.Desktop;
import java.net.URL;

import org.apache.log4j.Logger;

import com.nuevapartida.scraping.GoogleScraping;

public class NetUtils {
	private static Logger logger = Logger.getLogger(NetUtils.class);
	
	private static final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	
	public static String getWebsiteLink(String system, String title, String type, String extra) {
		String link = "";
		if (type.equalsIgnoreCase("GameFAQs")) {
			link = GoogleScraping.searchForUrl("\"" + title + " for " + system.replace("PS ", "PlayStation ") + " " + (extra != null ? extra : "") + " " + "\" site:gamefaqs.com", "http://www.gamefaqs.com/ps");
		} else if (type.equalsIgnoreCase("RF Generation")) {
			link = GoogleScraping.searchForUrl("\"" + title + "\" " + system + " " + (extra != null ? extra : "") + " " + " site:rfgeneration.com", "http://www.rfgeneration.com/cgi-bin/getinfo.pl");
		}
		
		return link;
	}
	
	public static void openBrowser(String url) {
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse((new URL(url)).toURI());
	        } catch (Exception e) {
	        	logger.error("No se puede abrir el navegador");
	        }
	    }
	}
}

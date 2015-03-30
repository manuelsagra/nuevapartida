package com.nuevapartida.scraping;

import java.net.URI;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.UserAgent;

public class GoogleScraping {
	private static Logger logger = Logger.getLogger(GoogleScraping.class);
	
	static final String SEARCH_PROTOCOL = "http";
	static final String BASE_URL = "www.google.es";
	static final String SEARCH_URL = "/search";
	
	public static String searchForUrl(String criteria, String subUrl) {		
		String result = null;
		try {
			URI uri = new URI(SEARCH_PROTOCOL, BASE_URL, SEARCH_URL, "q=" + criteria, null);
			UserAgent userAgent = new UserAgent();
			userAgent.visit(uri.toASCIIString());

			Elements headers = userAgent.doc.findEvery("<h3 class=r>");
			for (Element header : headers) {
				String href = header.getFirst("<a>").getAt("href");
				if (href.contains(subUrl)) {
					result = href.substring(href.indexOf("?q=") + 3, href.indexOf("&amp;"));
					return URLDecoder.decode(result, "UTF-8");
				}
			}			
		} catch (Exception e) {
			logger.error("Error buscando " + criteria);
		} 
		return result;
	}
}

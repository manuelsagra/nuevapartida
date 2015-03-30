package com.nuevapartida.utils;

import org.apache.log4j.Logger;

public class ScrapingUtils {
	private static Logger logger = Logger.getLogger(ScrapingUtils.class);
	static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public static String parseMobyGamesDate(String date) {
		if (date != null) {
			String[] auxC = date.split(",");
			String[] auxD = date.split("\\.");
			
			String yyyy = "0000";
			String mm = "00";
			String dd = "00";
			
			// Year only
			if (auxC.length == 1 && auxD.length == 1) {
				yyyy = date.trim();
			// Comma
			} else if (auxC.length > 1) {
				yyyy = auxC[1].trim();
				String[] md = auxC[0].split(" ");				
				int m = 0;
				for (int i = 0; (i < MONTHS.length) && (m == 0); i++) {
					if (MONTHS[i].equals(md[0])) {
						m = ++i;
					}
				}
				if (m == 0) {
					logger.error(date + "(" + md[0] + ")");
				}
				mm = m + "";
				if (md.length > 1) {
					dd = md[1].trim();
				}
			// Dots
			} else {
				yyyy = auxD[2].trim();
				mm = auxD[1].trim();
				dd = auxD[0].trim();
			}
			return yyyy + "-" + Utils.leftPad(mm, '0', 2) + "-" + Utils.leftPad(dd, '0', 2);
		} else {
			return null;
		}
	}
}

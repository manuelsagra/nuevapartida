package com.nuevapartida.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MysqlConnection {
	private static Logger logger = Logger.getLogger(MysqlConnection.class);
	
	static String server = "localhost";
	static String user = "root";
	static String pwd = "";
	
	static Connection connection = null;
	
	public static void connect(String db) {
		try {
			String url = "jdbc:mysql://" + server + "/" + db + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, pwd);
			
			if (connection == null) {
				logger.error("Error conectando a la base de datos");
			}
		} catch (Exception e) {
			logger.error("Error conectando a la base de datos");
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static void disconnect() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("Error desconectando de la base de datos");
			}
		}
	}
	
	public static void emptyTables() throws Exception {
		connection.prepareStatement("TRUNCATE items").executeUpdate();
		connection.prepareStatement("TRUNCATE item_tags").executeUpdate();
		connection.prepareStatement("TRUNCATE metadata").executeUpdate();
		connection.prepareStatement("TRUNCATE relationships").executeUpdate();
		connection.prepareStatement("TRUNCATE tags").executeUpdate();
		connection.prepareStatement("TRUNCATE types").executeUpdate();
	}
}

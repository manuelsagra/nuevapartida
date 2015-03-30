package com.nuevapartida.mysql.dao;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.mysql.dto.ItemTagDTO;

public class ItemTagDAO {
	private static Logger logger = Logger.getLogger(ItemTagDAO.class);
	
	public static void insert(ItemTagDTO obj) {
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"INSERT INTO item_tags (item_id, tag_id)"
							+ " VALUES (?, ?)");
			
			query.setLong(1, obj.getItemId());
			query.setLong(2, obj.getTagId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error insertando item tag");
		}
	}

	public static void delete(long item_id, long tag_id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM item_tags WHERE item_id = ? AND tag_id = ?");
			
			query.setLong(1, item_id);
			query.setLong(2, tag_id);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando item tag");
		}
	}
	
	public static void deleteItemTags(long item_id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM item_tags WHERE item_id = ?");
			
			query.setLong(1, item_id);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando item tag");
		}
	}
}

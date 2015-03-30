package com.nuevapartida	.mysql.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.mysql.dto.MetadataDTO;


public class MetadataDAO {
	private static Logger logger = Logger.getLogger(MetadataDAO.class);
	
	public static long insert(MetadataDTO obj) {
		long ret = -1;

		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"INSERT INTO metadata (item_id, type_id, name, value, comment)"
							+ " VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			query.setLong(1, obj.getItemId());
			query.setLong(2, obj.getTypeId());
			query.setString(3, obj.getName());
			query.setString(4, obj.getValue());
			query.setString(5, obj.getComment());

			query.executeUpdate();
			
			ResultSet rs = query.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);
			rs.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error insertando metadata");
		}

		return ret;
	}

	public static void update(MetadataDTO obj) {
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"UPDATE metadata SET item_id = ?,"
							+ " type_id = ?,"
							+ " name = ?,"
							+ " value = ?,"
							+ " comment = ?"
							+ " WHERE id = ?");
			
			query.setLong(1, obj.getItemId());
			query.setLong(2, obj.getTypeId());
			query.setString(3, obj.getName());
			query.setString(4, obj.getValue());
			query.setString(5, obj.getComment());
			query.setLong(6, obj.getId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error actualizando metadata");
		}
	}

	public static void delete(long id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM metadata WHERE id = ?");
			
			query.setLong(1, id);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando metadata");
		}
	}

	public static MetadataDTO getElementById(long id) {
		MetadataDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM metadata WHERE id = ?");
			
			query.setLong(1, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new MetadataDTO();

				obj.setId(res.getLong("id"));
				obj.setItemId(res.getLong("item_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setValue(res.getString("value"));
				obj.setComment(res.getString("comment"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando metadata");
		}

		return obj;
	}
	
	public static MetadataDTO getElementByItemIdAndType(long itemId, long typeId) {
		MetadataDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM metadata WHERE item_id = ? AND type_id = ?");
			
			query.setLong(1, itemId);
			query.setLong(2, typeId);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new MetadataDTO();

				obj.setId(res.getLong("id"));
				obj.setItemId(res.getLong("item_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setValue(res.getString("value"));
				obj.setComment(res.getString("comment"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando metadata");
		}

		return obj;
	}

	public static MetadataDTO getElementByTypeAndValue(long typeId, String value) {
		MetadataDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM metadata WHERE type_id = ? AND value = ?");
			
			query.setLong(1, typeId);
			query.setString(2, value);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new MetadataDTO();

				obj.setId(res.getLong("id"));
				obj.setItemId(res.getLong("item_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setValue(res.getString("value"));
				obj.setComment(res.getString("comment"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando metadata");
		}

		return obj;
	}

	public static List<String> getValuesByType(long typeId) {
		List<String> values = new ArrayList<String>();
		
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT value FROM metadata WHERE type_id = ?");
			
			query.setLong(1, typeId);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				values.add(res.getString("value"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando metadata");
		}
		
		return values;
	}

	public static List<MetadataDTO> getElementsByItemId(long id) {
		List<MetadataDTO> objs = new ArrayList<MetadataDTO>();

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM metadata WHERE item_id = ?");
			
			query.setLong(1, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				MetadataDTO obj = new MetadataDTO();

				obj.setId(res.getLong("id"));
				obj.setItemId(res.getLong("item_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setValue(res.getString("value"));
				obj.setComment(res.getString("comment"));
				
				objs.add(obj);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando metadata");
		}

		return objs;
	}
}

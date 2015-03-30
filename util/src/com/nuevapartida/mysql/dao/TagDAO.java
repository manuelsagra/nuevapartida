package com.nuevapartida.mysql.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.mysql.dto.TagDTO;

public class TagDAO {
	private static Logger logger = Logger.getLogger(TagDAO.class);
	
	public static long insert(TagDTO obj) {
		long ret = -1;
		
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"INSERT INTO tags (name,shortname,type_id)"
							+ " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			query.setString(1, obj.getName());
			query.setString(2, obj.getShortname());
			query.setLong(3, obj.getType_id());

			query.executeUpdate();
			
			ResultSet rs = query.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);
			rs.close();
			query.close();
			obj.setId(ret);
		} catch (Exception e) {
			logger.error("Error insertando tag");
		}
		
		return ret;
	}

	public static void update(TagDTO obj) {
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"UPDATE tags SET name = ?, shortname = ?, type_id = ?"
							+ " WHERE id = ?");
			
			query.setString(1, obj.getName());
			query.setString(2, obj.getShortname());
			query.setLong(3, obj.getType_id());
			query.setLong(4, obj.getId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error actualizando tag");
		}
	}

	public static void delete(long id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM tags WHERE id = ?");
			
			query.setLong(1, id);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando tag");
		}
	}

	public static TagDTO getElementById(long id) {
		TagDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM tags WHERE id = ?");

			query.setLong(1, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new TagDTO();

				obj.setId(res.getLong("id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setType_id(res.getLong("type_id"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando tag");
		}

		return obj;
	}
	
	public static TagDTO getElementByShortname(String shortname) {
		TagDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM tags WHERE shortname = ?");

			query.setString(1, shortname);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new TagDTO();

				obj.setId(res.getLong("id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setType_id(res.getLong("type_id"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando tag");
		}

		return obj;
	}
	
	public static TagDTO getElementByName(String name) {
		TagDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM tags WHERE name = ?");

			query.setString(1, name);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new TagDTO();

				obj.setId(res.getLong("id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setType_id(res.getLong("type_id"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando tag");
		}

		return obj;
	}
	
	public static List<TagDTO> getItemTags(long id) {
		ArrayList<TagDTO> list = new ArrayList<TagDTO>();

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT t.* FROM tags t, item_tags i WHERE i.tag_id = t.id AND i.item_id = ? ORDER BY t.name");

			query.setLong(1, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				TagDTO obj = new TagDTO();

				obj.setId(res.getLong("id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setType_id(res.getLong("type_id"));
				
				list.add(obj);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando tag");
		}

		return list;
	}
	
	public static List<TagDTO> getAllTags() {
		ArrayList<TagDTO> list = new ArrayList<TagDTO>();

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM tags ORDER BY type_id, name");
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				TagDTO obj = new TagDTO();

				obj.setId(res.getLong("id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setType_id(res.getLong("type_id"));
				
				list.add(obj);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando tag");
		}

		return list;
	}
}

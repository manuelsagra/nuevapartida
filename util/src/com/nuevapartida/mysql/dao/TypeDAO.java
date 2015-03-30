package com.nuevapartida.mysql.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.mysql.dto.TypeDTO;

public class TypeDAO {
	private static Logger logger = Logger.getLogger(TypeDAO.class);
	
	public static long insert(TypeDTO obj) {
		long ret = -1;

		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"INSERT INTO types (parent_id, name, shortname, content, excerpt, status, template)"
							+ " VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			query.setLong(1, obj.getParentId());
			query.setString(2, obj.getName());
			query.setString(3, obj.getShortname());
			query.setString(4, obj.getContent());
			query.setString(5, obj.getExcerpt());
			query.setString(6, obj.getStatus());
			query.setString(7, obj.getTemplate());

			query.executeUpdate();
			
			ResultSet rs = query.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);
			rs.close();
			query.close();
			obj.setId(ret);
		} catch (Exception e) {
			logger.error("Error insertando type");
		}

		return ret;
	}

	public static void update(TypeDTO obj) {
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"UPDATE types SET parent_id = ?,"
							+ " name = ?,"
							+ " shortname = ?,"
							+ " content = ?,"
							+ " excerpt = ?,"
							+ " status = ?,"
							+ " modified = ?"
							+ " template = ?"
							+ " WHERE id = ?");
			
			query.setLong(1, obj.getParentId());
			query.setString(2, obj.getName());
			query.setString(3, obj.getShortname());
			query.setString(4, obj.getContent());
			query.setString(5, obj.getExcerpt());
			query.setString(6, obj.getStatus());
			query.setDate(7, new Date(Calendar.getInstance().getTimeInMillis()));
			query.setString(8, obj.getTemplate());
			query.setLong(9, obj.getId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error actualizando type");
		}
	}

	public static void delete(long id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM types WHERE id = ?");
			
			query.setLong(1, id);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando type");
		}
	}

	public static TypeDTO getElementById(long id) {
		TypeDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM types WHERE id = ?");
			
			query.setLong(1, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new TypeDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
				obj.setTemplate(res.getString("template"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando type");
		}

		return obj;
	}
	
	public static TypeDTO getElementByShortname(String shortname) {
		TypeDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM types WHERE shortname = ?");
			
			query.setString(1, shortname);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new TypeDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
				obj.setTemplate(res.getString("template"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando type");
		}

		return obj;
	}
	
	public static TypeDTO getElementByName(String name) {
		TypeDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM types WHERE name = ?");
			
			query.setString(1, name);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new TypeDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setName(res.getString("name"));
				obj.setShortname(res.getString("shortname"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
				obj.setTemplate(res.getString("template"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando type");
		}

		return obj;
	}
}

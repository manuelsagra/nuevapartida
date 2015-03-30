package com.nuevapartida.mysql.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.mysql.dto.ItemDTO;

public class ItemDAO {
	private static Logger logger = Logger.getLogger(ItemDAO.class);
	
	public static long insert(ItemDTO obj) {
		long ret = -1;

		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"INSERT INTO items (type_id, parent_id, name, altname, shortname, date, content, excerpt, status)"
									+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);

			query.setLong(1, obj.getTypeId());
			query.setLong(2, obj.getParentId());
			query.setString(3, obj.getName());
			query.setString(4, obj.getAltname());
			query.setString(5, obj.getShortname());
			query.setString(6, obj.getDate());
			query.setString(7, obj.getContent());
			query.setString(8, obj.getExcerpt());
			query.setString(9, obj.getStatus());

			query.executeUpdate();

			ResultSet rs = query.getGeneratedKeys();
			rs.next();
			ret = rs.getLong(1);
			rs.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error insertando item");
		}

		return ret;
	}

	public static void update(ItemDTO obj) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"UPDATE items SET type_id = ?, parent_id = ?, " + " name = ?,"
									+ " altname = ?," + " shortname = ?,"
									+ " date = ?," + " content = ?,"
									+ " excerpt = ?," + " status = ?,"
									+ " modified = ?" + " WHERE id = ?");

			query.setLong(1, obj.getTypeId());
			query.setLong(2, obj.getParentId());
			query.setString(3, obj.getName());
			query.setString(4, obj.getAltname());
			query.setString(5, obj.getShortname());
			query.setString(6, obj.getDate());
			query.setString(7, obj.getContent());
			query.setString(8, obj.getExcerpt());
			query.setString(9, obj.getStatus());
			query.setDate(10, new Date(Calendar.getInstance().getTimeInMillis()));
			query.setLong(11, obj.getId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error actualizando item");
		}
	}

	public static void delete(long id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("DELETE FROM items WHERE ID = ?");

			query.setLong(1, id);

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando item");
		}
	}

	public static ItemDTO getElementById(long id) {
		ItemDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT *, CAST(date AS char) as date2 FROM items WHERE id = ?");

			query.setLong(1, id);

			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new ItemDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setAltname(res.getString("altname"));
				obj.setShortname(res.getString("shortname"));
				obj.setDate(res.getString("date2"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando item");
		}

		return obj;
	}

	public static ItemDTO getElementByShortname(String shortname) {
		ItemDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"SELECT *, CAST(date AS char) as date2 FROM items WHERE shortname = ?");

			query.setString(1, shortname);

			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new ItemDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setAltname(res.getString("altname"));
				obj.setShortname(res.getString("shortname"));
				obj.setDate(res.getString("date2"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando item");
		}

		return obj;
	}

	public static ItemDTO getElementByNameAndType(String name, long type_id) {
		ItemDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"SELECT *, CAST(date AS char) as date2 FROM items WHERE type_id = ? AND name = ?");

			query.setLong(1, type_id);
			query.setString(2, name);

			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new ItemDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setAltname(res.getString("altname"));
				obj.setShortname(res.getString("shortname"));
				obj.setDate(res.getString("date2"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando items");
		}

		return obj;
	}

	public static List<ItemDTO> getElementsByType(long type_id) {
		ArrayList<ItemDTO> obj = new ArrayList<ItemDTO>();

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"SELECT *, CAST(date AS char) as date2 FROM items WHERE type_id = ? ORDER BY name");
			
			query.setLong(1, type_id);

			ResultSet res = query.executeQuery();
			while (res.next()) {
				ItemDTO o = new ItemDTO();

				o.setId(res.getLong("id"));
				o.setParentId(res.getLong("parent_id"));
				o.setTypeId(res.getLong("type_id"));
				o.setName(res.getString("name"));
				o.setAltname(res.getString("altname"));
				o.setShortname(res.getString("shortname"));
				o.setDate(res.getString("date2"));
				o.setModified(res.getDate("modified"));
				o.setContent(res.getString("content"));
				o.setExcerpt(res.getString("excerpt"));
				o.setStatus(res.getString("status"));

				obj.add(o);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando items");
		}

		return obj;
	}
	
	public static List<ItemDTO> getItemsByRelationshipAndType(long parent_id, long type_id) {
		List<ItemDTO> objs = new ArrayList<ItemDTO>();

		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"SELECT i.*, CAST(i.date AS char) as date2 FROM items i, relationships r WHERE r.parent_id = ? AND r.child_id = i.id AND r.type_id = ?");
			
			query.setLong(2, type_id);
			query.setLong(1, parent_id);

			ResultSet res = query.executeQuery();
			while (res.next()) {
				ItemDTO obj = new ItemDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setTypeId(res.getLong("type_id"));
				obj.setName(res.getString("name"));
				obj.setAltname(res.getString("altname"));
				obj.setShortname(res.getString("shortname"));
				obj.setDate(res.getString("date2"));
				obj.setModified(res.getDate("modified"));
				obj.setContent(res.getString("content"));
				obj.setExcerpt(res.getString("excerpt"));
				obj.setStatus(res.getString("status"));
				
				objs.add(obj);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando item");
		}

		return objs;
	}
	
	public static List<ItemDTO> getChildren(long id) {
		ArrayList<ItemDTO> obj = new ArrayList<ItemDTO>();

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"SELECT *, CAST(date AS char) as date2 FROM items WHERE parent_id = ?");

			query.setLong(1, id);

			ResultSet res = query.executeQuery();
			while (res.next()) {
				ItemDTO o = new ItemDTO();

				o.setId(res.getLong("id"));
				o.setParentId(res.getLong("parent_id"));
				o.setTypeId(res.getLong("type_id"));
				o.setName(res.getString("name"));
				o.setAltname(res.getString("altname"));
				o.setShortname(res.getString("shortname"));
				o.setDate(res.getString("date2"));
				o.setModified(res.getDate("modified"));
				o.setContent(res.getString("content"));
				o.setExcerpt(res.getString("excerpt"));
				o.setStatus(res.getString("status"));

				obj.add(o);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando items");
		}

		return obj;		
	}
}

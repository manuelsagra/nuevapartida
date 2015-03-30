package com.nuevapartida.mysql.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.mysql.dto.RelationshipDTO;

public class RelationshipDAO {
	private static Logger logger = Logger.getLogger(RelationshipDAO.class);
	
	public static void insert(RelationshipDTO obj) {
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"INSERT INTO relationships (parent_id, child_id, type_id)"
							+ " VALUES (?, ?, ?)");
			
			query.setLong(1, obj.getParentId());
			query.setLong(2, obj.getChildId());
			query.setLong(3, obj.getTypeId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error insertando relationship");
		}
	}

	public static void update(RelationshipDTO obj) {
		try {
			PreparedStatement query = MysqlConnection
					.getConnection()
					.prepareStatement(
							"UPDATE relationships SET type_id = ?, parent_id = ? AND child_id = ?"
							+ " WHERE id = ?");
			
			query.setLong(1, obj.getTypeId());
			query.setLong(2, obj.getParentId());
			query.setLong(3, obj.getChildId());
			query.setLong(4, obj.getId());

			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error actualizando relationship");
		}
	}

	public static void delete(long id) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM relationships WHERE id = ?");
			
			query.setLong(1, id);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando relationship");
		}
	}

	public static RelationshipDTO getElementById(long id) {
		RelationshipDTO obj = null;

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM relationships WHERE id = ?");

			query.setLong(1, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				obj = new RelationshipDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setChildId(res.getLong("child_id"));
				obj.setTypeId(res.getLong("type_id"));
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando relationship");
		}

		return obj;
	}
	
	public static List<RelationshipDTO> getElementsByItemId(long id) {
		List<RelationshipDTO> objs = new ArrayList<RelationshipDTO>();

		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement("SELECT * FROM relationships WHERE parent_id = ? OR child_id = ?");

			query.setLong(1, id);
			query.setLong(2, id);
			
			ResultSet res = query.executeQuery();
			while (res.next()) {
				RelationshipDTO obj = new RelationshipDTO();

				obj.setId(res.getLong("id"));
				obj.setParentId(res.getLong("parent_id"));
				obj.setChildId(res.getLong("child_id"));
				obj.setTypeId(res.getLong("type_id"));
				
				objs.add(obj);
			}
			res.close();
			query.close();
		} catch (Exception e) {
			logger.error("Error buscando relationship");
		}

		return objs;
	}

	public static void deleteByItemsAndType(long parentId, long childId, long typeId) {
		try {
			PreparedStatement query = MysqlConnection.getConnection()
					.prepareStatement(
							"DELETE FROM relationships WHERE parent_id = ? AND child_id = ? AND type_id = ?");
			
			query.setLong(1, parentId);
			query.setLong(2, childId);
			query.setLong(3, typeId);
			
			query.executeUpdate();
			query.close();
		} catch (Exception e) {
			logger.error("Error borrando relationship");
		}
	}
}

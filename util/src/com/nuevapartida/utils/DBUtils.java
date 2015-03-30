package com.nuevapartida.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dao.ItemTagDAO;
import com.nuevapartida.mysql.dao.MetadataDAO;
import com.nuevapartida.mysql.dao.RelationshipDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.ItemTagDTO;
import com.nuevapartida.mysql.dto.MetadataDTO;
import com.nuevapartida.mysql.dto.RelationshipDTO;
import com.nuevapartida.mysql.dto.TagDTO;
import com.nuevapartida.parser.References;

public class DBUtils {
	public static void insertRelationship(long parentId, long childId, long typeId) {
		RelationshipDTO rdto = new RelationshipDTO();
		rdto.setParentId(parentId);
		rdto.setChildId(childId);
		rdto.setTypeId(typeId);
		RelationshipDAO.insert(rdto);
	}
	
	public static void insertMetadata(long itemId, long typeId, String name, String value) {
		MetadataDTO mdto = new MetadataDTO();
		mdto.setItemId(itemId);
		mdto.setTypeId(typeId);
		mdto.setName(name);
		mdto.setValue(value);
		MetadataDAO.insert(mdto);
	}

	public static void syncronizeVersionEditionRelationships(long parentId, long childId, long typeId) {
		syncronizeRelationships(childId, typeId, ItemDAO.getItemsByRelationshipAndType(parentId, typeId), ItemDAO.getItemsByRelationshipAndType(childId, typeId));
	}
	
	public static void syncronizeRelationships(long itemId, long typeId, List<ItemDTO> newItems, List<ItemDTO> oldItems) {
		newItems = Utils.removeDuplicatedItems(newItems);
		oldItems = Utils.removeDuplicatedItems(oldItems);
	
		List<Long> newIds = Utils.getItemIds(newItems);
		List<Long> oldIds = Utils.getItemIds(oldItems);
		
		// Delete items in child that aren't in parent
		for (long oldId : oldIds) {
			if (!Utils.idListContainsId(newIds, oldId)) {
				RelationshipDAO.deleteByItemsAndType(itemId, oldId, typeId);
			}
		}
		
		// Insert missing items in child
		for (long newId : newIds) {
			if (!Utils.idListContainsId(oldIds, newId)) {
				insertRelationship(itemId, newId, typeId);
			}
		}
	}
	
	public static void synchronizeTags(long itemId, List<TagDTO> newTags, List<TagDTO> oldTags) {
		newTags = Utils.removeDuplicatedTags(newTags);
		oldTags = Utils.removeDuplicatedTags(oldTags);
		
		List<Long> newIds = Utils.getTagIds(newTags);
		List<Long> oldIds = Utils.getTagIds(oldTags);
		
		// Delete tags in child that aren't in parent
		for (long oldId : oldIds) {
			if (!Utils.idListContainsId(newIds, oldId)) {
				ItemTagDAO.delete(itemId, oldId);
			}
		}
		
		// Insert missing tags in child
		for (long newId : newIds) {
			if (!Utils.idListContainsId(oldIds, newId)) {
				ItemTagDTO it = new ItemTagDTO();
				it.setItemId(itemId);
				it.setTagId(newId);
				ItemTagDAO.insert(it);
			}
		}
	}

	public static void mixGames(List<Long> mixGamesIds) {
		long typeSystem = References.get("Tipos", "Plataformas");
		
		if (mixGamesIds != null && mixGamesIds.size() > 1) {
			List<ItemDTO> games = new ArrayList<ItemDTO>();
			for (long id : mixGamesIds) {
				ItemDTO game = ItemDAO.getElementById(id);
				games.add(game);
			}
			Collections.sort(games);

			ItemDTO primaryGame = games.get(0);
			List<ItemDTO> primaryVersions = ItemDAO.getChildren(primaryGame.getId());
			for (int i = 1; i < games.size(); i++) {
				ItemDTO g = games.get(i);
				g.setStatus("mezclado");
				ItemDAO.update(g);
				
				// Move all versions / edition to primaryGame
				List<ItemDTO> versions = ItemDAO.getChildren(g.getId());
				for (ItemDTO version : versions) {
					boolean primaryHasSystem = false;
					ItemDTO system = ItemDAO.getItemsByRelationshipAndType(version.getId(), typeSystem).get(0);
					for (ItemDTO primaryVersion : primaryVersions) {
						ItemDTO primarySystem = ItemDAO.getItemsByRelationshipAndType(primaryVersion.getId(), typeSystem).get(0);
						if (primarySystem.getId() == system.getId()) {
							primaryHasSystem = true;
							// Move all editions to primaryVersion and mark as merged
							List<ItemDTO> editions = ItemDAO.getChildren(version.getId());
							for (ItemDTO edition : editions) {
								edition.setParentId(primaryVersion.getId());
								ItemDAO.update(edition);
							}
							version.setStatus("mezclado");
							ItemDAO.update(version);
						}
					}
					// Move version to primaryGame
					if (!primaryHasSystem) {
						version.setParentId(primaryGame.getId());
						ItemDAO.update(version);
						primaryVersions = ItemDAO.getChildren(primaryGame.getId());
					}					
				}
			}
		}
	}
	
	public static ItemDTO getVersionSystem(long versionId) {
		ItemDTO system = null;
		List<ItemDTO> systems = ItemDAO.getItemsByRelationshipAndType(versionId, References.get("Tipos", "Plataformas"));
		if (systems != null && systems.size() > 0) {
			system = systems.get(0);
		}
		return system;
	}
}

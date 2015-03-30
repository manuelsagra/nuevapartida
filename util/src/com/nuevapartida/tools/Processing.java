package com.nuevapartida.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nuevapartida.images.Covers;
import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dao.TagDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.TagDTO;
import com.nuevapartida.parser.References;
import com.nuevapartida.utils.Config;
import com.nuevapartida.utils.DBUtils;
import com.nuevapartida.utils.Utils;

public class Processing {
	private static Logger logger = Logger.getLogger(Processing.class);
	
	private static enum ItemType {GAME, VERSION, EDITION};
	
	public static void reprocessGames() {
		// Multithread processing of games
		ExecutorService executor = Executors.newFixedThreadPool(Config.THREADS);

		for (final ItemDTO game : ItemDAO.getElementsByType(References.get("Tipos", "Juegos"))) {
			executor.execute(new Runnable() {
	            public void run() {
	            	checkGame(game);
	            }
	        });
		}
		
		// Wait for ExecutorService to complete
		executor.shutdown();
		try {
			executor.awaitTermination(7, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			logger.error("Timeout al procesar juegos");
		}
	}
	
	public static void checkGame(ItemDTO game) {	
		long typeSystem = References.get("Tipos", "Plataformas");
		long typeGenre = References.get("Tipos", "Géneros");
		long typeRegion = References.get("Tipos", "Regiones");
		long typeDeveloper = References.get("Tipos", "Desarrollado por");
		long typePublisher = References.get("Tipos", "Publicado por");
		
		List<String> dateList = new ArrayList<String>();
		List<ItemDTO> systemList = new ArrayList<ItemDTO>();
		List<ItemDTO> publisherList = new ArrayList<ItemDTO>();
		List<ItemDTO> developerList = new ArrayList<ItemDTO>();
		List<ItemDTO> genreList = new ArrayList<ItemDTO>();
		List<ItemDTO> regionList = new ArrayList<ItemDTO>();
		List<TagDTO> tagList = new ArrayList<TagDTO>();

		for (ItemDTO version : ItemDAO.getChildren(game.getId())) {
			if (version.getStatus().equals("publicado")) {
				ItemDTO system = DBUtils.getVersionSystem(version.getId());				
				if (system != null) {					
					logger.info("--- " + version.getName() + " (" + system.getName() + ")");
					
					List<String> datesEditions = new ArrayList<String>();
					List<ItemDTO> publishersEditions = new ArrayList<ItemDTO>();
					List<ItemDTO> regionsEditions = new ArrayList<ItemDTO>();
					List<TagDTO> tagsEdition = new ArrayList<TagDTO>();
					
					List<ItemDTO> editions = ItemDAO.getChildren(version.getId());
					for (ItemDTO edition : editions) {
						if (edition.getStatus().equals("publicado")) {						
							// Date
							if (edition.getDate() != null && !edition.getDate().equals("0000-00-00")) {
								datesEditions.add(edition.getDate());
							}
							
							// System, Genres and Developers
							DBUtils.syncronizeVersionEditionRelationships(version.getId(), edition.getId(), typeSystem);
							DBUtils.syncronizeVersionEditionRelationships(version.getId(), edition.getId(), typeGenre);
							DBUtils.syncronizeVersionEditionRelationships(version.getId(), edition.getId(), typeDeveloper);
							
							// Publishers
							publishersEditions.addAll(ItemDAO.getItemsByRelationshipAndType(edition.getId(), typePublisher));
							
							// Regions
							regionsEditions.addAll(ItemDAO.getItemsByRelationshipAndType(edition.getId(), typeRegion));
							
							// Tags
							tagsEdition.addAll(TagDAO.getItemTags(edition.getId()));
							
							// Cover
							checkCover(edition, ItemType.EDITION);
						}
					}
					
					// Date
					Collections.sort(datesEditions);
					if (datesEditions.size() > 0) {
						version.setDate(datesEditions.get(0));
						ItemDAO.update(version);
					}
					dateList.addAll(datesEditions);
					
					// System
					systemList.add(system);
					
					// Genres
					genreList.addAll(ItemDAO.getItemsByRelationshipAndType(version.getId(), typeGenre));
					
					// Regions
					DBUtils.syncronizeRelationships(version.getId(), typeRegion, regionsEditions, ItemDAO.getItemsByRelationshipAndType(version.getId(), typeRegion));
					regionList.addAll(regionsEditions);
					
					// Developers
					developerList.addAll(ItemDAO.getItemsByRelationshipAndType(version.getId(), typeDeveloper));
					
					// Publishers
					DBUtils.syncronizeRelationships(version.getId(), typePublisher, publishersEditions, ItemDAO.getItemsByRelationshipAndType(version.getId(), typePublisher));
					publisherList.addAll(publishersEditions);
					
					// Tags
					DBUtils.synchronizeTags(version.getId(), tagsEdition, TagDAO.getItemTags(version.getId()));
					tagList.addAll(tagsEdition);
					
					// Cover
					checkCover(version, ItemType.VERSION);
				} else {
					logger.error("Error al recuperar sistema de " + version.getName());
				}
			}
		}
		
		// Date
		Collections.sort(dateList);
		if (dateList.size() > 0) {
			game.setDate(dateList.get(0));
			ItemDAO.update(game);
		}
		
		// Systems
		DBUtils.syncronizeRelationships(game.getId(), typeSystem, systemList, ItemDAO.getItemsByRelationshipAndType(game.getId(), typeSystem));
		
		// Genres
		DBUtils.syncronizeRelationships(game.getId(), typeGenre, genreList, ItemDAO.getItemsByRelationshipAndType(game.getId(), typeGenre));

		// Regions
		DBUtils.syncronizeRelationships(game.getId(), typeRegion, regionList, ItemDAO.getItemsByRelationshipAndType(game.getId(), typeRegion));
		
		// Developers
		DBUtils.syncronizeRelationships(game.getId(), typeDeveloper, developerList, ItemDAO.getItemsByRelationshipAndType(game.getId(), typeDeveloper));
		
		// Publishers
		DBUtils.syncronizeRelationships(game.getId(), typePublisher, publisherList, ItemDAO.getItemsByRelationshipAndType(game.getId(), typePublisher));
			
		// Tags
		DBUtils.synchronizeTags(game.getId(), tagList, TagDAO.getItemTags(game.getId()));
		
		// Cover
		checkCover(game, ItemType.GAME);
	}

	private static void checkCover(ItemDTO item, ItemType type) {
		if (!checkImageCover(item)) {
			switch (type) {
			case GAME:
				Covers.findGameCover(item);
				break;
				
			case VERSION:
				// TODO: Order by region / platform...
				List<ItemDTO> children = ItemDAO.getChildren(item.getId());
				for (ItemDTO child : children) {
					if (!checkImageCover(item)) {
						copyImageCovers(child, item);
						return;
					}
				}
				break;
				
			case EDITION:
				Covers.findEditionCover(item);
				break;
			}			
		}
	}
	
	private static void copyImageCovers(ItemDTO source, ItemDTO destiny) {
		File sourceDir = Utils.getDir(Utils.getHtdocsPathBase(), Utils.getItemDir(source.getId()));
		File destinyDir = Utils.getDir(Utils.getHtdocsPathBase(), Utils.getItemDir(destiny.getId()));
		
		for (File img : sourceDir.listFiles(new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("item") && name.endsWith(".jpg")) {
					return true;
				}
				return false;
			}
		})) {
			try {
				Files.copy(img.toPath(), new File(destinyDir, img.getName()).toPath());
			} catch (IOException e) {
			}
		}
	}

	private static boolean checkImageCover(ItemDTO item) {
		File itemDir = Utils.getDir(Utils.getHtdocsPathBase(), Utils.getItemDir(item.getId()));
		File itemImg = new File(itemDir, "item64.jpg");
		return itemImg.exists();
	}
}

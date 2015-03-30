package com.nuevapartida.images;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.dao.MetadataDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.MetadataDTO;
import com.nuevapartida.parser.References;
import com.nuevapartida.utils.Utils;

public class Covers {
	private static Logger logger = Logger.getLogger(Covers.class);
	
	public static void findGameCover(ItemDTO game) {
		String coversPath = Utils.getCoverPathBase();
		String gameName = Utils.fixFileName(game.getName());
		
		File subDir = new File(coversPath + File.separator + "Juegos");
		if (subDir.exists()) {
			for (File file : subDir.listFiles()) {
				String fileName = Utils.fixFileName(file.getName());
				fileName = fileName.substring(0, fileName.length() - 3);
				if (fileName.equals(gameName)) {
					processItemImage(game.getId(), file);
				}
			}
		}
	}
	
	public static void findEditionCover(ItemDTO edition) {
		String coversPath = Utils.getCoverPathBase();
		
		MetadataDTO mdto = MetadataDAO.getElementByItemIdAndType(edition.getId(), References.get("Tipos", "Catálogo"));
		
		// Find by Sony ID
		if (mdto != null) {
			File[] subDirs = (new File(coversPath)).listFiles();		
			
			List<File> candidates = new ArrayList<File>();
			
			for (File subDir : subDirs) {
				if (subDir.isDirectory()) {
					for (File file : subDir.listFiles()) {
						if (file.getName().indexOf(mdto.getValue() + ".") != -1) {
							candidates.add(file);
						}
					}
				}
			}
			
			if (candidates.size() > 0) {
				// Sort by size
				Collections.sort(candidates, new Comparator<File>() {
					@Override
					public int compare(File f1, File f2) {
						if (f1.getTotalSpace() > f2.getTotalSpace()) {
							return 1;
						} else if (f1.getTotalSpace() > f2.getTotalSpace()) {
							return -1;
						} else {
							return 0;
						}
					}
				});
	
				processItemImage(edition.getId(), candidates.get(0));
			}
		}
	}
	
	public static void processItemImage(long itemId, File image) {
		int sizes[] = {64, 256, 512, 1024};
		File itemDir = Utils.getDir(Utils.getHtdocsPathBase(), Utils.getItemDir(itemId));
		logger.info("Convirtiendo " + image.getAbsolutePath() + "...");
		
		for (int i = 0; i < sizes.length; i++) {
			try {
				ImageConversion.resizeImage(image, itemDir, "item", sizes[i]);
			} catch (Exception e) {
				logger.error("Error procesando imagen " + image.getAbsolutePath());
			}
		}
	}
}

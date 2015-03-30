package com.nuevapartida.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.TagDTO;

public class Utils {
	private static Logger logger = Logger.getLogger(Utils.class);
	
	static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	static final Pattern WHITESPACE = Pattern.compile("[\\s]");
	
	public static List<String> removeDuplicates(List<String> list) {
		ArrayList<String> result = new ArrayList<String>();
		HashSet<String> set = new HashSet<String>();

		for (String item : list) {
			if (!set.contains(item)) {
				result.add(item);
				set.add(item);
			}
		}
		
		return result;
	}
	
	public static List<ItemDTO> removeDuplicatedItems(List<ItemDTO> list) {
		ArrayList<ItemDTO> result = new ArrayList<ItemDTO>();
		HashSet<Long> set = new HashSet<Long>();

		for (ItemDTO item : list) {
			if (!set.contains(item.getId())) {
				result.add(item);
				set.add(item.getId());
			}
		}
		
		return result;
	}
	
	public static List<TagDTO> removeDuplicatedTags(List<TagDTO> list) {
		ArrayList<TagDTO> result = new ArrayList<TagDTO>();
		HashSet<Long> set = new HashSet<Long>();

		for (TagDTO item : list) {
			if (!set.contains(item.getId())) {
				result.add(item);
				set.add(item.getId());
			}
		}
		
		return result;
	}
	
	public static String getCoverPathBase() {
		return Config.COVER_PATH_HOME;
	}
	
	public static Path getCoverPath(String coverPath, String fileName) {
		return Paths.get(getCoverPathBase() + coverPath + File.separator + fileName);
	}
	
	public static Path getScreenshotsPath(String screenshotPath, String fileName) {
		return Paths.get(Config.SCREENS_PATH_HOME + screenshotPath + File.separator + fileName);
	}
	
	public static String getHtdocsPathBase() {
		return Config.HTDOCS_PATH_HOME;
	}
	
	public static String fixFileName(String name) {
		return NONLATIN.matcher(name).replaceAll("");
	}
	
	public static String getSlug(String name) {
		String nowhitespace = WHITESPACE.matcher(name).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH).substring(0, slug.length() > Config.SLUG_LENGTH ? Config.SLUG_LENGTH : slug.length());
	}
	
	public static HashMap<String, String> loadHashmap(String fileName) {
		HashMap<String, String> hm = new HashMap<String, String>();
		List<String> lines = null;
		try {
			lines = Files.readAllLines(FileSystems.getDefault().getPath("res", fileName), Charset.forName("UTF-8"));
			for(String line : lines) {
				String[] aux = line.split(",");
				if (aux.length == 2) {
					hm.put(aux[0].trim(), aux[1].trim());
				} else {
					logger.error("Error cargando hashmap: " + line);
					System.exit(-1);
				}
			}
		} catch (Exception e) {
			logger.error("Error leyendo hashmap");
		}
		return hm;
	}
	
	public static List<String> loadLines(String fileName) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(FileSystems.getDefault().getPath("res", fileName), Charset.forName("UTF-8"));
		} catch (Exception e) {
			logger.error("Error leyendo líneas");
		}
		return lines;
	}
	
	public static String join(List<String> list, String separator) {
		String ret = null;

		if (list != null) {
			ret = "";
			Iterator<String> i = list.iterator();
			while (i.hasNext()) {
				String str = i.next();
				if (str != null && !str.equals("")) {
					ret += str + separator;
				}
			}
			if (ret.length() >= separator.length()) {
				ret = ret.substring(0, ret.length() - separator.length())
						.trim();
			}
		}

		return ret;
	}
	
	public static String leftPad(String str, char c, int size) {
		while (str.length() < size) {
			str = c + str;
		}
		return str;
	}
	
	public static String getItemDir(long i) {
		String id = i + "";
		String r = "items/";
		ArrayList<String> p = new ArrayList<String>();
		while (id.length() > 3) {
			p.add(id.substring(id.length() - 3));
			id = id.substring(0, id.length() - 3);
		}
		p.add(id);
		Collections.reverse(p);
		Iterator<String> pi = p.iterator();
		while (pi.hasNext()) {
			r += pi.next() + "/";
		}
		return r;
	}
	
	public static File getDir(String base, String path) {
		File f = new File(base);
		String[] dirs = path.split("/");
		for (int i = 0; i < dirs.length; i++) {
			f = new File(f, dirs[i]);
			if (!f.isDirectory()) {
				f.mkdir();
			}
		}
		return f;
	}
	
	public static List<Long> getItemIds(List<ItemDTO> items) {
		List<Long> ids = new ArrayList<Long>();
		for (ItemDTO item : items) {
			ids.add(item.getId());
		}
		return ids;
	}
	
	public static List<String> getItemNames(List<ItemDTO> items) {
		List<String> names = new ArrayList<String>();
		for (ItemDTO item : items) {
			names.add(item.getName());
		}
		return names;
	}
	
	public static List<String> getTagNames(List<TagDTO> tags) {
		List<String> names = new ArrayList<String>();
		for (TagDTO tag : tags) {
			names.add(tag.getName());
		}
		return names;
	}
	
	public static List<Long> getTagIds(List<TagDTO> tags) {
		List<Long> ids = new ArrayList<Long>();
		for (TagDTO item : tags) {
			ids.add(item.getId());
		}
		return ids;
	}
	
	public static boolean idListContainsId(List<Long> idList, long id) {
		for (long i : idList) {
			if (i == id) {
				return true;
			}
		}
		return false;
	}

	public static String fixDate(String date) {
		if (date == null || date.trim().equals("")) {
			return "0000-00-00";
		}
		
		String[] aux = date.split("-");
		String yyyy = "0000";
		String mm = "00";
		String dd = "00";
		
		if (aux.length > 0) {
			yyyy = leftPad(aux[0], '0', 4);
			if (aux.length > 1) {
				mm = leftPad(aux[1], '0', 2);
				if (aux.length > 2) {
					dd = leftPad(aux[2], '0', 2);
				}
			}
		}
		
		return yyyy + "-" + mm + "-" + dd;
	}
}

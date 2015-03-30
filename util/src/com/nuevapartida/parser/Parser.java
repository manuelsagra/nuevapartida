package com.nuevapartida.parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.nuevapartida.beans.SimpleObject;
import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dao.TagDAO;
import com.nuevapartida.mysql.dao.TypeDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.TagDTO;
import com.nuevapartida.mysql.dto.TypeDTO;
import com.nuevapartida.utils.Utils;

public class Parser {
	private static Logger logger = Logger.getLogger(Parser.class);
	
	public static void insertTypes(String file) throws Exception {
		logger.info("Insertando Tipos... ");		
		logger.info(insertTypes(parseObjects(file), "Tipos", 0) + " registros nuevos");
	}
	
	private static int insertTypes(List<SimpleObject> objects, String type, long parent) {
		int inserts = 0;
		
		if (objects != null) {
			Iterator<SimpleObject> i = objects.iterator();
			while (i.hasNext()) {
				SimpleObject o = i.next();
				
				TypeDTO t = TypeDAO.getElementByName(o.getName());
				long id = 0;
				
				if (t == null) {	
					t = new TypeDTO();
					t.setName(o.getName());
					t.setShortname(Utils.getSlug(o.getName()));
					t.setTemplate(o.getCode());
					t.setParentId(parent);
					t.setStatus("publicado");
	
					id = TypeDAO.insert(t);	
					
					inserts++;
				} else {
					id = t.getId();
				}
				
				References.put(type, o.getName(), id);

				inserts += insertTypes(o.getChildren(), type, id);
			}
		}
		
		return inserts;
	}
	
	public static void insertObjects(String file, String type)
			throws Exception {
		logger.info("Insertando " + type + "... ");
		logger.info(insertObjects(parseObjects(file), type, References.get("Tipos", type), 0) + " registros nuevos o actualizados");
	}
	
	private static int insertObjects(List<SimpleObject> objects, String type, long typeId, long parent) throws Exception {
		int inserts = 0;
		
		if (objects != null) {
			Iterator<SimpleObject> i = objects.iterator();
			while (i.hasNext()) {
				SimpleObject o = i.next();
				
				ItemDTO item = ItemDAO.getElementByNameAndType(o.getName(), typeId);
				long id = 0;
				
				if (item == null) {
					item = new ItemDTO();
					item.setParentId(parent);
					item.setName(o.getName());
					item.setShortname(Utils.getSlug(o.getName()));
					item.setTypeId(typeId);
					item.setDate("0000-00-00");
					item.setStatus("publicado");
	
					id = ItemDAO.insert(item);
					
					inserts++;
				} else {
					id = item.getId();
					if (item.getParentId() != parent) {
						item.setParentId(parent);
						ItemDAO.update(item);
						
						inserts++;
					}
				}
				
				References.put(type, o.getName(), id);

				inserts += insertObjects(o.getChildren(), type, typeId, id);
			}
		}
		
		return inserts;
	}
	
	private static List<SimpleObject> parseObjects(String file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis, "UTF8"));

		String line;
		ArrayList<String> lines = null;
		ArrayList<SimpleObject> objects = new ArrayList<SimpleObject>();
		SimpleObject object = null;

		while ((line = br.readLine()) != null) {
			if (line.charAt(0) != '\t') {
				object = SimpleObjectParser.parse(lines);
				if (object != null) {
					objects.add(object);
				}
				lines = new ArrayList<String>();
			}
			lines.add(line);
		}
		fis.close();

		// Last one
		object = SimpleObjectParser.parse(lines);
		if (object != null) {
			objects.add(object);
		}

		return objects;
	}
	
	public static void insertTags(String file) throws Exception {
		logger.info("Insertando Tags... ");
		List<SimpleObject> objects = parseObjects(file);
		Iterator<SimpleObject> i = objects.iterator();
		int inserts = 0;
		while (i.hasNext()) {
			SimpleObject tagObj = i.next();
			String name = tagObj.getName();
			long type = References.get("Tipos", tagObj.getCode());
			TagDTO tag = TagDAO.getElementByName(name);
			
			if (tag == null) {
				tag = new TagDTO();
				tag.setName(name);
				tag.setShortname(Utils.getSlug(name));
				tag.setId(TagDAO.insert(tag));
				tag.setType_id(type);
				inserts++;
			} else {
				if (tag.getType_id() != type) {
					tag.setType_id(type);
					TagDAO.update(tag);
					
					inserts++;
				}
			}
			
			References.put("Etiquetas", tagObj.getName(), tag.getId());
		}
		logger.info(inserts + " registros nuevos o actualizados");
	}
}

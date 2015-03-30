package com.nuevapartida.parser;

import java.util.ArrayList;
import java.util.List;

import com.nuevapartida.beans.SimpleObject;

public class SimpleObjectParser {
	public static SimpleObject parse(List<String> lines) {
		if (lines != null && lines.size() > 0) {
			SimpleObject object = new SimpleObject();

			// Name
			String[] aux = lines.get(0).trim().split(",");
			String name = aux[0];
			object.setName(name);
			object.setCode(aux.length > 1 ? aux[1]: null);

			// Children
			ArrayList<SimpleObject> children = new ArrayList<SimpleObject>();
			int i = 1;
			boolean isChildren = true;
			while (i < lines.size()) {
				if (isChildren && lines.get(i).charAt(0) == '\t' && lines.get(i).charAt(1) != '\t') {
					SimpleObject child = parse(subListAndTrim(lines, i,	lines.size()));
					if (child != null) {
						children.add(child);
					}
				} else if (isChildren && lines.get(i).charAt(0) != '\t') {
					isChildren = false;
				}
				i++;
			}
			object.setChildren(children);

			return object;
		} else {
			return null;

		}
	}

	private static List<String> subListAndTrim(List<String> list, int start,
			int end) {
		ArrayList<String> res = new ArrayList<String>();
		
		if (list.get(start).charAt(0) != '\t') {
			return res;
		}
		
		while (start < end && start < list.size() && list.get(start).charAt(0) == '\t') {
			res.add(list.get(start++).substring(1));
		}

		return res;
	}
}

package com.nuevapartida.ui;

import java.awt.Component;
import java.io.File;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboCellRenderer extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = 4905540297640642993L;
	private HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	
	public ComboCellRenderer(String name, String[] options) {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        for (String option : options) {
        	File iconFile = new File("res" + File.separator + name + File.separator + option + ".png");
        	if (iconFile.exists()) {
        		icons.put(option, new ImageIcon(iconFile.getAbsolutePath()));
        	}
        }
    }
	
	@Override
	public Component getListCellRendererComponent(JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
		String label = value.toString();
		setText(label);
		setIcon(icons.get(label));
		return this;
	}
}

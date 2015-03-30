package com.nuevapartida.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dao.MetadataDAO;
import com.nuevapartida.mysql.dao.TagDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.MetadataDTO;
import com.nuevapartida.mysql.dto.TagDTO;
import com.nuevapartida.parser.References;
import com.nuevapartida.utils.DBUtils;
import com.nuevapartida.utils.NetUtils;

public abstract class GameManagerItemWindow {
	protected JFrame container;
	protected ItemDTO item;
	
	protected JTextField itemTitle, itemAltTitle;
	protected JTextArea itemContent, itemExcerpt;
	protected JPanel buttons;
	protected JButton accept, cancel;
	
	protected HashMap<String, List<Component>> components = new HashMap<String, List<Component>>();
	protected HashMap<String, List<String>> combos = new HashMap<String, List<String>>();
		
	protected static final String SEPARATOR = "_";
	
	protected static final Color ALTBG = new Color(0xc8, 0xdb, 0xef);
	protected static final Color URLBG = new Color(0xf8, 0xf6, 0xdb);
	protected static final Color BUTBG = new Color(0x23, 0x64, 0xb5);
	protected static final Color BUTFG = new Color(0xee, 0xee, 0xee);
	
	public GameManagerItemWindow(ItemDTO item) {
		this.item = item;
		
		// Title / Alt Title
		itemTitle = new JTextField("");
		setTitleAttributes(itemTitle);
		if (item != null) {
			itemTitle.setText(item.getName());
		}
		
		itemAltTitle = new JTextField();
		itemAltTitle.setBackground(ALTBG);
		if (item != null) {
			itemAltTitle.setText(item.getAltname());
		}
		
		// Content / Excerpt
		itemContent = new JTextArea();
		if (item != null) {
			itemContent.setText(item.getContent());
		}
		itemContent.setToolTipText("Contenido");
		itemContent.setPreferredSize(new Dimension(300, 150));
		
		itemExcerpt = new JTextArea();
		if (item != null) {
			itemExcerpt.setText(item.getExcerpt());
		}
		itemExcerpt.setToolTipText("Resumen");
		itemExcerpt.setPreferredSize(new Dimension(300, 60));
		itemExcerpt.setBackground(ALTBG);
		
		// Buttons
		buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));		
		
		accept = new JButton("Aceptar");
		buttons.add(accept);
		
		cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close Window
				container.setVisible(false);
				container.dispose();
			}			
		});
		buttons.add(cancel);
	}
	
	// Special attributes
	protected void setButtonAttributes(JButton button) {
		button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBackground(BUTBG);
		button.setForeground(BUTFG);
		button.setContentAreaFilled(false);
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	
	protected void setTitleAttributes(JTextField title) {
		title.setBorder(BorderFactory.createCompoundBorder(
				title.getBorder(), 
		        BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		Font titleFont = title.getFont();
		title.setFont(new Font(titleFont.getName(), Font.BOLD, (int) (titleFont.getSize() * 1.5)));
	}
	
	
	// Component management
	protected void addComponent(Component c) {
		if (components.get(c.getName()) == null) {
			components.put(c.getName(), new ArrayList<Component>());	
		}
		List<Component> l = components.get(c.getName());
		l.add(c);
		components.put(c.getName(), l);		
	}
	
	protected void removeComponent(Component c) {
		if (c != null) {
			List<Component> l = components.get(c.getName());
			if (l != null) {
				l.remove(c);
				components.put(c.getName(), l);
			}
		}
	}
	
	protected void removeComponents(String keyStart) {
		Set<String> keys = components.keySet();
		for (String key : keys) {
			if (key.startsWith(keyStart)) {
				components.put(key, null);
			}
		}		
	}	
	protected List<Component> getComponents(String name) {
		return components.get(name);
	}
	
	protected List<Component> getComponentsByEndName(String end) {
		return getComponentsByStartAndEndName("", end);
	}
	
	protected List<Component> getComponentsByStartAndEndName(String start, String end) {
		List<Component> cs = new ArrayList<Component>();
		Set<String> keys = components.keySet();
		for (String key : keys) {
			if (key.startsWith(start) && key.endsWith(end)) {
				List<Component> c = components.get(key);
				if (c != null) {
					cs.addAll(c);
				}
			}
		}
		return cs;
	}	
	
	// Swing components
	protected JTextField createTextField(ItemDTO item, String name, String type, int copyEditionNumber) {
		JTextField tf = new JTextField(type);
		if (item != null) {
			MetadataDTO value = MetadataDAO.getElementByItemIdAndType(item.getId(), References.get("Tipos", type));
			if (value != null) {
				tf.setText(value.getValue());
			}
		} else if (copyEditionNumber != -1) {
			String[] aux = name.split("=");
			tf.setText(getTextFieldValue(aux[0] + "=" + copyEditionNumber + SEPARATOR + type, ""));
		}
		tf.setName(name + SEPARATOR + type);
		addComponent(tf);
		return tf;
	}
	
	protected String getTextFieldValue(String name, String placeHolder) {
		String res = "";
		
		List<Component> list = getComponents(name);
		if (list != null && list.size() > 0) {
			res = ((JTextField) list.get(0)).getText().trim();
			if (placeHolder != null && res.equals(placeHolder)) {
				res = "";
			}
		}		
		
		return res;
	}	
	
	protected JPanel createURL(String system, String type, ItemDTO item, int editionNumber) {
		JPanel urlPanel = new JPanel();
		urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.LINE_AXIS));

		JButton search = new JButton();
		search.setIcon(new ImageIcon("res/icons/search.png"));
		search.setName(system + "," + type);
		search.setToolTipText("Buscar");
		search.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		search.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				String[] aux = b.getName().split(",");
				String url = NetUtils.getWebsiteLink(aux[0], itemTitle.getText(), aux[1], "");
				
				if (url != null) {
					JPanel p = (JPanel) b.getParent();
					Component[] cs = p.getComponents();
					((JTextField) cs[cs.length - 1]).setText(url);
				}
			}
		});
		urlPanel.add(search);
		
		
		JButton get = new JButton("");
		get.setIcon(new ImageIcon("res/icons/link.png"));
		get.setToolTipText("Visitar");
		get.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		get.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				JPanel p = (JPanel) b.getParent();
				Component[] cs = p.getComponents();
				JTextField tf = (JTextField) cs[cs.length - 1];
				if (!tf.getText().equals("")) {
					NetUtils.openBrowser(tf.getText());
				}
			}
		});
		urlPanel.add(get);
		
		JTextField url = new JTextField();
		url.setBackground(URLBG);
		url.setToolTipText(type);
		url.setName(system + (editionNumber != -1 ? "=" + editionNumber : "") + SEPARATOR + type);
		addComponent(url);
		if (item != null) {
			MetadataDTO value = MetadataDAO.getElementByItemIdAndType(item.getId(), References.get("Tipos", type));
			if (value != null) {
				url.setText(value.getValue());
			}
		}
		urlPanel.add(url);
		
		return urlPanel;
	}
	
	protected JPanel createCombo(String name, String comboName, List<String> comboValues, String value) {
		JPanel comboPanel = new JPanel(new BorderLayout());
		JButton remove = new JButton("-");
		remove.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				JPanel p = (JPanel) b.getParent();
				Component[] c = p.getComponents();
				removeComponent(c[1]);
				JPanel devPanel = (JPanel) p.getParent();
				devPanel.remove(p);
				devPanel.revalidate();
			}
		});
		comboPanel.add(remove, BorderLayout.WEST);
		
		String[] options = comboValues.toArray(new String[comboValues.size()]);
		JComboBox<String> combo = new JComboBox<String>(options);
		combo.setRenderer(new ComboCellRenderer(comboName, options));
		combo.setName(name);
		addComponent(combo);
		if (value != null) {
			combo.setSelectedItem(value);
		}
		comboPanel.add(combo, BorderLayout.CENTER);
		
		return comboPanel;
	}	
	
	protected JButton createComboButton(String label, String baseName, final String comboName) {
		JButton comboButton = new JButton(label);
		setButtonAttributes(comboButton);
		comboButton.setName(baseName + SEPARATOR + comboName);
		comboButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				String name = b.getName();
				String[] aux = b.getName().split(SEPARATOR);
				JPanel p = (JPanel) getComponents(name + "Panel").get(0);
				p.add(createCombo(name, comboName, combos.get(aux[1]), "Japón"));
				p.revalidate();
			}
		});
		return comboButton;
	}
	
	@SuppressWarnings("unchecked")
	protected JPanel createComboPanel(String baseName, String comboName, ItemDTO item, String type, boolean defaultCombo, int copyEditionNumber) {
		JPanel comboPanel = new JPanel(new GridLayout(0, 1));
		String name = baseName + SEPARATOR + comboName;
		comboPanel.setName(name + "Panel");				
		if (item != null) {
			List<ItemDTO> relationships = ItemDAO.getItemsByRelationshipAndType(item.getId(), References.get("Tipos", type));
			for (ItemDTO rel : relationships) {
				comboPanel.add(createCombo(name, comboName, combos.get(comboName), rel.getName()));
			}
		} else if (copyEditionNumber != -1) {
			String[] aux = baseName.split("=");
			String copyName = aux[0] + "=" + copyEditionNumber + SEPARATOR + comboName;
			List<Component> selectedCombos = getComponents(copyName);
			if (selectedCombos != null) {
				for (Component combo : selectedCombos) {
					comboPanel.add(createCombo(name, comboName, combos.get(comboName), ((JComboBox<String>) combo).getSelectedItem().toString()));
				}
			}
		} else if (defaultCombo) {
			comboPanel.add(createCombo(name, comboName, combos.get(comboName), null));
		}
		
		addComponent(comboPanel);
		return comboPanel;
	}
	
	// Database insertion
	@SuppressWarnings("unchecked")
	protected void synchronizeCombos(long itemId, long typeRelationship, long typeItem, String comboName) {
		List<Component> combos = getComponents(comboName);
		List<ItemDTO> selectedItems = new ArrayList<ItemDTO>();
		if (combos != null) {
			for (Component combo : combos) {
				JComboBox<String> itemCombo = (JComboBox<String>) combo;
				selectedItems.add(ItemDAO.getElementByNameAndType(itemCombo.getSelectedItem().toString(), typeItem));
			}
		}
		DBUtils.syncronizeRelationships(itemId, typeRelationship, selectedItems, ItemDAO.getItemsByRelationshipAndType(itemId, typeRelationship));
	}	

	@SuppressWarnings("unchecked")
	protected void synchronizeTags(long itemId, String comboName) {
		List<Component> combos = getComponents(comboName);
		List<TagDTO> selectedTags = new ArrayList<TagDTO>();
		if (combos != null) {
			for (Component combo : combos) {
				JComboBox<String> comboTag = (JComboBox<String>) combo;
				String tagName = (String) comboTag.getSelectedItem();
				selectedTags.add(TagDAO.getElementByName(tagName));
			}
		}
		DBUtils.synchronizeTags(itemId, selectedTags, TagDAO.getItemTags(itemId));
	}	
	
	protected void synchronizeMetadata(long itemId, String name, String placeHolder, String metadataType) {
		long typeId = References.get("Tipos", metadataType);
		
		String value = getTextFieldValue(name, placeHolder);
		MetadataDTO metadata = MetadataDAO.getElementByItemIdAndType(itemId, typeId);
		if (metadata != null) {
			if (metadata.equals("")) {
				MetadataDAO.delete(metadata.getId());
			} else if (!value.equals(metadata.getValue())) {
				metadata.setValue(value);
				MetadataDAO.update(metadata);
			}
		} else if (!value.equals("")) {
			DBUtils.insertMetadata(itemId, typeId, metadataType, value);
		}
	}
}

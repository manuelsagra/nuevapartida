package com.nuevapartida.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dao.TagDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.mysql.dto.TagDTO;
import com.nuevapartida.parser.References;
import com.nuevapartida.tools.Processing;
import com.nuevapartida.utils.DBUtils;
import com.nuevapartida.utils.Utils;

public class GameManagerGameWindow extends GameManagerItemWindow {	
	private static List<ItemDTO> genres;
	private static List<ItemDTO> systems;
	
	private static List<String> regionsStr;
	private static List<String> tagsStr;
	private List<String> companiesStr;
	
	private HashMap<String, ItemDTO> versions = new HashMap<String, ItemDTO>();
	private HashMap<String, JPanel> systemPanels = new HashMap<String, JPanel>();
	
	private CheckTreeManager genreTreeManager;
	private String oldTitle;	
	private int editionNumber = 0;
	
	private static final String VERSION_TITLE = "_VersionTitle";
	private static final String VERSION_ALT_TITLE = "_TitleAlt";
	private static final String DEVELOPER = "Developer";
	private static final String PUBLISHER = "Publisher";
	private static final String EDITIONS_PANEL = "_EditionsPanel";
	private static final String EDITION_PANEL = "_EditionPanel";
	private static final String EDITION_BUTTON = "_EditionsButton";
	private static final String EDITION_ID = "_EditionId";
	private static final String EDITION_TITLE = "_EditionTitle";
	private static final String EDITION_ALT_TITLE = "_EditionTitleAlt";
	private static final String DATE = "Date";
	private static final String REGION = "Region";
	private static final String BARCODE = "Código de barras";
	private static final String SERIAL = "Catálogo";
	private static final String TAG = "Tags";
	
	public GameManagerGameWindow(ItemDTO item) {
		super(item);
		// Initialize lists
		if (genres == null) {
			genres = ItemDAO.getElementsByType(References.get("Tipos", "Géneros"));
			systems = ItemDAO.getElementsByType(References.get("Tipos", "Plataformas"));			
			regionsStr = Utils.getItemNames(ItemDAO.getElementsByType(References.get("Tipos", "Regiones")));
			tagsStr = Utils.getTagNames(TagDAO.getAllTags());
		}
		companiesStr = Utils.getItemNames(ItemDAO.getElementsByType(References.get("Tipos", "Compañías")));
		
		// Initialize combos
		combos.put(REGION, regionsStr);
		combos.put(TAG, tagsStr);
		combos.put(PUBLISHER, companiesStr);
		combos.put(DEVELOPER, companiesStr);
		
		// Create window
		String title = "Nuevo juego";		
		if (item != null) {
			title = item.getName();
			List<ItemDTO> vs = ItemDAO.getChildren(item.getId());
			for (ItemDTO version : vs) {
				versions.put(DBUtils.getVersionSystem(version.getId()).getName(), version);
			}
		}		
		container = new JFrame("Game Manager :: " + title);
		
		JPanel panel = (JPanel) container.getContentPane();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
				
		// Title && Alt
		itemTitle.addKeyListener(new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {
			}			
			@Override
			public void keyReleased(KeyEvent e) {
				String title = itemTitle.getText();
				for (Component cs : getComponentsByEndName("Title")) {
					JTextField tf = (JTextField) cs;
					if (tf.getText().equals("") || tf.getText().equals(oldTitle)) {
						tf.setText(title);
					}
				}
				oldTitle = title;
			}			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		oldTitle = itemTitle.getText();
		panel.add(itemTitle, gbc);
		gbc.gridy++;
		
		panel.add(itemAltTitle, gbc);
		gbc.gridy++;
		
		// Systems and genres
		JPanel middlePanel = new JPanel(new GridBagLayout());
		JPanel systemsPanel = new JPanel(new GridLayout(0, systems.size()));
		for (ItemDTO system : systems) {
			systemsPanel.add(createSystemPanel(system));
		}
		JScrollPane scrollPane = new JScrollPane(systemsPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(32);
		GridBagConstraints gbcs = new GridBagConstraints();
		gbcs.gridx = 0;
		gbcs.gridy = 0;
		gbcs.fill = GridBagConstraints.BOTH;
		gbcs.weightx = 0.85;
		gbcs.weighty = 1.0;		
		middlePanel.add(scrollPane, gbcs);
		gbcs.weightx = 0.15;
		gbcs.gridwidth = GridBagConstraints.REMAINDER;
		gbcs.gridx = GridBagConstraints.RELATIVE;
		middlePanel.add(createGenresPanel(), gbcs);
		
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(middlePanel, gbc);
		gbc.gridy++;
		
		// Buttons
		accept.addActionListener(new SaveData());

		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, gbc);
		
		container.pack();
		container.setVisible(true);		
		container.setFocusable(true);
		container.toFront();
		container.setLocationRelativeTo(null);
		container.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private Component createSystemPanel(ItemDTO system) {
		JPanel panel = new JPanel(new BorderLayout());
		
		ItemDTO version = versions.get(system.getName());
		
		// System checkbox
		JCheckBox cb = new JCheckBox(system.getName());
		cb.setName(system.getName());
		cb.setSelected(isVersionPublished(version));
		cb.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				JPanel panel = systemPanels.get(cb.getName());
				panel.setVisible(cb.isSelected());
			}
		});
		panel.add(cb, BorderLayout.NORTH);
		addComponent(cb);
	
		// System data
		JPanel systemPanel = new JPanel(new GridBagLayout());
		systemPanels.put(system.getName(), systemPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		// Title && Alt
		JTextField tf = new JTextField("");
		tf.setName(system.getName() + VERSION_TITLE);
		addComponent(tf);
		if (version != null) {
			tf.setText(version.getName());
		}
		systemPanel.add(tf, gbc);
		gbc.gridy++;
		
		tf = new JTextField("");
		tf.setBackground(ALTBG);
		tf.setName(system.getName() + VERSION_ALT_TITLE);
		addComponent(tf);
		if (version != null) {
			tf.setText(version.getAltname());
		}
		systemPanel.add(tf, gbc);
		gbc.gridy++;
		
		// Developers
		systemPanel.add(createComboButton("+ Desarrollado por", system.getName(), DEVELOPER), gbc);
		gbc.gridy++;
		
		systemPanel.add(createComboPanel(system.getName(), DEVELOPER, version, "Desarrollado por", true, -1), gbc);
		gbc.gridy++;
		
		// GameFAQs
		systemPanel.add(createURL(system.getName(), "GameFAQs", version, -1), gbc);
		gbc.gridy++;

		// MobyGames
		systemPanel.add(createURL(system.getName(), "MobyGames", version, -1), gbc);
		gbc.gridy++;
		
		// Editions
		JButton editionsButton = new JButton("+ Ediciones");
		setButtonAttributes(editionsButton);
		editionsButton.setName(system.getName() + EDITION_BUTTON);
		editionsButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				String[] aux = b.getName().split(SEPARATOR);
				JPanel p = (JPanel) getComponents(aux[0] + EDITIONS_PANEL).get(0);
				p.add(createEditionPanel(aux[0], null, -1));
				p.revalidate();
			}
		});
		systemPanel.add(editionsButton, gbc);
		gbc.gridy++;
		
		JPanel editionsPanel = new JPanel();
		editionsPanel.setLayout(new BoxLayout(editionsPanel, BoxLayout.PAGE_AXIS));
		editionsPanel.setName(system.getName() + EDITIONS_PANEL);				
		if (version != null) {
			List<ItemDTO> editions = ItemDAO.getChildren(version.getId());
			for (ItemDTO edition : editions) {
				if (edition.getStatus().equals("publicado")) {
					editionsPanel.add(createEditionPanel(system.getName(), edition, -1));
				}
			}
		} else {
			editionsPanel.add(createEditionPanel(system.getName(), null, -1));
		}	
		systemPanel.add(editionsPanel, gbc);
		addComponent(editionsPanel);
		gbc.gridy++;
		
		// Padding
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		systemPanel.add(new JPanel(), gbc);
		
		systemPanel.setVisible(isVersionPublished(version));
		panel.add(systemPanel);		
		return panel;
	}

	private Component createGenresPanel() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Géneros");
		
		for (DefaultMutableTreeNode genre : getGenres(0)) {
			top.add(genre);
		}
		
		JTree genreTree = new JTree(top);
		genreTreeManager = new CheckTreeManager(genreTree);
		JScrollPane scrollPane = new JScrollPane(genreTree);
		
		// Mark genres
		if (item != null) {
			List<ItemDTO> gameGenres = ItemDAO.getItemsByRelationshipAndType(item.getId(), References.get("Tipos", "Géneros"));
			List<String> gameGenresStr = new ArrayList<String>();
			for (ItemDTO genre : gameGenres) {
				gameGenresStr.add(genre.getName());
			}			
			List<TreePath> paths = new ArrayList<TreePath>();
			@SuppressWarnings("unchecked")
			Enumeration<DefaultMutableTreeNode> e = top.depthFirstEnumeration();
			while (e.hasMoreElements()) {
				DefaultMutableTreeNode node = e.nextElement();
				if (gameGenresStr.contains(node.toString())) {
					paths.add(new TreePath(node.getPath()));
				}
			}
			genreTreeManager.getSelectionModel().setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
		}
		
		return scrollPane;
	}

	private List<DefaultMutableTreeNode> getGenres(long parent) {
		List<DefaultMutableTreeNode> res = new ArrayList<DefaultMutableTreeNode>();
		for (ItemDTO genre : genres) {
			if (genre.getParentId() == parent) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(genre.getName());
				for (DefaultMutableTreeNode g : getGenres(genre.getId())) {
					node.add(g);
				}
				res.add(node);
			}
		}
		return res;
	}	
	
	private boolean isVersionPublished(ItemDTO version) {
		return version != null && version.getStatus().equals("publicado");
	}
	
	@SuppressWarnings("unchecked")
	private Component createEditionPanel(String system, ItemDTO edition, int copyEditionNumber) {
		JPanel editionPanel = new JPanel(new BorderLayout());	
		editionPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.GRAY));
		JPanel editionDataPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		
		String name = system + "=" + editionNumber;
		String nameCopy = system + "=" + copyEditionNumber;
		
		// Id
		JLabel id = new JLabel("");
		if (edition != null) {
			id.setText("" + edition.getId());
		}
		id.setName(name + EDITION_ID);
		addComponent(id);
		
		// Title & Alt
		JTextField title = new JTextField(itemTitle.getText());
		if (edition != null) {
			title.setText(edition.getName());
		} else if (copyEditionNumber != -1) {
			title.setText(getTextFieldValue(nameCopy + EDITION_TITLE, ""));
		}
		title.setName(name + EDITION_TITLE);
		addComponent(title);
		editionDataPanel.add(title, gbc);
		gbc.gridy++;
		
		JTextField alttitle = new JTextField("");
		alttitle.setBackground(ALTBG);
		if (edition != null) {
			alttitle.setText(edition.getAltname());
		} else if (copyEditionNumber != -1) {
			alttitle.setText(getTextFieldValue(nameCopy + EDITION_ALT_TITLE, ""));
		}
		alttitle.setName(name + EDITION_ALT_TITLE);
		addComponent(alttitle);
		editionDataPanel.add(alttitle, gbc);
		gbc.gridy++;
		
		// Date
		JTextField date = new JTextField("");
		if (edition != null) {
			date.setText(edition.getDate());
		} else if (copyEditionNumber != -1) {
			date.setText(getTextFieldValue(nameCopy + DATE, ""));
		}
		date.setName(name + DATE);
		addComponent(date);
		editionDataPanel.add(date, gbc);
		gbc.gridy++;
		
		// Region
		editionDataPanel.add(createComboButton("+ Región", name, REGION), gbc);
		gbc.gridy++;

		editionDataPanel.add(createComboPanel(name, REGION, edition, "Regiones", true, copyEditionNumber), gbc);
		gbc.gridy++;
		
		// Publishers
		editionDataPanel.add(createComboButton("+ Publicado por", name, PUBLISHER), gbc);
		gbc.gridy++;

		editionDataPanel.add(createComboPanel(name, PUBLISHER, edition, "Publicado por", true, copyEditionNumber), gbc);
		gbc.gridy++;
		
		// Barcode
		editionDataPanel.add(createTextField(edition, name, BARCODE, copyEditionNumber), gbc);
		gbc.gridy++;
		
		// Serial
		editionDataPanel.add(createTextField(edition, name, SERIAL, copyEditionNumber), gbc);
		gbc.gridy++;
		
		// Tags
		editionDataPanel.add(createComboButton("+ Etiquetas", name, TAG), gbc);
		gbc.gridy++;
		
		JPanel tagsPanel = new JPanel(new GridLayout(0, 1));
		tagsPanel.setName(name + SEPARATOR + TAG + "Panel");				
		if (edition != null) {
			List<TagDTO> versionTags = TagDAO.getItemTags(edition.getId());
			for (TagDTO tag : versionTags) {
				tagsPanel.add(createCombo(name + SEPARATOR + TAG, "tags", tagsStr, tag.getName()));
			}
		} else if (copyEditionNumber != -1) {
			List<Component> selectedCombos = getComponents(nameCopy + SEPARATOR + TAG);
			if (selectedCombos != null) {
				for (Component combo : selectedCombos) {
					tagsPanel.add(createCombo(name, "tags", tagsStr, ((JComboBox<String>) combo).getSelectedItem().toString()));
				}
			}
		}
		editionDataPanel.add(tagsPanel, gbc);
		addComponent(tagsPanel);
		gbc.gridy++;

		// RF Generation
		editionDataPanel.add(createURL(system, "RF Generation", edition, editionNumber), gbc);
		gbc.gridy++;
						
		// Remove
		JButton remove = new JButton("-");
		remove.setName(name);
		remove.setToolTipText("Eliminar esta edición");
		remove.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		remove.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				JPanel p = (JPanel) b.getParent().getParent();
				removeComponents(b.getName());
				JPanel editionPanel = (JPanel) p.getParent();
				editionPanel.remove(p);
				editionPanel.revalidate();
			}
		});
		
		JButton copy = new JButton("+");
		copy.setName(name);
		remove.setToolTipText("Duplicar esta edición");
		copy.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		copy.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {				
				JButton b = (JButton) e.getSource();
				JPanel p = (JPanel) b.getParent().getParent();
				JPanel editionPanel = (JPanel) p.getParent();
				String[] aux = b.getName().split("=");
				editionPanel.add(createEditionPanel(aux[0], null, Integer.parseInt(aux[1])));
				editionPanel.revalidate();
			}
		});
		JPanel buttonsPannel = new JPanel(new GridLayout(0, 1));
		buttonsPannel.add(remove);
		buttonsPannel.add(copy);
		
		editionPanel.add(buttonsPannel, BorderLayout.WEST);
		editionPanel.add(editionDataPanel, BorderLayout.CENTER);
		
		editionPanel.setName(name + EDITION_PANEL);
		addComponent(editionPanel);
		
		editionNumber++;
				
		return editionPanel;
	}
	
	class SaveData implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			long typeGame = References.get("Tipos", "Juegos");
			long typeVersion = References.get("Tipos", "Versiones");
			long typeEdition = References.get("Tipos", "Ediciones");
			long typeSystem = References.get("Tipos", "Plataformas");
			long typeGenre = References.get("Tipos", "Géneros");
			long typeRegion = References.get("Tipos", "Regiones");
			long typeCompany = References.get("Tipos", "Compañías");
			long typeDeveloper = References.get("Tipos", "Desarrollado por");
			long typePublisher = References.get("Tipos", "Publicado por");
			
			// Insert / Update game			
			boolean updating = true;
			if (item == null) {
				updating = false;
				item = new ItemDTO();
				item.setTypeId(typeGame);
				item.setStatus("publicado");
			}
			item.setName(itemTitle.getText());
			item.setShortname(Utils.getSlug(item.getName()));
			item.setAltname(itemAltTitle.getText());
			if (updating) {
				ItemDAO.update(item);
			} else {
				item.setId(ItemDAO.insert(item));
			}			
			
			// Get versions
			for (ItemDTO sys : systems) {
				String system = sys.getName();
				ItemDTO version = versions.get(system);
				JCheckBox cb = (JCheckBox) getComponents(system).get(0);
				
				if (cb.isSelected()) {					
					// Insert / Update version
					boolean existingVersion = false;
					if (version == null) {
						version = new ItemDTO();
						version.setTypeId(typeVersion);
						version.setParentId(item.getId());
						version.setStatus("publicado");
					} else {
						existingVersion = true;
						version = versions.get(system);
					}
					version.setName(getTextFieldValue(system + VERSION_TITLE, null));
					version.setShortname(Utils.getSlug(version.getName()));
					version.setAltname(getTextFieldValue(system + VERSION_ALT_TITLE, null));
					if (existingVersion) {
						ItemDAO.update(version);
					} else {
						version.setId(ItemDAO.insert(version));
						DBUtils.insertRelationship(version.getId(), References.get("Plataformas", system), typeSystem);
					}
					
					// Genres
					List<ItemDTO> selectedGenres = new ArrayList<ItemDTO>();
					TreePath[] checkedPaths = genreTreeManager.getSelectionModel().getSelectionPaths();
					for (TreePath checkedPath : checkedPaths) {
						selectedGenres.add(ItemDAO.getElementById(References.get("Géneros", checkedPath.getLastPathComponent().toString())));
					}
					DBUtils.syncronizeRelationships(version.getId(), typeGenre, selectedGenres, ItemDAO.getItemsByRelationshipAndType(version.getId(), typeGenre));
					
					// Developers
					synchronizeCombos(version.getId(), typeDeveloper, typeCompany, system + SEPARATOR + DEVELOPER);
					
					// URLs
					synchronizeMetadata(version.getId(), system + "_GameFAQs", null, "GameFAQs");
					synchronizeMetadata(version.getId(), system + "_MobyGames", null, "MobyGames");
					
					// Editions
					List<Component> editionPanels = getComponentsByStartAndEndName(system + "=", EDITION_PANEL);
					List<Long> existingEditions = new ArrayList<Long>();
					for (Component editionPanel : editionPanels) {
						if (editionPanel.isVisible()) {
							// Insert / Update version
							String name = editionPanel.getName().split(SEPARATOR)[0];
							String idName = ((JLabel) getComponents(name + EDITION_ID).get(0)).getText();
							long editionId = -1;
							if (!idName.equals("")) {
								editionId = Long.parseLong(idName);
							}
							ItemDTO edition = ItemDAO.getElementById(editionId);
							if (edition == null) {
								edition = new ItemDTO();
								edition.setTypeId(typeEdition);
								edition.setParentId(version.getId());
								edition.setStatus("publicado");
							}
							edition.setName(getTextFieldValue(name + EDITION_TITLE, null));
							edition.setShortname(Utils.getSlug(version.getName()));
							edition.setAltname(getTextFieldValue(name + EDITION_ALT_TITLE, null));
							// Date
							String date = Utils.fixDate(getTextFieldValue(name + DATE, null));
							edition.setDate(date);
							if (editionId != -1) {
								ItemDAO.update(edition);
							} else {
								edition.setId(ItemDAO.insert(edition));
							}
							existingEditions.add(edition.getId());
							
							// Publishers
							synchronizeCombos(edition.getId(), typePublisher, typeCompany, name + SEPARATOR + PUBLISHER);
							
							// Region
							synchronizeCombos(edition.getId(), typeRegion, typeRegion, name + SEPARATOR + REGION);
							
							// Barcode
							synchronizeMetadata(edition.getId(), name + SEPARATOR + BARCODE, BARCODE, BARCODE); 
							
							// Serial
							synchronizeMetadata(edition.getId(), name + SEPARATOR + SERIAL, SERIAL, SERIAL);
														
							// URLs
							synchronizeMetadata(edition.getId(), name + "_RF Generation", null, "RF Generation");
							
							// Tags
							synchronizeTags(edition.getId(), name + SEPARATOR + TAG);
						}
					}
					
					// Delete nonexistent editions
					if (existingVersion) {
						List<ItemDTO> editions = ItemDAO.getChildren(version.getId());
						for (ItemDTO edition : editions) {
							if (!existingEditions.contains(edition.getId())) {
								edition.setStatus("borrado");
								ItemDAO.update(edition);
							}
						}
					}
				} else {
					// Delete version (and editions) if exists
					if (version != null) {
						version.setStatus("borrado");
						ItemDAO.update(version);
						for (ItemDTO edition : ItemDAO.getChildren(version.getId())) {
							edition.setStatus("borrado");
							ItemDAO.update(edition);
						}
					}
				}
			}
			
			Processing.checkGame(item);
			
			container.setVisible(false);
			container.dispose();
		}
	}
}

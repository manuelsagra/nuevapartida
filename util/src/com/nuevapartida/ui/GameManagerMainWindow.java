package com.nuevapartida.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.MysqlConnection;
import com.nuevapartida.parser.Parser;
import com.nuevapartida.tools.Processing;
import com.nuevapartida.ui.GameManagerActionListener.Action;
import com.nuevapartida.utils.Config;

public class GameManagerMainWindow {
	private static Logger logger = Logger.getLogger(GameManagerMainWindow.class);
	
	private JFrame container;
	private HashMap<String, JTable> tables = new HashMap<String, JTable>();	
	
	public GameManagerMainWindow() {
		setup();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error("Look and feel not supported!");
		}
		
		container = new JFrame("Game Manager");
		JPanel panel = (JPanel) container.getContentPane();		
		JTabbedPane tabs = new JTabbedPane();
		
		// Tabs	
		tabs.addTab("Juegos", createPanel("Juegos"));
		tabs.addTab("Sagas", createPanel("Sagas"));
		tabs.addTab("Compañías", createPanel("Compañías"));
				
		panel.add(tabs, BorderLayout.CENTER);
		
		// Buttons
		JPanel buttons = new JPanel(new FlowLayout());
				
		JButton reprocessGames = new JButton("Reprocesar datos");
		reprocessGames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread() {
					public void run() {
						Processing.reprocessGames();
					}
				};
				thread.start();				
			}			
		});
		buttons.add(reprocessGames);
		
		JButton refresh = new JButton("Refrescar datos");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread() {
					public void run() {
						for (String key : tables.keySet()) {
							((GameManagerTableModel) tables.get(key).getModel()).reload();
						}
					}
				};
				thread.start();				
			}			
		});
		buttons.add(refresh);
		panel.add(buttons, BorderLayout.SOUTH);
		
		container.addWindowListener(new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
		       cleanup();
		    }
		});
		
		container.pack();
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setVisible(true);
		container.setFocusable(true);
		container.toFront();
		container.setLocationRelativeTo(null);
	}
	
	private JPanel createPanel(String type) {
		logger.info("Cargando " + type + "...");
		GameManagerTableModel model = null;
		JPanel panel = new JPanel(new GridBagLayout());
				
		// Create table
		if (type.equals("Juegos")) {
			model = new GameManagerTableModel(type);
		} else if (type.equals("Sagas")) {
			model = new GameManagerTableModel(type);
		} else if (type.equals("Compañías")) {
			model = new GameManagerTableModel(type);
		}
		JTable table = new JTable(model);
		tables.put(type, table);
		table.setRowSorter(new TableRowSorter<GameManagerTableModel>(model));
		table.setDefaultRenderer(Object.class, new GameManagerCellRenderer());
		
		// Actions		
		table.addMouseListener(new GameManagerTableMouseAdapter());
		JButton newItem = null, merge = null, reprocess = null, publish = null, delete = null;
		
		newItem = new JButton("Nuevo");
		newItem.addActionListener(new GameManagerActionListener(table, type, Action.NEW));
		newItem.setPreferredSize(new Dimension(100, 40));
		
		if (type.equals("Juegos")) {
			merge = new JButton("Mezclar");
			merge.addActionListener(new GameManagerActionListener(table, type, Action.MERGE));
			merge.setPreferredSize(new Dimension(100, 40));
			
			reprocess = new JButton("Reprocesar");
			reprocess.addActionListener(new GameManagerActionListener(table, type, Action.REPROCESS));
			reprocess.setPreferredSize(new Dimension(100, 40));
		}
		
		delete = new JButton("Borrar");
		delete.addActionListener(new GameManagerActionListener(table, type, Action.DELETE));
		delete.setPreferredSize(new Dimension(100, 40));
		
		publish = new JButton("Publicar");
		publish.addActionListener(new GameManagerActionListener(table, type, Action.PUBLISH));
		publish.setPreferredSize(new Dimension(100, 40));
		
		// Add table & buttons to panel		
		GridBagConstraints gbc =  new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		JTextField filter = new JTextField();
		filter.setName(type);
		filter.addKeyListener(new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void keyReleased(KeyEvent e) {
				JTextField tf = (JTextField) e.getComponent();
				JTable table = tables.get(tf.getName());
				RowFilter<GameManagerTableModel, Object> rf = null;
			    try {
			        rf = RowFilter.regexFilter(tf.getText(), 0);
			    } catch (java.util.regex.PatternSyntaxException ex) {
			        return;
			    }
			    ((TableRowSorter<GameManagerTableModel>) table.getRowSorter()).setRowFilter(rf);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
		panel.add(filter, gbc);
		
		JScrollPane jsp = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		jsp.getVerticalScrollBar().setUnitIncrement(128);
		panel.add(jsp, gbc);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
		buttonsPanel.add(newItem);
		if (type.equals("Juegos")) {
			buttonsPanel.add(merge);
			buttonsPanel.add(reprocess);
		}
		buttonsPanel.add(delete);
		buttonsPanel.add(publish);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 10, 10, 0);
		panel.add(buttonsPanel, gbc);
		
		return panel;
	}
	
	void setup() {
		// Database
		MysqlConnection.connect(Config.DB);
		if (Config.DB_EMPTY) {
			try {
				MysqlConnection.emptyTables();
			} catch (Exception e) {
				logger.error("Error al vaciar las tablas");
			}
		}
		updateBasicData();
	}
	
	void updateBasicData() {
		try {
			Parser.insertTypes("res/types.txt");
			Parser.insertObjects("res/genres.txt", "Géneros");
			Parser.insertObjects("res/systems.txt", "Plataformas");
			Parser.insertObjects("res/regions.txt", "Regiones");
			Parser.insertObjects("res/sagas.txt", "Sagas");
			Parser.insertTags("res/tags.txt");
		} catch (Exception e) {
			logger.info("Error al cargar los datos");
		}
	}
	
	void cleanup() {
		MysqlConnection.disconnect();
		logger.info("Terminado!");
	}
}

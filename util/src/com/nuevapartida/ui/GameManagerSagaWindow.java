package com.nuevapartida.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.parser.References;
import com.nuevapartida.utils.Utils;

public class GameManagerSagaWindow extends GameManagerItemWindow {	
	private List<String> gamesStr;
	
	private JPanel gamesPanel;
		
	private static final String GAME = "Saga-Game";
	
	public GameManagerSagaWindow(ItemDTO item) {
		super(item);
		gamesStr = Utils.getItemNames(ItemDAO.getElementsByType(References.get("Tipos", "Juegos")));
	
		String title = "Nueva saga";		
		if (item != null) {
			title = item.getName();
		}
		
		// Create window
		container = new JFrame("Game Manager :: " + title);
		
		JPanel panel = (JPanel) container.getContentPane();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// Title && Alt
		panel.add(itemTitle, gbc);
		gbc.gridy++;
		
		panel.add(itemAltTitle, gbc);
		gbc.gridy++;
		
		// Games
		JButton gameButton = new JButton("+ Juegos");
		setButtonAttributes(gameButton);
		gameButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				gamesPanel.add(getGameCombo(null));
				gamesPanel.revalidate();
			}
		});
		panel.add(gameButton, gbc);
		gbc.gridy++;
		
		gamesPanel = new JPanel(new GridLayout(0, 1));			
		if (item != null) {
			List<ItemDTO> games = ItemDAO.getItemsByRelationshipAndType(item.getId(), References.get("Tipos", "Sagas"));
			for (ItemDTO game : games) {
				gamesPanel.add(getGameCombo(game.getName()));
			}
		}
		panel.add(gamesPanel, gbc);
		gbc.gridy++;
		
		// Text
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(itemContent, gbc);
		gbc.gridy++;
		
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(itemExcerpt, gbc);
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
	}
	
	private Component getGameCombo(String gameStr) {
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
		
		JComboBox<String> combo = new JComboBox<String>(gamesStr.toArray(new String[gamesStr.size()]));
		combo.setName(GAME);
		addComponent(combo);
		if (gameStr != null) {
			combo.setSelectedItem(gameStr);
		}
		comboPanel.add(combo, BorderLayout.CENTER);
		
		return comboPanel;
	}
	
	class SaveData implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			long typeSaga = References.get("Tipos", "Sagas");
			long typeGame = References.get("Tipos", "Juegos");
			
			// Insert / Update saga			
			boolean updating = true;
			if (item == null) {
				updating = false;
				item = new ItemDTO();
				item.setTypeId(typeSaga);
				item.setStatus("publicado");
			}
			item.setName(itemTitle.getText());
			item.setShortname(Utils.getSlug(item.getName()));
			item.setAltname(itemAltTitle.getText());
			item.setContent(itemContent.getText());
			item.setExcerpt(itemExcerpt.getText());
			if (updating) {
				ItemDAO.update(item);
			} else {
				item.setId(ItemDAO.insert(item));
			}
			
			// Games
			synchronizeCombos(item.getId(), typeSaga, typeGame, GAME);
			
			container.setVisible(false);
			container.dispose();
		}
	}
}

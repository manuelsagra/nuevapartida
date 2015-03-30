package com.nuevapartida.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.parser.References;
import com.nuevapartida.utils.Utils;

public class GameManagerCompanyWindow extends GameManagerItemWindow {	
	public GameManagerCompanyWindow(ItemDTO item) {
		super(item);
		String title = "Nueva compañía";		
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
	
	class SaveData implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			long typeSaga = References.get("Tipos", "Compañías");
			
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
						
			container.setVisible(false);
			container.dispose();
		}
	}
}

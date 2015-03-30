package com.nuevapartida.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import org.apache.log4j.Logger;

public class GameManagerTableMouseAdapter extends MouseAdapter {
	private static Logger logger = Logger.getLogger(GameManagerTableMouseAdapter.class);
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			JTable table = (JTable) e.getSource();
			GameManagerTableModel model = (GameManagerTableModel) table.getModel();
			int row = table.convertRowIndexToModel(table.getSelectedRow());
			if (model.getType().equals("Juegos")) {
				new GameManagerGameWindow(model.getItem(row));
			} else if (model.getType().equals("Sagas")) {
				new GameManagerSagaWindow(model.getItem(row));
			} else if (model.getType().equals("Compañías")) {
				new GameManagerCompanyWindow(model.getItem(row));
			} else {
				logger.error("No implementado");
			}
		}
	}
}

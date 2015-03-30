package com.nuevapartida.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.tools.Processing;
import com.nuevapartida.utils.DBUtils;

public class GameManagerActionListener implements ActionListener {
	private static Logger logger = Logger.getLogger(GameManagerActionListener.class);
	
	public static enum Action {
		MERGE, DELETE, NEW, PUBLISH, REPROCESS
	};

	private JTable table;
	private GameManagerTableModel model;
	private String type;
	private Action action;

	public GameManagerActionListener(JTable table, String type, Action action) {
		this.table = table;
		model = (GameManagerTableModel) table.getModel();
		this.type = type;
		this.action = action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread thread = new Thread() {
			public void run() {
				int[] rows = null;
				switch (action) {
				case DELETE:
					rows = table.getSelectedRows();
					if (rows.length > 0) {
						for (int row : rows) {
							row = table.convertRowIndexToModel(row);
							ItemDTO game = model.getItem(row);
							if (game.getStatus().equals("publicado")) {
								game.setStatus("borrado");
								ItemDAO.update(game);
							}
							List<ItemDTO> versions = ItemDAO.getChildren(game
									.getId());
							for (ItemDTO version : versions) {
								if (version.getStatus().equals("publicado")) {
									version.setStatus("borrado");
									ItemDAO.update(version);
								}
								List<ItemDTO> editions = ItemDAO
										.getChildren(version.getId());
								for (ItemDTO edition : editions) {
									if (edition.getStatus().equals("publicado")) {
										edition.setStatus("borrado");
										ItemDAO.update(edition);
									}
								}
							}
						}
						table.clearSelection();
						model.refresh();
					}
					break;
					
				case PUBLISH:
					rows = table.getSelectedRows();
					if (rows.length > 0) {
						for (int row : rows) {
							row = table.convertRowIndexToModel(row);
							ItemDTO game = model.getItem(row);
							if (game.getStatus().equals("borrado")) {
								game.setStatus("publicado");
								ItemDAO.update(game);
							}
							List<ItemDTO> versions = ItemDAO.getChildren(game
									.getId());
							for (ItemDTO version : versions) {
								if (version.getStatus().equals("borrado")) {
									version.setStatus("publicado");
									ItemDAO.update(version);
								}
								List<ItemDTO> editions = ItemDAO
										.getChildren(version.getId());
								for (ItemDTO edition : editions) {
									if (edition.getStatus().equals("borrado")) {
										edition.setStatus("publicado");
										ItemDAO.update(edition);
									}
								}
							}
						}
						table.clearSelection();
						model.refresh();
					}
					break;
					
				case MERGE:
					rows = table.getSelectedRows();
					if (rows.length > 1) {
						List<Long> mixGamesIds = new ArrayList<Long>();
						for (int row : rows) {
							row = table.convertRowIndexToModel(row);
							mixGamesIds.add(model.getItem(row).getId());							
						}
						DBUtils.mixGames(mixGamesIds);
						table.clearSelection();
						model.refresh();
					}
					break;
					
				case REPROCESS:
					rows = table.getSelectedRows();
					if (rows.length > 0) {
						for (int row : rows) {
							row = table.convertRowIndexToModel(row);
							Processing.checkGame(model.getItem(row));
						}
						table.clearSelection();
						model.refresh();
					}
					break;
					
				case NEW:
					if (type.equals("Juegos")) {
						new GameManagerGameWindow(null);
					} else if (type.equals("Sagas")) {
						new GameManagerSagaWindow(null);
					} else if (type.equals("Compañías")) {
						new GameManagerCompanyWindow(null);
					} else {
						logger.error("Acción no implementada");
					}					
					break;
					
				default:
					logger.error("Acción no implementada");
					break;
				}

			}
		};
		thread.start();
	}

}

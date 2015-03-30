package com.nuevapartida.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.nuevapartida.mysql.dto.ItemDTO;

public class GameManagerCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -4130222817818509241L;

	private Color deleted = new Color(0xe8, 0x8b, 0x8b);
	private Color merged = new Color(0xcc, 0xbb, 0xef);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		if (!table.isRowSelected(row)) {
			GameManagerTableModel model = (GameManagerTableModel) table.getModel();
			ItemDTO item = model.getItem(table.convertRowIndexToModel(row));
			if (item != null && item.getStatus() != null) {
				if (item.getStatus().equals("borrado")) {
					c.setBackground(deleted);
				} else if (item.getStatus().equals("mezclado")) {
					c.setBackground(merged);
				} else {
					c.setBackground(Color.WHITE);
				}
			} else {
				c.setBackground(Color.WHITE);
			}
		}

		return c;
	}

}

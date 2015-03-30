package com.nuevapartida.ui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.nuevapartida.mysql.dao.ItemDAO;
import com.nuevapartida.mysql.dto.ItemDTO;
import com.nuevapartida.parser.References;

public class GameManagerTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -3673153267606192564L;
	
	private List<ItemDTO> items;
	private String type;

	public GameManagerTableModel(String type) {
		this.type = type;
		reloadItems();
	}
	
	private void reloadItems() {
		items = ItemDAO.getElementsByType(References.get("Tipos", type));
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
    public int getRowCount() {
		if (items != null) {
			return items.size();
		}
		return 0;
    }
	
	 @Override
	 public String getColumnName(int column) {
	  return "Nombre";
	 }
	
	@Override
    public int getColumnCount() {
        return 1;
    }
	
	@Override
    public Object getValueAt(int row, int column) {
		if (items != null && items.size() > row) {
			return items.get(row).getName();
		}
		return null;
    }
		
	public ItemDTO getItem(int row) {
		if (items != null && items.size() > row) {
			return items.get(row);
		}
		return null;
	}

	public String getType() {
		return type;
	}
	
	public void refresh() {	
		reloadItems();
		fireTableStructureChanged();
	}

	public void reload() {
		reloadItems();
		fireTableStructureChanged();
	}
}

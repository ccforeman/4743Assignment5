package view;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import model.InventoryItem;

public class InventoryItemListCellRenderer implements ListCellRenderer<InventoryItem> {

	
private final DefaultListCellRenderer DEFAULT_RENDERER = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends InventoryItem> list, InventoryItem value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) DEFAULT_RENDERER.getListCellRendererComponent(list, value.getPartId(), index, isSelected, cellHasFocus);
		return renderer;
	}
}

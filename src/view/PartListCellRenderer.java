package view;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import model.Part;

public class PartListCellRenderer  implements ListCellRenderer<Part> {

	
private final DefaultListCellRenderer DEFAULT_RENDERER = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Part> list, Part value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) DEFAULT_RENDERER.getListCellRendererComponent(list, value.getPartNum(), index, isSelected, cellHasFocus);
		return renderer;
	}
}

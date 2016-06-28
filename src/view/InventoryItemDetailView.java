package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import model.InventoryItem;
import model.Warehouse;

public class InventoryItemDetailView extends MDIChild implements Observer{

	
	private InventoryItem myInventoryItem;
	private Warehouse warehouse;
	
	public InventoryItemDetailView(String title, InventoryItem inventoryItem, Warehouse warehouse, MDIParent m) {
		super(title, m);
		
		myInventoryItem = inventoryItem;

		myInventoryItem.addObserver(this);
		
		this.warehouse = warehouse;
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(360, 210));
		JPanel panel = addCenterFields();
		this.add(panel, BorderLayout.CENTER);
		
		
		panel = new JPanel();
		JButton buttonToEdit = new JButton("Edit");
		buttonToEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openEditView();
			}
		});
		panel.add(buttonToEdit);
		this.add(panel, BorderLayout.SOUTH);
	}
	
	private JPanel addCenterFields() {
		JPanel panel = new JPanel();
		JLabel label;
		
		
		panel.setLayout(new GridLayout(4, 2));
		
		label = new JLabel("Id");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel("" + myInventoryItem.getId()));
		
		label = new JLabel("Warehouse Id");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myInventoryItem.getWarehouseId()));
		
		label = new JLabel("Part Id");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myInventoryItem.getPartId()));

		label = new JLabel("Quantity");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel("" + myInventoryItem.getQuantity()));
		
		return panel;
	}
	

	@Override
	protected void childClosing() {
		setInternalFrameVisible(false);
		super.childClosing();
				
		myInventoryItem.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
	}
	
	public void openEditView() {
		parent.doCommand(MenuCommands.SHOW_EDIT_INVENTORY_ITEM, this);
		childClosing();
	}
	
	public InventoryItem getInventoryItem() {
		return myInventoryItem;
	}
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	
}

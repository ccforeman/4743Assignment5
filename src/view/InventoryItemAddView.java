package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.MDIChild;
import controller.MDIParent;
import database.GatewayException;
import database.InventoryItemTableGateway;
import model.InventoryItem;
import model.InventoryItemList;
import model.Part;
import model.PartList;
import model.Warehouse;
import model.WarehouseList;

public class InventoryItemAddView extends MDIChild {
	
	private JComboBox<String> warehouseDropDown;
	private JComboBox<String> partDropDown;
	
	private InventoryItem newInventory;
	
	private MDIParent parent;
	
	private String selectedWarehouseId;
	private String selectedPartId;
	
	private Warehouse selectedWarehouse;
	private Part selectedPart;
	
	private double desiredQuantity;
	private double remainingCapacity;
	
	private JTextField q;
	private JLabel reCapLabel;
	
	
	public InventoryItemAddView(String title, WarehouseList wl, PartList pl, InventoryItemList il, Warehouse warehouseSelected, InventoryItem newInventory, MDIParent m) {
		super(title, m);
		
		this.setPreferredSize(new Dimension(300, 250));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 0, 0, 15));
		
		this.newInventory = newInventory;
		
		this.selectedWarehouse = warehouseSelected;
		
		parent = m;
		
		remainingCapacity = selectedWarehouse.getRemainingCapacity();
		reCapLabel = new JLabel("Remaining Capacity: " + remainingCapacity);
		
		warehouseDropDown = new JComboBox<String>();
		warehouseDropDown.addItem(selectedWarehouse.getWarehouseName());
//		for(Warehouse w : wl.getList()) {
//			warehouseDropDown.addItem(w.getWarehouseName());
//		}
		warehouseDropDown.addActionListener(new ActionListener() {
			// May need to look into this combo box situation if there's strange behavior
			@Override
			public void actionPerformed(ActionEvent evt) {
				JComboBox wcb = (JComboBox)evt.getSource();
//				selectedWarehouseId = (String) wcb.getSelectedItem();
//				selectedWarehouse = wl.getList().get(warehouseDropDown.getSelectedIndex());
//				remainingCapacity = wl.getList().get(warehouseDropDown.getSelectedIndex()).getRemainingCapacity();
//				reCapLabel.setText("Remaining Capacity: " + remainingCapacity);
			}
			
		});
		
		partDropDown = new JComboBox<String>();
		for(Part p : pl.getList()) {
			partDropDown.addItem(p.getPartNum());
		}
		partDropDown.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				JComboBox pcb = (JComboBox)evt.getSource();
				selectedPartId = (String) pcb.getSelectedItem();
			}
			
		});
		
		
		JLabel label = new JLabel("Quantity");
		panel.add(label);
		
		q = new JTextField();
		panel.add(q);
		
		panel.add(warehouseDropDown);
		panel.add(partDropDown);
		panel.add(reCapLabel);
		
		JButton finishButton = new JButton("Add Inventory");
		finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				addInventory();
			}
		
		});
		panel.add(finishButton);
		
		
		this.add(panel);
	
		
	}
	
	/*  See WarehouseListView for (likely) better implementation of the InventoryItemList
	 */
	
	public void addInventory() {
		selectedWarehouseId = (String) warehouseDropDown.getSelectedItem();
		selectedPartId = (String) partDropDown.getSelectedItem();
		
		try {
			desiredQuantity = Double.parseDouble(q.getText().trim());
		} catch(Exception e) {
			parent.displayChildMessage("Invalid Quantity: Cannot be empty or null! --- " + e.getMessage());
			return;
		}
		
		if(desiredQuantity > remainingCapacity) {
			parent.displayChildMessage("Desired quantity exceeds the Warehouse's capacity!");
			return;
		}
		
		try {
			newInventory.setGateway(new InventoryItemTableGateway());
			newInventory.setWarehouseId(selectedWarehouseId);
			newInventory.setPartId(selectedPartId);
			newInventory.setQuantity(desiredQuantity);
			selectedWarehouse.setRemainingCapacity(remainingCapacity);
		} catch (Exception e ) {
			parent.displayChildMessage(e.getMessage());
			return;
		}

		try {
//			try {
//				newInventory.setGateway(new InventoryItemTableGateway());
//			} catch (SQLException se) {
//				parent.displayChildMessage("Couldn't establish gateway from view: " + se.getMessage());
//				return;
//			}
//			
//			newInventory.getGateway().insertInventoryItem(newInventory);
			newInventory.finishUpdate();
			selectedWarehouse.syncWarehouse();
		} catch (GatewayException e) {
			parent.displayChildMessage("Inventory couldn't be added: " + e.getMessage());
			return;
		}
		parent.displayChildMessage("Inventory Successfully added");
		setInternalFrameVisible(false);
		childClosing();
	}
	

}

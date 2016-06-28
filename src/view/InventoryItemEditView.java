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
import javax.swing.JTextField;

import controller.MDIChild;
import controller.MDIParent;
import database.GatewayException;
import model.InventoryItem;
import model.Warehouse;

@SuppressWarnings("serial")
public class InventoryItemEditView extends MDIChild implements Observer {
	
	private MDIParent parent;
	
	private InventoryItem myInventoryItem;
	private Warehouse warehouse;

	private JLabel fldId, fldWarehouseId, fldPartId, fldRemainingCap;
	private JTextField fldQuantity /*, fldWarehouseId, fldPartId*/;
	private double startQuantity;
	
	public InventoryItemEditView(String title, InventoryItem inventoryItem, Warehouse warehouse, MDIParent m) {
		super(title, m);
		
		this.warehouse = warehouse;
		myInventoryItem = inventoryItem;
		parent = m;

		myInventoryItem.addObserver(this);
		this.warehouse.addObserver(this);
		
		startQuantity = myInventoryItem.getQuantity();
		
		JPanel panel = new JPanel(); 
		panel.setLayout(new GridLayout(7, 2));
		
		panel.add(new JLabel("Id"));
		fldId = new JLabel("" + myInventoryItem.getId());
		panel.add(fldId);
		
		panel.add(new JLabel("Warehouse"));
		fldWarehouseId = new JLabel(myInventoryItem.getWarehouseId());
		panel.add(fldWarehouseId);
		
		panel.add(new JLabel("Part"));
		fldPartId = new JLabel(myInventoryItem.getPartId());
		panel.add(fldPartId);

		panel.add(new JLabel("Quantity"));
		fldQuantity = new JTextField("" + myInventoryItem.getQuantity());
		panel.add(fldQuantity);
		
		panel.add(new JLabel("Max quantity to add is: "));
		fldRemainingCap = new JLabel("" + this.warehouse.getRemainingCapacity());
		panel.add(fldRemainingCap);

		this.add(panel);
		
		panel = new JPanel();
		this.setPreferredSize(new Dimension(350, 210));
		
		JButton button = new JButton("Save Record");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(saveInventoryItem())
					update(myInventoryItem, this);
			}
		});
		panel.add(button);

		
		this.add(panel, BorderLayout.SOUTH);

		refreshFields();
	}
	
	public void refreshFields() {
		fldId.setText("" + myInventoryItem.getId());
		fldWarehouseId.setText(myInventoryItem.getWarehouseId());
		fldPartId.setText(myInventoryItem.getPartId());
		fldQuantity.setText("" + myInventoryItem.getQuantity());
		this.setTitle("" + myInventoryItem.getId());
		fldRemainingCap.setText("" + warehouse.getRemainingCapacity());
	}

	public boolean saveInventoryItem() {
		//display any error message if field data are invalid
		
		double testQuantity = Double.parseDouble(fldQuantity.getText().trim());
		if(!myInventoryItem.validQuantity(testQuantity)) {
			parent.displayChildMessage("Invalid quantity!");
			refreshFields();
			return false;
		}
		// make sure testQuantity passes warehouse Capacity test
		if(testQuantity != startQuantity && (testQuantity - startQuantity) > warehouse.getRemainingCapacity()) {
			parent.displayChildMessage("Quantity can't be exceed Warehouse's capacity");
			return false;
		}

		setChanged(true);
		
		try {
			myInventoryItem.setQuantity(testQuantity);
		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		}
		
		try {
			myInventoryItem.finishUpdate();
			warehouse.refreshWarehouseRemainingCap();
			warehouse.syncWarehouse();
			warehouse.finishUpdate();
//			setChanged(false);
		} catch (GatewayException e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		}
		
		parent.displayChildMessage("Changes saved");
		return true;
	}

	@Override
	protected void childClosing() {
		super.childClosing();
				
		myInventoryItem.deleteObserver(this);
		warehouse.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		refreshFields();
	}


}

package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import model.Warehouse;

@SuppressWarnings("serial")
public class WarehouseEditView extends MDIChild implements Observer {

	private Warehouse myWarehouse;

	private JLabel fldId;
	private JTextField fldName, fldAddress, fldCity, fldState, fldZip;
	private JTextField fldCapacity;
	
	public WarehouseEditView(String title, Warehouse warehouse, MDIParent m) {
		super(title, m);
		
		myWarehouse = warehouse;

		myWarehouse.addObserver(this);
		
		JPanel panel = new JPanel(); 
		panel.setLayout(new GridLayout(7, 2));
		
		panel.add(new JLabel("Id"));
		fldId = new JLabel("");
		panel.add(fldId);
		
		panel.add(new JLabel("Name"));
		fldName = new JTextField("");
		panel.add(fldName);
		
		panel.add(new JLabel("Address"));
		fldAddress = new JTextField("");
		panel.add(fldAddress);

		panel.add(new JLabel("City"));
		fldCity = new JTextField("");
		panel.add(fldCity);
		
		panel.add(new JLabel("State"));
		fldState = new JTextField("");
		panel.add(fldState);
		
		panel.add(new JLabel("Zip"));
		fldZip = new JTextField("");
		panel.add(fldZip);
		
		panel.add(new JLabel("Capacity"));
		fldCapacity = new JTextField("");
		panel.add(fldCapacity);
		
		this.add(panel, BorderLayout.CENTER);
		
		panel = new JPanel();
		this.setPreferredSize(new Dimension(280, 240));
		panel.setLayout(new FlowLayout());
		
		JButton button = new JButton("Save Record");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(saveWarehouse())
					update(myWarehouse, this);
			}
		});
		panel.add(button);
		
		this.add(panel, BorderLayout.SOUTH);

		refreshFields();
	
	}
	
	public void refreshFields() {
		fldId.setText("" + myWarehouse.getId());
		fldName.setText(myWarehouse.getWarehouseName());
		fldAddress.setText(myWarehouse.getAddress());
		fldCity.setText(myWarehouse.getCity());
		fldState.setText(myWarehouse.getState());
		fldZip.setText(myWarehouse.getZip());
		fldCapacity.setText("" + Double.toString(myWarehouse.getCapacity()));
		this.setTitle(myWarehouse.getWarehouseName());
	}

	public boolean saveWarehouse() {
		//display any error message if field data are invalid
		String testName = fldName.getText().trim();
		String testAddress = fldAddress.getText().trim();
		String testCity = fldCity.getText().trim();
		String testState = fldState.getText().trim();
		String testZip = fldZip.getText().trim();
		double testCapacity;
		
		try {
			testCapacity = Double.parseDouble(fldCapacity.getText().trim());
		} catch (Exception e) {
			parent.displayChildMessage("Invalid Capacity");
			refreshFields();
			return false;
		}
		
		try {
			myWarehouse.setWarehouseName(testName);
			myWarehouse.setAddress(testAddress);
			myWarehouse.setCity(testCity);
			myWarehouse.setState(testState);
			myWarehouse.setZip(testZip);
			myWarehouse.setCapacity(testCapacity);
		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		}
		
		try {
			myWarehouse.finishUpdate();
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
				
		myWarehouse.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		refreshFields();
	}


}

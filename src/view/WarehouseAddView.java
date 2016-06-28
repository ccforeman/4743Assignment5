package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import controller.WarehouseListController;
import database.GatewayException;
import model.Warehouse;
import model.WarehouseList;

public class WarehouseAddView extends MDIChild implements Observer {

	private Warehouse newWarehouse;
	//private WarehouseListController warehouseList;
	private MDIParent parent;
	
	
	private JTextField newName, newAddress, newCity, newState, newZip, newCapacity;
	
	public WarehouseAddView(String title, Warehouse warehouse, MDIParent m) {
		super(title, m);
		newWarehouse = warehouse;
		//warehouseList = list;
		parent = m;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(7, 2));
		
		panel.add(new JLabel("<html><font color='red'>*</font>Warehouse Name: </html>"));
		newName = new JTextField("");
		panel.add(newName);
		
		panel.add(new JLabel("<html><font color='red'>*</font>Address: </html>"));
		newAddress = new JTextField("");
		panel.add(newAddress);
		
		panel.add(new JLabel("City: "));
		newCity = new JTextField("");
		panel.add(newCity);
		
		panel.add(new JLabel("State: "));
		newState = new JTextField("");
		panel.add(newState);
		
		panel.add(new JLabel("<html><font color='red'>*</font>Zip: </html>"));
		newZip = new JTextField("");
		panel.add(newZip);
		
		panel.add(new JLabel("<html><font color='red'>*</font>Capacity: </html>: "));
		newCapacity = new JTextField("");
		panel.add(newCapacity);
		
		
		JLabel star = new JLabel("<html><font color='red'>*</font> denotes required field</html>");
		star.setFont(new Font("Serif", Font.ITALIC, 10));
		panel.add(star);
		
		this.add(panel, BorderLayout.SOUTH);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton button = new JButton("Add");
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				addWarehouse();
			}		
		});
		panel.add(button);
		this.add(panel);
		this.setPreferredSize(new Dimension(360, 220));
		
	}
	
	
	public void addWarehouse() {
		String testName = newName.getText().trim();
		String testAddress = newAddress.getText().trim();
		String testCity = newCity.getText().trim();
		String testState = newState.getText().trim();
		String testZip = newZip.getText().trim();
		int testCapacity;
		
		try {
			testCapacity = Integer.parseInt(newCapacity.getText().trim());
		} catch (Exception e) {
			parent.displayChildMessage("Invalid Capacity: " + e.getMessage());
			
			return;
		}
		
		try {
			newWarehouse.setWarehouseName(testName);
			newWarehouse.setAddress(testAddress);
			newWarehouse.setCity(testCity);
			newWarehouse.setState(testState);
			newWarehouse.setZip(testZip);
			newWarehouse.setCapacity(testCapacity);
			newWarehouse.setRemainingCapacity(testCapacity);
		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());
			
			return;
		}
		
		try {
			newWarehouse.finishUpdate();
		} catch (GatewayException g) {
			parent.displayChildMessage(g.getMessage());
		}
		
		parent.displayChildMessage("Warehouse added\nWarehouse Id: " + newWarehouse.getId());
		setInternalFrameVisible(false);
		childClosing();
	}
	
	

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}

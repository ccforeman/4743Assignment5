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
import controller.MenuCommands;
import model.Warehouse;

@SuppressWarnings("serial")
public class WarehouseDetailView extends MDIChild implements Observer {

	private Warehouse myWarehouse;
	
	public WarehouseDetailView(String title, Warehouse warehouse, MDIParent m) {
		super(title, m);
		
		myWarehouse = warehouse;

		myWarehouse.addObserver(this);
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(370, 250));
		
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
		panel.setLayout(new GridLayout(7, 4, 0, 10));
		
		panel.add(new JLabel());
		panel.add(new JLabel("Id"));
		panel.add(new JLabel("" + myWarehouse.getId()));
		panel.add(new JLabel());
		
		panel.add(new JLabel());
		panel.add(new JLabel("Name"));
		panel.add(new JLabel(myWarehouse.getWarehouseName()));
		panel.add(new JLabel());
		
		panel.add(new JLabel());
		panel.add(new JLabel("Address"));
		panel.add(new JLabel(myWarehouse.getAddress()));
		panel.add(new JLabel());
		
		panel.add(new JLabel());
		panel.add(new JLabel("City"));
		panel.add(new JLabel(myWarehouse.getCity()));
		panel.add(new JLabel());
		
		panel.add(new JLabel());
		panel.add(new JLabel("State"));
		panel.add(new JLabel(myWarehouse.getState()));
		panel.add(new JLabel());
		
		panel.add(new JLabel());
		panel.add(new JLabel("Zip"));
		panel.add(new JLabel(myWarehouse.getZip()));
		panel.add(new JLabel());
		
		panel.add(new JLabel());
		panel.add(new JLabel("Capacity"));
		panel.add(new JLabel("" + myWarehouse.getCapacity()));
		panel.add(new JLabel());
		
		return panel;
	}

	@Override
	protected void childClosing() {
		setInternalFrameVisible(false);
		super.childClosing();
				
		myWarehouse.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
	}
	
	public void openEditView() {
		parent.doCommand(MenuCommands.SHOW_EDIT_WAREHOUSE, this);
		childClosing();
	}
	
	public Warehouse getSelectedWarehouse() {
		return myWarehouse;
	}

}

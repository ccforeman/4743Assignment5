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
import model.Part;

public class PartDetailView extends MDIChild implements Observer{

	
	private Part myPart;
	private MDIParent parent;
	
	public PartDetailView(String title, Part part, MDIParent m) {
		super(title, m);
		
		myPart = part;

		myPart.addObserver(this);
		
		parent = m;
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(360, 210));
		JPanel panel = addCenterFields();
		this.add(panel);
		
		
		panel = new JPanel();
		JButton buttonToEdit = new JButton("Edit");
		buttonToEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(MDIParent.partsBeingEdited.contains(myPart)) {
					parent.displayChildMessage("Part is currently being edited");
				} else {
					openEditView(); 
				}
			}
		});
		panel.add(buttonToEdit);
		this.add(panel, BorderLayout.SOUTH);
	}
	
	private JPanel addCenterFields() {
		JPanel panel = new JPanel();
		JLabel label;
		
		
		panel.setLayout(new GridLayout(6, 2, 0, 10));
		
		label = new JLabel("Id");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel("" + myPart.getId()));
		
		label = new JLabel("Part #");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myPart.getPartNum()));
		
		label = new JLabel("Part Name");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myPart.getPartName()));

		label = new JLabel("Vendor");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myPart.getVendor()));
		
		label = new JLabel("Unit");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myPart.getUnitOfQuantity()));
		
		label = new JLabel("Vendor Part #");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		panel.add(new JLabel(myPart.getVendorPartNum()));
		
		return panel;
	}
	

	@Override
	protected void childClosing() {
		setInternalFrameVisible(false);
		super.childClosing();
				
		myPart.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
	}
	
	public void openEditView() {
		parent.doCommand(MenuCommands.SHOW_EDIT_PART, this);
		childClosing();
	}
	
	public Part getSelectedPart() {
		return myPart;
	}
	
}

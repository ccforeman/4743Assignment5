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
import model.Part;

@SuppressWarnings("serial")
public class PartEditView extends MDIChild implements Observer {
	
	private MDIParent parent;
	
	private Part myPart;

	private JLabel fldId;
	private JTextField fldPartNum, fldPartName, fldVendor, fldUnitOfQuantity, fldVendorPartNum;
	
	public PartEditView(String title, Part part, MDIParent m) {
		super(title, m);
		
		myPart = part;
		parent = m;

		myPart.addObserver(this);
		
		JPanel panel = new JPanel(); 
		panel.setLayout(new GridLayout(6, 2));
		
		panel.add(new JLabel("Id"));
		fldId = new JLabel("" + myPart.getId());
		panel.add(fldId);
		
		panel.add(new JLabel("Part #"));
		fldPartNum = new JTextField(myPart.getPartNum());
		panel.add(fldPartNum);
		
		panel.add(new JLabel("Part Name"));
		fldPartName = new JTextField(myPart.getPartName());
		panel.add(fldPartName);

		panel.add(new JLabel("Vendor"));
		fldVendor = new JTextField(myPart.getVendor());
		panel.add(fldVendor);
		
		panel.add(new JLabel("Unit"));
		fldUnitOfQuantity = new JTextField(myPart.getUnitOfQuantity());
		panel.add(fldUnitOfQuantity);
		
		panel.add(new JLabel("Vendor Part #"));
		fldVendorPartNum = new JTextField(myPart.getVendorPartNum());
		panel.add(fldVendorPartNum);

		this.add(panel, BorderLayout.CENTER);
		
		panel = new JPanel();
		this.setPreferredSize(new Dimension(280, 210));
		panel.setLayout(new FlowLayout());
		
		JButton button = new JButton("Save Record");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(savePart())
					update(myPart, this);
			}
		});
		panel.add(button);

		
		this.add(panel, BorderLayout.SOUTH);

		refreshFields();
	}
	
	public void refreshFields() {
		fldId.setText("" + myPart.getId());
		fldPartNum.setText(myPart.getPartNum());
		fldPartName.setText(myPart.getPartName());
		fldVendor.setText(myPart.getVendor());
		fldUnitOfQuantity.setText(myPart.getUnitOfQuantity());
		fldVendorPartNum.setText(myPart.getVendorPartNum());
		this.setTitle(myPart.getPartName());
		// setChanged(false);
	}

	public boolean savePart() {
		//display any error message if field data are invalid
		String testNum = fldPartNum.getText().trim();
		if(!myPart.validPartNum(testNum)) {
			parent.displayChildMessage("Invalid Part #");
			refreshFields();
			return false;
		}
		
		try {
			if(myPart.partAlreadyExists(testNum, myPart.getId())) {
				parent.displayChildMessage("Duplicate Part #");
				refreshFields();
				return false;
			}
		} catch (GatewayException e1) {
			parent.displayChildMessage("Error checking for duplicate.");
		}
		
		String testName = fldPartName.getText().trim();
		if(!myPart.validPartNum(testNum)) {
			parent.displayChildMessage("Invalid Part Name");
			refreshFields();
			return false;
		}
		
		String testVendor = fldVendor.getText().trim();
		if(!myPart.validPartNum(testNum)) {
			parent.displayChildMessage("Invalid Vendor");
			refreshFields();
			return false;
		}
		String testUnit = fldUnitOfQuantity.getText().trim();
		if(!myPart.validPartNum(testNum)) {
			parent.displayChildMessage("Invalid Unit of Quantity");
			refreshFields();
			return false;
		}
		
		String testVendorNum = fldVendorPartNum.getText().trim();
		if(!myPart.validPartNum(testNum)) {
			parent.displayChildMessage("Invalid Vendor Part #");
			refreshFields();
			return false;
		}
		setChanged(true);
		
		try {
			myPart.setPartNum(testNum);
			myPart.setPartName(testName);
			myPart.setVendor(testVendor);
			myPart.setUnitOfQuantity(testUnit);
			myPart.setVendorPartNum(testVendorNum);
//			setChanged(true);
		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		}
		
		try {
			myPart.finishUpdate();
			setChanged(false);
		} catch (GatewayException e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		}
		try {
			myPart.getGateway().releaseLock(myPart.getId());
		} catch (GatewayException g6) {
			parent.displayChildMessage(g6.getMessage());
			return false;
		}
		parent.displayChildMessage("Changes saved");
		setInternalFrameVisible(false);
		childClosing();
		return true;
	}

	@Override
	protected void childClosing() {
		super.childClosing();
				
		myPart.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		refreshFields();
	}
	
	public Part getCurrentPart() {
		return this.myPart;
	}


}

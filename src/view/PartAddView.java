package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import controller.PartListController;
import database.GatewayException;
import model.Part;

public class PartAddView extends MDIChild implements Observer {

	private Part newPart;
	private PartListController partList;
	private MDIParent parent;
	
	
	private JTextField newPartNum, newPartName, newVendor, newUnitOfQuantity, newVendorPartNum, newCapacity;
	
	public PartAddView(String title, Part part, MDIParent m) {
		super(title, m);
		newPart = part;
		parent = m;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2, 10, 10));
		
		panel.add(new JLabel("<html><font color='red'>*</font>Part #: </html>"));
		newPartNum = new JTextField("");
		panel.add(newPartNum);
		
		panel.add(new JLabel("<html><font color='red'>*</font>Part Name: </html>"));
		newPartName = new JTextField("");
		panel.add(newPartName);
		
		panel.add(new JLabel("Vendor: "));
		newVendor = new JTextField("");
		panel.add(newVendor);
		
		panel.add(new JLabel("<html><font color='red'>*</font>Unit of Quantity: </html>"));
		newUnitOfQuantity = new JTextField("");
		panel.add(newUnitOfQuantity);
		
		panel.add(new JLabel("<html><font color='red'>*</font>Vendor Part #: </html>"));
		newVendorPartNum = new JTextField("");
		panel.add(newVendorPartNum);
		
		
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
				addPart();
			}		
		});
		panel.add(button);
		this.add(panel);
		this.setPreferredSize(new Dimension(360, 220));
		
	}
	
	
	public void addPart() {
		String testPartNum = newPartNum.getText().trim();
		String testPartName = newPartName.getText().trim();
		String testVendor = newVendor.getText().trim();
		String testUnitOfQuantity = newUnitOfQuantity.getText().trim();
		String testVendorPartNum = newVendorPartNum.getText().trim();
		
		
		try {
			newPart.setPartNum(testPartNum);
			newPart.setPartName(testPartName);
			newPart.setVendor(testVendor);
			newPart.setUnitOfQuantity(testUnitOfQuantity);
			newPart.setVendorPartNum(testVendorPartNum);

		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());	
			return;
		}
		try {
			newPart.finishUpdate();
		} catch (GatewayException g) {
			parent.displayChildMessage(g.getMessage());
			return;
		}

		parent.displayChildMessage("Part added\nPart Id: " + newPart.getId());
		setInternalFrameVisible(false);
		childClosing();
	}
	
	

	@Override
	public void update(Observable o, Object arg) {
		
	}
}

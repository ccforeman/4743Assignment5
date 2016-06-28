package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;

public class CreateFileView extends MDIChild {
	private String filename;
	private JTextField file;
	private MDIParent parent;
	
	public CreateFileView(MDIParent parent) {
		super("New File", parent);
		
		this.parent = parent;
		
		this.setPreferredSize(new Dimension(300, 100));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(2, 0));
		file = new JTextField("WarehouseInventorySummary");
		
		center.add(new JLabel("Enter name of file: "));
		center.add(file);
		
		
		JPanel south = new JPanel();
		south.setLayout(new GridLayout(0, 2));
		
		JButton excel = new JButton("Export as Excel");
		
		excel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFileName("excel");
			}
		});
		
		JButton pdf = new JButton("Export as PDF");
		
		pdf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFileName("pdf");
			}
		});
		
		south.add(pdf);
		south.add(excel);
		panel.add(BorderLayout.CENTER, center);
		panel.add(BorderLayout.SOUTH, south);
		this.add(panel);
	
	}
	
	
	public void setFileName(String format) {
		Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		
		if(file.getText() != null && !file.getText().equals("")
				&&  !p.matcher(file.getText()).find() ) {
			this.filename = file.getText();
		} else {
			parent.displayChildMessage("Invalid File name!");
			return;
		}
		
		if(format.equals("excel")) {
			setInternalFrameVisible(false);
			childClosing();
			parent.doCommand(MenuCommands.GENERATE_EXCEL, this);
		} else {
			setInternalFrameVisible(false);
			childClosing();
			parent.doCommand(MenuCommands.GENERATE_PDF, this);
		}
	}
	
	public String getFileName() {
		return this.filename;
	}

}

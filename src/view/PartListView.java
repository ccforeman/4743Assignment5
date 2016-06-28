package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import controller.PartListController;
import model.Part;
import security.Authenticator;
import security.Credentials;
import security.SecurityException;

public class PartListView extends MDIChild {

	private JList<Part> listPart;
	private PartListController myList;
	private Part selectedModel;
	private MDIParent parent;
	
	
	public PartListView(String title, PartListController list, MDIParent parent) {
		super(title, parent);
		
		list.setMyListView(this);
		
		this.parent = parent;
		myList = list;
		
		listPart = new JList<Part>(myList);
		listPart.setCellRenderer(new PartListCellRenderer());
		listPart.setPreferredSize(new Dimension(220, myList.getSize() * 20));
		
		listPart.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					int index = listPart.locationToIndex(evt.getPoint());
					selectedModel = myList.getElementAt(index);
					openDetailView();
				}
			}
		});
		
		
		
		JButton button = new JButton("New");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddView();
			}
		});
		
		
		JButton delButton = new JButton("Delete");
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index;
				try {
					index = listPart.getMinSelectionIndex();
					selectedModel = myList.getElementAt(index);
					deleteSelectedModel();
				} catch (Exception exc) {
					return;
				}
				myList.deleteFromList(index);
				
			}
		});
		
		this.add(new JScrollPane(listPart));
		this.add(button);
		this.add(delButton);
		JLabel instruction = new JLabel("Double click a Part to see details and edit.");
		instruction.setFont(new Font("Serif", Font.ITALIC, 11));
		
		this.add(instruction);
		
		this.setPreferredSize(new Dimension(260, 200));
		
	}
	
	public void openDetailView() {
		parent.doCommand(MenuCommands.SHOW_DETAIL_PART, this);
	}
	
	public void openAddView() {
		parent.doCommand(MenuCommands.SHOW_ADD_PART, this);
	}
	
	public void openEditView() {
		parent.doCommand(MenuCommands.SHOW_EDIT_PART, this);
	}
	
	public void deleteSelectedModel() {
		try {
			if(Authenticator.serverHasAccess(parent.getCurrentSession(), Credentials.DELETE_PART)) {
				parent.doCommand(MenuCommands.DELETE_PART, this);
			} else {
				parent.displayChildMessage("You don't have permission to do that");
			}
		} catch (SecurityException e) {
			parent.displayChildMessage(e.getMessage());
		}
	}

	public Part getSelectedPart() {
		return selectedModel;
	}

	@Override
	protected void childClosing() {
		super.childClosing();
			
		myList.unregisterAsObserver();
	}
}

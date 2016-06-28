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
import controller.InventoryItemListController;
import model.InventoryItem;

public class InventoryItemListView extends MDIChild {

	private JList<InventoryItem> listInventoryItem;
	private InventoryItemListController myList;
	private InventoryItem selectedModel;
	private MDIParent parent;
	
	
	public InventoryItemListView(String title, InventoryItemListController list, MDIParent parent) {
		super(title, parent);
		
		list.setMyListView(this);
		
		this.parent = parent;
		myList = list;
		
		listInventoryItem = new JList<InventoryItem>(myList);
		listInventoryItem.setCellRenderer(new InventoryItemListCellRenderer());
		listInventoryItem.setPreferredSize(new Dimension(220, 200));
		
		listInventoryItem.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					int index = listInventoryItem.locationToIndex(evt.getPoint());
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
				try {
					int index = listInventoryItem.getMinSelectionIndex();
					selectedModel = myList.getElementAt(index);
					// might be able to remove below line; seems redundant
					myList.deleteFromList(index);
					deleteSelectedModel();
				} catch (Exception exc) {
					return;
				}
			}
		});
		
		this.add(new JScrollPane(listInventoryItem));
		this.add(button);
		this.add(delButton);
		JLabel instruction = new JLabel("Double click a InventoryItem to see details and edit.");
		instruction.setFont(new Font("Serif", Font.ITALIC, 11));
		
		this.add(instruction);
		
		this.setPreferredSize(new Dimension(260, 200));
		
	}
	
	public void openDetailView() {
		parent.doCommand(MenuCommands.SHOW_DETAIL_INVENTORY_ITEM, this);
	}
	
	public void openAddView() {
		parent.doCommand(MenuCommands.SHOW_ADD_INVENTORY_ITEM, this);
	}
	
	public void openEditView() {
		parent.doCommand(MenuCommands.SHOW_EDIT_INVENTORY_ITEM, this);
	}
	
	public void deleteSelectedModel() {
		parent.doCommand(MenuCommands.DELETE_PART, this);
	}

	public InventoryItem getSelectedInventoryItem() {
		return selectedModel;
	}

	@Override
	protected void childClosing() {
		super.childClosing();
			
		myList.unregisterAsObserver();
	}
}

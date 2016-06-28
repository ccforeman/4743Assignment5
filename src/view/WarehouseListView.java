package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import controller.InventoryItemListController;
import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import controller.WarehouseListController;
import database.GatewayException;
import model.InventoryItem;
import model.InventoryItemList;
import model.Warehouse;


/*  Likely better idea for changing the way the InventoryItem lists are updated:
 * 
 *  Instead of having an InventoryItemList model, have a method in Warehouse model
 *  that fetches its Inventory from the db.  Set the JList<InventoryItem> to the
 *  list that returns.
 *  
 *  Or, alternatively, each Warehouse object could have its own List<InventoryItem>
 *  field (with getters and setters, obviously) to make updating the JLists easier.
 *   
 */

@SuppressWarnings("serial")
public class WarehouseListView extends MDIChild implements Observer {
   
   private JList<InventoryItem> listInventory;
   private JList<Warehouse> listWarehouse;
   
   private JLabel capacities;
   
   private WarehouseListController myList;
   
   private Warehouse selectedWarehouse;
   private InventoryItem selectedInventory;
   
   private MDIParent parent;
   
   private InventoryItemListController inventoryListController;
   
   private InventoryItemList inventoryList;
   
   private int listSize;
   
   public WarehouseListView(String title, WarehouseListController list, InventoryItemList iList, MDIParent m) {
      super(title, m);
      
      parent = m;
      
      myList = list;
      myList.setMyListView(this);
      
      int max = myList.getElementAt(0).getWarehouseInventory().getList().size();
      for(int i = 0; i < myList.getSize(); i++) {
    	  if(max < myList.getElementAt(i).getWarehouseInventory().getList().size())
    		  max = myList.getElementAt(i).getWarehouseInventory().getList().size();
    	  myList.getElementAt(i).addObserver(this);
      }
      listSize = max;
      
      inventoryList = iList;
      
      inventoryListController = new InventoryItemListController(inventoryList);
      inventoryListController.setMyListView(this);
      
      
      this.setPreferredSize(new Dimension(430, 220));
      this.setLayout(new GridLayout(0, 2));
      this.add(leftPanel());
      this.add(rightPanel());
      
   }
   
   protected JPanel leftPanel() {
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(2,0));
      
      JPanel panel1 = new JPanel();
      capacities = new JLabel("Remaining Capacity: ");
      listWarehouse = new JList<Warehouse>(myList);
      listWarehouse.setCellRenderer(new WarehouseListCellRenderer());
      listWarehouse.setPreferredSize(new Dimension(150, myList.getSize() * 18));
      
      listWarehouse.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
              if(evt.getClickCount() == 2) {
               int index = listWarehouse.locationToIndex(evt.getPoint());
               selectedWarehouse = myList.getElementAt(index);
               openDetailView();
              }
              else if(evt.getClickCount() == 1) {
               int index1 = listWarehouse.locationToIndex(evt.getPoint());
               selectedWarehouse = myList.getElementAt(index1);
               setInventoryList(selectedWarehouse.getWarehouseInventory());
               capacities.setText("Remaining Capacity: " + selectedWarehouse.getRemainingCapacity());
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
               int index = listWarehouse.getMinSelectionIndex();
               selectedWarehouse = myList.getElementAt(index);
               deleteSelectedWarehouse();
            } catch (Exception exc) {
               parent.displayChildMessage("Select a Warehouse to Delete");
               return;
            }
         }
      });
      
      JScrollPane scroller = new JScrollPane(listWarehouse);
      scroller.setPreferredSize(new Dimension(180, 100));
      scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      panel1.add(scroller);
      
      JPanel panel2 = new JPanel();
      panel2.add(capacities);
      panel2.add(button);
      panel2.add(delButton);
      
      JLabel instruction = new JLabel("Double click a Warehouse to see details and edit.");
      instruction.setFont(new Font("Serif", Font.ITALIC, 11));
      
      panel2.add(instruction);
      
      panel.add(panel1);
      panel.add(panel2);
      return panel;
   }
   
   protected JPanel rightPanel() {
      JPanel panel = new JPanel();
      listInventory = new JList<InventoryItem>(inventoryListController);
      listInventory.setCellRenderer(new InventoryItemListCellRenderer());
      listInventory.setPreferredSize(new Dimension(150, listSize * 18 * 2));
      
      listInventory.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            int index;
            if(evt.getClickCount() == 1) {
               index = listInventory.locationToIndex(evt.getPoint());
               selectedInventory = inventoryListController.getElementAt(index);
            } else if(evt.getClickCount() == 2) {
               index = listInventory.locationToIndex(evt.getPoint());
               selectedInventory = inventoryListController.getElementAt(index);
               openInventoryDetailView();
            }
         }
      });
      
      JScrollPane scroller = new JScrollPane(listInventory);
      scroller.setPreferredSize(new Dimension(180, 100));
      scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      panel.add(scroller);
      
      JButton addInvButton = new JButton("Add Part To Inventory");
      addInvButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
        	if(selectedWarehouse == null)
        		parent.displayChildMessage("Selected a Warehouse!");
        	else if(selectedWarehouse.getRemainingCapacity() > 0.0)
        		openAddPartsToInventory();
        	else
        		parent.displayChildMessage("Warehouse is at full Capacity!\nMust create room before adding more inventory.");
         }
      });
      
      JButton delButton = new JButton("Delete");
      delButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               deleteSelectedInventory();
            } catch (Exception exc) {
               parent.displayChildMessage("Select an Inventory Item to Delete");
               return;
            }
            
            inventoryList.removeInventoryItemFromList(selectedInventory);
         }
      });

      panel.add(addInvButton);
      panel.add(delButton);
      
      JLabel instruction = new JLabel("Double click Inventory to see details.");
      instruction.setFont(new Font("Serif", Font.ITALIC, 11));
      
      panel.add(instruction);
      
      return panel;
   }

   private void openDetailView() {
      parent.doCommand(MenuCommands.SHOW_DETAIL_WAREHOUSE, this);
   }
   
   private void openEditView() {
      parent.doCommand(MenuCommands.SHOW_EDIT_WAREHOUSE, this);
   }
   
   private void openAddView() {
      parent.doCommand(MenuCommands.SHOW_ADD_WAREHOUSE, this);
   }
   
   private void deleteSelectedWarehouse() {
      parent.doCommand(MenuCommands.DELETE_WAREHOUSE, this);
   }
   
   private void deleteSelectedInventory() {
      parent.doCommand(MenuCommands.DELETE_INVENTORY_ITEM, this);
   }
   
   private void openAddPartsToInventory() {
      parent.doCommand(MenuCommands.SHOW_ADD_INVENTORY_ITEM, this);
      
   }
   
//   private void openInventoryEditView() {
//      parent.doCommand(MenuCommands.SHOW_EDIT_INVENTORY_ITEM, this);
//   }
   
   private void openInventoryDetailView() {
	   parent.doCommand(MenuCommands.SHOW_DETAIL_INVENTORY_ITEM, this);
   }
   
// public void updateInventoryListPanel() {
//    selectedWarehouse.getGateway().fetchAllInventoryForWarehouse(selectedWarehouse.getId());
// }

   public Warehouse getSelectedWarehouse() {
      return selectedWarehouse;
   }
   
   public InventoryItem getSelectedInventory() {
      return selectedInventory;
   }
   
   public void setInventoryList(InventoryItemList iil) {
      this.inventoryList.setList(iil.getList());
   }
   
   public InventoryItemList getInventoryListForSelectedWarehouse() {
      return this.inventoryList;
   }
   
   public void refreshFields() {
		capacities.setText("Remaining Capacity: " + selectedWarehouse.getRemainingCapacity());
	}

   @Override
   protected void childClosing() {
      super.childClosing();   
      myList.unregisterAsObserver();
      inventoryListController.unregisterAsObserver();
      for(int i = 0; i < myList.getSize(); i++) {
    	  myList.getElementAt(i).deleteObserver(this);
      }
   }

@Override
public void update(Observable o, Object arg) {
	refreshFields();
}

      
}

package controller;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.MDIChild;
import controller.MDIChildFrame;
import database.GatewayException;
import database.InventoryItemTableGateway;
import logging.ExcelGenerator;
import logging.PDFGenerator;
import model.InventoryItem;
import model.InventoryItemList;
import model.Part;
import model.PartList;
import model.Session;
import model.Warehouse;
import model.WarehouseList;
import security.ABACPolicy;
import security.Authenticator;
import security.Credentials;
import security.SecurityException;
import view.CreateFileView;
import view.InventoryItemAddView;
import view.InventoryItemDetailView;
import view.InventoryItemEditView;
import view.InventoryItemListView;
//import view.LoginView;
import view.PartAddView;
import view.PartDetailView;
import view.PartEditView;
import view.PartListView;
import view.WarehouseAddView;
import view.WarehouseDetailView;
import view.WarehouseEditView;
import view.WarehouseListView;

public class MDIParent extends JFrame {
	private Session currentSession;
	private Authenticator auth;
	
	private static final long serialVersionUID = 1L;
	private JDesktopPane desktop;
	private int newFrameX = 0, newFrameY = 0;
	
	private WarehouseList warehouseList;
	public PartList partList;
	private InventoryItemList inventoryList;

	private List<MDIChild> openViews;
	public static ArrayList<Part> partsBeingEdited;
	
	public MDIParent(String title, WarehouseList wList, PartList pList) {
		super(title);
		
		partsBeingEdited = new ArrayList<Part>();
		
		warehouseList = wList;
		
		partList = pList;
		
		inventoryList = new InventoryItemList();

		openViews = new LinkedList<MDIChild>();
		
		MDIMenu menuBar = new MDIMenu(this);
		setJMenuBar(menuBar);
		
		desktop = new JDesktopPane();
		add(desktop);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				closeProperly();
			}
		});

	}
	
	public void doCommand(MenuCommands cmd, Container caller) {
		switch(cmd) {
			case APP_QUIT :
				closeChildren();
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				break;
			case SHOW_LIST_WAREHOUSES :
				WarehouseListView v1 = new WarehouseListView("Warehouse List", new WarehouseListController(warehouseList), inventoryList, this);
				openMDIChild(v1);
				
				break;
			case SHOW_DETAIL_WAREHOUSE :
				Warehouse w = ((WarehouseListView) caller).getSelectedWarehouse();
		    	WarehouseDetailView v = new WarehouseDetailView(w.getWarehouseName(), w, this);
				openMDIChild(v);
				break;
			case SHOW_ADD_WAREHOUSE :
				Warehouse newHouse = new Warehouse();
				warehouseList.addWarehouseToList(newHouse);
				warehouseList.addToNewRecords(newHouse);
				
				WarehouseAddView wav = new WarehouseAddView(newHouse.getWarehouseName(), newHouse, this);
				
				openMDIChild(wav);
				break;
			case DELETE_WAREHOUSE :
				Warehouse wh = ((WarehouseListView) caller).getSelectedWarehouse();
				try {
					wh.getGateway().deleteWarehouse(wh.getId());
					this.displayChildMessage("Deleted");
				} catch (GatewayException e) {
					System.err.println(e.getMessage());
					this.displayChildMessage("Error trying to delete warehouse.");
					return;
				}
				
				warehouseList.removeWarehouseFromList(wh);
				break;
			case SHOW_EDIT_WAREHOUSE :
				Warehouse whz = ((WarehouseDetailView) caller).getSelectedWarehouse();
				WarehouseEditView ev = new WarehouseEditView("Edit " + whz.getId(), whz, this);
				openMDIChild(ev);
				break;
			case SHOW_LIST_PARTS :
				PartListView pv = new PartListView("Part List", new PartListController(partList), this);
				openMDIChild(pv);
				break;
			case SHOW_DETAIL_PART :
				Part p = ((PartListView) caller).getSelectedPart();
		    	PartDetailView pdv = new PartDetailView(p.getPartName(), p, this);
				openMDIChild(pdv);
				break;
			case SHOW_ADD_PART :
				try {
					if(!Authenticator.serverHasAccess(currentSession, Credentials.ADD_PART)) {
						displayChildMessage("You don't have permission to do that!");
						return;
					}
				} catch (SecurityException e1) {
					displayChildMessage(e1.getMessage());
					return;
				}
				Part p1 = new Part();
				partList.addPartToList(p1);
				partList.addToNewRecords(p1);
				PartAddView pav = new PartAddView("Add Part", p1, this);
				openMDIChild(pav);
				break;
			case DELETE_PART :
				try {
					if(!Authenticator.serverHasAccess(currentSession, Credentials.DELETE_PART)) {
						displayChildMessage("You don't have permission to do that!");
						return;
					}
				} catch (SecurityException e1) {
					displayChildMessage(e1.getMessage());
					return;
				}
				Part p2 = ((PartListView) caller).getSelectedPart();
				partList.removePartFromList(p2);
				try {
					p2.getGateway().deletePart(p2.getId());
					this.displayChildMessage("Deleted");
				} catch (GatewayException e) {
					System.err.println(e.getMessage());
					this.displayChildMessage("Error trying to delete part.");
				}
				break;
			case SHOW_EDIT_PART :
				Part p3 = ((PartDetailView) caller).getSelectedPart();
				
				try {
					if(!Authenticator.serverHasAccess(currentSession, Credentials.EDIT_PART)) {
						displayChildMessage("You don't have permission to do that!");
						return;
					}
					
					if(!p3.getGateway().checkLock(p3.getId())) {
						p3.getGateway().pessimisticLock(p3.getId());
					} else {
						displayChildMessage("Part is currently being edited.");
						return;
					}
					
					
				} catch (SecurityException e1) {
					displayChildMessage(e1.getMessage());
					return;
				} catch (GatewayException ge1) {
					displayChildMessage(ge1.getMessage());
					return;
				}
				
				partsBeingEdited.add(p3);
				PartEditView pev = new PartEditView("Edit " + p3.getId(), p3, this);
				openMDIChild(pev);
				break;
			case SHOW_DETAIL_INVENTORY_ITEM :
				InventoryItem i = ((WarehouseListView) caller).getSelectedInventory();
				Warehouse pwh = ((WarehouseListView) caller).getSelectedWarehouse();
				InventoryItemDetailView idv = new InventoryItemDetailView(i.getId() + " Detail", i, pwh, this);
				openMDIChild(idv);
				break;
			case SHOW_ADD_INVENTORY_ITEM :
				Warehouse sw = ((WarehouseListView) caller).getSelectedWarehouse();
				InventoryItem i3 = new InventoryItem();
				inventoryList.addInventoryItemToList(i3);
				inventoryList.addToNewRecords(i3);
				InventoryItemAddView iav = new InventoryItemAddView("Add Inventory", warehouseList, partList, inventoryList, sw, i3, this);
				openMDIChild(iav);
				break;
			case SHOW_EDIT_INVENTORY_ITEM :
				InventoryItem i1 = ((InventoryItemDetailView) caller).getInventoryItem();
				Warehouse pwh2 = ((InventoryItemDetailView) caller).getWarehouse();
				InventoryItemEditView iev = new InventoryItemEditView("Edit " + i1.getId(), i1, pwh2, this);
				openMDIChild(iev);
				break;
			case DELETE_INVENTORY_ITEM :
				InventoryItem i2 = ((WarehouseListView) caller).getSelectedInventory();
				Warehouse sw1 = ((WarehouseListView) caller).getSelectedWarehouse();
				inventoryList.removeInventoryItemFromList(i2);
				try {
					i2.getGateway().deleteInventoryItem(i2.getId());
					sw1.syncWarehouse();
				} catch (GatewayException e ) {
					this.displayChildMessage("Error Deleting Inventory Item.");
				}
				break;
			case GENERATE_PDF :
				try {
					String filename1 = ((CreateFileView) caller).getFileName();
					PDFGenerator pdf = new PDFGenerator(filename1, new InventoryItemTableGateway(), this);
				} catch (IOException | GatewayException | SQLException e) {
					this.displayChildMessage(e.getMessage());
				}
				break;
			case GENERATE_EXCEL :
				try {
					String filename = ((CreateFileView) caller).getFileName();
					ExcelGenerator excel = new ExcelGenerator(new InventoryItemTableGateway(), this, filename);
				} catch ( GatewayException | SQLException exe) {
					this.displayChildMessage(exe.getMessage());
				}
				break;
			case CREATE_FILE_VIEW :
				CreateFileView cfv = new CreateFileView(this);
				openMDIChild(cfv);
				break;
			
		}
		
	}
		
	public void closeChildren() {
		JInternalFrame [] children = desktop.getAllFrames();
		for(int i = children.length - 1; i >= 0; i--) {
			children[i].dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void cleanupChildPanels() {
		JInternalFrame [] children = desktop.getAllFrames();
		for(int i = children.length - 1; i >= 0; i--) {
			if(children[i] instanceof MDIChildFrame)
				((MDIChildFrame) children[i]).closeChild();
		}
		
		try {
			partList.getGateway().resetAllLocks();
		} catch (GatewayException e) {
			e.printStackTrace();
		}
		
		warehouseList.getGateway().close();
		partList.getGateway().close();
	}

	public void closeProperly() {
		cleanupChildPanels();
	}

	public JInternalFrame openMDIChild(MDIChild child) {
		if(child.isSingleOpenOnly()) {
			for(MDIChild testChild : openViews) {
				if(testChild.getClass().getSimpleName().equals(child.getClass().getSimpleName())) {
					try {
						testChild.restoreWindowState();
					} catch(PropertyVetoException e) {
						e.printStackTrace();
					}
					JInternalFrame c = (JInternalFrame) testChild.getMDIChildFrame();
					return c;
				}
			}
		}
		
		MDIChildFrame frame = new MDIChildFrame(child.getTitle(), true, true, true, true, child);
		
		frame.pack();
		frame.setLocation(newFrameX, newFrameY);
		newFrameX = (newFrameX + 50) % desktop.getWidth();
		newFrameY = (newFrameY + 10) % desktop.getHeight();
		desktop.add(frame);
		frame.setVisible(true);
		
		openViews.add(child);
		
		return frame;
	}
	
	public void displayChildMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public void removeFromOpenViews(MDIChild child) {
		openViews.remove(child);
	}
	
	public void refreshWarehouseRemainingCap(Warehouse w) throws GatewayException {
		w.setRemainingCapacity(w.getGateway().fetchWarehouse(w.getId()).getRemainingCapacity());
	}
	
	public void deleteFromList(Warehouse wh) {
		warehouseList.removeWarehouseFromList(wh);
		try {
			wh.getGateway().deleteWarehouse(wh.getId());
		} catch (GatewayException e) {
			displayChildMessage(e.getMessage());
		}
	}

	public Session getCurrentSession() {
		return currentSession;
	}
	
	public void setCurrentSession(Session s) {
		this.currentSession = s;
	}
	
	
}

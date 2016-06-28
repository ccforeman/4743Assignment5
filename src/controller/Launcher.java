package controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import database.PartTableGateway;
import database.WarehouseTableGateway;
import database.GatewayException;
import database.InventoryItemTableGateway;

import model.InventoryItem;
import model.InventoryItemList;
import model.Part;
import model.PartList;
import model.States;
import model.Warehouse;
import model.WarehouseList;

import security.Hasher;
import security.Authenticator;
/**
 * 
 * CS 4743 Assignment 5 by Charles Foreman
 * 
 *
 */

public class Launcher {

	public static void createAndShowGUI() {
		
		WarehouseTableGateway wtg = null;
		PartTableGateway ptg = null;
		InventoryItemTableGateway itg = null;
		
		WarehouseList wl = new WarehouseList();
		PartList pl = new PartList();
		
		
		try {
			wtg = new WarehouseTableGateway();
			ptg = new PartTableGateway();
			itg = new InventoryItemTableGateway();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "DB is not responding");
			System.exit(1);
		}
		
		wl.setGateway(wtg);
		wl.loadFromGateway();
		
		pl.setGateway(ptg);
		pl.loadFromGateway();
		
		for(Warehouse w : wl.getList()) {
			InventoryItemList il = new InventoryItemList();
			il.setGateway(itg);
			try {
				il.setList(wtg.fetchAllInventoryForWarehouse(w.getWarehouseName()));
			} catch (GatewayException e) {
				e.printStackTrace();
				System.exit(1);
			}
			w.setWarehouseInventory(il);
		}
		
		
		MDIParent myFrame = new MDIParent("4743 Assignment 5", wl, pl);
		
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		myFrame.pack();
		myFrame.setSize(1000, 600);
		myFrame.setVisible(true);
		
		
	}
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Error setting Look and Feel.  Error class and message: " + e.getClass() + " - " + e.getMessage());
		}
		
		Authenticator.insert();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

package model;

import java.util.Observable;

import database.GatewayException;
import database.WarehouseTableGateway;


/**
 * For the 'state' field of Warehouse I included a more strict constraint.
 * 
 * Instead of:			
 * 					Optional, alphanumeric and symbols, max length 50
 * 
 * I implemented:		
 * 					Optional, valid 2 letter state abbreviation
 * 
 * 
 * @author cameronforeman
 *
 */

public class Warehouse extends Observable {
	private long id;
	private String warehouseName;
	private String address;
	private String city;
	private String state;
	private String zip;
	private double capacity;
	private double remainingCapacity;
	
	private WarehouseTableGateway gateway;
	private InventoryItemList warehouseInventory;
//	private List<InventoryItem> warehouseInventory;
	
	public Warehouse() {
		warehouseName = "New Warehouse";
		warehouseName = address = city = state = zip = "";
		capacity = remainingCapacity = 0;
		warehouseInventory = new InventoryItemList();
		setChanged();
	}
	
	public Warehouse(String name, String address, String city, String state, String zip, double cap, double remainingCap){
		this();
		if(!validWarehouseName(name))
			throw new IllegalArgumentException("Invalid Name!");
		if(!validAddress(address))
			throw new IllegalArgumentException("Invalid Address!");
		if(!validCity(city))
			throw new IllegalArgumentException("Invalid City!");
		if(!validState(state))
			throw new IllegalArgumentException("Invalid State!");
		if(!validZip(zip))
			throw new IllegalArgumentException("Invalid Zip!");
		if(!validCapacity(cap))
			throw new IllegalArgumentException("Invalid Capacity!");
		
		this.warehouseName = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.capacity = cap;
		this.remainingCapacity = remainingCap;
	}
	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		if(!validWarehouseName(warehouseName))
			throw new IllegalArgumentException("Invalid Warehouse Name!");
		this.warehouseName = warehouseName;
		setChanged();
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		if(!validAddress(address))
			throw new IllegalArgumentException("Invalid Address!");
		this.address = address;
		setChanged();
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		if(city == null)
			return;
		if(!validCity(city))
			throw new IllegalArgumentException("Invalid City!");
		this.city = city;
		setChanged();
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		if(state == null)
			return;
		if(!validState(state))
			throw new IllegalArgumentException("Invalid State!\nNote: Use Two Letter State Abbreviation");
		this.state = state;
		setChanged();
	}
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		if(!validZip(zip))
			throw new IllegalArgumentException("Invalid Zip!");
		this.zip = zip;
		setChanged();
	}
	
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		if(!validCapacity(capacity))
			throw new IllegalArgumentException("Invalid Capacity!");
		this.capacity = capacity;
		setChanged();
	}
	
	public double getRemainingCapacity() {
		return remainingCapacity;
	}
	
	public void setRemainingCapacity(double remainingCapacity) {
		if(remainingCapacity < 0)
			throw new IllegalArgumentException("Invalid Remaining Capacity!");
		this.remainingCapacity = remainingCapacity;
		setChanged();
	}
	
	
	public void finishUpdate() throws GatewayException {
		Warehouse original = null;
		
		if(this.getId() == 0)
			if(gateway.warehouseAlreadyExists(this.getWarehouseName(), this.getId()))
				throw new GatewayException("This Warehouse name is already in the Database.");
		
		try {
			if(this.getId() == 0) {
				this.setId(gateway.insertWarehouse(this));
			} else {
				original = gateway.fetchWarehouse(this.getId());
				gateway.updateWarehouse(this);
				syncWarehouse();
			}
			notifyObservers();
		} catch (GatewayException e) {
			if(original != null) {
				this.setWarehouseName(original.getWarehouseName());
				this.setAddress(original.getAddress());
				this.setCity(original.getCity());
				this.setState(original.getState());
				this.setZip(original.getZip());
				this.setCapacity(original.getCapacity());
				this.setRemainingCapacity(original.getRemainingCapacity());
			}
			throw new GatewayException("Error trying to update Warehouse in Database." + e.getMessage());
		}
	}
	
	/* Gets called after an inventory change or warehouse edit*/
	public void syncWarehouse() throws GatewayException {
		Warehouse updatedWarehouse = null;
		
		try {
			updatedWarehouse = gateway.fetchWarehouse(this.getId());
		} catch (GatewayException e) {
			throw new GatewayException("Error trying to update Warehouse in Database.");
		}
		
		if(updatedWarehouse != null) {
			this.setWarehouseName(updatedWarehouse.getWarehouseName());
			this.setAddress(updatedWarehouse.getAddress());
			this.setCity(updatedWarehouse.getCity());
			this.setState(updatedWarehouse.getState());
			this.setZip(updatedWarehouse.getZip());
			this.setCapacity(updatedWarehouse.getCapacity());
			this.setRemainingCapacity(updatedWarehouse.getRemainingCapacity());
			notifyObservers();
		}
	}
	
	@Override
	public String toString() {
//		StringBuilder str = new StringBuilder(getWarehouseName());
//	
//		str.append(" Address: ").append(address).append(" City: ").append(city);
//		str.append(" State: ").append(state).append(" Zip: ").append(zip).append(" Capacity: ").append(capacity);
	
		return this.getWarehouseName();
	}
	
	public boolean validWarehouseName(String name) {
		if(name == null || name.equals("") || name.length() > 255)
			return false;
			
		return true;
			
	}
	
	public boolean validAddress(String address) {
		if(address == null || address.equals(""))
			return false;
		return true;
	}
	
	public boolean validCity(String city) {
		if(city == null)
			return true;
		if(city.length() > 100)
			return false;
		
		return true;
	}
	
	// Added extra constraints
	public boolean validState(String state) {
		if(state == null || state.equals(""))
			return true;
		if(state.length() > 2)
			return false;
		if(!States.getStates().contains(state.toUpperCase()))
			return false;
		return true;
	}
	
	public boolean validZip(String zip) {
		if(zip == null || zip.length() != 5)
			return false;
		if(!zip.matches("[0-9]+"))
			return false;
		
		return true;
	}
	
	public boolean validCapacity(double capacity) {
		if(capacity < 0)
			return false;
		return true;
	}
	
	public WarehouseTableGateway getGateway() {
		return this.gateway;
	}
	
	public void setGateway(WarehouseTableGateway gateway) {
		this.gateway = gateway;
	}
	
	public boolean warehouseAlreadyExists(String warehouseName, long id) throws GatewayException {
		if(gateway.warehouseAlreadyExists(warehouseName, id))
			return true;
		else
			return false;
	}
	
	public InventoryItemList getWarehouseInventory() {
		return warehouseInventory;
	}
	
	public void setWarehouseInventory(InventoryItemList inventoryList) {
		warehouseInventory = inventoryList;
		setChanged();
		notifyObservers();
	}
	
	public void refreshWarehouseRemainingCap() throws GatewayException {
		this.setRemainingCapacity(gateway.fetchWarehouse(this.getId()).getRemainingCapacity());
	}
	
//	public List<InventoryItem> getWarehouseInventory() {
//		try {
//			warehouseInventory = gateway.fetchAllInventoryForWarehouse(this.warehouseName);
//		} catch (GatewayException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return warehouseInventory;
//	}
//	
//	public void setWarehouseInventory(List<InventoryItem> inventory) {
//		warehouseInventory = inventory;
//	}

}

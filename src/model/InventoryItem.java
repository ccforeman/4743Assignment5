package model;

import java.util.Observable;

import database.GatewayException;
import database.InventoryItemTableGateway;

public class InventoryItem extends Observable {

	private long id;
	private String warehouseId;
	private String partId;
	private double quantity;
	
	private InventoryItemTableGateway gateway;
	
	public InventoryItem() {
		id = 0L;
		warehouseId = partId = "";
		setChanged();
	}
	
	
	public InventoryItem(String warehouseId, String partId, double quantity) {
		this();
		if(!validWarehouseId(warehouseId))
			throw new IllegalArgumentException("Invalid Warehouse Id!");
		if(!validPartId(partId))
			throw new IllegalArgumentException("Invalid Part Id!");
		if(!validQuantity(quantity))
			throw new IllegalArgumentException("Invalid Quantity!");
		
		this.warehouseId = warehouseId;
		this.partId = partId;
		this.quantity = quantity;
	}
	
	public InventoryItemTableGateway getGateway() {
		return this.gateway;
	}
	
	public void setGateway(InventoryItemTableGateway gateway) {
		this.gateway = gateway;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	
	public String getWarehouseId() {
		return warehouseId;
	}
	
	public void setWarehouseId(String warehouseId) {
		if(!validWarehouseId(warehouseId))
			throw new IllegalArgumentException("Invalid Warehouse ID!");
		this.warehouseId = warehouseId;
		setChanged();
	}
	
	
	public String getPartId() {
		return partId;
	}
	
	public void setPartId(String partId) {
		if(!validPartId(partId))
			throw new IllegalArgumentException("Invalid Part ID!");
		
		this.partId = partId;
		setChanged();
	}
	
	public double getQuantity() {
		return quantity;
	}


	public void setQuantity(double quantity) {
		if(!validQuantity(quantity))
			throw new IllegalArgumentException("Invalid quantity!");
		
		this.quantity = quantity;
		setChanged();
	}
	
	public boolean validWarehouseId(String warehouse) {
	/* Probably need to make sure it exists in the part db */
		
		if(warehouse == null || warehouse.equals(""))
			return false;
		
		if(warehouse.length() > 255)
			return false;
		
		return true;
	}
	
	public boolean validPartId(String part) {
	/* Probably need to make sure it exists in the part db */
		if(part == null || part.equals(""))
			return false;
		
		if(part.length() > 20)
			return false;
		
		return true;
	}
	
	public boolean validQuantity(double q) {
		if(q < 0)
			return false;
		
		return true;
	}
	
	public void finishUpdate() throws GatewayException {
		InventoryItem original = null;
		
		if(this.getId() == 0 )
			if(gateway.inventoryItemAlreadyExists(this.getWarehouseId(), this.getPartId(), this.getId()))
				throw new GatewayException("Inventory # is already in Database.");
		
		try {
			if(this.getId() == 0 ) {
				this.setId(gateway.insertInventoryItem(this));
			} else {
				original = gateway.fetchInventoryItem(this.getId());
				gateway.updateInventoryItem(this);
			}
			
			notifyObservers();
		
		} catch (GatewayException e) {
			if(original != null) {
				this.setId(original.getId());
				this.setWarehouseId(original.getWarehouseId());
				this.setPartId(original.getPartId());
				this.setQuantity(original.getQuantity());
			}
			throw new GatewayException("Error trying to update Inventory Item in Database. Message: " + e.getMessage());
		}
	}
	
	public boolean inventoryItemAlreadyExists(String warehouseId, String partId, long id) throws GatewayException {
		if(gateway.inventoryItemAlreadyExists(warehouseId, partId, id))
			return true;
		else
			return false;
	}

	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Id: ");
		str.append(this.getId()).append("\tWarehouse: ").append(this.getWarehouseId()).append("\tPart: ").append(this.getPartId()).append("\tQuantity: ").append(this.getQuantity());
		return str.toString();
	}
	
	
}

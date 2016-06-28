package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import database.GatewayException;
import database.InventoryItemTableGateway;


public class InventoryItemList extends Observable implements Observer {
	private List<InventoryItem> inventoryItemList;
	
	private HashMap<Long, InventoryItem> myIdMap;
	
	private ArrayList<InventoryItem> newRecords;
	
	private InventoryItemTableGateway gateway;
	
	private boolean dontNotify;
	
	public InventoryItemList() {
		inventoryItemList = new ArrayList<InventoryItem>();
		myIdMap = new HashMap<Long, InventoryItem>();
		newRecords = new ArrayList<InventoryItem>();
		dontNotify = false;
	}
	
	public void loadFromGateway() {
		List<InventoryItem> inventoryItems = null;
		
		try {
			inventoryItems = gateway.fetchInventoryItems();
		} catch (GatewayException e) {
			return;
		}
		
		dontNotify = true;
		
		for(int i = inventoryItemList.size() -1; i >= 0; i--) {
			InventoryItem inventoryItem = inventoryItemList.get(i);
			boolean remove = true;
			
			if(inventoryItem.getId() == 0)
				remove = false;
			else {
				for(InventoryItem p : inventoryItems){
					if(inventoryItem.getId() == p.getId()){
						remove = false;
						break;
					}
				}
			}
			if(remove)
				removeInventoryItemFromList(inventoryItem);
		}
		
		for(InventoryItem p : inventoryItems) {
			if(!myIdMap.containsKey(p.getId()))
				addInventoryItemToList(p);
		}
		
		this.notifyObservers();
		
		dontNotify = false;
	}
	
	public InventoryItemTableGateway getGateway() {
		return this.gateway;
	}
	
	public void setGateway(InventoryItemTableGateway gateway) {
		this.gateway = gateway;
	}
	

	public void addInventoryItemToList(InventoryItem p) {
		inventoryItemList.add(p);
		p.addObserver(this);
		p.setGateway(gateway);
		myIdMap.put(p.getId(), p);
		this.setChanged();
		
		if(!dontNotify)
			this.notifyObservers();
	}
	
	public void addToNewRecords(InventoryItem p) {
		newRecords.add(p);
	}
	
	public InventoryItem removeInventoryItemFromList(InventoryItem p) {
		if(inventoryItemList.contains(p)) {
			inventoryItemList.remove(p);
			p.deleteObserver(this);
			myIdMap.remove(p.getId());
			this.setChanged();
			
			if(!dontNotify)
				this.notifyObservers();
			
			return p;
		}
		return null;
	}
	
	public List<InventoryItem> getList() {
		return inventoryItemList;
	}
	
	public void setList(List<InventoryItem> pl) {
		inventoryItemList = pl;
		this.setChanged();
		notifyObservers();
	}

	@Override
	public void update(Observable o, Object arg) {
		InventoryItem p = (InventoryItem) o;
		if(newRecords.contains(p)) {
			myIdMap.remove(0);
			myIdMap.put(p.getId(), p);
			newRecords.remove(p);
		}
		
		setChanged();
		notifyObservers();
		
	}
}

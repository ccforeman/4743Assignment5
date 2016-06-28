package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import database.WarehouseTableGateway;

public class WarehouseList extends Observable implements Observer {
	private List<Warehouse> warehouseList;
	
	private HashMap<Long, Warehouse> myIdMap;
	
	private ArrayList<Warehouse> newRecords;
	
	private WarehouseTableGateway gateway;
	
	private boolean dontNotify;
	
	
	public WarehouseList() {
		warehouseList = new ArrayList<Warehouse>();
		myIdMap = new HashMap<Long, Warehouse>();
		newRecords = new ArrayList<Warehouse>();
		dontNotify = false;
	}
	
	
	public void loadFromGateway() {
		List<Warehouse> warehouses = null;
		
		try {
			warehouses = gateway.fetchWarehouses();
		} catch (Exception e) {
			return;
		}
		
		dontNotify = true;
		// warehouses in the list that are not in the DB need to be removed from the list
		for(int i = warehouseList.size() - 1; i >= 0; i-- ) {
			Warehouse w = warehouseList.get(i);
			boolean remove = true;
			
			// invalid id when new warehouse hasn't been saved yet
			if(w.getId() == 0)
				remove = false;
			else {  // IF SOMEHOW SOMETHING STOPS WORKING IT IS SOMEHOW HERE
				for(Warehouse wh : warehouses) {
					if(wh.getId() == w.getId()) {
						remove = false;
						break;
					}
				}
			}
			
			if(remove)
				removeWarehouseFromList(w);
		}
		
		
		for(Warehouse wh : warehouses) {
			if(!myIdMap.containsKey(wh.getId())) {
				addWarehouseToList(wh);
			}
		}
		
		
		this.notifyObservers();
		
		dontNotify = false;
	}
	
	public WarehouseTableGateway getGateway() {
		return this.gateway;
	}
	
	public void setGateway(WarehouseTableGateway gateway) {
		this.gateway = gateway;
	}
	
	public void addWarehouseToList(Warehouse w) {
		warehouseList.add(w);
		w.addObserver(this);
		w.setGateway(gateway);
		myIdMap.put(w.getId(), w);
		this.setChanged();
		
		if(!dontNotify)
			this.notifyObservers();
	}
	
	public Warehouse removeWarehouseFromList(Warehouse w) {
		if(warehouseList.contains(w)) {
			warehouseList.remove(w);
			myIdMap.remove(w.getId());
			
			this.setChanged();
			if(!dontNotify)
				this.notifyObservers();
			
			return w;
		}
		return null;
	}
	
	public List<Warehouse> getList() {
		return warehouseList;
	}
	
	public void setList(List<Warehouse> wl) {
		warehouseList = wl;
	}
	
	public void addToNewRecords(Warehouse w) {
		newRecords.add(w);
	}


	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
		Warehouse w = (Warehouse) o;
		if(newRecords.contains(w)) {
			myIdMap.remove(0);
			myIdMap.put(w.getId(), w);
			newRecords.remove(w);
		}
		
		setChanged();
		notifyObservers();
	}
}

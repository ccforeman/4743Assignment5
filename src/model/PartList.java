package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import database.GatewayException;
import database.PartTableGateway;


public class PartList extends Observable implements Observer {
	private List<Part> partList;
	
	private HashMap<Long, Part> myIdMap;
	
	private ArrayList<Part> newRecords;
	
	private PartTableGateway gateway;
	
	private boolean dontNotify;
	
	public PartList() {
		partList = new ArrayList<Part>();
		myIdMap = new HashMap<Long, Part>();
		newRecords = new ArrayList<Part>();
		dontNotify = false;
	}
	
	public void loadFromGateway() {
		List<Part> parts = null;
		
		try {
			parts = gateway.fetchParts();
		} catch (GatewayException e) {
			return;
		}
		
		dontNotify = true;
		
		for(int i = partList.size() -1; i >= 0; i--) {
			Part part = partList.get(i);
			boolean remove = true;
			
			if(part.getId() == 0)
				remove = false;
			else {
				for(Part p : parts){
					if(part.getId() == p.getId()){
						remove = false;
						break;
					}
				}
			}
			if(remove)
				removePartFromList(part);
		}
		
		for(Part p : parts) {
			if(!myIdMap.containsKey(p.getId()))
				addPartToList(p);
		}
		
		this.notifyObservers();
		
		dontNotify = false;
	}
	
	public PartTableGateway getGateway() {
		return this.gateway;
	}
	
	public void setGateway(PartTableGateway gateway) {
		this.gateway = gateway;
	}
	

	public void addPartToList(Part p) {
		partList.add(p);
		p.addObserver(this);
		p.setGateway(gateway);
		myIdMap.put(p.getId(), p);
		this.setChanged();
		
		if(!dontNotify)
			this.notifyObservers();
	}
	
	public void addToNewRecords(Part p) {
		newRecords.add(p);
	}
	
	public Part removePartFromList(Part p) {
		if(partList.contains(p)) {
			partList.remove(p);
			p.deleteObserver(this);
			myIdMap.remove(p.getId());
			this.setChanged();
			
			if(!dontNotify)
				this.notifyObservers();
			
			return p;
		}
		return null;
	}
	
	public List<Part> getList() {
		return partList;
	}
	
	public void setList(List<Part> pl) {
		partList = pl;
	}

	@Override
	public void update(Observable o, Object arg) {
		Part p = (Part) o;
		if(newRecords.contains(p)) {
			myIdMap.remove(0);
			myIdMap.put(p.getId(), p);
			newRecords.remove(p);
		}
		
		setChanged();
		notifyObservers();
		
	}
}

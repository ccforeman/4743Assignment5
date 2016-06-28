package controller;

import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;

import model.Warehouse;
import model.WarehouseList;

public class WarehouseListController extends AbstractListModel<Warehouse> implements Observer {

	// private List<Warehouse> myList;
	private WarehouseList myList;
	private MDIChild myListView;
	
	public WarehouseListController(WarehouseList wl) {
		super();
//		myList = wl.getList();
		myList = wl;
		
		wl.addObserver(this);
	}
	
	@Override
	public int getSize() {
		return myList.getList().size();
	}

	@Override
	public Warehouse getElementAt(int index) {
		if(index > getSize())
			throw new IndexOutOfBoundsException("Index " + index + " is out of list bounds!");
		return myList.getList().get(index);
	}

	public MDIChild getMyListView() {
		return myListView;
	}

	public void setMyListView(MDIChild myListView) {
		this.myListView = myListView;
	}

//	public void registerAsObserver() {
//		for(Warehouse w: myList)
//			w.addObserver(this);
//	}

	public void unregisterAsObserver() {
//		for(Warehouse w: myList.getList())
//			w.deleteObserver(this);
		myList.deleteObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		fireContentsChanged(this, 0, getSize());
		myListView.repaint();
	}
	
	public void deleteFromList(int index) {
		myList.getList().remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	public void deleteFromList(Warehouse wh) {
		int index = myList.getList().indexOf(wh);
		myList.getList().remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	public void addToList(Warehouse wh) {
		myList.getList().add(wh);
		int index = myList.getList().indexOf(wh);
		fireIntervalAdded(this, index, index);
	}
}

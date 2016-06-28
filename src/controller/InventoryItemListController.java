package controller;

import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;

import model.InventoryItem;
import model.InventoryItemList;


public class InventoryItemListController extends AbstractListModel<InventoryItem> implements Observer {
	
	private InventoryItemList myList;
	private MDIChild myListView;
	
	public InventoryItemListController(InventoryItemList il) {
		super();
		myList = il;
		
		il.addObserver(this);
	}
	
	@Override
	public int getSize() {
		return myList.getList().size();
	}

	@Override
	public InventoryItem getElementAt(int index) {
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
//		for(InventoryItem p: myList)
//			p.addObserver(this);
//	}

	public void unregisterAsObserver() {
//		for(InventoryItem p: myList)
//			p.deleteObserver(this);
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
	
	public void deleteFromList(InventoryItem i) {
		int index = myList.getList().indexOf(i);
		myList.getList().remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	public void addToList(InventoryItem i) {
		myList.getList().add(i);
		int index = myList.getList().indexOf(i);
		fireIntervalAdded(this, index, index);
	}

}

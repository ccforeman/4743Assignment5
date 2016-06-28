package controller;

import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;

import model.Part;
import model.PartList;


public class PartListController extends AbstractListModel<Part> implements Observer {
	
	private PartList myList;
	private MDIChild myListView;
	
	public PartListController(PartList pl) {
		super();
		myList = pl;
		
		pl.addObserver(this);
	}
	
	@Override
	public int getSize() {
		return myList.getList().size();
	}

	@Override
	public Part getElementAt(int index) {
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
//		for(Part p: myList)
//			p.addObserver(this);
//	}

	public void unregisterAsObserver() {
//		for(Part p: myList)
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
	
	public void deleteFromList(Part wh) {
		int index = myList.getList().indexOf(wh);
		myList.getList().remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	public void addToList(Part wh) {
		myList.getList().add(wh);
		int index = myList.getList().indexOf(wh);
		fireIntervalAdded(this, index, index);
	}

}

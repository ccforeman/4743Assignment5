package controller;

import java.awt.Container;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class MDIChild extends JPanel {
	
	private Container myFrame;
	
	protected MDIParent parent;

	private String myTitle;

	private boolean singleOpenOnly;
	
	private boolean changed;
	
	public MDIChild(String title, MDIParent parent) {
		this(title);
		setMDIParent(parent);
		myFrame = null;
		singleOpenOnly = false;
	}
	
	public MDIChild(String title) {
		myTitle = title;
	}
	
	
	public String toString() {
		return myTitle;
	}
	
	public void setTitle(String title) {
		myTitle = title;
		setInternalFrameTitle(myTitle);
	}
	
	public MDIParent getMasterParent() {
		return parent;
	}
	
	public void restoreWindowState() throws PropertyVetoException {
		JInternalFrame c = (JInternalFrame) getMDIChildFrame();
		c.setIcon(false);
		c.moveToFront();
	}
	
	public String getTitle() {
		return myTitle;
	}

	public void setMDIParent(MDIParent mf) {
		parent = mf;
	}

	public boolean isSingleOpenOnly() {
		return singleOpenOnly;
	}

	public void setSingleOpenOnly(boolean singleOpen) {
		this.singleOpenOnly = singleOpen;
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public boolean saveModel() {
		return true;
	}

	protected JInternalFrame getMDIChildFrame() {
		Container tempContainer = this;
		
		while(!(tempContainer instanceof JInternalFrame) && tempContainer != null) 
			tempContainer = tempContainer.getParent();
		if(tempContainer != null)
			return (JInternalFrame) tempContainer;
		return null;
	}

	private void setInternalFrameTitle(String t) {
		if(myFrame == null)
			myFrame = getMDIChildFrame();
		if(myFrame != null)
			((JInternalFrame) myFrame).setTitle(t);
	}

	protected void setInternalFrameVisible(boolean v) {
		if(myFrame == null)
			myFrame = getMDIChildFrame();
		if(myFrame != null)
			((JInternalFrame) myFrame).setVisible(v);
	}

	protected void childClosing() {
		parent.removeFromOpenViews(this);
		//TEST: this should always print as the MDI Child closes, no matter how the child is closed
		//e.g., click close on the JInternalFrame, click Quit on menu, kill JVM, click close on MDI Parent
		System.err.println("MDIChild is closing...");
	}
	

}

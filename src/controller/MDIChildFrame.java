package controller;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import database.GatewayException;

public class MDIChildFrame extends JInternalFrame implements InternalFrameListener {

	protected MDIChild myChild;

	public MDIChildFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable
			, MDIChild child) {
		super(title, resizable, closable, maximizable, iconifiable);
		myChild = child;
		this.add(child, BorderLayout.CENTER);
		
		this.addInternalFrameListener(this);
	}
	
	
	public boolean okToClose() {
		if(myChild.isChanged()) {
			int option = JOptionPane.showConfirmDialog(myChild.getMasterParent(), "Do you want to save your changes first?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
            if(option == JOptionPane.CANCEL_OPTION)
            	return false;
            //if Yes, try to save. if error then abort closing
            if(option == JOptionPane.YES_NO_OPTION) {
            	if(!myChild.saveModel())
            		return false;
            }
		}
		return true;
	}
	
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
	}

	public void closeChild() {
		myChild.childClosing();
	}
	
	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		if(myChild.getClass().equals(view.PartEditView.class)) {
			try {
				((view.PartEditView) myChild).getCurrentPart().getGateway().releaseLock(((view.PartEditView) myChild).getCurrentPart().getId());
			} catch (GatewayException e1) {
				return;
			}
			MDIParent.partsBeingEdited.remove(((view.PartEditView) myChild).getCurrentPart());
		}
		if(!okToClose())
			return;
		else
			closeChild();
			
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

}

package model;

import java.util.Observable;

import database.GatewayException;
import database.PartTableGateway;

public class Part extends Observable {

	private long id;
	private String partNum;
	private String partName;
	private String vendor;
	private String unitOfQuantity;
	private String vendorPartNum;
	
	private PartTableGateway gateway;
	
	public Part() {
		id = 0L;
		// partNum = "New Part";
		partNum = "";
		partName = vendor = unitOfQuantity = vendorPartNum = "";
		setChanged();
	}
	
	
	public Part(String partNum, String partName, String vendor, String unitOfQuantity, String vendorPartNum) {
		this();
		if(!validPartNum(partNum))
			throw new IllegalArgumentException("Invalid Part #!");
		if(!validPartName(partName))
			throw new IllegalArgumentException("Invalid Part Name!");
		if(!validVendor(vendor))
			throw new IllegalArgumentException("Invalid Vendor!");
		if(!validUnitOfQuantity(unitOfQuantity))
			throw new IllegalArgumentException("Invalid Unit of Quantity!");
		if(!validVendorPartNum(vendorPartNum))
			throw new IllegalArgumentException("Invalid Vendor Part #!");
		
		this.partNum = partNum;
		this.partName = partName;
		this.setVendor(vendor);
		this.unitOfQuantity = unitOfQuantity;
		this.vendorPartNum = vendorPartNum;
	}
	
	public PartTableGateway getGateway() {
		return this.gateway;
	}
	
	public void setGateway(PartTableGateway gateway) {
		this.gateway = gateway;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	
	public String getPartNum() {
		return partNum;
	}
	
	public void setPartNum(String partNum) {
		if(!validPartNum(partNum))
			throw new IllegalArgumentException("Invalid Part #!");
		this.partNum = partNum;
		setChanged();
	}
	
	
	public String getPartName() {
		return partName;
	}
	
	public void setPartName(String partName) {
		if(!validPartName(partName))
			throw new IllegalArgumentException("Invalid Part Name!");
		
		this.partName = partName;
		setChanged();
	}
	
	public String getVendor() {
		return vendor;
	}


	public void setVendor(String vendor) {
		if(!validVendor(vendor))
			throw new IllegalArgumentException("Invalid Vendor!");
		
		this.vendor = vendor;
		setChanged();
	}
	
	
	public String getUnitOfQuantity() {
		return unitOfQuantity;
	}
	
	public void setUnitOfQuantity(String unitOfQuantity) {
		if(!validUnitOfQuantity(unitOfQuantity))
			throw new IllegalArgumentException("Invalid Unit of Quantity!");
		
		this.unitOfQuantity = unitOfQuantity;
		setChanged();
	}
	
	public String getVendorPartNum() {
		return vendorPartNum;
	}
	
	public void setVendorPartNum(String vendorPartNum) {
		if(!validVendorPartNum(vendorPartNum))
			throw new IllegalArgumentException("Invalid Vendor Part #!");
		
		this.vendorPartNum = vendorPartNum;
		setChanged();
	}
	
	public boolean validPartNum(String partNum) {
		if(partNum == null || partNum.equals(""))
			return false;
		
		if(partNum.length() > 20)
			return false;
		
		return true;
	}
	
	public boolean validPartName(String partName) {
		if(partName == null || partName.equals(""))
			return false;
		
		if(partName.length() > 255)
			return false;
		
		return true;
	}
	
	public boolean validVendor(String vendor) {
		if(vendor == null)
			return false;
		
		if(vendor.length() > 255)
			return false;
		
		return true;
	}
	
	public boolean validUnitOfQuantity(String unit) {
		if(unit == null)
			return false;
		if(unit.equals("Linear Ft.") || unit.equals("Pieces"))
			return true;
		else
			return false;
	}
	
	public boolean validVendorPartNum(String vpn) {
		if(vpn == null)
			return false;
		
		if(vpn.length() > 255)
			return false;
		
		return true;
	}
	
	public void finishUpdate() throws GatewayException {
		Part original = null;
		
		if(this.getId() == 0 )
			if(gateway.partAlreadyExists(this.getPartNum(), this.getId()))
				throw new GatewayException("Part # is already in Database.");
		
		try {
			if(this.getId() == 0 ) {
				this.setId(gateway.insertPart(this));
			} else {
				original = gateway.fetchPart(this.getId());
				gateway.updatePart(this);
			}
			
			notifyObservers();
		
		} catch (GatewayException e) {
			if(original != null) {
				this.setPartNum(original.getPartNum());
				this.setPartName(original.getPartName());
				this.setVendor(original.getVendor());
				this.setUnitOfQuantity(original.getUnitOfQuantity());
				this.setVendorPartNum(original.getVendorPartNum());
				
			}
			throw new GatewayException("Error trying to update Part in Database.");
		}
		controller.MDIParent.partsBeingEdited.remove(this);
	}
	
	public boolean partAlreadyExists(String partNum, long id) throws GatewayException {
		if(gateway.partAlreadyExists(partNum, id))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
//		StringBuilder str = new StringBuilder(getPartNum());
//	
//		str.append(" partName: ").append(partName).append(" Vendor: ").append(vendor);
//		str.append(" unitOfQuantity: ").append(unitOfQuantity).append(" vendorPartNum: ").append(vendorPartNum);
	
		return this.getPartNum();
	}
	
	
}

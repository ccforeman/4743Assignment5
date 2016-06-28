package logging;

public class ReportData {
	private String warehouseName;
	private String partNum;
	private String partName;
	private double quantity;
	private String unitOfQuantity;
	
	public ReportData(String name, String pnum, String pname, double q, String uofq) {
		warehouseName = name;
		partNum = pnum;
		partName = pname;
		quantity = q;
		unitOfQuantity = uofq;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public String getPartNum() {
		return partNum;
	}

	public String getPartName() {
		return partName;
	}

	public double getQuantity() {
		return quantity;
	}

	public String getUnitOfQuantity() {
		return unitOfQuantity;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public void setUnitOfQuantity(String unitOfQuantity) {
		this.unitOfQuantity = unitOfQuantity;
	}
	
	@Override
	public String toString() {
		StringBuilder row = new StringBuilder("");
		row.append(getWarehouseName() + "\t").append(getPartNum() + "\t").append(getPartName() 
				+ "\t").append(((Double) getQuantity()).toString() + "\t").append(getUnitOfQuantity());		
		return row.toString();
	}
}

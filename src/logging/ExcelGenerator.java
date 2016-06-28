package logging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import controller.MDIParent;
import database.GatewayException;
import database.InventoryItemTableGateway;

public class ExcelGenerator {
	private List<ReportData> reportData;
	
	private static Logger log = Logger.getLogger(ExcelGenerator.class);
	
	
	public ExcelGenerator(InventoryItemTableGateway gateway, MDIParent parent, String filename) {
		
		try {
			PropertyConfigurator.configure(new FileInputStream("log4j.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		log.info("Report creation started");
		
		Path file = Paths.get(filename + ".xls");
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date now = Calendar.getInstance().getTime();
		String date = format.format(now);
		String header = "Warehouse Inventory Summary - " + date + "\n";
		String columns = "Warehouse Name\tPart #\tPart Name\tQuantity\tUnit"; 
		
		try {
			this.reportData = gateway.fetchDataForReport();
		} catch (GatewayException e) {
			parent.displayChildMessage(e.getMessage());
		}
		
		int numrecs = this.reportData.size();
		if(numrecs > 0) 
			log.debug("Fetched " + numrecs + " inventory records for report");
		else
			log.warn("No inventory records found for report");
		
		List<String> iterableList = new ArrayList<String>();
		iterableList.add(header);
		iterableList.add(columns);
		
		int i = 0;
		for(ReportData rd : this.reportData) {
			i++;
			log.trace("Writing record #" + i + " to report");
			iterableList.add(rd.toString());
		}
		
		
		try {
			Files.write(file, iterableList, Charset.forName("UTF-8"));
			log.info("Writing report to file");
		} catch (IOException e) {
			parent.displayChildMessage(e.getMessage());
		}
	}

}

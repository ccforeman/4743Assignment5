package logging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import controller.MDIParent;
import database.GatewayException;
import database.InventoryItemTableGateway;

public class PDFGenerator {
	private InventoryItemTableGateway gateway;
	private List<ReportData> dataList;
	private String filename;
	
	
	private PDPageContentStream contents;
	private PDDocument doc;
	private PDPage page;
	
	private PDRectangle pageSize;
    private float pageHeight;
    private float pageWidth;
    private float fontSize;
    private float titleWidth;
    private float tCenterW;
    private int pagecount;
    private String dateString;
	
	
	private float headerWidth;
	private float headerCentered;
	private float tabsize;
	
	private float nextline;
	private float runningPS;
	
	private final PDFont fontPlain = PDType1Font.HELVETICA;
	private final PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	private final PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
	private final PDFont fontMono = PDType1Font.COURIER;
	private final String title = "Warehouse Inventory Summary";
	
	private final String colWH = "Warehouse Name";
	private final String colPNo = "Part #";
	private final String colPName = "Part Name";
	private final String colQuant = "Quantity";
	private final String colUnit = "Unit";
	
	private static Logger log = Logger.getLogger(PDFGenerator.class);
	
	public PDFGenerator( String filename, InventoryItemTableGateway gateway, MDIParent parent ) throws IOException {
		try {
			PropertyConfigurator.configure(new FileInputStream("log4j.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date now = Calendar.getInstance().getTime();
		dateString = format.format(now);
		
		log.info("Report creation started");
		
		try {
			this.dataList = gateway.fetchDataForReport();
		} catch (GatewayException e) {
			parent.displayChildMessage("Error fetching Warehouse Inventory Data");
		}
		int numrecs = this.dataList.size();
		if(numrecs > 0)
			log.debug("Fetched " + numrecs + " inventory records for report");
		else
			log.warn("No inventory records found for report");
		
		this.filename = filename;
		
		/* create doc */
        doc = new PDDocument();
        createNewPage();
        createContentStream();
        createPageHeader();
        createPageColumns();
        createPageData();

		
	}
	
	private void createNewPage() throws IOException {
		/* add page */
        page = new PDPage(PDRectangle.A4);
        page.setRotation(90);
        doc.addPage(page);
        pagecount++;
        log.info("Page " + pagecount + " created");
        
        /* get important values */
        pageSize = page.getMediaBox();
        pageHeight = pageSize.getWidth();
        pageWidth = pageSize.getHeight();
        fontSize = 24;
        titleWidth = fontPlain.getStringWidth( title ) * fontSize / 1000f;
        tCenterW = (pageWidth - titleWidth) / 2;
        
        headerWidth = fontPlain.getStringWidth(colWH + colPNo + colPName + colQuant + colUnit) * 12 / 1000f;
        tabsize = (pageWidth - headerWidth) / 5; 
        headerCentered = (pageWidth - headerWidth) / 2;
        
	}
	
	private void createContentStream() throws IOException {
		contents = new PDPageContentStream(doc, page);
        contents.transform(new Matrix(0, 1, -1, 0, pageHeight, 0));
        contents.setFont( fontBold, fontSize );
	}
	
	private void createPageHeader() throws IOException {
		/* Title Start */
        contents.beginText();
        contents.newLineAtOffset(tCenterW, pageHeight - 100);
        contents.showText(title);
        contents.endText();
        /* Title End */
        
        
        /* Add Date and Time */
        contents.setFont(fontPlain, 8);
        contents.beginText();
        contents.newLineAtOffset(15, 15);
        contents.showText(dateString);
        contents.endText();
        
        /* Add Page Number */
        contents.beginText();
        contents.newLineAtOffset(pageWidth - 15, 15);
        contents.showText("" + pagecount);
        contents.endText();
	}
	
	private void createPageColumns() throws IOException {
		/* Column Headers Start */
        contents.setFont(fontBold, 12);
        contents.beginText();
        contents.newLineAtOffset( tabsize, pageHeight - 150);
        contents.showText(colWH);
        contents.newLineAtOffset(colWH.length() +  tabsize, 0);
        contents.showText(colPNo);
        contents.newLineAtOffset(colPNo.length() + tabsize, 0);
        contents.showText(colPName);
        contents.newLineAtOffset(colPName.length() + tabsize, 0);
        contents.showText(colQuant);
        contents.newLineAtOffset(colQuant.length() + tabsize, 0);
        contents.showText(colUnit);
        contents.endText();
        contents.setFont(fontPlain, 12);
        /* Column Headers End */
	}
	
	private void createPageData() throws IOException {
		nextline = fontPlain.getHeight(12) + 20;
        runningPS = pageHeight - 160;
        /* Warehouse Data Start */
        int i = 0;
        for(ReportData rd : dataList) {
        	i++;
        	log.trace("Writing record #" + i + " to report");
        	if((runningPS - nextline - fontPlain.getHeight((int)12)) < 0) {
        		log.info("Record #" + i + " will cause a page break");
        		contents.close();
        		createNewPage();
        		createContentStream();
                createPageHeader();
                createPageColumns();
                runningPS = pageHeight - 160;
        	}
        		
        	contents.beginText();
        	contents.newLineAtOffset(tabsize, (runningPS -= nextline));
        	contents.showText(rd.getWarehouseName());
        	contents.newLineAtOffset(colWH.length() +  tabsize, 0);
        	contents.showText(rd.getPartNum());
        	contents.newLineAtOffset(colPNo.length() + tabsize, 0);
        	contents.showText(rd.getPartName());
        	contents.newLineAtOffset(colPName.length() + tabsize, 0);
        	contents.showText(((Double) rd.getQuantity()).toString());
        	contents.newLineAtOffset(colQuant.length() + tabsize, 0);
        	contents.showText(rd.getUnitOfQuantity());
        	contents.endText();
        }
        /* Warehouse Data End */
        
        
        /* Save and Close everything */
        contents.close();
        doc.save(filename + ".pdf" );
        log.info("Writing report to file");
        doc.close();
	}
	
	
	
	
}

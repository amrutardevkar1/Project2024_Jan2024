package project2024.jan2024.excelreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelConnector {
	
	private volatile static ExcelConnector uniqueConnector;
	FileInputStream excelFileReader;
	FileOutputStream excelFileWriter;
	private XSSFWorkbook excelWorkbook;
	OPCPackage opcPackage;
	private XSSFSheet excelSheet;
	private static final Logger logger = Logger.getLogger(ExcelConnector.class);
	private Map<Object, Object> dictionary = new HashMap<>();
	private Map<Long, Map<Object, Object>> globalHashmap= new HashMap<>();
	private List<HashMap<Object,Object>> list = new ArrayList<>();
	DataFormatter formatter =  new DataFormatter();
	private int totalRow;
	private int totalCol;
	private int currentRow;
	private int currentCol;
	private int currentColLoadDataSheet;
	private int currentCellNum =0;
	Cell cell;
	Cell currentCell;
	CellStyle cellstyle;
	String key="";
	String value="";
	private Row row= null;
	private Row currentDataRow = null;
	private Row headerRow=null;
	
	private ExcelConnector()
	{
		
	}
	
	public static ExcelConnector getUniqueConnector()
	{
		if(uniqueConnector==null) {
			synchronized (ExcelConnector.class)
			{
				if(uniqueConnector==null)
			    {
					uniqueConnector = new ExcelConnector();
				}
			}
		}
		
		return uniqueConnector;
	}
	
	public List<HashMap<Object,Object>> getList()
	{
		return list;
	}
	
	public Map<Object, Object> getGlobalHashmap(){
		return globalHashmap.get(Thread.currentThread().getId());
	}
	
	public XSSFSheet getExcelSheet()
	{
		return excelSheet;
	}
	
	public void getTestData(String sheetname)
	{
		try
		{
			readExcelFile(System.getProperty("user.dir")+ "\\TestData\\TestData.xlsx");
			loadTestdataSheet(sheetname);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void readExcelFile(String excelFileName) 
	{
		try
		{
			if(new File(excelFileName).exists())
			{
				OPCPackage opcPackage = OPCPackage.open(excelFileName);
				excelFileReader= new FileInputStream(new File(excelFileName));
				excelWorkbook = new XSSFWorkbook(excelFileReader);
			}
			else
			{
				logger.info("Unable to read file at given location:");
			}
		}
		catch(IOException | InvalidFormatException e)
		{
			logger.info("Excel file not found" + e.getLocalizedMessage());
		}
	}
	
	public void loadTestCasesExcelSheet(String excelSheetName)
	{
		excelSheet = excelWorkbook.getSheet(excelSheetName);
		totalRow= excelSheet.getPhysicalNumberOfRows();
		totalCol = excelSheet.getRow(0).getLastCellNum();
		currentCol=2;
		
	}
	
	public void loadExcelSheet(String excelSheetName)
	{
		excelSheet = excelWorkbook.getSheet(excelSheetName);
		totalRow= excelSheet.getPhysicalNumberOfRows();
		totalCol= excelSheet.getRow(0).getLastCellNum();
		currentCol=2;
	}
	
	public void loadTestdataSheet(String excelSheetName)
	{
		loadExcelSheet(excelSheetName);
		currentCol=2;
		dictionary= new HashMap<>();
		while(currentCol<= totalCol)
		{
			dictionary= new HashMap<>();
			Iterator<Row> rowIterator = excelSheet.iterator();
			currentDataRow= rowIterator.next();
			while(rowIterator.hasNext())
			{
				row= rowIterator.next();
				if((row.getCell(currentCol)!= null && row.getCell(1)!= null))
				{
					key= row.getCell(1).toString().trim();
					value = formatter.formatCellValue(row.getCell(currentCol));
					
					if(key!=null && value !=null)
					{
						dictionary.put(key, value);
					}
				}
			}
			
			currentCol= currentCol+1;
			list.add((HashMap<Object,Object>) dictionary);
		}
	}
 
	public List<String> getColumnData(String path, int colIndex, String sheetname)
	{
		List<String> rowvalues = new ArrayList<>();
		try(FileInputStream file = new FileInputStream(new File(path)))
		{
			opcPackage= OPCPackage.open(file);
			excelWorkbook = new XSSFWorkbook(opcPackage);
			excelSheet =excelWorkbook.getSheet(sheetname);
			 int rowCount = excelSheet.getPhysicalNumberOfRows();
			 logger.info("Row Count is:" + rowCount);
			 
			 Iterator<Row> rowIterator = excelSheet.iterator();
			 Row row1;
			 
			 while(rowIterator.hasNext())
			 {
				 row1= rowIterator.next();
				 if((!row1.getCell(colIndex).toString().isEmpty()) && (row1.getCell(colIndex)!= null) && (row1.getCell(colIndex).toString().equalsIgnoreCase("Yes")))
				 {
					 rowvalues.add(row1.getCell(colIndex-5).toString() + ";" + row1.getCell(colIndex-4).toString());
						 
				 }
			 }
			 
			 return rowvalues;
		}
		catch(IOException | InvalidFormatException e)
		{
			logger.info("Unable to getColumnData");
			return rowvalues;
		}
	}
}

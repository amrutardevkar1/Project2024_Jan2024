package project2024.jan2024.testrunner;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import project2024.jan2024.testbase.BaseClass;

public class TestRunner extends BaseClass {
	
	private static final Logger logger = Logger.getLogger(TestRunner.class);
	private static String testname;
	private static String classname;
	private static String testCaseSheet = "TestCases";
	private static String testClassString = "project2024.jan2024.";
	
	public TestRunner()
	{
		
	}
	
	public static String getClassname()
	{
		return classname;
	}
	
	public static void setClassname(String classname)
	{
		TestRunner.classname = classname;
	}
	
	public static String getTestname()
	{
		return testname;
	}
	
	public static void setTestname(String testname)
	{
		TestRunner.testname = testname;
	}
	
	@DataProvider(name ="excel data")
	 public static Object[][] createExcelData()
	 {
		String excelFilePath = System.getProperty("user.dir") +"\\TestData\\TestCases\\TestCaseSheet.xlsx";
		Map<String, String> executableTestCases = ExcelConnector.getUniqueConnector().getRunnableTestdata(excelFilePath, testCaseSheet);
		
		Object[][] twoDArray= new Object[executableTestCases.size()][2];
		Object[] keys = executableTestCases.keySet().toArray();
		Object[] values = executableTestCases.values().toArray();
		
		for(int row=0; row< twoDArray.length; row++)
		{
			twoDArray[row][0]= keys[row];
			twoDArray[row][1]= values[row];
		}
		 return twoDArray;
	 }
	
	@Test(dataProvider = "excel data")
	
	public void fetchExcelTestCase(String classNameString, String methodNameString)
	{
		try
		{
			Class<?> classname = Class.forName(testClassString + classNameString);
			logger.info("Loaded Class:" + classname);
			setClassname(classNameString);
			
			Object object = classname.newInstance();
			Method methodname = classname.getDeclaredMethod(methodNameString);
			logger.info("Got Method:" +  methodname);
			setTestname(methodNameString);
			methodname.invoke(object);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

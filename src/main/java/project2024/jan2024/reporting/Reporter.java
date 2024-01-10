package project2024.jan2024.reporting;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Reporter {
	
	private static StringBuilder reportLogger = new StringBuilder();
	
	public static StringBuilder getReportLogger()
	{
		return reportLogger;
	}
	
	public static void setReportLogger(StringBuilder reportLogger)
	{
		Reporter.reportLogger = reportLogger;
	}

	private static final String AHREF = "<p style= 'color:black;'><strong><b> Click Here : </b></strong> <a href ='";
	private static final String ANCHOR = "' target=\"_blank\"> Screen Print </a></p>";
	private static ExtentReports extent = new ExtentReports();
	private static ExtentSparkReporter  htmlReporter;
	private static Map<Long, ExtentTest> childTestMap = new HashMap();
	private static String reportFilePath = "";
	private static final Logger logger = Logger.getLogger(Reporter.class);
	
	
	public static String getReportFilePath()
	{
		return reportFilePath;
	}
	
	public static void reportExecutionInfo(String status, String message)
	{
		if(DriverProvider.getDriverProviders().get(Thread.currentThread().getId()) != null)
		{
			PrintScreenCatcher.capture(TestRunner.getTestName());
			reportExecutionInfo(status, message, PrintScreenCatcher.latestScreenshotPath);
		}
		else
		{
			reportExecutionInfo(status, message, null);
		}
	}
	
	public static void reportEXecutionInfo(String status, String message, String screenshotPath)
	{
		
		ExtentTest childTest = childTestMap.get(Thread.currentThread().getId());
		
		try
		{
			reportLogger.append(message);
			reportLogger.append("\r\n");
			if(status.equalsIgnoreCase("FAIL"))
			{
				logMessage(message, screenshotPath, childTest, Status.FAIL, "red");
				
			}
			else if(status.equalsIgnoreCase("PASS"))
			{
				logMessage(message, screenshotPath, childTest, Status.PASS, "red");
				
			}
			else if(status.equalsIgnoreCase("INFO"))
			{
				logMessage(message, screenshotPath, childTest, Status.INFO, "red");
				
			}
			else
			{
				childTest.log(Status.SKIP, message);
			}
		}
		
		catch(NoSuchElementException | TimeoutException e)
		{
			logger.info("Report writing failed");
			
		}
	}
	
	private static synchronized void logMessage(String message, String screenshotPath, ExtentTest childTest, Status status, String color)
	{
		String logDetails= "<p style=\"color:"+ color + ";\"><strong> <b>" + message + "</b></strong> </p>";
		
		if(screenshotPath != null)
		{
			logDetails +="<p>"+ AHREF + screenshotPath + ANCHOR+ "</p>";
		}
		
		childTest.log(status, logDetails);
		extent.flush();
	}
	
	public static ExtentTest addParentTest(String testName, String category)
	{
		return extent.createTest(testName).assignCategory(category);
	}
	
	public static void addChildTest(ExtentTest parentTest, String childName)
	{
		childTestMap.put(Thread.currentThread().getId(), parentTest.createNode(childName));
		
	}
	
	public static void setUp(String filePath)
	{
		reportFilePath= filePath;
		try
		{
			InetAddress ip = InetAddress.getLocalHost();
			htmlReporter = new ExtentSparkReporter(filePath);
			extent.attachReporter(htmlReporter);
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Host Name", ip.getHostName());
			extent.setSystemInfo("User Name", System.getProperty("user.Name"));
			
			htmlReporter.config().setDocumentTitle("Project 2024 Autpmation Execution Report");
			htmlReporter.config().setReportName("Test Execution Report Jan 2024");
			htmlReporter.config().setTheme(Theme.STANDARD);
		}
		
		catch(Exception e)
		{
			logger.info("Setup Failed " + e.getMessage() + e.getStackTrace());
		}
	}
	
	public static void flush()
	{
		extent.flush();
		
	}
}


package project2024.jan2024.utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class PrintScreenCatcher {
	
	public static String latestScreenshotPath;
	protected static String screenshotPath= "";
	
	public static void setUp(String screenshotPath)
	{
		PrintScreenCatcher.screenshotPath= screenshotPath;
		File screenshotFile = new File(screenshotPath);
		if(!(screenshotFile.exists() && screenshotFile.isDirectory()))
		{
			screenshotFile.mkdirs();
		}
		
	}
	
	public static void capture(String testname)
	{
		if(DriverProvider.getDriverProvider().get(Thread.currentThread().getId())==null)
		{
			return;
		}
	  
		String dateFormat = new SimpleDateFormat("dd-mm--yyyy_HH-mm-ss").format(new GregorianCalendar().getTime());
		String finalPath= null;
		try
		{
			File screenshot = ((TakesScreenshot) DriverProvider.getDriverProvider().get(Thread.currentThread().getId()).getDriver()).getScreenshotAs(OutputType.FILE);
		    finalPath = screenshotPath+ testname+ "_" +Thread.currentThread().getId() + "_" + dateFormat +".png";
		    FileUtils.copyFile(screenshot , new File(finalPath));
		    finalPath = ".\\ScreenShots\\"+ testname+ "_" +Thread.currentThread().getId() + "_" + dateFormat +".png";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		latestScreenshotPath= finalPath;

	}

}

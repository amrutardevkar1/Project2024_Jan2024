package project2024.jan2024.utilities;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import project2024.jan2024.reporting.Reporter;

public class DriverProvider {

	private static final Logger logger = Logger.getLogger(DriverProvider.class);
	private static ConcurrentHashMap<Long, DriverProvider> driverProvider = new ConcurrentHashMap();
	
	public static ConcurrentHashMap<Long, DriverProvider> getDriverProvider()
	{
		return driverProvider;
		
	}
	
	private WebDriver driver;
	private WebElement element = null;
	private static String downloadPath = System.getProperty("user.dir") + "\\TestData\\Reports\\DownloadReports\\" + Thread.currentThread().getId() +"\\";
	private final PropertyManager props = PropertyManager.getInstance();
	protected static ThreadLocal<WebDriver> threadlocal = new ThreadLocal<>();
	
	public DriverProvider()
	{
		if(props.getProperty("browser").equalsIgnoreCase("chrome"))
		{
			System.setProperty("webdriver.driver.chrome", System.getProperty("USER_DIR") + props.getProperty("chromepath"));
			HashMap<String, Object>  chromePrefs = new HashMap();
		    
			File file = new File(downloadPath);
			if(!file.exists())
			{
				file.mkdirs();
			}
			
			chromePrefs.put("download.default_ditectory", downloadPath);
			chromePrefs.put("profile.default_content_setting_vlaues.automatic_downloads", 1);
			chromePrefs.put("download.prompt_for_downlaod", false);
			chromePrefs.put("download.directory_upgrade", true);
			
			ChromeOptions options = new ChromeOptions();
			options.addArguments("enable-automation");
			options.setExperimentalOption("prefs", chromePrefs);
			
			driver = new ChromeDriver(options);
			
			driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
			
			threadlocal.set(driver);
			options.addArguments("start-maximized");
		}
		
		driverProvider.put(Thread.currentThread().getId(), this);
	}
	
	public WebDriver getDriver()
	{
		return threadlocal.get();
	}
	
	public String getDownloadPath()
	{
		return downloadPath;
	}
	
	public void quit()
	{
		if(getDriver() != null)
		{
			getDriver().close();
			getDriver().quit();
			threadlocal.remove();
		}
	}
	
	public void implicitWait(int time)
	{
		try
		{
			getDriver().manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
			
		}
		
		catch(Exception e)
		{
			Reporter.reportExecutionInfo("FAIL", "Page load timeout time is exceeded:" + e.getMessage());
		}
	}
	
	public void driverWait(int time)
	{
		try {
		int wait = time*1000;
		Thread.sleep(wait);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean waitTillPresent(By locator, int timeout)
	{
		try
		{
			WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
			wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			return true;
		}
		catch(Exception e)
		{
			Reporter.reportExecutionInfo("FAIL", "Element to be located is not present" + e.getMessage());
			return false;
		}
	}
	
	public void explicitWait(By element, int timeout)
	{
		try
		{
			WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
			wait.until(ExpectedConditions.visibilityOf(getDriver().findElement(element)));
		}
		catch(NoSuchElementException | TimeoutException e)
		{
			logger.info("Excplicit Wait exceeded");
			Reporter.reportExecutionInfo("FAIL", "ExplicitWait timeout exceeded:" +e.getMessage());
		}
		{
			
		}
	}
	
	public WebElement getElement(By identifier)
	{
		try
		{
			waitTillPresent(identifier,10);
			element= getDriver().findElement(identifier);
		}
		catch(NoSuchElementException e)
		{
			logger.info("Element to be find is not found: " + e.getMessage());
		}
		
		if(element == null)
		{
			logger.info("Element not found");
		}
		return element;
	}
	
	public void moveToElement(By identifier)
	{
		try
		{
			waitTillPresent(identifier,10);
			((JavascriptExecutor)getDriver()).executeScript("arguments[0].scrollIntoView(false);",getElement(identifier));
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void hoverOverElement(By identifier)
	{
		try 
		{
			waitTillPresent(identifier, 10);
			Actions act = new Actions(getDriver());
			act.moveToElement(getElement(identifier)).build().perform();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void doubleClickElement(By identifier)
	{
		try
		{
			waitTillPresent(identifier, 10);
			Actions act = new Actions(getDriver());
			act.doubleClick(getElement(identifier)).build().perform();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void clickOnButton(By identifier)
	{
		try
		{
			waitTillPresent(identifier, 10);
			moveToElement(identifier);
			getElement(identifier).click();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void enterValueInTextbox(By identifier, String value)
	{
		try
		{
			waitTillPresent(identifier, 10);
			moveToElement(identifier);
			WebElement ele = getElement(identifier);
			if(ele.isEnabled())
			{
				driverWait(2);
				ele.clear();
				ele.sendKeys(value);
				Reporter.reportExecutionInfo("PASS", "Value entered in textbox:");
			}
			
		}
		catch(Exception e)
		{
			Reporter.reportExecutionInfo("FAIL", "Value is not entered :" +e.getMessage());
		}
	}
	
	public void scrollByJavascriptExecutor(By identifier)
	{
		try
		{
			WebElement ele = getElement(identifier);
			((JavascriptExecutor)getDriver()).executeScript("arguments[0]. scrollIntoView;", ele);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void selectDropdownByValue(By identifier, String value)
	{
		try
		{
			WebElement ele = getElement(identifier);
			waitTillPresent(identifier, 10);
			
			if(!ele.isSelected())
			{
				Select s = new Select(ele);
				List<WebElement> options = s.getOptions();
				
				for(WebElement op: options)
				{
					String value1 = op.getAttribute(value);
					if(value1.trim().equalsIgnoreCase(value.trim()))
							{
						         s.selectByValue(value);
							}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

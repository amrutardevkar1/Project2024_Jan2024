package project2024.jan2024.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertyManager {

	private static final String ConfigFilePath = System.getProperty("user.dir") + "\\src\\main\\java\\Poperties\\config.properties";
	private Properties properties = null;
	private static PropertyManager instance = null;
	
	private PropertyManager()
	{
		loadProperties();
	}
	
	public static PropertyManager getInstance()
	{
		if(instance == null)
		{
			synchronized(PropertyManager.class) {
				if(instance==null)
				{
					instance = new PropertyManager();
				}
			}
		}
		return instance;
	}
	
	private void loadProperties()
	{
		properties = new Properties();
		try {
		FileInputStream file = new FileInputStream(ConfigFilePath);
		properties.load(file);
		file.close();
	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public String getProperty(String key)
	{
		
			String val = null;
			if(key!= null) {
				if(properties!= null)
				{
					val= properties.getProperty(key);
				}
			}
			return val;
		}
	}


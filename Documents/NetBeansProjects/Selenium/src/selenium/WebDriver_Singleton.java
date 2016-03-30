package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriver_Singleton {

	private static WebDriver driver = null;
	
	public WebDriver_Singleton() {
		
	}
	
	public static WebDriver getInstance() {
		if(driver == null) {
			driver = new FirefoxDriver();
		}
		return driver;
	}
	
}

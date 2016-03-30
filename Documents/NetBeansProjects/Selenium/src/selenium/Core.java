package selenium;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

public class Core {
	
	WebDriver driver = WebDriver_Singleton.getInstance();

	
	private static final String USER_NAME = "";
	private static final String PASSWORD = "";
	
	Map<String, String> findElements = new HashMap<String, String>();
    private static final String FILE_NAME = "messages.txt";
	protected ArrayList<String> messages = new ArrayList<String>();
	protected ArrayList<String> alreadyMessaged = new ArrayList<String>();	
	protected HashMap<String, Integer> messagesSent = new HashMap<String, Integer>();
    String baseUrl = "https://www.okcupid.com/";

    public static void main(String[] args) {
    	Core core = new Core();
    	//core.run_statistics();
    	core.initialize();
    }
    private void run_statistics() {
        //Statistics stats =  new Statistics(driver);
    }
    protected void initialize() {
    	populateMap();
    	driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    	driver.get(baseUrl);
    	createFileReader();
        signIn();

    }
    
    private void populateMap() {
    	findElements = new HashMap<String, String>();
    	
    	findElements.put("browse" , ".//*[@id='navigation']/div[1]/ul/li[1]/a/span");
    	findElements.put("profile" , "a[class=\"image_link\"]");
    	findElements.put("message" , ".//*[@id='profile2015']/div[1]/div/div[2]/button[1]");
    	findElements.put("signIn" , ".//*[@id='open_sign_in_button']");
    	findElements.put("userName", ".//*[@id='login_username']");
    	findElements.put("password", ".//*[@id='login_password']");
    	findElements.put("submit", ".//*[@id='login_password']");
    	findElements.put("write_message", ".//textarea[starts-with(@id,'message_')]");
    	findElements.put("alreadyMsg", "div[class=\"timestamp\"]");
    	findElements.put("dialogBox", ".//*[@id='global_messaging_container']/div[2]/div[1]/div[1]/a[3]/i");;
    	findElements.put("profileName", ".//*[@id='profile2015']/div[1]/div/div[1]/div[2]/div[1]");
    	
    	//Delete Messages
    	findElements.put("messages", ".//*[@id='nav_mailbox']/a/span[1]/i");
    	findElements.put("SeeAll", ".//*[@id='messages-dropdown']/a[2]");
    	findElements.put("Sent", ".//*[@id='main_content']/div[1]/ul/li[2]/a");
    	
    	//Statistics
    	findElements.put("Visitors", ".//*[@id='nav_visitors']/a/span[1]/i");
    	findElements.put("VisitsNext", ".//*[@id='main_column']/div[2]/ul/li[2]/a");	
    	findElements.put("sent", ".//*[@id='main_content']/div[1]/ul/li[2]/a");
    	findElements.put("received", "");
	}

	private void createFileReader() {
    	String line = null;
    	try {
    		FileReader fileReader = new FileReader("resources/" + FILE_NAME);
    		BufferedReader bufferedReader = new BufferedReader(fileReader);
    		
    		while((line = bufferedReader.readLine()) != null) {
    			messages.add(line);
    		}
    		System.out.println("Already Messaged:");
    		FileReader fileReader1 = new FileReader("resources/statistics.txt");
    		BufferedReader bufferedReader1 = new BufferedReader(fileReader1);

    		while((line = bufferedReader1.readLine()) != null) {
    			alreadyMessaged.add(line.split(" ")[0]);
    			System.out.println("Added: " + line.split(" ")[0]);
    		}
    		bufferedReader.close();
    		bufferedReader1.close();
    	}
    	catch(FileNotFoundException ex) {
    		System.out.println("Can't find file: " + FILE_NAME);
    		ex.printStackTrace();
    	}
    	catch(IOException ex) {
    		System.out.println("Error reading file " + FILE_NAME);
    		ex.printStackTrace();
    	}		
	}
	
	protected void populateStats() {
		for(String msg : messages) {
			messagesSent.put(msg, 0);
		}
    	String line = null;
    	try {
    		FileReader fileReader = new FileReader("resources/statistics.txt");
    		BufferedReader bufferedReader = new BufferedReader(fileReader);

    		while((line = bufferedReader.readLine()) != null) {
    			if(line.split("-").length > 1) {
    				String msg = line.split("-")[1].trim();
    				if(messagesSent.get(msg) != null){
	    				int totalMessages = messagesSent.get(msg);
	    				messagesSent.put(msg, ++totalMessages);
    				}
    			}
    			//System.out.println("Added: " + line.split(" ")[1]);
    		}
    		bufferedReader.close();
    	}
    	catch(FileNotFoundException ex) {
    		System.out.println("Can't find file: " + FILE_NAME);
    		ex.printStackTrace();
    	}
    	catch(IOException ex) {
    		System.out.println("Error reading file " + FILE_NAME);
    		ex.printStackTrace();
    	}		
}

	protected void scrollToBottomOfPage() {
	    	((JavascriptExecutor) driver)
	        .executeScript("window.scrollTo(0, document.body.scrollHeight)");  
	    	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	    	System.out.println("SCROLLING...");
    }
	
	protected void scrollToMaximumBottom() {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		Long value = (Long) executor.executeScript("return window.scrollY;"); 
		while(true) {
			if(value == 0) {
				System.out.println("Bottom Reached!");
				break;
			}
			else {
				scrollToBottomOfPage();
				value = (Long) executor.executeScript("return window.scrollY;");
			}			
		}
	}
	public void writeToTextFile(String str) {
		FileWriter writer = null;
		try {
			writer = new FileWriter("resources/statistics.txt", true);
			writer.write(str);
			writer.write(System.getProperty("line.separator"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
    
    public void closeMessageBox() {
    	if(!driver.findElements(By.xpath(findElements.get("dialogBox"))).isEmpty())
    		driver.findElement(By.xpath(findElements.get("dialogBox"))).click();
    }
    
	private void signIn() {
    	driver.findElement(By.xpath(findElements.get("signIn"))).click();
    	driver.findElement(By.xpath(findElements.get("userName"))).sendKeys(USER_NAME);
    	driver.findElement(By.xpath(findElements.get("password"))).sendKeys(PASSWORD);
    	driver.findElement(By.xpath(findElements.get("submit"))).submit();
    }
    public ArrayList<String> getMessages() {
    	return messages;
    }
    public ArrayList<String> getAlreadyMessaged() {
    	return alreadyMessaged;
    }
    public void addAlreadyMessaged(String userName) {
    	alreadyMessaged.add(userName);
    }
    
    //Prevents NoSuchElementException error...
    protected WebElement findDynamicElement(String path) {
    	WebElement result;
    	String xPath = path;
    	new WebDriverWait(driver, 25).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
    	try {
    		result = driver.findElement(By.xpath(xPath));
    		return result;
    	}
    	catch(WebDriverException e) {
    		return null;
    	}
    }
    protected List<WebElement> findDynamicElements(String path) {
    	List<WebElement> result;
    	String xPath = path;
    	new WebDriverWait(driver, 25).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xPath)));

    	try {
    		result = driver.findElements(By.xpath(xPath));
    		return result;
    	}
    	catch(WebDriverException e) {
    		return null;
    	}
    	
    }
    
    /**
     * Makes sure an element is within view before attempting to click.
     * @param element
     */
    public void click(WebElement element, String scroll) {
		Actions actions = new Actions(driver);
		try {
			element.click(); 
		} catch (WebDriverException e) {
			if(scroll.equals("down")){
				actions.sendKeys(Keys.ARROW_DOWN).perform();
				actions.sendKeys(Keys.ARROW_DOWN).perform();
				actions.sendKeys(Keys.ARROW_DOWN).perform();
			}
			else if(scroll.equals("up")){
				actions.sendKeys(Keys.ARROW_UP).perform();
				actions.sendKeys(Keys.ARROW_UP).perform();
				actions.sendKeys(Keys.ARROW_UP).perform();
			}
			element.click(); 
			System.out.println("EXCEPTION CAUGHT!");
		}
    }

    private static Function<WebDriver,WebElement> elementIdentified(final By locator) {
        return new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        };
    }
}

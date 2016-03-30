package selenium;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;

import mx4j.tools.stats.StatisticsRecorderMBeanDescription;

public class Statistics extends Core {

	private int visits;
	private Map<String, int[]> statistics = new HashMap<String, int[]>();
	
	public static void main(String[] args) {
		Statistics stats = new Statistics();
		stats.findStatistics();
	}
	
	public Statistics() {
		super();
		initialize();
		populateStats();
		createMap();
	}
	
	private void createMap() {
		int[] stats = new int[2];
		for(String msg : messages) {
			stats[1] = messagesSent.get(msg);
			statistics.put(msg, stats);
		}
	}

	private void findStatistics() {
		driver.findElement(By.xpath(findElements.get("Visitors"))).click();
		driver.findElement(By.xpath(findElements.get("VisitsNext"))).click();
		List<WebElement> elements = new ArrayList<WebElement>();
		
		elements = driver.findElements(By.xpath("//div[starts-with(@id, 'usr-')]"));
		String current_page = driver.findElement(By.xpath(".//*[@id='main_column']/div[2]/ul/li[3]/span")).getText();
		String max_page = driver.findElement(By.xpath(".//*[@id='main_column']/div[2]/ul/li[3]/a")).getText();;
		String userName = "";

        	while(!current_page.equals(max_page)) {
        	//while(driver.findElements(By.className("next disabled")).isEmpty()) { 
        		elements = findDynamicElements("//a[@class='name']");
        		int size = elements.size();
        		for(int i = 0; i < size; i++) {
		            userName = elements.get(i).getText();
		            check_visitor_profile(userName);
		            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	        		elements = findDynamicElements("//a[@class='name']");

		        }
        		scrollToBottomOfPage();
        		current_page = driver.findElement(By.xpath(".//*[@id='main_column']/div[2]/ul/li[3]/span")).getText();
                max_page = driver.findElement(By.xpath(".//*[@id='main_column']/div[2]/ul/li[3]/a")).getText();
		        driver.findElement(By.xpath(findElements.get("VisitsNext"))).click();
        	}		
        	for(int i = 0; i < statistics.size(); i++) {
        		System.out.println("" + messages.get(i) + ": " + statistics.get(messages.get(i)) + "%");
        	}
        	
        	System.out.println("Visits: " + visits + " / " + alreadyMessaged.size());
	}

	private void check_visitor_profile(String userName) {
		List<WebElement> element;
		click(driver.findElement(By.id("usr-" + userName)), "up");

        driver.findElement(By.xpath(findElements.get("message"))).click();
        element = driver.findElements(By.xpath(".//*[@id='global_messaging_container']/div[2]/ul[1]/li[1]/div[2]"));
        if(!element.isEmpty()) {
        	String firstMessage = element.get(0).getText();
        	System.out.println("TEST: " + firstMessage);
        	if(getMessages().contains(firstMessage)) {
        		updateStatistics(firstMessage);
        	}
        }
        closeMessageBox();
        driver.navigate().back();
    }
	
	private void updateStatistics(String message) {
		int[] visits = statistics.get(message);
		visits[0]++;
		statistics.put(message, visits);
    	System.out.println("Current visits: " + statistics.get(message)[0] + " / " + statistics.get(message)[1]);
	}
}

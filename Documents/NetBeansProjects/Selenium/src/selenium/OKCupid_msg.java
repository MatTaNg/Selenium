package selenium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class OKCupid_msg extends Core{
	
	int message_counter = 0;
    
    public static void main(String[] args) {
    	OKCupid_msg okCupid_msg = new OKCupid_msg();
    	okCupid_msg.main();
    }
	public void deleteMessages() {
		//Under Development...
		//How would you delete the latest messages?
		driver.findElement(By.xpath(".//*[@id='nav_mailbox']/a/span[1]/i")).click();
		driver.findElement(By.xpath(".//*[@id='messages-dropdown']/a[2]")).click();
		driver.findElement(By.xpath(".//*[@id='main_content']/div[1]/ul/li[2]/a")).click();
		Actions action = new Actions(driver);
		WebElement we = driver.findElement(By.className("inner"));//(".//li[starts-with(@id,'message_']"));
		action.moveToElement(we).perform();
		action.click(driver.findElement(By.xpath(".//*[@id='message_6015057114485823259']/div/div[1]/a/i"))).build().perform();
		driver.switchTo().alert().accept();
		action.sendKeys(Keys.ENTER).build().perform();
	//	driver.findElement(By.xpath(".//*[@id='message_6015057114485823259']/div/div[1]/a/i"));
		//scrollToBottom();
	}
	
	private void main() {
    	initialize();
    	sendMessages();
    }
    protected void sendMessages() {
    	driver.findElement(By.xpath(findElements.get("browse"))).click();
    	
	    List<WebElement> elements = new ArrayList<WebElement>();
	    closeMessageBox();
	       
	    System.out.println("");
	    while(driver.findElements(By.xpath(".//*[@id='match_bs']/div/h2")).isEmpty()) { //Look for the end of the page
	      	elements = driver.findElements(By.xpath("//a[@class='name']"));

		    for (WebElement e : elements) {
		        e.getText();
		        if(!getAlreadyMessaged().contains(e.getText())) {
		           	message(e.getText());
		        }
		    }
		    scrollToBottomOfPage();
	    }
	    System.out.println("Messaged: " + message_counter + " users");
	    driver.quit();
    }


    private void message(String userName) {
    	WebElement userNameElement;
    	String xpath = ".//*[@id='usr-" + userName + "']";
    	//Nav to profile
    	if((userNameElement = findDynamicElement(xpath)) != null) {
    		click(userNameElement, "down");
    		//userNameElement.click();
	        driver.findElement(By.xpath(findElements.get("message"))).click();
	        
	        if(driver.findElements(By.cssSelector(findElements.get("alreadyMsg"))).isEmpty()) {
		    	Random rand = new Random();
		    	int num = rand.nextInt(getMessages().size());
		        driver.findElement(By.xpath(findElements.get("write_message"))).sendKeys(getMessages().get(num));
		        driver.findElement(By.xpath(findElements.get("write_message"))).submit();
		    	System.out.println("Sent " + getMessages().get(num));
		    	message_counter++;
		    	addAlreadyMessaged(userName);
		    	
		        String profile_name = driver.findElement(By.xpath(findElements.get("profileName"))).getText();
		        writeToTextFile(profile_name + " - " + getMessages().get(num));
	        }
	        else { //We already messaged this person.
	        	String profile_name = driver.findElement(By.className("userinfo2015-basics-username")).getText();
		        String message = driver.findElement(By.cssSelector(findElements.get("alreadyMsg"))).getText();
		        writeToTextFile(profile_name + " - " + message);
		        addAlreadyMessaged(profile_name);
		        System.out.println("Already Messaged " + profile_name);
	        }
	        driver.navigate().back();
	        sendMessages();
    	}
    	else {
    		System.out.println("Could not message " + userName);
    	}
    }
}
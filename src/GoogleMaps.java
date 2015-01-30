/***
 * Class for connecting to google maps and pulling various information from the site. It can get distance
 * between points and the latitude and longitude values of points. This class was the original way I was
 * trying to solve the problem, but I used the text file routing instead as it is much more efficient.
 * 
 * @author Sean M Brown
 * Version 1.0
 * Revision date: March 2013
 */
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

public class GoogleMaps {
	
	//create variables for your web driver (firefox web browser), and the actions
	//the actions allow you to do more advanced things with the mouse/keyboard like right click
	private static WebDriver driver = new FirefoxDriver();
	private static Actions advancedActions = new Actions(driver);
	
	/**
	 * Default constructor for GoogleMaps it does nothing
	 */
	GoogleMaps(){}
	
	/**
	 * Start the firefox web browser and make it navigate to google maps website
	 */
	public static void startMap() 
	{
		driver.get("http://maps.google.com");
	}
	
	/**
	 * Enters the initial end spot into the text box at the top of google maps, it then searches
	 * for this place, and clicks the directions button to pop up the text box for the start point.
	 * 
	 * @param end
	 */
	public static void initialEndSpot(String end) 
	{
		//Finds the search box by its name, which is "q", then it submits the end spot
		WebElement firstSearch = driver.findElement(By.name("q"));
	    firstSearch.sendKeys(end);
	    firstSearch.submit();
	    
	    //Clicks the directions button by finding its class name "actbar-text"
	    WebElement directions =  driver.findElement(By.className("actbar-text"));
	    directions.click();
	    
	    //Waits for the start text box to load before allowing more input
	    long stop = System.currentTimeMillis() + 1000;
	    while (System.currentTimeMillis() < stop) {
	        WebElement resultsDiv = driver.findElement(By.id("d_d"));

	        if (resultsDiv.isDisplayed()) {
	          break;
	        }
	     }
	}
	
	/**
	 * Returns the latitude and longitude value for the desired address.
	 * 
	 * @param point
	 * @return coordinates
	 */
	public static String getLatLongFromPoint(String point) 
	{
		//refreshes google maps to its inital state
		startMap();
		
		//enters the address into the search bar by finding its name "q"
		WebElement firstSearch = driver.findElement(By.name("q"));
	    firstSearch.sendKeys(point);
	    firstSearch.submit();
	    
	    //wait a second for everything to load
	    long stop = System.currentTimeMillis() + 1000;
		while(System.currentTimeMillis() < stop) {}
		
		//finds "gmnoprint" which is your entered address on the map, and right clicks on it
		//this pops up a little box that contains the words 'What's here?'this is what needs to be
		//clicked next
		WebElement endPoint = driver.findElement(By.className("gmnoprint"));
		Actions rightClick = advancedActions.contextClick(endPoint);
		rightClick.perform();
		
		//wait a second for everything to load
		long quickPause = System.currentTimeMillis() + 1000;
		while(System.currentTimeMillis() < quickPause) {}
		
		//Searches through the dialog box for the 7th element (6 in code because it starts at 0),
		//then it clicks 'What's here'. This re-populates the text box at the top with the lat/long value
		WebElement whatsHere = driver.findElement(By.xpath("//*[@class='unselectable kd-menulistitem'][6]"));
		whatsHere.click();
		
		//wait a second for everything to load
		long pause = System.currentTimeMillis() + 1000;
		while(System.currentTimeMillis() < pause) {}
		
		//submits the populated value by clicking the search button, this needs done because the
		//box at the top is a text view, not text box, so it still has the previous value stored
		//in it that was originally searched, clicking it again stores the lat/long in it
		WebElement click = driver.findElement(By.id("gbqfb"));
		click.click();
		
		//get the lat/long from the "q" search box by its attribute name "value", and return it
		WebElement latLong = driver.findElement(By.name("q"));
		String coordinates = latLong.getAttribute("value");
		return coordinates;
	}
	
	/**
	 * Enter the start spot into the start spot text box on Google Maps
	 * 
	 * @param start
	 */
	public static void enterStartSpot(String start) 
	{
		//find the start box, put the start spot into it, and submit it
		WebElement secondSearch = driver.findElement(By.id("d_d"));
	    secondSearch.sendKeys(start);
	    secondSearch.submit();
	}
	
	/**
	 * Enter the end spot into the end spot text box on Google Maps
	 * 
	 * @param end
	 */
	public static void enterEndSpot(String end)
	{
		//find the end box, clear its previous data that was initially entered, add new end spot, submit
		WebElement endTextBox = driver.findElement(By.id("d_daddr"));
		endTextBox.clear();
		endTextBox.sendKeys(end);
		endTextBox.submit();
	}
	
	/**
	 * Clicks the Get Directions button on Google Maps to display all of the directions and distances
	 * from your start spot to your end spot
	 */
	public static void clickDirections() 
	{
		WebElement clickDirections = driver.findElement(By.id("d_sub"));
		clickDirections.click();
		
		long stop = System.currentTimeMillis() + 5000;
	    while (System.currentTimeMillis() < stop) 
	    {
	    	WebElement resultsDiv = driver.findElement(By.id("d_d"));

	        if (resultsDiv.isDisplayed()) {
	          break;
	        }
	    }
	}
	
	/**
	 * Returns the distance in miles between your start spot and end spot
	 * 
	 * @return total
	 */
	public static double getDistance() 
	{
		/*I am too lazy to comment all the stupid string splitting I did, so I'll just 
		 * explain it. Basically all the instructions pop up on the bottom left, with distances.
		 * Each distance is called by the same ID, and at this point I was too scared to try an xpath
		 * like I did above to click the 'What's here?'. So i pulled all the directions off in blocks,
		 * because they had different IDs and I could. Then I did a bunch of parsing to get the miles
		 * and feet values that were beside them. Then I did the simple calculation at the very end
		 * to convert everything into miles. */
		
		String text = "";
		String numbers = "";
		int i = 0;
		double feet = 0;
		double miles = 0;
		double total = 0;
		
		long end = System.currentTimeMillis() + 500;
		while(System.currentTimeMillis() < end) {}
		
		while(true)
		{
			try {
			//each block was called step_0_i, i being a number 0-whatever. So I could pull each block
			//off and parse it.
			WebElement getText = driver.findElement(By.id("step_0_" + i));
			text += getText.getText() + "\n";
			i++;
			} catch (Exception e) {
				break;
			}
		}
		String[] splitText = text.split("\n");
		String[] splittedText = null;
		boolean flag = false;
		
		for(int j = 0; j < splitText.length; j++) 
		{
			if(splitText[j].contains("Destination will be on")) 
			{
				splittedText = new String[splitText.length - 1];
				flag = true;
			}
		}
		
		if(!flag) 
		{
			splittedText = new String[splitText.length];
		}
		
		int k = 0;
		for(int j = 0; j < splitText.length; j++) 
		{
			if(!splitText[j].contains("Destination will be on")) 
			{
				splittedText[k] = splitText[j];
				k++;
			}
		}
		
		for(int j = 0; j < splitText.length; j++) 
		{
			if(!(j % 2 == 0))
			{
				numbers += splittedText[j] + "\n";
			}
		}
		
		String[] splitNumbers = numbers.split("\n");
		for(int j = 0; j < splitNumbers.length; j++) 
		{
			if(splitNumbers[j].contains(" ft"))
			{
				splitNumbers[j] = splitNumbers[j].replaceAll(" ft", "");
				feet += Double.parseDouble(splitNumbers[j]);
			}
			else 
			{
				try {
					splitNumbers[j] = splitNumbers[j].replaceAll(" mi", "");
					miles += Double.parseDouble(splitNumbers[j]);
				} catch(Exception e) {}
			}
		}
		
		total = (feet/5280) + miles;
		
	    return total;
	}
	
	 public static void main(String[] args) 
	 {
			driver.get("http://maps.google.com");
			//LinkedLists.workbookToLinkedLists(LinkedLists.trashRoutesWorkbook, LinkedLists.sixDayTrash, 6);
			//String start = LinkedLists.getLatLongByRow(LinkedLists.sixDayTrash, 0);
			//String end = LinkedLists.getLatLongByRow(LinkedLists.sixDayTrash, 5);
			String test = getLatLongFromPoint("253 S Main Street, Slippery Rock PA");
			
			System.out.println(test);
	 }
}

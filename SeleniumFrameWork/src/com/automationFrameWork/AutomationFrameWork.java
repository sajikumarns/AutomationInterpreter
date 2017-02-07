package com.automationFrameWork;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;



public class AutomationFrameWork {
	
	public static WebDriver driver = null;
	public static Properties serverProps = new Properties();
	public static Actions builder = null;
	private static ExtentReports extent;
	static String serverUrl = null;

	public static void init()

	{	
		String browserMode = serverProps.getProperty("mode","1"); // browserMode - 1 chrome ; 2 headless browser
		System.out.println("  browserMode  : "+browserMode);
			
		if(browserMode.equals("1"))
		{
			/*********************************    CHROME BROWSER  STARTS *************************************/
			System.setProperty("webdriver.chrome.driver", ".\\drivers\\chromedriver.exe");
			System.setProperty("webdriver.chrome.logfile", ".\\logs\\chromedriver.log");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-extensions");	
	        	driver = new ChromeDriver(options);
			/*********************************    CHROME BROWSER  ENDS *************************************/
		}else
		{


		//System.setProperty("webdriver.gecko.driver", ".\\geckodriver.exe");
		//driver = new FirefoxDriver();
		
		/*********************************    HEADLESS BROWSER  STARTS *************************************/
	      	        File file = new File(".\\drivers\\phantomjs.exe");				
	                System.setProperty("phantomjs.binary.path", file.getAbsolutePath());		
	                driver = new PhantomJSDriver();
			 
		/*********************************    HEADLESS BROWSER  STARTS *************************************/
		}

		driver.manage().window().maximize();
		
	        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //it will wait max 5 second before throwing an exception.	
	        builder=new Actions(driver);	
	       	//setServerParams();
		    extent = new ExtentReports(".\\TestResultNew_"+new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss").format(new Date())+".html", true);

		    extent.config().documentTitle("OpManager Automation Report").reportName("Regression");
		
		    createScreenshotFolder();
		

		  
	}
	
	
	private static Document xmlDoc(File xmlFile) throws Exception
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	
	
	public static void main(String args[])throws Exception {



		System.out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		parseArgs(args,serverProps);
		init();
		try
		{
		//recorder.start();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	       
	    File xmlFile11 = new File("./Input.xml");
	    if(xmlFile11.exists())
	    {
	    	Document doc11 = xmlDoc(xmlFile11);
	    	NodeList nList11 = doc11.getElementsByTagName("InputFile");

	    	Node nNode11 = nList11.item(0);
	    	NodeList list11 = nNode11.getChildNodes();
	    	int length11 = list11.getLength();

	    	for(int ii=0; ii<length11; ii++)
	    	{
	    	      Node nextNode11 = list11.item(ii);
	    	      if(nextNode11.getNodeType() != Node.ELEMENT_NODE)
	    	      {
	    	      		continue;
	    	      }
	    	      Element paramNode11 = (Element)nextNode11;
	    	      File testCaseFilePath = new File((String)paramNode11.getAttribute("path"));
	    		
	    	      Document doc = xmlDoc(testCaseFilePath);


	    	      HashMap workFlowTemplateMap = new HashMap();
	    	      NodeList nList = doc.getElementsByTagName("ActionTemplate");

	    	      for (int temp = 0; temp < nList.getLength(); temp++) 
	    	      {

	    	    	  Node nNode = nList.item(temp);
	    	    	  Element eElement = (Element) nNode;

	    	    	  NodeList workFlowNodeList = nNode.getChildNodes();
	    	    	  int workFlowNodelength = workFlowNodeList.getLength();

	    	    	  List workFlowList = new ArrayList();

	    	    	  for(int i=0; i<workFlowNodelength; i++)
	    	    	  {
	    	    		  Node nextNode = workFlowNodeList.item(i);
	    	    		  if(nextNode.getNodeType() != Node.ELEMENT_NODE)
	    	    		  {
	    	    			  continue;
	    	    		  }
	    	    		  Element paramNode = (Element)nextNode;
	    	    		  try
	    	    		  {
	    	    			  HashMap step = new HashMap();
	    	    			  step.put("actionToPerform",paramNode.getAttribute("actionToPerform"));
	    	    			  step.put("elementName",paramNode.getAttribute("elementName"));
	    	    			  step.put("elementType",paramNode.getAttribute("elementType"));
	    	    			  step.put("valueReferenceTag",paramNode.getAttribute("valueReferenceTag"));
	    	    			  step.put("desc",paramNode.getAttribute("desc"));

	    	    			  workFlowList.add(step);
	 
	    	    		  }catch(Exception e)
	    	    		  {
	    	    			  e.printStackTrace();
	    	    		  }

	    	    	  }

	    	    	  workFlowTemplateMap.put(eElement.getAttribute("id"),workFlowList);
	    	      }

	    	      NodeList nList1 = doc.getElementsByTagName("ActionList");

	    	      Node nNode1 = nList1.item(0);
	    	      NodeList list = nNode1.getChildNodes();
	      		  int length = list.getLength();

	      		  int count =0;
	      		  for(int i=0; i<length; i++)
	      		  {
	      			  Node nextNode = list.item(i);
	      			  if(nextNode.getNodeType() != Node.ELEMENT_NODE)
	      			  {
	      				  continue;
	      			  }

	      			  Element paramNode = (Element)nextNode;
	      			  try
	      			  {
			
	      				  List stepsList = (ArrayList)workFlowTemplateMap.get(paramNode.getAttribute("ActionTemplateid"));	

	      				  ExtentTest test = extent.startTest("Test Case "+(++count)+" - "+/*paramNode.getAttribute("ActionTemplateid")*/""+" - "+paramNode.getAttribute("desc"), paramNode.getAttribute("desc"));

	      				  for(int j=0;j<stepsList.size();j++)
	      				  {
	      					  HashMap taskMap = null;
	      					  try
	      					  {
	      						  boolean isTestCaseStatusSet = false;

	      						  System.out.println(" Step "+j+" Action"+stepsList.get(j));
	      						  taskMap = (HashMap)stepsList.get(j);
						
	      						  String actionToPerform = (String)taskMap.get("actionToPerform");
	      						  String valueReferenceTag = (String)taskMap.get("valueReferenceTag");
	      						  System.err.println(" Page source :"+driver.getCurrentUrl());
	      						  if(valueReferenceTag!= null && !valueReferenceTag.equals(""))
	      						  {
	      							  System.out.println(" Value is !!!!!!!! "+paramNode.getAttribute(valueReferenceTag));
	      						  }

	      						  if(actionToPerform.equals("openUrl"))
	      						  {
	      							  openUrl(paramNode.getAttribute(valueReferenceTag));
	      						  }
	      						  else if(actionToPerform.equals("frame"))
	      						  {
	      							  driver.switchTo().frame("zohoiam");
	      						  }
	      						  else if(actionToPerform.equals("setValue") && ((String)taskMap.get("elementType")).equals("name"))
	      						  {
	      							  setElementNameValue((String)taskMap.get("elementName"),paramNode.getAttribute(valueReferenceTag));
	      						  }
	      						  else if(actionToPerform.equals("setValue") && ((String)taskMap.get("elementType")).equals("id"))
	      						  {
	      							  if(valueReferenceTag!=null && valueReferenceTag.equals("CODE")) // Just a hack-- Disable it later
	      							  {
	      								  setElementIdValue((String)taskMap.get("elementName"),"Chennai@123");
	      							  }
	      							  else
	      							  {
										  setElementIdValue((String)taskMap.get("elementName"),paramNode.getAttribute(valueReferenceTag));
	      							  }
	      						  }
	      						  else if(actionToPerform.equals("OnClick"))
	      						  {
							
	      							  String elementType = ((String)taskMap.get("elementType"));
	      							  String elementName = (String)taskMap.get("elementName");
	      							  if(elementType == null || elementType.equals("") || elementName==null || elementName.equals(""))
	      							  {
	      								  System.out.println(" WARNING : elementType/elementName not specified");
								
	      							  }
	      							  else if(elementType.equals("id"))
	      							  {
	      								  clickElementById(elementName);
	      							  }
	      							  else if(elementType.equals("xPath"))
	      							  {
	      								  clickElementByxPath(elementName);
	      							  }
	      							  else if(elementType.equals("css"))
	      							  {
	      								  clickElementCssSelector(elementName);
	      							  }
	      						  }
	      						  else if(((String)actionToPerform).equals("clickPassingElement"))
	      						  {
	      							  String elementType = ((String)taskMap.get("elementType"));
	      							  String elementToChoose = (String)paramNode.getAttribute(valueReferenceTag);
							
	      							  if(elementType.equals("xPath"))
	      							  {
	      								  clickElementByxPath(elementToChoose);
	      							  }
	      							  else if(elementType.equals("id"))
	      							  {
	      								  clickElementById(elementToChoose);
	      							  }
	      							  else if(elementType.equals("css"))
	      							  {
	      								  clickElementCssSelector(elementToChoose);
	      							  }
	      						  }
	      						  else if(((String)actionToPerform).equals("isElementPresent") && ((String)taskMap.get("elementType")).equals("id"))
	      						  {
	      							  boolean val = isElementPresent((String)taskMap.get("elementName"),(String)taskMap.get("elementType"));

	      							  if(!val)
	      							  {

	      								  String fName = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

	      								  captureScreenshot(fName);

	      								  File file = new File("./");
	      								  String absolutePath = file.getAbsolutePath();
	      								  String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
	      								  String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName);
	      								  test.log(LogStatus.FAIL, (String)taskMap.get("desc"), "Image example: " + img);
	      								  isTestCaseStatusSet = true;
	      							  }
						}
						else if(((String)actionToPerform).equals("isStringMatched") && ((String)taskMap.get("elementType")).equals("id"))
						{
							String strToMatch =  (String)paramNode.getAttribute(valueReferenceTag);
							isStringMatched((String)taskMap.get("elementName"),(String)taskMap.get("elementType"),strToMatch);
							if(!isTestCaseStatusSet)
							{
								String fName = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

								captureScreenshot(fName);

								File file = new File("./");
								String absolutePath = file.getAbsolutePath();
								String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
								String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName);
								test.log(LogStatus.PASS, (String)taskMap.get("desc"), "Image example: " + img);
								isTestCaseStatusSet = true;
							}
						}
						else if(((String)actionToPerform).equals("isStringNotMatched") && ((String)taskMap.get("elementType")).equals("id"))
						{
							String strToMatch =  (String)paramNode.getAttribute(valueReferenceTag);
							boolean val = isStringNotMatched((String)taskMap.get("elementName"),(String)taskMap.get("elementType"),strToMatch);
							if(!isTestCaseStatusSet && !val)
							{
								String fName = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

								captureScreenshot(fName);

								File file = new File("./");
								String absolutePath = file.getAbsolutePath();
								String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
								String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName);
								test.log(LogStatus.FAIL, (String)taskMap.get("desc"), "Image example: " + img);
								isTestCaseStatusSet = true;
							}
						}
						else if(actionToPerform.equals("getValueAndClickAllOpmReport"))
						{
						WebElement ele = driver.findElement(By.id((String)taskMap.get("elementName")));

						//strFromPage = (String)driver.findElement(By.id(elementName)).getText();
						String strFromPage = (String)ele.getText();
						System.err.println(" strFromPage wwww :"+strFromPage);
						StringTokenizer st = new StringTokenizer(strFromPage, "\n");
						int iii=0;
						while(st.hasMoreTokens())
						{
							String reportId = st.nextToken();
							System.err.println("value "+(iii++)+"nnnnnnn"+reportId);
							openUrl(serverUrl+"/apiclient/ember/index.jsp#/Reports/Opmanager/Report/"+reportId);
							//Thread.sleep(1000);
							
							boolean val = isElementPresent("1","id");

							if(!val)
							{

								String fName = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

								captureScreenshot(fName);

								File file = new File("./");
								String absolutePath = file.getAbsolutePath();
								String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
								String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName);
								String rePortName = getReportName();
								rePortName = rePortName!=null ? rePortName : "Report Name Not Found";
								
								test.log(LogStatus.FAIL, rePortName, "Image example: " + img);
								isTestCaseStatusSet = true;
							}
							else
							{
							
							
							String fName = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

							captureScreenshot(fName);

							File file = new File("./");
							String absolutePath = file.getAbsolutePath();
							String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
							String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName);
							
							
							String rePortName = getReportName();
							rePortName = rePortName!=null ? rePortName : "Report Name Not Found";
							
							test.log(LogStatus.PASS, rePortName, "Image example: " + img);
							}
							
						}

						}
						else if(actionToPerform.equals("waitforUserInput"))
						{
							//System.out.println(" Waiting for user Input... Give ProfileName " );
						        DataInputStream dis = new DataInputStream(System.in);
		        				dis.readLine();
						}
						else if(actionToPerform.equals("isElementNotExist"))
						{
							isElementNotExist((String)taskMap.get("elementName"),paramNode.getAttribute("elementType"));
						}
						else if(actionToPerform.equals("waitForSeconds"))
						{
							//System.out.println(" Waiting for user Input... Give ProfileName " );
						        //DataInputStream dis = new DataInputStream(System.in);
		        				//dis.readLine();

							String msStr = paramNode.getAttribute(valueReferenceTag);
								if(msStr!=null && !msStr.trim().equals(""))
								{
									int waitTimeInSec = Integer.valueOf(msStr);
									//System.out.println(" GOING TO WAIT FOR :"+waitTimeInSec+" Seconds ");
									Thread.sleep(waitTimeInSec*1000);
								}
								else
								{
									//System.out.println(" Wait Time is not Specified. So wait for defult 5 seconds !!!!!! ");
									Thread.sleep(5*1000); // Default Wait is 5 seconds
								}

						}
						else if(actionToPerform.equals("refresh"))
						{
							driver.navigate().refresh();
						}

						/*try
						{
						Assert.assertEquals("a", "b");
						}catch(Exception e1)
						{
							e1.printStackTrace();
						}*/

						if(!isTestCaseStatusSet)
						{
						String fName = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

						captureScreenshot(fName);

						File file = new File("./");
						String absolutePath = file.getAbsolutePath();
						String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
						String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName);
						test.log(LogStatus.PASS, (String)taskMap.get("desc"), "Image example: " + img);
						
						}

					System.out.println(" Test Case Passed !!!!!!!!!!!!");	

					}catch(Exception e)
					{
						e.printStackTrace();

						String fName1 = paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg";

						captureScreenshot(paramNode.getAttribute("ActionTemplateid")+System.currentTimeMillis()+".jpg");
						System.out.println(" Test Case Failed !!!!! "+e.getMessage());

						File file = new File("./");
						String absolutePath = file.getAbsolutePath();
						String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
						String img = test.addScreenCapture(filePath+File.separator+"screenShot"+File.separator+fName1);
						test.log(LogStatus.FAIL, (String)taskMap.get("desc")+" Trace "+e.getMessage(), "Image example: "+ img);
					}
						

				       		
				}

				extent.endTest(test);	
		

			//Thread.sleep(1000);
			//boolean isTextPrest=driver.findElement(By.id("errorMsg")).getText().contains("admin");  
			//System.out.println("  isTextPrest YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY :"+isTextPrest+" val :"+driver.findElement(By.id("errorMsg")).getText());
				
	      
	      			}catch(Exception e)
	      			{
		      			e.printStackTrace();
	      			}

	      		}

			extent.flush();
	    	      		}


		





	        }

		driver.close();
		
		//recorder.stop();
	    }
	public static void setServerParams()
	{

		try
		{

			File file = new File(".\\conf\\server.properties");			
			serverProps.load(new FileInputStream(file));
			
		}
		catch(Exception excep)
		{
			excep.printStackTrace();
			
		}
	}
	
	
    private static void openUrl(String urlStr) throws Exception
    {
	    serverUrl = serverProps.getProperty("serverUrl");
	    System.err.println("  Server Url : " +serverUrl);
		urlStr = urlStr.replace("$SEVERNAME$",serverUrl);
		System.err.println("  Server Url TO CONNECT: " +urlStr);
		driver.get(urlStr);
		//driver.save_screenshot("screenie.png");
    }
    private static void clickElementById(String idStr) throws Exception
    {
	    //System.out.println(" Mouse Moment is called !!!!!!!!!!");
		//WebElement ele = driver.findElement(By.id(idStr));
		//builder.moveToElement(ele).build().perform();
		//Thread.sleep(2000);
		//ele.click();
		//builder.click(ele).build().perform();
	    Actions act = new Actions(driver);
	    act.moveToElement(driver.findElement(By.id(idStr))).click().build().perform();

		//driver.findElement(By.id(idStr)).click();


		
    }
    private static void clickElementByText(String idStr) throws Exception
    {
		//driver.findElement(By.ByClassName(idStr)).click();
    }
    private static void clickElementByxPath(String xPathStr) throws Exception
    {
		driver.findElement(By.xpath(xPathStr)).click();
		
    }
    private static void clickElementCssSelector(String cssStr) throws Exception
    {
	    //System.out.println("  driver.findElement(By.cssSelector(cssStr)) :"+driver.findElement(By.cssSelector(cssStr)));
		driver.findElement(By.cssSelector(cssStr)).click();
    }
    private static void setElementIdValue(String idStr,String valueStr) throws Exception
    {

 	    	driver.findElement(By.id(idStr)).clear();
		driver.findElement(By.id(idStr)).sendKeys(valueStr);
    } 
    private static void setElementNameValue(String idStr,String valueStr) throws Exception
    {
	    	driver.findElement(By.name(idStr)).clear();
		driver.findElement(By.name(idStr)).sendKeys(valueStr);


    }
    private static boolean isElementPresent(String elementName,String elementType) throws Exception
    {
    	return isElementPresent(elementName,elementType,true);
    }
    private static boolean isElementPresent(String elementName,String elementType,boolean firstTime) throws Exception
    {

	    boolean isElementFound = true;

	    long st = System.currentTimeMillis();
	    
	    if(elementType.equalsIgnoreCase("id"))
	    {
	    	try
	    	{
	    		WebElement ele = driver.findElement(By.id(elementName));
	    		
	    	}catch(Exception e){
	    		
	    		
	    		System.err.println(" Exception occurs after "+(System.currentTimeMillis()-st)+"  Seconds ");
	
	    		isElementFound = false;
	    	}
	    }
	    
	return isElementFound;
	
    }
    
    private static boolean isElementNotExist(String elementName,String elementType) throws Exception
    {

	    boolean isElementNotFound = true;
	try
	{
	    if(elementType.equalsIgnoreCase("id"))
	    {
	    		WebElement ele = driver.findElement(By.id(elementName));
	    }
	    else if(elementType.equalsIgnoreCase("name"))
	    {
	    		WebElement ele = driver.findElement(By.name(elementName));
	    }
	    else if(elementType.equalsIgnoreCase("xPath"))
	    {
	    		WebElement ele = driver.findElement(By.xpath(elementName));
	    }
	    else if(elementType.equalsIgnoreCase("css"))
	    {
	    		WebElement ele = driver.findElement(By.xpath(elementName));

	    }
		isElementNotFound = false;
	}catch(Exception e){
		
	}
	    
	return isElementNotFound;
	
    }

	private static boolean isStringMatched(String elementName,String ElementType,String strToMatch) throws Exception
	{
		boolean isStringMatched = false;
			String strFromPage = (String)driver.findElement(By.id(elementName)).getText();
			//System.out.println(" strFromPage :"+strFromPage+"  strToMatch :"+strToMatch);
			if(strFromPage!=null && !strFromPage.equals("") && strFromPage.trim().contains(strToMatch))
			{
				isStringMatched = true;
			}

	    return isStringMatched;
	}
	
	private static String getReportName() throws Exception
	{
		
			String reportName  = (String)driver.findElement(By.id("dataGraphHeader")).getText();
			//System.out.println(" strFromPage :"+strFromPage+"  strToMatch :"+strToMatch);
			if(reportName!=null && !reportName.equals(""))
			{
				return reportName;
			}

	    return null;
	}
	private static boolean isStringNotMatched(String elementName,String ElementType,String strToMatch) throws Exception
	{
		//String strFromPage = (String)driver.findElement(By.id(elementName));
		

		//System.err.println("  isStringNotMatched 111111111111111111111111 : "+driver.findElement(By.id("reportName")).getText());
		//Thread.sleep(1000);
		//System.err.println("  isStringNotMatched 222222222222222222222222 : "+driver.findElement(By.id("reportName")).getText());
		boolean isStringNotMatched = true;

			String strFromPage = waitAndGetText(elementName);
			//System.out.println(" strFromPage :"+strFromPage+"  strToMatch :"+strToMatch);

			
			if(strFromPage!=null && !strFromPage.equals("") && strFromPage.trim().contains(strToMatch))
			{
				isStringNotMatched = false;
			}

			//System.out.println("  isStringNotMatched  : "+strToMatch+" Result : "+isStringNotMatched);
	    return isStringNotMatched;
	}

	public static String waitAndGetText(String elementName) 
	{
		String strFromPage = null;
		try
		{
			int retryCount = 2; // Default 2
			String retryStr = serverProps.getProperty("retryCount");
			if(retryStr!=null && !retryStr.equals(""))
			{
				retryCount = Integer.parseInt(retryStr);
			}
			for(int i=1;i<=retryCount;i++)
			{
				
				

				WebElement ele = driver.findElement(By.id(elementName));

				//strFromPage = (String)driver.findElement(By.id(elementName)).getText();
				strFromPage = (String)ele.getText();
				
				if(strFromPage==null || strFromPage.equals(""))
				{
					//System.out.println(" strFromPage is Empty !!!!!!!!!!!!!!!!!!!!!!. Going to wait for 1 Sec"+ele);
					waitForASeconds();
				}
				else
				{
					//System.out.println(" GOT itttttttttttttttttttt :"+strFromPage);
					break;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("  Final Return value is !!!!!!!!!!!!!!!!!!!!!! :"+strFromPage);
		return strFromPage;
	}


	public static void waitForASeconds() throws Exception 
	{
		Thread.sleep(1000);
	}

	public static void captureScreenshot(String imageName) throws Exception
	{
		Thread.sleep(250);

		File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			 // now copy the  screenshot to desired location using copyFile //method
			
					File file = new File("./");
					String absolutePath = file.getAbsolutePath();
					String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
			FileUtils.copyFile(src, new File(filePath+File.separator+"screenShot"+File.separator+imageName));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void createScreenshotFolder()
	{
		try
		{
			System.err.println(" createScreenshotFolder :");
		     File file = new File(".\\screenShot");
        		if (!file.exists()) 
			{
					//System.err.println(" 222222222222 :");
            			file.mkdir();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
                
        }


	private static void parseArgs(String args[], Properties prop) {
		String arg, temp, errMsg = null;
		int i=0;
		String usage = "\nUsage: BackUpImpl [-mode (all | configdata | configfiles | zip)] [-targetdb (mssql | pgsql)] [-destination (destination directory)] [-threads (1-15)]  [-debug]\n";  //No I18N
		try {
			while (i < args.length && args[i].startsWith("-")) {
				arg = args[i++].toLowerCase();
				if (arg.equals("-url")) {
					temp = args[i++].toLowerCase();

					System.out.println("  !!!!!!!!!!!!!!!! SERVER URL to set :"+temp);

					serverProps.setProperty("serverUrl",temp);
						
				}
			       if(arg.equals("-mode"))
			       {
				       
					String str = args[i++].toLowerCase();	
				 	serverProps.setProperty("mode",str);
			       }	
			}

		} catch (Exception ex) {
			
			ex.printStackTrace();
		}

	}


}

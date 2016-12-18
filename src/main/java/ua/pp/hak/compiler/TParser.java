package ua.pp.hak.compiler;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

import ua.pp.hak.ui.Notepad;

public class TParser {
	final static Logger logger = LogManager.getLogger(TParser.class);
	final static int DEFAULT_TIMEOUT = 15;
	final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

	public static void parse(Notepad npd) {
		logger.info("Parsing expression...");
		Color green = new Color(0, 188, 57);
		Color red = new Color(189, 0, 0);

		RSyntaxTextArea taExpr = npd.getExprTextArea();
		JTextArea taExprRes = npd.getExprResTextArea();
		JTextArea taParameters = npd.getParametersTextArea();
		JTextField tfSKU = npd.getSkuField();

		String[] expressionResult = getExpressionResultInfoByChrome(taExpr.getText(), taParameters.getText(),
				tfSKU.getText());
		String expressionRes = expressionResult[0];
		String expressionStatus = expressionResult[1];

		Color color = null;
		if (expressionStatus != null && expressionStatus.equals("form-group has-success")) {
			color = green;
		} else {
			color = red;
		}

		// set color of the expression result's text area
		taExprRes.setBorder(
				new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, color), new EmptyBorder(2, 5, 2, 0)));

		taExprRes.setText(expressionRes);
		logger.info("Finish parsing expression");
	}

	private static String[] getExpressionResultInfoByChrome(String exprText, String paramText, String skuIdText) {
		// https://solutionspy.wordpress.com/2013/08/24/selenium-script-to-login-gmail-and-logout-successfully/

		String expressionResult = null;
		String expressionStatus = null;

		// initialize Chrome driver
		System.setProperty("webdriver.chrome.driver", "webdrivers/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		// driver.manage().window().setSize(new org.openqa.selenium.Dimension(1,
		// 1));
		driver.manage().window().setPosition(new Point(-2000, 0));
		try {
			// hide Google Chrome driver http://stackoverflow.com/a/5506230
			// Runtime.getRuntime().exec("HideNSeek.exe 0 \"" + "Google Chrome"
			// + "\"");
			String[] cmdarray = { "webdrivers/HideNSeek.exe", "0", "data:," };
			Runtime.getRuntime().exec(cmdarray);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			// Open gmail
			driver.get("http://templex.cnetcontent.com/Home/Parser");

			// Enter userd id
			WebElement element = driver.findElement(By.name("Email"));
			element.sendKeys("test@test.com");

			// wait 5 secs for userid to be entered
			// driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			// Enter Password
			WebElement element1 = driver.findElement(By.name("Password"));
			element1.sendKeys("password");

			// Submit button
			element.submit();

			WebElement skuId = driver.findElement(By.name("skuId"));
			skuId.clear();
			skuId.sendKeys(skuIdText);
			WebElement expression = driver.findElement(By.name("expression"));
			expression.sendKeys(exprText);
			WebElement parameters = driver.findElement(By.name("parameters"));
			parameters.clear();
			parameters.sendKeys(paramText);

			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

			WebElement myDynamicElement = (new WebDriverWait(driver, DEFAULT_TIMEOUT))
					.until(ExpectedConditions.visibilityOfElementLocated(By.id("loading")));
			waitForElToBeRemove(driver, By.id("loading"));

			new WebDriverWait(driver, DEFAULT_TIMEOUT).until(new Predicate<WebDriver>() {
				public boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				}

			});
			
			expressionResult = driver.findElement(By.name("result")).getAttribute("value");
			expressionStatus = driver.findElement(By.name("result")).findElement(By.xpath(".."))
					.findElement(By.xpath("..")).getAttribute("class");
			
			logger.info("Expression status: " + expressionStatus);
			logger.info("Expression result: " + expressionResult);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			driver.quit();
		}

		try {
			Runtime.getRuntime().exec("webdrivers/HideNSeek.exe 1 \"" + "Google Chrome" + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[] { expressionResult, expressionStatus };
	}

	private static String[] getExpressionResultInfoByPhantomJS(String exprText, String paramText, String skuIdText) {
		// https://solutionspy.wordpress.com/2013/08/24/selenium-script-to-login-gmail-and-logout-successfully/
		// System.setProperty("phantomjs.page.settings.userAgent", USER_AGENT);

		String expressionResult = null;
		String expressionStatus = null;

		Capabilities caps = new DesiredCapabilities();
		((DesiredCapabilities) caps).setJavascriptEnabled(true);
		((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent",
				USER_AGENT);
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"webdrivers/phantomjs.exe");
		WebDriver driver = new PhantomJSDriver(caps);
		try {
			// Open gmail
			driver.get("http://templex.cnetcontent.com/Home/Parser");

			// Enter userd id
			WebElement element = driver.findElement(By.name("Email"));
			element.sendKeys("test@test.com");

			// wait 5 secs for userid to be entered
			// driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			// Enter Password
			WebElement element1 = driver.findElement(By.name("Password"));
			element1.sendKeys("password");

			// Submit button
			element.submit();

			WebElement parameters = driver.findElement(By.name("parameters"));
			parameters.clear();
			parameters.sendKeys(paramText);
			WebElement skuId = driver.findElement(By.name("skuId"));
			skuId.clear();
			skuId.sendKeys(skuIdText);
			WebElement expression = driver.findElement(By.name("expression"));
			expression.sendKeys(exprText);

			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

			WebElement myDynamicElement = (new WebDriverWait(driver, DEFAULT_TIMEOUT))
					.until(ExpectedConditions.visibilityOfElementLocated(By.id("loading")));
			waitForElToBeRemove(driver, By.id("loading"));

			expressionResult = driver.findElement(By.name("result")).getAttribute("value");
			expressionStatus = driver.findElement(By.name("result")).findElement(By.xpath(".."))
					.findElement(By.xpath("..")).getAttribute("class");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			driver.quit();
		}

		return new String[] { expressionResult, expressionStatus };
	}

	final public static boolean waitForElToBeRemove(WebDriver driver, final By by) {
		// http://stackoverflow.com/a/30607213
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

			WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);

			boolean present = wait.ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class)
					.until(ExpectedConditions.invisibilityOfElementLocated(by));

			return present;
		} catch (Exception e) {
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		}
	}
}

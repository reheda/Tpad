package ua.pp.hak.compiler;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PhantomJsTest {
	static int DEFAULT_TIMEOUT = 15;
	static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

	public static void main(String[] args) {
		long start = System.nanoTime();
		String[] expressionResult = getExpressionResultInfo("SKU.Brand", "AccTree=-1, Evaluate=false", "12");
		long elapsedTime = System.nanoTime() - start;
		System.out.println(elapsedTime);
		String expressionRes = expressionResult[0];
		String expressionStatus = expressionResult[1];
		System.out.println(expressionRes);
		System.out.println(expressionStatus);
	}

	private static String[] getExpressionResultInfo(String exprText, String paramText, String skuIdText) {
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
				"phantomjs.exe");
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

			WebElement expression = driver.findElement(By.name("expression"));
			expression.sendKeys(exprText);
			WebElement parameters = driver.findElement(By.name("parameters"));
			parameters.clear();
			parameters.sendKeys(paramText);
			WebElement skuId = driver.findElement(By.name("skuId"));
			skuId.clear();
			skuId.sendKeys(skuIdText);

			// driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

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

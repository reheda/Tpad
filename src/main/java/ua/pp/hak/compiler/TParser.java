package ua.pp.hak.compiler;

import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Set;
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
import org.openqa.selenium.Cookie;
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

import ua.pp.hak.ui.LoadingPanel;
import ua.pp.hak.ui.Notepad;
import ua.pp.hak.util.ProcessKiller;

public class TParser {
	final static Logger logger = LogManager.getLogger(TParser.class);
	private final static int DEFAULT_TIMEOUT = 15;
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
	private static Set<Cookie> cookies;

	public static void parse(Notepad npd) {
		long start = System.nanoTime();

		try {
			logger.info("Parsing expression...");
			Color green = new Color(0, 188, 57);
			Color red = new Color(189, 0, 0);
			Color orange = new Color(233, 144, 2);

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
			} else if (expressionStatus != null && expressionStatus.equals("form-group has-warning")) {
				color = orange;
			} else {
				color = red;
			}

			// set color of the expression result's text area
			taExprRes.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, color),
					new EmptyBorder(2, 5, 2, 0)));

			taExprRes.setText(expressionRes);
			logger.info("Finish parsing expression");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		long elapsedTime = System.nanoTime() - start;
		logger.info("Elapsed time to parse: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");
	}

	private static String[] getExpressionResultInfoByChrome(String exprText, String paramText, String skuIdText) {
		// https://solutionspy.wordpress.com/2013/08/24/selenium-script-to-login-gmail-and-logout-successfully/

		String expressionResult = null;
		String expressionStatus = null;

		// kill process if needed
		String processName = "chromedriver.exe";
		try {
			if (ProcessKiller.isProcessRunning(processName)) {
				ProcessKiller.killProcess(processName);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		// initialize Chrome driver
		System.setProperty("webdriver.chrome.driver", "webdrivers/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().setPosition(new Point(-10000, 0));
		driver.manage().window().setSize(new org.openqa.selenium.Dimension(1, 1));
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

			if (cookies == null) {
				driver.get("http://templex.cnetcontent.com/Home/Parser");

				// Enter userd id
				WebElement element = driver.findElement(By.name("Email"));
				element.sendKeys("test@test.com");

				// wait 5 secs for userid to be entered
				// driver.manage().timeouts().implicitlyWait(5,
				// TimeUnit.SECONDS);

				// Enter Password
				WebElement element1 = driver.findElement(By.name("Password"));
				element1.sendKeys("password");

				// Submit button
				element.submit();

				cookies = driver.manage().getCookies();
			} else {

				driver.get("http://templex.cnetcontent.com/Home/Parser");

				for (Cookie cookie : cookies) {
					driver.manage().addCookie(cookie);
				}
				driver.get("http://templex.cnetcontent.com/Home/Parser");

			}

			// process expression
			// evaluateExpressionWithSendkeys(driver, exprText, paramText,
			// skuIdText);
			evaluateExpressionWithJavascript(driver, exprText, paramText, skuIdText);

			// wait for loading to disappear
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

			try {
				WebElement myDynamicElement = (new WebDriverWait(driver, DEFAULT_TIMEOUT))
						.until(ExpectedConditions.visibilityOfElementLocated(By.id("loading")));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			waitForElToBeRemove(driver, By.id("loading"));

			new WebDriverWait(driver, DEFAULT_TIMEOUT).until(new Predicate<WebDriver>() {
				public boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				}

			});

			// get results
			expressionResult = driver.findElement(By.name("result")).getAttribute("value");
			expressionStatus = driver.findElement(By.name("result")).findElement(By.xpath(".."))
					.findElement(By.xpath("..")).getAttribute("class");

			logger.info("Expression status: " + expressionStatus);
			logger.info("Expression result: " + expressionResult);

		} catch (Exception e) {
			logger.error(e.getMessage());
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

	public static String parseForSkuList(String expressionText, String parametersText, String[] skuList, int loadTime,
			int parseTime) throws Exception {
		long start = System.nanoTime();

		StringBuilder sb = new StringBuilder();
			logger.info("Parsing expression for SKU list...");

			// kill process if needed
			String processName = "chromedriver.exe";
			if (ProcessKiller.isProcessRunning(processName)) {
				ProcessKiller.killProcess(processName);
			}

			// initialize Chrome driver
			System.setProperty("webdriver.chrome.driver", "webdrivers/chromedriver.exe");
			WebDriver driver = new ChromeDriver();
			driver.manage().window().setPosition(new Point(-10000, 0));
			driver.manage().window().setSize(new org.openqa.selenium.Dimension(1, 1));

			String[] cmdarray = { "webdrivers/HideNSeek.exe", "0", "data:," };
			Runtime.getRuntime().exec(cmdarray);

			if (cookies == null) {
				driver.get("http://templex.cnetcontent.com/Home/Parser");

				// Enter userd id
				WebElement element = driver.findElement(By.name("Email"));
				element.sendKeys("test@test.com");

				// wait 5 secs for userid to be entered
				// driver.manage().timeouts().implicitlyWait(5,
				// TimeUnit.SECONDS);

				// Enter Password
				WebElement element1 = driver.findElement(By.name("Password"));
				element1.sendKeys("password");

				// Submit button
				element.submit();

				cookies = driver.manage().getCookies();
			} else {

				driver.get("http://templex.cnetcontent.com/Home/Parser");

				for (Cookie cookie : cookies) {
					driver.manage().addCookie(cookie);
				}
				driver.get("http://templex.cnetcontent.com/Home/Parser");

			}
			try {

				sb.append("<html>");
				sb.append(
						"<head><style> div.centered {text-align: center;} div.centered table { border-collapse: collapse; margin: 0 auto;  background-color: white; padding:5px;} tr {border-bottom: 1px solid #dddddd; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; border-bottom: none; } td { text-align: left; border-right: 1px solid #dddddd;} td.red { border-right: 1px solid #E03134; } td.cntr {text-align: center;} body {font-family: Segoe UI; font-size:9px; } </style></head>");
				sb.append("<body>");
				sb.append("<div class='centered'>");
				sb.append("<table>");
				sb.append("<tbody>");
				sb.append("<tr class='header'>");
				sb.append("<td>SKU</td>");
				sb.append("<td>Expression Result (Parameters: " + parametersText + ")</td>");
				sb.append("</tr>");

				String oldProcessingLabelText = LoadingPanel.getLabel().getText();
				for (int i = 0; i < skuList.length; i++) {
					
					if (Thread.currentThread().isInterrupted()){
						logger.warn("Canceling process...");
						if (ProcessKiller.isProcessRunning(processName)) {
							ProcessKiller.killProcess(processName);
						}
						break;
//						return "canceled";
					}

					LoadingPanel.getLabel()
							.setText(oldProcessingLabelText.concat(" (" + (i + 1) + "/" + skuList.length + ")"));
					driver.get("http://templex.cnetcontent.com/Home/Parser");
					String[] expressionResult = getExpressionResultInfoByChromeForSkuList(driver, expressionText,
							parametersText, skuList[i], loadTime, parseTime);

					String expressionRes = expressionResult[0];
					String expressionStatus = expressionResult[1];
					sb.append("<tr style='font-family: Consolas;'>");
					sb.append("<td>");
					sb.append(skuList[i]);
					sb.append("</td>");
					sb.append("<td>");
					if (expressionStatus != null && expressionStatus.equals("form-group has-success")) {
						sb.append(escapeHtml(expressionRes).replaceAll("\\n", "<br />"));
					} else if (expressionStatus != null && expressionStatus.equals("form-group has-error")) {
						sb.append("<font color='red'>");
						sb.append(escapeHtml(expressionRes).replaceAll("\\n", "<br />"));
						sb.append("</font>");
					} else if (expressionStatus != null && expressionStatus.equals("form-group has-warning")) {
						sb.append("<font color='orange'>");
						sb.append(escapeHtml(expressionRes).replaceAll("\\n", "<br />"));
						sb.append("</font>");
					} else {
						sb.append("<font color='red'>");
						sb.append("Something goes wrong. Report it.");
						sb.append("</font>");
					}
					sb.append("</td>");

				}

				sb.append("<tr class='header'>");
				sb.append("<td class='red'>&nbsp;</td>");
				sb.append("<td class='red'>&nbsp;</td>");
				sb.append("</tr>");
				sb.append("</tbody>");
				sb.append("</table>");
				sb.append("</div>");
				sb.append("</body>");
				sb.append("</html>");
			} finally {
				if (Thread.currentThread().isAlive()){
					driver.quit();					
				}
			}

			Runtime.getRuntime().exec("webdrivers/HideNSeek.exe 1 \"" + "Google Chrome" + "\"");

			logger.info("Finish parsing expression for SKU list");


		long elapsedTime = System.nanoTime() - start;
		logger.info("Elapsed time to parse: " + elapsedTime + " ns (~ "
				+ new DecimalFormat("#.###").format(elapsedTime * 1e-9) + " s)");

		return sb.toString();
	}

	private static String[] getExpressionResultInfoByChromeForSkuList(WebDriver driver, String exprText,
			String paramText, String skuIdText, int loadTime, int parseTime) {

		String expressionResult = null;
		String expressionStatus = null;

		try {

			// process expression
			evaluateExpressionWithJavascript(driver, exprText, paramText, skuIdText);

			// wait for loading to disappear
			driver.manage().timeouts().implicitlyWait(loadTime, TimeUnit.SECONDS);
			try {
				WebElement myDynamicElement = (new WebDriverWait(driver, loadTime))
						.until(ExpectedConditions.visibilityOfElementLocated(By.id("loading")));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			waitForElToBeRemove(driver, By.id("loading"));

			new WebDriverWait(driver, parseTime).until(new Predicate<WebDriver>() {
				public boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				}

			});

			// get results
			expressionResult = driver.findElement(By.name("result")).getAttribute("value");
			expressionStatus = driver.findElement(By.name("result")).findElement(By.xpath(".."))
					.findElement(By.xpath("..")).getAttribute("class");

			logger.info("Expression status: " + expressionStatus);
			logger.info("Expression result: " + expressionResult);

		} catch (Exception e) {
			logger.error(e.getMessage());
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

	private static void evaluateExpressionWithJavascript(WebDriver driver, String expressionText, String parametersText,
			String skuIdText) {
		// its x3 faster than 'Sendkeys' with huge expression
		if (driver instanceof JavascriptExecutor) {

			// must escape new lines and single quote for correct work of JS
			expressionText = expressionText.replaceAll("\n", "\\\\n").replaceAll("\'", "\\\\'");
			parametersText = parametersText.replaceAll("\n", "\\\\n").replaceAll("\'", "\\\\'");
			skuIdText = skuIdText.replaceAll("\n", "\\\\n").replaceAll("\'", "\\\\'");

			((JavascriptExecutor) driver).executeScript(
					"$.connection.hub.start().done(function parse(){hub.server.parseExpression('" + expressionText
							+ "','" + parametersText + "',++hub.lastServedRequest,'" + skuIdText + "');});");
		}

	}

	private static void evaluateExpressionWithSendkeys(WebDriver driver, String expressionText, String parametersText,
			String skuIdText) {
		WebElement skuId = driver.findElement(By.name("skuId"));
		skuId.clear();
		skuId.sendKeys(skuIdText);
		WebElement expression = driver.findElement(By.name("expression"));
		expression.sendKeys(expressionText);
		WebElement parameters = driver.findElement(By.name("parameters"));
		parameters.clear();
		parameters.sendKeys(parametersText);

	}

	public static String escapeHtml(String input) {
		StringBuilder escapedText = new StringBuilder();
		boolean isChanged = false;

		final String[][] basicEscape = { { "\"", "&quot;" }, // " - double-quote
				{ "&", "&amp;" }, // & - ampersand
				{ "<", "&lt;" }, // < - less-than
				{ ">", "&gt;" } }; // > - greater-than

		for (int i = 0; i < input.length(); i++) {
			isChanged = false;
			for (int j = 0; j < basicEscape.length; j++) {
				if (input.charAt(i) == basicEscape[j][0].charAt(0)) {
					escapedText.append(basicEscape[j][1]);
					isChanged = true;
				}

			}

			if (!isChanged) {
				escapedText.append(input.charAt(i));
			}
		}

		return escapedText.toString();
	}

}

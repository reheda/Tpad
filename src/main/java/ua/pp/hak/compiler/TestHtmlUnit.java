package ua.pp.hak.compiler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

public class TestHtmlUnit {
	// To remove all output from the latest version of HtmlUnit you just have to
	// add these lines in a static block or in your main class:
	// static {
	// java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	// System.setProperty("org.apache.commons.logging.Log",
	// "org.apache.commons.logging.impl.NoOpLog");
	// }

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiesEnabled(true);

		WebClient webClient1 = new WebClient(BrowserVersion.CHROME);
		webClient1.getOptions().setJavaScriptEnabled(false);
		// webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient1.setCookieManager(cookieManager);

		try {
			final HtmlPage page1 = webClient1.getPage("http://templex.cnetcontent.com/Home/Parser");
			final HtmlForm form = page1.getForms().get(1);
			final HtmlTextInput textField = form.getInputByName("Email");
			final HtmlPasswordInput pwd = form.getInputByName("Password");
			textField.setValueAttribute("test@test.com");
			pwd.setValueAttribute("password");

			final HtmlPage page3 = (HtmlPage) form.getInputByValue("Log in").click();

			LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
					"org.apache.commons.logging.impl.NoOpLog");

			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

			WebClient webClient2 = new WebClient(BrowserVersion.CHROME);
			webClient2.setCookieManager(cookieManager);
			webClient2.getOptions().setJavaScriptEnabled(true);
			webClient2.getOptions().setCssEnabled(false);

			webClient2.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient2.getOptions().setThrowExceptionOnScriptError(false);
			final HtmlPage page2 = webClient2.getPage("http://templex.cnetcontent.com/Home/Parser");
			final HtmlForm form2 = page2.getForms().get(1);
			final HtmlTextArea expression = form2.getTextAreaByName("expression");
			final HtmlTextArea parameters = form2.getTextAreaByName("parameters");
			final HtmlTextArea result = form2.getTextAreaByName("result");
			final HtmlTextInput skuId = form2.getInputByName("skuId");
			expression.setText("SKU.Brand");
			parameters.setText("blabla");
			skuId.setValueAttribute("12");

			waitForJsLoad(page2, 10);
			
			System.out.println(page2.asXml());
			System.out.println(result.asText());
			System.out.println("Base Uri 1 : " + page1);
			System.out.println("Base Uri 2 : " + page2);
			
			webClient1.close();
			webClient2.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void waitForJsLoad(HtmlPage currentPage, int maxSecondsToWait) {
		if (maxSecondsToWait <= 0)
			return;

		JavaScriptJobManager manager = currentPage.getEnclosingWindow().getJobManager();
		while (manager.getJobCount() > 0 && maxSecondsToWait > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			maxSecondsToWait--;
		}
	}

}

package ua.pp.hak.compiler;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

public class Test2 {
	public static void main(String[] args) throws IOException {

		// * Connect to website
		String url = "http://templex.cnetcontent.com/Home/Parser";
		Connection.Response resp = Jsoup.connect(url) //
				.timeout(30000) //
				.method(Connection.Method.GET) //
				.execute();
		
		//This will get you cookies
		Map<String, String> cookies = resp.cookies();

		
		// * Find the form
		Document responseDocument = resp.parse();
		Element potentialForm = responseDocument.select(".form-horizontal").first();
		checkElement("form element", potentialForm);
		FormElement form = (FormElement) potentialForm;

		// * Fill in the form and submit it
		// ** Search Type
		Element inputEmail = form.select("[name$=Email]").first();
		checkElement("input Email", inputEmail);
		inputEmail.val("test@test.com");

		// ** Name search
		Element inputPass = form.select("[name$=Password]").first();
		checkElement("input Password", inputPass);
		inputPass.val("password");
		
		// ** Submit the form
		Document searchResults = form.submit().cookies(cookies).post();

		// * Extract results (entity numbers in this sample code)
//		System.out.println(searchResults);

		
		
		
		
		// -----------------------------------------------
		Element potentialForm2 = searchResults.select(".form-horizontal").first();
		checkElement("form element", potentialForm2);
		FormElement form2 = (FormElement) potentialForm2;
		
		// * Fill in the form and submit it
		form2.attr("action", "/Parser/Parser");
		
		// ** Search Type
		Element expression = form2.select("[name$=expression]").first();
		checkElement("expression", expression);
		expression.val("SKU.Brand");

		// ** Name search
		Element parameters = form2.select("[name$=parameters]").first();
		checkElement("parameters", parameters);
		parameters.val("AccTree=-1, Evaluate=false");

		// ** Name search
		Element skuId = form2.select("[name$=skuId]").first();
		checkElement("skuId", skuId);
		skuId.val("S17876488");

		
		System.out.println(form2);
		// ** Submit the form
		Document searchResults2 = form2.submit().cookies(cookies).post();

		// * Extract results (entity numbers in this sample code)
		System.out.println(searchResults2);

	}

	public static void checkElement(String name, Element elem) {
		if (elem == null) {
			throw new RuntimeException("Unable to find " + name);
		}

	}
}

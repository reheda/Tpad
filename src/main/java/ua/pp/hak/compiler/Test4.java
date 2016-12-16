package ua.pp.hak.compiler;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test4 {
	public static void main(String[] args) throws IOException {
		Connection.Response response = Jsoup.connect("http://www.mikeportnoy.com/forum/login.aspx")
				.method(Connection.Method.GET).execute();

		response = Jsoup.connect("http://www.mikeportnoy.com/forum/login.aspx")
				.data("ctl00$ContentPlaceHolder1$ctl00$Login1$UserName", "username")
				.data("ctl00$ContentPlaceHolder1$ctl00$Login1$Password", "password").cookies(response.cookies())
				.method(Connection.Method.POST).execute();

		Document homePage = Jsoup.connect("http://www.mikeportnoy.com/forum/default.aspx").cookies(response.cookies())
				.get();
	}
}

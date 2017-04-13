package ua.pp.hak.servlet;

import ua.pp.hak.compiler.TChecker;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;

/**
 * Created by rev on 4/12/2017.
 */

@WebServlet("/")
public class TpadServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	resp.getWriter().print("Server is up and running!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String expression = req.getParameter("expression");
        if (expression == null) {
            expression = "";
        }

        String expressionResult = TChecker.checkExpression(expression);
        final String NEW_LINE = "\n";
        if (expressionResult == null) {
            expressionResult = "Expression is valid";

            // check deactivated attributes
            String deactivatedAttrNote = TChecker.checkDeactivatedAttributes(expression);
            if (deactivatedAttrNote != null) {
                expressionResult += NEW_LINE;
                expressionResult += NEW_LINE;
                expressionResult += deactivatedAttrNote;
            }
        } else {
//            expressionResult = expressionResult.replace("\n", NEW_LINE);
        }

        req.setAttribute("expressionResult", expressionResult);
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
//        resp.addHeader("Access-Control-Allow-Headers","Origin, Content-Type, X-Auth-Token");
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
        resp.getWriter().print(expressionResult);
//        req.getRequestDispatcher("expression-result.jsp").forward(req, resp);
    }
}

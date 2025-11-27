package controller.HomePage;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/about-us")
public class AboutUsServlet extends HttpServlet {
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Imposta il tipo di contenuto
        //response.setContentType("text/html;charset=UTF-8");

        // Inoltra la richiesta alla JSP
        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/about-us.jsp");
        dispatcher.forward(request, response);
    }
}
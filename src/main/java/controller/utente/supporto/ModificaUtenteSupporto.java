package controller.utente.supporto;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/modifica-dati-supporto")
public class ModificaUtenteSupporto extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/areaPservices/modificaDati.jsp");
        dispatcher.forward(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

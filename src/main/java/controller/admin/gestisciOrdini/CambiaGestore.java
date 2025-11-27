package controller.admin.gestisciOrdini;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ordineService.Ordine;
import model.ordineService.OrdineDAO;

import java.io.IOException;

@WebServlet("/cambia-gestore")
public class CambiaGestore extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final OrdineDAO ordineDAO = new OrdineDAO();
        final Ordine ordine = ordineDAO.doRetrieveById(request.getParameter("ordineID"));

        ordine.setMatricola(request.getParameter("matricola"));
        ordineDAO.updateOrdineMatricola(ordine);
        request.setAttribute("utenteScelto", request.getParameter("utenteScelto"));

        final RequestDispatcher dispatcher = request.getRequestDispatcher("gestisci-ordiniByUtente");
        dispatcher.forward(request, response);
    }
}

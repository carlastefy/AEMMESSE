package controller.admin.gestisciOrdini;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ordineService.Ordine;
import model.ordineService.OrdineDAO;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/gestisci-ordiniByUtente")
public class GestisciOrdiniByUtente extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final OrdineDAO ordineDAO = new OrdineDAO();
        final UtenteDAO utenteDAO = new UtenteDAO();
        final Utente utente = utenteDAO.doRetrieveById(request.getParameter("utenteScelto"));
        final List<Ordine> ordini = ordineDAO.doRetrieveByUtente(utente.getEmail());
        request.setAttribute("utenteScelto", utente);
        request.setAttribute("ordiniUtente", ordini);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/ordini/stampaOrdini.jsp");
        dispatcher.forward(request, response);
    }
}
